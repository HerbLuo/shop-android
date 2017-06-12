package cn.cloudself.weexshop.weex.module;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import cn.cloudself.weexshop.activity.ShopActivity;
import cn.cloudself.weexshop.util.CollectionAdapter;

@SuppressWarnings("unused")
public class WXQrCode extends WXModule {

    private final static String TAG = "JAVA NATIVE WXQrCode";
    private static JSCallback callback;

    /**
     * 通知js扫描完毕，参数为扫描出的内容
     */
    public static void onFinished(String uri) {
        callback.invoke(CollectionAdapter.map("uri", uri));
    }

    /**
     * 扫描二维码
     * 由js调用
     * @param callback 用于通知js扫描完毕
     */
    @JSMethod
    public void scanner(JSCallback callback) {
        WXQrCode.callback = callback;
        ShopActivity.showQrDecoder();
    }

}
