package com.jia54321.utils.entity.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jia54321.utils.*;
import com.jia54321.utils.entity.IStorageConstants;
import com.jia54321.utils.entity.query.QueryContent;
import com.jia54321.utils.entity.query.QueryContentFactory;
import com.jia54321.utils.entity.query.SimpleCondition;
import com.jia54321.utils.entity.service.context.IDynamicEntityContext;
import com.jia54321.utils.entity.service.context.IEntityEnvContext;
import com.jia54321.utils.entity.service.context.IStorageContext;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import com.jia54321.utils.entity.DynamicEntity;


import com.jia54321.utils.hash.FileHashUtil;
import com.jia54321.utils.oss.compress.Zip;
import com.jia54321.utils.oss.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StorageContext
 * @author gg
 * @date 2019-08-25
 */
public class StorageContext implements IStorageContext {
	private static Logger log = LoggerFactory.getLogger(StorageContext.class);

	private IEntityEnvContext entityEnvContext;

	private IDynamicEntityContext dynamicEntityContext;

	public StorageContext(IEntityEnvContext entityEnvContext, IDynamicEntityContext dynamicEntityContext) {
		this.entityEnvContext = entityEnvContext;
		this.dynamicEntityContext = dynamicEntityContext;
	}

	@Override
	public Object cmd(String cmd) {
		Map<String, Object> result = Maps.newLinkedHashMap();
		if (JsonHelper.isEmpty(cmd)) {

		} else if ("info".equals(cmd)) {
			StorageRoot[] list = StorageRoot.values();
			for (int i = 0; i < list.length; i++) {
				result.put(list[i].toString(), rootPath(list[i]));
			}
		} else {

		}

		return result;
	}

	@Override
	public Boolean isFileResourceAvailable(String localOrUrl) {
		if (log.isDebugEnabled()) {
			log.debug(localOrUrl);
		}
		return IOUtil.contentIsAvailable(localOrUrl, rootPath(StorageRoot.webRoot));
	}

	@Override
	public Map<String,Object> getObject(String id) {
		SimpleCondition sc = null;
		if(null == sc){
			sc = new SimpleCondition();
		}
		if(null != id && !"".equals(id)){
			sc.setIds(id);
		}
		List<Map<String,Object>> result = queryObjects(Lists.newArrayList(sc));
		if (null != result && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String,Object>> simpleQuery(String typeId, SimpleCondition sc) {
		return queryObjects(Lists.newArrayList(sc));
	}

	@Override
	public List<Map<String, Object>> queryObjects(List<SimpleCondition> sc) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		List<QueryContent<DynamicEntity>> rqcList = null;
		try {
			for (SimpleCondition simpleCondition : sc) {
				simpleCondition.setTypeId(TYPE_ID_OBJECT);
			}
			rqcList = dynamicEntityContext.multiQuery(QueryContentFactory.createQueryContents(sc));
			for (QueryContent<DynamicEntity> rqc : rqcList) {
				if(null != rqc && null != rqc.getResult() && rqc.getResult().size() > 0) {
					for (int i = 0; i < rqc.getResult().size(); i++) {
						Map<String, Object> objectInfo = ((List<DynamicEntity>)rqc.getResult()).get(i).getItems();
						objectInfo.put(ITEM_KEY, rqc.getKey());
						updateLocalInfo(objectInfo);

						result.add(objectInfo);
					}
				}
			}
		} catch (Exception e) {
			if(log.isErrorEnabled()) {
				log.error(String.format("查询文件对象失败sc=%s",JsonHelper.toJson(sc)), e);
			}
		}
		return result;
	}

	@Override
	public long count(List<SimpleCondition> sc) {
		long result = 0;
		List<QueryContent<DynamicEntity>> rqcList = null;
		try {
			for (SimpleCondition simpleCondition : sc) {
				simpleCondition.setTypeId(TYPE_ID_OBJECT);
			}
			rqcList = dynamicEntityContext.multiQuery(QueryContentFactory.createQueryContents(sc));
			for (QueryContent<DynamicEntity> rqc : rqcList) {
				result += rqc.getPage().getTotalElements();
			}
		} catch (Exception e) {
			if(log.isErrorEnabled()) {
				log.error(String.format("查询文件对象失败sc=%s",JsonHelper.toJson(sc)), e);
			}
		}
		return result;
	}

	@Override
	public Map<String,Object> putObject(InputStream input, String jsonItems, Map<String, Object> extItems) {
		if (log.isDebugEnabled()) {
			log.debug(jsonItems);
		}
		String filePath = rootPath(StorageRoot.tmpUploadRoot) + File.separator + IdGeneration.getStringId() + ".dat";
		File temp = IOUtil.createParentDirByFilePath(filePath);

		IOUtil.copyByPath(input, temp.getPath());


		// 文件关联对象
		DynamicEntity objectAssociationInfo = dynamicEntityContext.newDynamicEntity(TYPE_ID_OBJECT, jsonItems, extItems);
		// SHA1
		String sha1 = FileHashUtil.getChecksum(temp.getPath(), FileHashUtil.SHA1);
		objectAssociationInfo.set(ITEM_OBJECT_DIGEST, sha1);
		File renameTemp = IOUtil.createParentDirByFilePath(temp.getParentFile().getAbsolutePath() + '/' + sha1);
		temp.renameTo(renameTemp);

		// 1 首次更新
		updateLocalInfo(objectAssociationInfo.getItems());

		String objectId = String.valueOf(objectAssociationInfo.get(ITEM_OBJECT_ID));
		if(JsonHelper.isEmpty(objectAssociationInfo.get(ITEM_OBJECT_ID))){
			//保存成功，返回 objectId
			objectId = dynamicEntityContext.insert(objectAssociationInfo);
		} else {
			dynamicEntityContext.get(TYPE_ID_OBJECT, objectId);
			dynamicEntityContext.update(objectAssociationInfo);
		}

		objectAssociationInfo.set(ITEM_OBJECT_ID, objectId);
		//objectAssociationInfo.setItems(getObjectWithRealPath(objectId, null));

		// 2 再次更新
		updateLocalInfo(objectAssociationInfo.getItems());

		// 保存文件对象
		saveObject(renameTemp, objectAssociationInfo.getItems());

		//tempFile.delete();

		return objectAssociationInfo.getItems();
	}

	@Override
	public List<Map<String, Object>> mvObjects(List<SimpleCondition> sc, Map<String, Object> targetOverItems) {
		for (SimpleCondition simpleCondition : sc) {
			if(JsonHelper.isEmpty(simpleCondition.getIds())){
				throw new RuntimeException("请传入SimpleCondition中的ids");
			}
		}
		List<Map<String, Object>> result = Lists.newArrayList();
		List<Map<String, Object>> queryList = queryObjects(sc);
		Map<String, Object> newMap = Maps.newHashMap(targetOverItems);
		for (Map<String, Object> map : queryList) {
			String oldId = (String) map.get(ITEM_OBJECT_ID);
			newMap.putAll(map);
			// new
			newMap.put(ITEM_OBJECT_BLOCK_NAME, JsonHelper.toStr(targetOverItems.get(ITEM_OBJECT_BLOCK_NAME), (String)map.get(ITEM_OBJECT_BLOCK_NAME)));
			newMap.put(ITEM_OBJECT_PATH_KEY, JsonHelper.toStr(targetOverItems.get(ITEM_OBJECT_PATH_KEY), (String)map.get(ITEM_OBJECT_PATH_KEY)));
			newMap.put(ITEM_OBJECT_NAME, JsonHelper.toStr(targetOverItems.get(ITEM_OBJECT_NAME), (String)map.get(ITEM_OBJECT_NAME)));
			newMap.remove(ITEM_OBJECT_ID);
			DynamicEntity newObject = dynamicEntityContext.newDynamicEntity( TYPE_ID_OBJECT, null, newMap);
			dynamicEntityContext.delete(TYPE_ID_OBJECT, oldId);
			newObject.set(ITEM_OBJECT_ID, dynamicEntityContext.insert(newObject));
			result.add(newObject.getItems());
		}
		return result;
	}


	@Override
	public List<Map<String, Object>> upObjects(List<Map<String, Object>> targetOverItems) {
		List<Map<String, Object>> result = Lists.newArrayList();
		for (Map<String, Object> overItems : targetOverItems) {
			String objectId = (String) overItems.get(ITEM_OBJECT_ID);
			if (JsonHelper.isEmpty(objectId)) {
				continue;
			}
			Map<String, Object> newMap = getObject(objectId);

			// 敏感字段禁止更新
			overItems.remove(ITEM_OBJECT_DIGEST);
			overItems.remove(ITEM_OBJECT_SIZE);

			newMap.putAll(overItems);
			DynamicEntity upObject = dynamicEntityContext.newDynamicEntity( TYPE_ID_OBJECT, null, newMap);
			dynamicEntityContext.update(upObject);
			result.add(newMap);
		}
		return result;
	}



//	/**
//	 *
//	 */
//	public Map<String,Object> getObject(OutputStream os, String objectId, SimpleCondition sc){
//		//文件关联对象
//		Map<String,Object> items = getObjectWithRealPath(objectId, sc);
//
//		String inputPath = String.valueOf(items.get(ITEM_OBJECT_REAL_PATH));
//
//		IOUtil.copyByPath(inputPath, os);
//
//        items.remove(ITEM_OBJECT_REAL_PATH);
//
//		return items;
//
//	}

	@Override
	public List<Map<String,Object>> delObjects(List<SimpleCondition> sc) {
		StringBuffer allIds = new StringBuffer();
		List<DynamicEntity> willDelLst = new ArrayList<DynamicEntity>();

		if( null != sc ) {
			for (SimpleCondition simpleCondition : sc) {
				simpleCondition.setTypeId(TYPE_ID_OBJECT);
			}
			List<QueryContent<DynamicEntity>> rqcList = dynamicEntityContext.multiQuery(QueryContentFactory.createQueryContents(sc));
			for (QueryContent<DynamicEntity> rqc : rqcList) {
				willDelLst.addAll((List<DynamicEntity>)rqc.getResult());
			}
		}

		for (Iterator<DynamicEntity> iterator = willDelLst.iterator(); iterator.hasNext();) {
			DynamicEntity objectAssociationEntity = (DynamicEntity) iterator.next();

			String path = getPath(objectAssociationEntity.getItems());

			boolean success = false;
			//TODO 内容寻址时 文件的删除问题

//			File f = new File(path), fInfo = new File(path + ".info"), parent = new File(f.getParent());
//			boolean success = parent.exists() && f.exists() && f.delete();
//			boolean successInfo = parent.exists() && fInfo.exists() && fInfo.delete();

			if(log.isDebugEnabled()){
				log.debug(String.format("path=%s, success=[%s]", path, success));
			}

			allIds.append(String.valueOf(objectAssociationEntity.get("OBJECT_ID")));

			allIds.append(',');
		}
		if(allIds.length() > 0 ) {
			String strAllIds = allIds.deleteCharAt(allIds.length()-1).toString();
			dynamicEntityContext.delete(TYPE_ID_OBJECT, strAllIds);
		}

		List<Map<String, Object>> result = JsonHelper.toMapList(willDelLst);
		return result;
	}

	@Override
	public List<Map<String,Object>> cpObjects(List<SimpleCondition> sc, Map<String, Object> targetOverItems) {
		if( null == sc ) {
			throw new RuntimeException("无法复制");
		}
		//TODO List<SimpleCondition> sc  ==>  SimpleCondition sc
		QueryContent rqc = dynamicEntityContext.copy(TYPE_ID_OBJECT,  QueryContentFactory.createQueryContent(sc.get(0)), targetOverItems);
		List<DynamicEntity> lst =  (List<DynamicEntity>)rqc.getResult();

		for (Iterator<DynamicEntity> iterator = lst.iterator(); iterator.hasNext();) {
			DynamicEntity dynamicEntity = iterator.next();
			String sourcePath = getPath(dynamicEntity.getItems(), StoragePathType.autoPath);
			saveObject(new File(sourcePath), dynamicEntity.getItems());
		}
		return  QueryContentFactory.createSimpleQueryContent(rqc).getRows();
	}

//	public SimpleQueryContent<Map<String, Object>> simpleQuery(String webContentPath, SimpleCondition sc) {
//		setWebContentPath(webContentPath);
//
//		SimpleQueryContent<Map<String, Object>> result =
//		QueryContentFactory.createSimpleQueryContent(dynamicEntityContext.query(TYPE_ID_OBJECT, QueryContentFactory.createQueryContent(sc)));
//
//		for (Map<String, Object> objectAssociationInfo : result.getRows()) {
//			updateLocalInfo(objectAssociationInfo);
//		}
//		return result;
//	}
//
//	@Override
//	public SimpleQueryContent queryObjects(SimpleCondition sc){
//		return dynamicEntityContext.simpleQuery(TYPE_ID_OBJECT, sc);
//	}
//
//
//	@Override
//	public List<File> queryFiles(String webContentPath, SimpleCondition sc) {
//		List<Map<String, Object>> entityList = this.simpleQuery(webContentPath, sc).getRows();
//		List<File> entityFile = new ArrayList<File>();
//		for (Map<String, Object> entity : entityList) {
//			URL url = null;
//			try {
//				url = new URL((String)entity.get(ITEM_OBJECT_URL));
//				entityFile.add(new File(url.toURI()));
//			} catch (MalformedURLException | URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return entityFile;
//	}

	@Override
	public Map<String,Object> resizeImgsByResult(List<Map<String, Object>> lstExtItems, String resizeImg) {
		Map<String, Object> extItems0 = Maps.newHashMap();
		//String objectId = (String)lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_ID);
		String objectPathKey = (String)lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_PATH_KEY);
		String objectBizFormId = (String)lstExtItems.get(0).get(IStorageConstants.ITEM_BIZ_FORM_ID);
		String objectName = (String)lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_NAME);
		String objectDigest =  (String)lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_DIGEST);
		String inputPath =  (String)lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_REAL_PATH);

		final String outPath = rootPath(StorageRoot.tmpResizeImgRoot) + File.separator + objectDigest + "-" + resizeImg + ".JPEG";
		File tempFile = IOUtil.createParentDirByFilePath(outPath);
		Image.resizeV1(resizeImg, new File(inputPath), tempFile);

		inputPath = tempFile.getAbsolutePath();

		extItems0.put(IStorageConstants.ITEM_OBJECT_NAME,  resizeImg + "-" + objectName);
		extItems0.put(IStorageConstants.ITEM_OBJECT_EXT, lstExtItems.get(0).get(IStorageConstants.ITEM_OBJECT_EXT));
		extItems0.put(IStorageConstants.ITEM_OBJECT_REAL_PATH, inputPath);

		return extItems0;
	}

	@Override
	public Map<String,Object> compressByResult(List<Map<String, Object>> lstExtItems, String compressType) {
		//排序对象
		final TreeMultimap<String, String> multimapLstObjectDigests =  TreeMultimap.create();
		final List<File> lstFiles = Lists.newArrayList();
		final List<String> lstFileNames =Lists.newArrayList(Lists.transform(lstExtItems, new Function<Map<String,Object>, String>() {
			@Override
			public String apply(Map<String, Object> input) {
				String key = (String)input.get(IStorageConstants.ITEM_KEY);
				if (null == key) {
					key = "";
				}
				multimapLstObjectDigests.put(key, (String)input.get(IStorageConstants.ITEM_OBJECT_DIGEST));

				lstFiles.add(new File((String)input.get(IStorageConstants.ITEM_OBJECT_REAL_PATH)));

				return String.format("%s/%s",
						key,
						input.get(IStorageConstants.ITEM_OBJECT_NAME))
						.replaceAll("//", "/");
			}
		}));
		//性能问题
		String json = JsonHelper.toJson(multimapLstObjectDigests.asMap());
		String sha1BySortedLstObjectDigests = FileHashUtil.toSha1(json);
		//
		final String outPath = rootPath(StorageRoot.tmpCompressRoot) + File.separator + sha1BySortedLstObjectDigests;
		File tempFile = IOUtil.createParentDirByFilePath(outPath);

		Map<String, Object> extItems0 = Maps.newHashMap();
		extItems0.put(IStorageConstants.ITEM_OBJECT_NAME, "file-" + DateUtil.toNowStampString() + ".zip");
		extItems0.put(IStorageConstants.ITEM_OBJECT_EXT, "zip");
		extItems0.put(IStorageConstants.ITEM_OBJECT_REAL_PATH, tempFile.getAbsolutePath());
		extItems0.put(IStorageConstants.ITEM_OBJECT_DIGEST, sha1BySortedLstObjectDigests);

		if(!tempFile.exists()) {
			tempFile = Zip.zipFile(null, lstFiles.toArray(new File[0]), lstFileNames.toArray(new String[0]), tempFile);
		}
		return extItems0;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 更新需要计算的字段
	 * @param objectAssociationInfo
	 */
	private void updateLocalInfo(Map<String, Object> objectAssociationInfo) {
		objectAssociationInfo.put(ITEM_OBJECT_REAL_PATH,  getPath(objectAssociationInfo, StoragePathType.autoPath));

		/*
		objectAssociationInfo.put(ITEM_OBJECT_REAL_PATH0, getPath(objectAssociationInfo, PathType.digestPath));
		objectAssociationInfo.put(ITEM_OBJECT_REAL_PATH1, getPath(objectAssociationInfo, PathType.virtualPath));
		/////////////////////////////////////////////////////////
		// 遗留代码
		/////////////////////////////////////////////////////////
		objectAssociationInfo.put(ITEM_OBJECT_REAL_PATH2, getPath(objectAssociationInfo, PathType.virtualPath));
		////////////////////////////////////////////////////////
		*/

		String ext = "." + JsonHelper.toStr(objectAssociationInfo.get(ITEM_OBJECT_EXT), "");

		String pathKey = JsonHelper.toStr(objectAssociationInfo.get(ITEM_OBJECT_PATH_KEY), "");
		String formKey = JsonHelper.toStr(objectAssociationInfo.get(ITEM_BIZ_FORM_KEY), "");
		String formId =  JsonHelper.toStr(objectAssociationInfo.get(ITEM_BIZ_FORM_ID), "");
		long objectSize =  JsonHelper.toLong(objectAssociationInfo.get(ITEM_OBJECT_SIZE), 0L);
		// 特殊处理
//		ITEM_OBJECT_PRINT_SIZE
		objectAssociationInfo.put(ITEM_OBJECT_PRINT_SIZE, JsonHelper.toFilePrintSize(objectSize));

		objectAssociationInfo.put(ITEM_OBJECT_URL, String.format(WEB_URL_GET_OBJECT_FORMAT_4_ARGS,
				entityEnvContext.envWebRootPath("storage"),
				ext,
				objectAssociationInfo.get(ITEM_OBJECT_ID),""));
		objectAssociationInfo.put(ITEM_OBJECT_VIEW_URL, String.format(WEB_URL_GET_OBJECT_FORMAT_4_ARGS,
				entityEnvContext.envWebRootPath("storage"),
				ext,
				objectAssociationInfo.get(ITEM_OBJECT_ID), "1"));
		objectAssociationInfo.put(ITEM_OBJECT_DEL_URL, String.format(WEB_URL_DEL_OBJECT_FORMAT_2_ARGS,
				entityEnvContext.envWebRootPath("storage"),
				objectAssociationInfo.get(ITEM_OBJECT_ID)));

		objectAssociationInfo.put(ITEM_OBJECT_REST_P_URL, String.format(OBJECT_REST_P_URL_FORMAT_5_ARGS,
				entityEnvContext.envWebRootPath("storage"), pathKey,formKey , formId, ""));
		objectAssociationInfo.put(ITEM_OBJECT_REST_O_URL, String.format(OBJECT_REST_O_URL_FORMAT_3_ARGS,
				entityEnvContext.envWebRootPath("storage"), objectAssociationInfo.get(ITEM_OBJECT_ID), ""));
	}


	private void saveObject(File inputPath, Map<String, Object> objectItems) {
		if(!inputPath.exists()){
			return ;
		}
		String realPath = getPath(objectItems);
		File storgeFile = new File(realPath);

		String inputFileSha1 = (String)objectItems.get(ITEM_OBJECT_DIGEST);

		if(storgeFile.exists()
				&& FileHashUtil.getSHA1Checksum(realPath).equalsIgnoreCase(inputFileSha1)) {
			return ;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("save:" + realPath);
			}
			IOUtil.copyByPath(inputPath.getAbsolutePath(), realPath);
		}

		inputPath.delete();
	}

	/**
	 * rootPath
	 * @param optOneKey 可选
	 * @return
	 */
	private String rootPath(StorageRoot... optOneKey) {
		final String result;
		if (null != optOneKey && optOneKey.length > 0 && null != optOneKey[0] ) {
			StorageRoot key = optOneKey[0];
		    if (StorageRoot.webRoot.equals(key)) {
			    result = entityEnvContext.envWebRootPath();

		    } else if (StorageRoot.storageRoot.equals(key)) {
			    result = entityEnvContext.envStorageRootPath();
		    } else if (StorageRoot.objectsDigestRoot.equals(key)) {
				result = entityEnvContext.envStorageRootPath() + "/objects/";
			} else if (StorageRoot.objectsVirtualRoot.equals(key)) {
				result = entityEnvContext.envStorageRootPath() + "/virtual/";

			} else if (StorageRoot.tmpRoot.equals(key)) {
				result = entityEnvContext.envTempRootPath();
			} else if (StorageRoot.tmpUploadRoot.equals(key)) {
				result = entityEnvContext.envTempRootPath() + "/upload/";
			} else if (StorageRoot.tmpResizeImgRoot.equals(key)) {
				result = entityEnvContext.envTempRootPath() + "/resizeImg/";
			} else if (StorageRoot.tmpCompressRoot.equals(key)) {
				result = entityEnvContext.envTempRootPath() + "/compress/";

			} else {
				result = entityEnvContext.envStorageRootPath();
			}
		} else {
			result = entityEnvContext.envStorageRootPath();
		}
		return result;
	}

	/**
	 * 查找文件路径
	 * @param objectInfo
	 * @param pathType PathType  0， 1， auto  摘要路径 INT_0 全虚拟路径 INT_1
	 * @return
	 */
	private String getPath(Map<String, Object> objectInfo, StoragePathType... pathType) {
		String path = "";

		StoragePathType localPathType = StoragePathType.autoPath;
		if (null != pathType && pathType.length >= 1 && null != pathType[0]) {
			localPathType = pathType[0];
		}

		if (StoragePathType.autoPath == localPathType) {
			//判断
			boolean isDigestPath  = (0 == NumberUtils.toInt(String.valueOf(objectInfo.get(ITEM_OBJECT_PATH_TYPE)), "null", 0));
			//存在摘要
			boolean isExistDigest = !JsonHelper.isEmpty(objectInfo.get(ITEM_OBJECT_DIGEST));

			if (isDigestPath && isExistDigest) {
				localPathType = StoragePathType.digestPath;
			} else {
				localPathType = StoragePathType.virtualPath;
			}
		}

		if (StoragePathType.digestPath == localPathType) {
			//=========================================================================================
			//内容寻址
			String objectDigest = String.valueOf(objectInfo.get(ITEM_OBJECT_DIGEST));
			String objectDigestPart1 = objectDigest.substring(0, 2);
			String objectDigestPart2 = objectDigest.substring(2);

			path = rootPath(StorageRoot.objectsDigestRoot) + objectDigestPart1 + "/" + objectDigestPart2;
			//=========================================================================================
		}

		if (StoragePathType.virtualPath == localPathType) {
			//=========================================================================================
			//全虚拟路径
			String objectBlockName = String.valueOf(objectInfo.get(ITEM_OBJECT_BLOCK_NAME));
			String objectPathKey = String.valueOf(objectInfo.get(ITEM_OBJECT_PATH_KEY));
			String objectName = String.valueOf(objectInfo.get(ITEM_OBJECT_NAME));
			path = rootPath(StorageRoot.objectsVirtualRoot) + "/" + objectBlockName + "/" + objectPathKey + "/" + objectName;
			//=========================================================================================
		}

		path = path.replaceAll("\\\\\\\\", "/");
		path = path.replaceAll("\\\\", "/");
		path = path.replaceAll("//", "/");

		objectInfo.put((StoragePathType.digestPath == localPathType)?ITEM_OBJECT_REAL_PATH0:ITEM_OBJECT_REAL_PATH1, path);

		if (log.isDebugEnabled()) {
			log.debug(String.format("localPathType=%s, path=%s", localPathType, path));
		}

		return path;
	}


}
