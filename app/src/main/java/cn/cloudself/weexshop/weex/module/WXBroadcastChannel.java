package cn.cloudself.weexshop.weex.module;

import android.util.Log;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cloudself.weexshop.util.CollectionAdapter;

@SuppressWarnings("unused")
public class WXBroadcastChannel extends WXModule{

    private static final String TAG = "WXBroadcastChannel";
    private Map<String, List<JSCallback>> appCallbacks = new HashMap<>();

    @JSMethod
    public void postMessage(String app, String msg) {
        List<JSCallback> callbacks = appCallbacks.get(app);
        if (callbacks == null) {
            Log.w(TAG, app + "未注册 onmessage");
            return;
        }
        for (JSCallback callback : callbacks) {
            callback.invoke(CollectionAdapter.map("data", msg));
        }
    }

    @JSMethod
    public void onmessage(String app, JSCallback callback) {
        List<JSCallback> callbacks = appCallbacks.get(app);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            appCallbacks.put(app, callbacks);
        }
        callbacks.add(callback);
    }

}
