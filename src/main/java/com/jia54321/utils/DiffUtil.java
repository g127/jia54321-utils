package com.jia54321.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * java diff算法
 */
public class DiffUtil {

    public static class DiffResult<T> {
        /**
         * 新增对象列表
         */
        private List<T> addedList;
        /**
         * 修改后的对象列表
         */
        private List<T> changedList;
        /**
         * 已删除对象列表
         */
        private List<T> deletedList;

        public List<T> getAddedList() {
            return addedList;
        }

        public DiffResult<T> setAddedList(List<T> addedList) {
            this.addedList = addedList;
            return this;
        }

        public List<T> getChangedList() {
            return changedList;
        }

        public DiffResult<T> setChangedList(List<T> changedList) {
            this.changedList = changedList;
            return this;
        }

        public List<T> getDeletedList() {
            return deletedList;
        }

        public DiffResult<T> setDeletedList(List<T> deletedList) {
            this.deletedList = deletedList;
            return this;
        }
    }

    /**
     * 对比两个List的元素
     * <p>
     * 如果 baseList 的元素在 targetList 中存在 PrimaryKey 相等的元素并且 elementComparator 比较结果不相等，则将修改后的值添加到changedList列表中；
     * 如果 baseList 的元素在 targetList 中不存在，将baseList中的元素添加到deletedList中；
     * 如果 targetList 的元素在 baseList 中不存在，将targetList中的元素添加到addedList中；
     * <p>
     * complexity: O(n)
     *
     * @param baseList            基础List(原来的List)
     * @param targetList          目标List(最新的List)
     * @param elementComparator   元素比较器
     * @param primaryKeyExtractor 主键选择器
     * @param <T>
     * @return 对比结果
     */
    public static <T> DiffResult<T> diffList(List<T> baseList,
                                             List<T> targetList,
                                             Function<T, Object> primaryKeyExtractor,
                                             Comparator<T> elementComparator) {

        DiffResult<T> checkResult = checkEmptyAndReturn(baseList, targetList);
        if (checkResult != null) {
            return checkResult;
        }

        Map<Object,T> baseMap = new HashMap<>(4096);
        for(T base : baseList){
            Object key = primaryKeyExtractor.apply(base);
            baseMap.put(key,base);
        }

        List<T> addedList = new ArrayList<>();
        List<T> changedList = new ArrayList<>();
        List<T> deletedList = new ArrayList<>();

        //找出新增的 和需要更新的
        for (T target : targetList) {
            Object key = primaryKeyExtractor.apply(target);
            T base = baseMap.get(key);
            if(base == null){
                addedList.add(target);
            }else{
                baseMap.remove(key);
                if (elementComparator.compare(base, target) != 0) {
                    changedList.add(target);
                }
            }
        }

        //剩余的就是需要删除的
        Set<Map.Entry<Object, T>> entrySet = baseMap.entrySet();
        if(JsonHelper.isNotEmpty(entrySet)){
            for(Map.Entry<Object, T> entry:entrySet){
                deletedList.add(entry.getValue());
            }
        }

        return new DiffResult<T>()
                .setAddedList(addedList)
                .setChangedList(changedList)
                .setDeletedList(deletedList);
    }
    /**
     * 检查baseList 和 targetList 为empty(null||size==0)的情况
     *
     * @param baseList
     * @param targetList
     * @param <T>
     * @return
     */
    private static <T> DiffResult<T> checkEmptyAndReturn(List<T> baseList, List<T> targetList) {

        if (JsonHelper.isEmpty(baseList) && JsonHelper.isEmpty(targetList)) {
            return new DiffResult<T>()
                    .setAddedList(null)
                    .setChangedList(null)
                    .setDeletedList(null);
        }

        if (JsonHelper.isEmpty(baseList) && JsonHelper.isNotEmpty(targetList)) {
            return new DiffResult<T>()
                    .setAddedList(targetList)
                    .setChangedList(null)
                    .setDeletedList(null);
        }

        if (JsonHelper.isNotEmpty(baseList) && JsonHelper.isEmpty(targetList)) {
            return new DiffResult<T>()
                    .setAddedList(null)
                    .setChangedList(null)
                    .setDeletedList(baseList);
        }
        return null;
    }
}
