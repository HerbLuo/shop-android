package cn.cloudself.weexshop.util;

import java.util.HashMap;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/9 HerbLuo 首次创建
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SingleMap<K, V> {

    private K key;
    private V value;

    public SingleMap() {
    }

    public SingleMap(K key) {
        this.key = key;
    }

    public SingleMap(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingleMap<?, ?> singleMap = (SingleMap<?, ?>) o;

        //noinspection SimplifiableIfStatement
        if (key != null ? !key.equals(singleMap.key) : singleMap.key != null) return false;
        return value != null ? value.equals(singleMap.value) : singleMap.value == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SingleMap{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    public HashMap<K, V> toMap() {
        HashMap<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
