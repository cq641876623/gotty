package com.roro.gotty.utils;

/**
 * @author chenqi
 * @date 2021-04-13 10:03
 */
public interface Filter<T> {
    /**
     * 筛选是否通过
     * @param t
     * @return true 表示通过
     */
    boolean pass(T t);
}
