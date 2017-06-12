package cn.cloudself.weexshop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ghosted on 2016/4/22.<br/>
 *
 * 语法糖
 * 集合适配器，用于快速创建只有一对值的Map, List, 以及数组K[]等<br/>
 *
 * 例：<br/>
 * return CollectionAdapter.map("success", true); <br/>
 * 等于以下功能 <br/>
 * Map&lt;String, Boolean&gt; map = new HashMap&lt;&gt;();<br/>
 * map.put("success", true);<br/>
 * return map;<br/>
 */
@SuppressWarnings("unused")
public class CollectionAdapter<K, V> {

    /**
     * 返回一个Map值，默认放入了key和value
     *
     * @param key key
     * @param value value
     * @param <K> 任意
     * @param <V> 任意
     * @return Map = new Map.put(key, value)
     */
    /*
     * 泛型方法， 指的是在static 后加上泛型声明
     * 实现方法的静态访问, 且运行时动态确定参数。。我也不大清楚。。。
     */
    public static <K, V> Map<K, V> map(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 返回一个List值，默认放入了element
     *
     * @param element element
     * @param <K> 任意
     * @return List = new ArrayList.add(element)
     */
    public static <K> List<K> list(K element) {
        List<K> list = new ArrayList<>();
        list.add(element);
        return list;
    }

    /**
     * 为list添加一个对象，并返回该list
     *
     * @param list 任意list对象
     * @param element 需放入的元素
     * @param <K> 任意
     * @return 放入了element对象的list
     */
    public static <K> List<K> add(List<K> list, K element) {
        list.add(element);
        return list;
    }

    /**
     * 返回一个单一值的数组
     *
     * @param value 元素
     * @param <K> 任意
     * @return 不要试图为该数组添加元素或修改该数组
     */
    @SuppressWarnings("unchecked")
    public static <K> K[] array(K value) {
        return (K[]) new Object[]{value};
    }


}
