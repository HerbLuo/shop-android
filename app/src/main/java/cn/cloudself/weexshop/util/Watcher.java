package cn.cloudself.weexshop.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/8 HerbLuo 首次创建
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Watcher<T> {

    private T watched;

    private List<OnDataChanged<T>> callbacks = new ArrayList<>();

    public T getObject() {
        return watched;
    }

    public void setObject(T watched) {
        this.watched = watched;
        for (OnDataChanged<T> callback : callbacks) {
            callback.onDataChanged(watched);
        }
    }

    public void onDataChanged(OnDataChanged<T> callback) {
        this.callbacks.add(callback);
    }

    public interface OnDataChanged<T> {
        void onDataChanged(T object);
    }

}
