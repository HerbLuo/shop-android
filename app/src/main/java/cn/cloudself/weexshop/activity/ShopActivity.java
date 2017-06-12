package cn.cloudself.weexshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.weex.WXSDKInstance;

import cn.cloudself.weexshop.R;
import cn.cloudself.weexshop.util.app.Bus;
import cn.cloudself.weexshop.util.app.MagicString;
import cn.cloudself.weexshop.weex.module.WXShpLifecycle;

/**
 * App 主页
 */
public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "JAVA NATIVE SHOP_ACTVT";

    /**
     * 保存当前activity的实例
     */
    private static Activity[] activities = new Activity[1];

    /**
     * 跳转至qrDecoder
     */
    public static boolean showQrDecoder() {
        Log.d(TAG, "跳转至QR扫描器");

        Activity activity = activities[0];
        if (activity == null) {
            return false;
        }

        ((ShopActivity) activity).showActivity(QrDecoderActivity.class);
        return true;
    }

    /**
     * 跳转至位置选择界面
     */
    public static boolean showLocationSelector() {
        Log.d(TAG, "跳转至位置选择界面");

        Activity activity = activities[0];
        if (activity == null) {
            return false;
        }

        ((ShopActivity) activity).showActivity(LocationSelectorActivity.class);
        return true;
    }

    private void showActivity(Class<? extends Activity> activity_class) {
        Intent intent = new Intent();
        intent.setClass(this, activity_class);
        startActivity(intent);
    }





    WXSDKInstance mWXSDKInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities[0] = this;

        setContentView(R.layout.activity_shop);

        WXSDKInstance mInstance = Bus.getWxsdkInstance(MagicString.MAIN_WEEX_SDK_INSTANCE);
        if (mInstance == null) {
            Log.e(TAG, "weex sdk instance 未缓存");
            return;
        }
        mInstance.init(this);
        mWXSDKInstance = mInstance;

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.viewGroup);

        View view = Bus.getView(MagicString.JS_VIEW_KEY);
        if (view != null) {
            viewGroup.addView(view);
        } else {
            Log.e(TAG, "view 未缓存");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        WXShpLifecycle.stop();
        super.onStop();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityDestroy();
        }
    }

}