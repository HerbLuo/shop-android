package cn.cloudself.weexshop.weex.module;

import android.util.Log;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import cn.cloudself.weexshop.activity.WelcomeActivity;

/**
 * Created by Ghosted on 2017/4/8
 */
@SuppressWarnings("unused")
public class WXShpLifecycle extends WXModule {

    @JSMethod
    public void loadSuccess() {
        WelcomeActivity.loadSuccess();
    }

    /**
     * 可进行长时间操作，
     * 但注意，以下状况下不会执行，
     * 1. 系统内存不足
     * 2. App被360等软件强行杀掉
     */
    @JSMethod
    public void onStop(JSCallback callback) {
        WXShpLifecycle.onStopCallback = callback;
    }

    private static JSCallback onStopCallback;
    public static void stop() {
        onStopCallback.invoke("");
    }

    @JSMethod
    public void log(String str) {
        Log.i("JS_NATIVE", str);
    }


}
