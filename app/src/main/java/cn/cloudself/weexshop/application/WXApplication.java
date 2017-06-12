package cn.cloudself.weexshop.application;

import android.app.Application;
import android.util.Log;


import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;

import cn.cloudself.weexshop.adapter.ImageAdapter;
import cn.cloudself.weexshop.weex.module.WXAMap;
import cn.cloudself.weexshop.weex.module.WXBroadcastChannel;
import cn.cloudself.weexshop.weex.module.WXQrCode;
import cn.cloudself.weexshop.weex.module.WXShpLifecycle;


public class WXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WXEnvironment.addCustomOptions("appName","WXShop");
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new ImageAdapter())
                .build();

        WXSDKEngine.initialize(this, config);

        /*
         * 注册module
         */
        try {
            WXSDKEngine.registerModule("WXShpLifecycle", WXShpLifecycle.class);
            WXSDKEngine.registerModule("WXBroadcastChannel", WXBroadcastChannel.class);
            WXSDKEngine.registerModule("WXQrCode", WXQrCode.class);
            WXSDKEngine.registerModule("WXAMap", WXAMap.class);
        } catch (WXException e) {
            Log.e("java native", "module register bad", e);
        }

    }

}
