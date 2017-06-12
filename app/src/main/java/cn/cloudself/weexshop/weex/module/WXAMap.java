package cn.cloudself.weexshop.weex.module;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.PoiItem;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import cn.cloudself.weexshop.activity.ShopActivity;

@SuppressWarnings("unused")
public class WXAMap extends WXModule {

    private static JSCallback callback;
    private static final String TAG = "WXAMap";

    @JSMethod
    public void chooseLocation(JSCallback callback) {
        WXAMap.callback = callback;
        ShopActivity.showLocationSelector();
    }

    public static void setLocation(PoiItem item){
        if (callback == null) {
            Log.w(TAG, "setLocation: callback未定义", null);
        }
        callback.invoke(JSON.toJSONString(item));
    }

}
