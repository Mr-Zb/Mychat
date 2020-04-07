package com.fun.framework.utils;

import java.util.Map;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {
    /**
     * @param map
     * @return
     * @Title: isEmpty
     * @Description: 判断集合类型为空
     */
    public static Boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * @param map
     * @return
     * @Title: isNotEmpty
     * @Description: 判断集合类型不为空
     */
    public static Boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
