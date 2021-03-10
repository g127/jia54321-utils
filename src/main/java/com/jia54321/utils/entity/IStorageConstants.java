package com.jia54321.utils.entity;

import java.util.Map;

import com.google.common.collect.Maps;

import com.jia54321.utils.Base64;
import com.jia54321.utils.IOUtil;

/**
 * 存储服务常量
 * @author G
 */
public interface IStorageConstants {
	/**
	 * 根路径枚举类
	 */
	static enum StorageRoot {
		/**web根路径 */
		webRoot("webRoot"), 
		/////////////////////////////////////////////////////////////
		//  本地存储
		/////////////////////////////////////////////////////////////
		/**本地存储根路径 */
		storageRoot("storageRoot"), 
		/**本地存储-摘要方式根路径 */
		objectsDigestRoot("objectsDigestRoot"), 
		/**本地存储- 全虚拟路径方式根路径 */
		objectsVirtualRoot("objectsVirtualRoot"),
		/////////////////////////////////////////////////////////////
		//  临时存储
		/////////////////////////////////////////////////////////////
		/**临时目录根路径 */
		tmpRoot("tmpRoot"), 
		/**临时目录 上传根路径 */
		tmpUploadRoot("tmpUploadRoot"), 
		/**临时目录 缩略图根路径 */
		tmpResizeImgRoot("tmpResizeImgRoot"), 
		/**临时目录 压缩文件根路径 */
		tmpCompressRoot("tmpCompressRoot");
		/////////////////////////////////////////////////////////////
		final private String value;
		private StorageRoot(String value) { // 必须是private的，否则编译错误
			this.value = value;
		}
		public String value() {
			return this.value;
		}
	}
	/////////////////////////////////////////////////////////
	// 枚举类型
	//////////////////////////////////////////////////////////
	static enum StoragePathType {
		autoPath(-1), digestPath(0), virtualPath(1);
		private int value = 0;

		private StoragePathType(int value) { // 必须是private的，否则编译错误
			this.value = value;
		}

		public static StoragePathType valueOf(int value) { // 手写的从int到enum的转换函数
			switch (value) {
			case 0:
				return digestPath;
			case 1:
				return virtualPath;
			default:
				return autoPath;
			}
		}

		public int value() {
			return this.value;
		}
	}
	
	static enum StorageBlockName {
		upload("upload"), ueditor("ueditor") ;
		/////////////////////////////////////////////////////////////
		final private String value;
		private StorageBlockName(String value) { // 必须是private的，否则编译错误
			this.value = value;
		}
		public String value() {
			return this.value;
		}
	}
	
	static enum StoragePathKey {
		avatar("avatar");
		/////////////////////////////////////////////////////////////
		final private String value;
		private StoragePathKey(String value) { // 必须是private的，否则编译错误
			this.value = value;
		}
		public String value() {
			return this.value;
		}
	}
	
	static enum StorageFormKey {
		user("user");
		/////////////////////////////////////////////////////////////
		final private String value;
		private StorageFormKey(String value) { // 必须是private的，否则编译错误
			this.value = value;
		}
		public String value() {
			return this.value;
		}
	}
	//
	static enum StorageObjects {
		/** 默认的头像对象 */
		avatarUserZero("avatarUserZero");
		final private String value;
		private final Map<String,Object> avatarUserZeroMap = Maps.newHashMap();
		/** data:image/png;base64, */
		final private String defaultAvatarBase64Png = "/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwKDAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgAUABQAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A+t+KKPxo/GgA70Yo/Gj8aADFH4VesdC1HUl3WtjcXCf344yV/PGKW+0HUtNXddWNzbp/fkjIX88YoAofhR+FH40fjQAfhR+FH40fjQAUUUUAFepeAPh5D9li1LVYhK8g3Q27j5VXszDuT6f5HA+FtOXVvEWn2rjMcko3j1UckfkDX0MBgYHAoARVCKFUBVHAA6ClZQwKkZBGCDS0UAec+Pvh3BJay6lpUQimjBeW3QYVx3Kjsfbv/PyqvpuvnvxfpqaT4l1C1QbY0lJUDsrfMB+RoAyKKKKACiiigDa8GXq6f4p02eQgIJQpJ7Bvlz+tfQP4V8yDg17P4A8cw65ZxWV5IE1KMbfmP+uA7j39R+NAHaUfhSUUAL+FeA+OL1NQ8WalNGQU83YCO+0Bf6V6b498cQ6BZyWlrIJNSkXaApz5QP8AEff0FeKk5OTyTQAUUUUAH40fjRU1naTX93DbQIXmlYIijuTQBc0Dw/eeI74W1mm49XkbhUHqTXsHhz4eaXoCpI8YvbscmaYZAP8Asr0H8/etHwv4cg8M6XHaxANIfmllxy7dz9PStigA/Gk/GlooA5bxJ8PdL19XkWMWd43PnwjGT/tL0P8AP3rx/X/D954cvjbXibT1SReVceoNfRFZHijw5B4m0uS1lAWQfNFLjlG7H6etAHz5+NH41NeWk1hdzW06FJonKMp7EGoaACvQfhBowudTudRkXK2y7I8j+Nup/Afzrz6vafhRaCDwmkgHM8zufwO3/wBloA7Kiij8KACkpaSgBaSj8KKAPJvi/owttTttRjXC3K7JMf3l6H8R/KvPq9p+K1qJ/CbyEcwTI4P1O3/2avFqAP/Z";
		private StorageObjects(String value) { // 必须是private的，否则编译错误
			this.value = value;
		}
//		public String value() {
//			return this.value;
//		}
		public Map<String,Object> mapValue() {
			if(avatarUserZero.value == this.value) {
				//String defaultAvatarPath = SpringUtils.getBean(IEntityEnvContext.class).envTempRootPath("defaultAvatar.png");
				String defaultAvatarPath = "";
				// 判断是否存在默认图片
				if(!IOUtil.contentIsAvailable(defaultAvatarPath)) {
					IOUtil.copyByPath(Base64.decodeBase64(defaultAvatarBase64Png), defaultAvatarPath);
				}
				
				if(null == avatarUserZeroMap || avatarUserZeroMap.size() == 0 ) {
					avatarUserZeroMap.put(ITEM_OBJECT_PATH_KEY, StoragePathKey.avatar.value());
					avatarUserZeroMap.put(ITEM_BIZ_FORM_ID, "0");
					avatarUserZeroMap.put(ITEM_OBJECT_NAME, "avatar");
					avatarUserZeroMap.put(ITEM_OBJECT_EXT, ".jpeg");
					avatarUserZeroMap.put(ITEM_OBJECT_DIGEST, "");
					// avatarUserZeroMap.put(ITEM_OBJECT_REAL_PATH, String.format(WEB_URL_CN_GRAVATAR_IMG_2_ARGS, "0", "200"));
					avatarUserZeroMap.put(ITEM_OBJECT_REAL_PATH, defaultAvatarPath);
				}
				return this.avatarUserZeroMap;
			}
			return null;
		}
	}
	/////////////////////////////////////////////////////////
	// 字段类型
	//////////////////////////////////////////////////////////

	public static final String TYPE_ID_OBJECT = "object";
	/** ITEM_KEY */
	public static final String ITEM_KEY = "KEY";
	/** 文件块名   全虚拟路径组成部分1 */
	public static final String ITEM_OBJECT_BLOCK_NAME = "OBJECT_BLOCK_NAME";
	/** 文件路径   全虚拟路径组成部分2 */
	public static final String ITEM_OBJECT_PATH_KEY = "OBJECT_PATH_KEY";
	/** 文件名     全虚拟路径组成部分3 */
	public static final String ITEM_OBJECT_NAME = "OBJECT_NAME";
	/** 文件后缀   全虚拟路径组成部分4 */
	public static final String ITEM_OBJECT_EXT = "OBJECT_EXT";
	/** 文件ID */
	public static final String ITEM_OBJECT_ID = "OBJECT_ID";
	/** 文件摘要 */
	public static final String ITEM_OBJECT_DIGEST = "OBJECT_DIGEST";
	/** 文件长度 */
	public static final String ITEM_OBJECT_SIZE = "OBJECT_SIZE";
	/** 文件打印长度  B KB MB GB  */
	public static final String ITEM_OBJECT_PRINT_SIZE = "OBJECT_PRINT_SIZE";
	/** 创建时间 */
	public static final String ITEM_CREATE_TIME = "CREATE_TIME";
	/** 排序序号 */
	public static final String ITEM_SORT_NUMBER = "SORT_NUMBER";
	/** 业务表单KEY */
	public static final String ITEM_BIZ_FORM_KEY = "BIZ_FORM_KEY";
	/** 业务表单ID */
	public static final String ITEM_BIZ_FORM_ID = "BIZ_FORM_ID";
	/** 文件路径类型  摘要路径  INT_0  全虚拟路径  INT_1 */
	public static final String ITEM_OBJECT_PATH_TYPE = "OBJECT_PATH_TYPE";
	/** 文件真实路径  */
	public static final String ITEM_OBJECT_REAL_PATH = "OBJECT_REAL_PATH";
	/** 文件真实路径 摘要路径  INT_0 */
	public static final String ITEM_OBJECT_REAL_PATH0 = "OBJECT_REAL_PATH0";
	/** 文件真实路径 全虚拟路径  INT_1 */
	public static final String ITEM_OBJECT_REAL_PATH1 = "OBJECT_REAL_PATH1";
	/** 文件下载地址 OBJECT_URL */
	public static final String ITEM_OBJECT_URL = "OBJECT_URL";
	/** 文件查看地址 OBJECT_VIEW_URL */
	public static final String ITEM_OBJECT_VIEW_URL = "OBJECT_VIEW_URL";
	/** 文件删除地址 OBJECT_DEL_URL */
	public static final String ITEM_OBJECT_DEL_URL = "OBJECT_DEL_URL";
	/** 文件REST_PATH 地址 OBJECT_REST_P_URL */
	public static final String ITEM_OBJECT_REST_P_URL = "OBJECT_REST_P_URL";
	/** 文件REST_OBJECT 地址 OBJECT_REST_O_URL */
	public static final String ITEM_OBJECT_REST_O_URL = "OBJECT_REST_O_URL";
	/////////////////////////////////////////////////////////
	// 遗留代码
	/////////////////////////////////////////////////////////
	/** 文件真实路径  全虚拟路径  INT_1 */
	public static final String ITEM_OBJECT_REAL_PATH2 = "OBJECT_REAL_PATH2";
	/////////////////////////////////////////////////////////
	public static final String WEB_URL_GET_OBJECT_FORMAT_4_ARGS = "%s/storage/getObject%s?objectId=%s&view=%s";
	public static final String WEB_URL_DEL_OBJECT_FORMAT_2_ARGS = "%s/storage/deleteObject?objectId=%s";
	/////////////////////////////////////////////////////////
	/** /storage/rest/p/{pathKey}/{bizFormKey}/{bizFormId} */
	public static final String OBJECT_REST_P_URL_FORMAT_5_ARGS = "%s/storage/rest/p/%s/%s/%s%s";
	/** /storage/rest/o/{objectIds} */
	public static final String OBJECT_REST_O_URL_FORMAT_3_ARGS = "%s/storage/rest/o/%s%s";
	/////////////////////////////////////////////////////////
	public static final String WEB_URL_CN_GRAVATAR_IMG_2_ARGS = "https://cn.gravatar.com/avatar/%s?s=%s&d=mm";
}