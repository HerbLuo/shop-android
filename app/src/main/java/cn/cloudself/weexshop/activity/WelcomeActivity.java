package cn.cloudself.weexshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.location.demo.CheckPermissionsActivity;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;


import cn.cloudself.weexshop.R;
import cn.cloudself.weexshop.thread.SetTimeout;
import cn.cloudself.weexshop.thread.UpdateRunnable;
import cn.cloudself.weexshop.util.Watcher;
import cn.cloudself.weexshop.util.app.Bus;
import cn.cloudself.weexshop.util.CommonUtils;
import cn.cloudself.weexshop.util.app.Config;
import cn.cloudself.weexshop.util.app.MagicString;
import cn.cloudself.weexshop.util.app.ThisAppUtils;
import cn.cloudself.weexshop.weex.module.WXShpLifecycle;

/**
 * App欢迎页
 * 该欢迎页是Java native界面
 * 创建的同时启动两个线程: 更新线程和预加载线程
 * <p>
 * 网络良好的情况下，
 * 若无更新，更新线程在50ms左右结束，
 * <p>
 * 预加载线程每隔100ms检测一次更新线程是否执行完毕，
 * 最多检测9次，
 * 更新完毕后调用该类下的预加载方法，该线程结束
 * <p>
 * 预加载方法用于启动weex实例
 * weex实例在加载完毕后会调用 weex.module.loadOk
 * <p>
 *
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/4/13 HerbLuo 首次创建
 */

public class WelcomeActivity extends CheckPermissionsActivity implements IWXRenderListener,
        Watcher.OnDataChanged<String>, SetTimeout.OnTimeout {

    private static final String TAG = "WelcomeActivity";

    /**
     * js 渲染和java 渲染是异步的
     * 必须两个全部完成后，才能启动下一个activity
     */
    private static boolean viewCreated = false;
    private static boolean loadSuccess = false;

    /**
     * 保存当前activity的实例
     * 用于{@link #loadSuccess()}
     */
    private static Activity[] activities = new Activity[1];

    /**
     * 跳转至ShopActivity
     * 该方法由js调用
     *
     * @return true if 进入成功
     * @see WXShpLifecycle#loadSuccess()
     */
    public static boolean loadSuccess() {
        Log.d(TAG, "loadSuccess，表明js已初始化完毕");

        if (!viewCreated) {
            loadSuccess = true;
            return true;
        }

        Activity activity = activities[0];
        if (activity == null) {
            return false;
        }

        ((WelcomeActivity) activity).nextActivity();
        return true;
    }


    /**
     * 跳转至 ShopActivity
     */
    private void nextActivity() {
        Intent intent = new Intent();
        intent.setClass(this, ShopActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activities[0] = this;

        super.onCreate(savedInstanceState);
    }


    /**
     * Welcome创建完毕，
     * <p>
     * 开启两个线程：更新线程 和 防超时线程（3000毫秒）
     * <p>
     * 两个线程race调用preLoadWeex方法
     */
    @Override
    protected void onStart() {
        super.onStart();

        // 3000 毫秒后调用 onTimeout
        new Thread(new SetTimeout(this, 3000)).start();

        // JS更新完毕后调用该方法
        Bus.jsVersionWatcher.onDataChanged(this);
        new Thread(new UpdateRunnable(this)).start();
    }

    /**
     * 更新线程和3000毫秒后回调线程
     * 是互斥的 只允许其中一个加载weex实例
     */
    private boolean loaded = false;
    private String tag;

    private synchronized boolean isLoaded_IfNotLoadedSetLoaded(String tag) {
        this.tag = tag;
        if (!loaded) {
            loaded = true;
            return false;
        }
        return true;
    }


    /**
     * JS文件 更新完毕后调用该方法
     * （jsVersion改变了的回调）
     * <p>
     * 与onTimeout方法互斥
     * <p>
     * 非UI线程
     *
     * @see #onTimeout()
     */
    @Override
    public void onDataChanged(String jsVersion) {
        String tag = "onDataChanged";
        if (isLoaded_IfNotLoadedSetLoaded(tag)) {
            if (Config.DEBUG && jsVersion != null && !tag.equals(this.tag)) {
                CommonUtils.toastOnNonUiThread(this, "JS更新完毕，将在下次开启App时使用");
            }
            if (jsVersion != null) {
                Log.i(TAG, "onDataChanged: 网络质量不佳，更新线程超时完成了任务");
            }
            return;
        }

        boolean haveUpdate = true;

        if (jsVersion == null) {
            haveUpdate = false;
            jsVersion = CommonUtils.getPreference(this,
                    MagicString.JS_VERSION_KEY,
                    MagicString.JS_VERSION_DEFAULT_VALUE_IN_PREFERENCE);
        }
        System.out.println(0);

        if (Config.DEBUG) {
            if (haveUpdate) {
                CommonUtils.toastOnNonUiThread(this, "JS更新完毕，当前使用的JS版本为：" + jsVersion);
            } else {
                CommonUtils.toastOnNonUiThread(this, "JS已是最新，当前使用的JS版本为：" + jsVersion);
            }
        }

        System.out.println(1);
        Log.i(TAG, "onDataChanged: JS"
                + (haveUpdate ? "更新完毕" : "无更新")
                + "，当前使用的JS版本为：" + jsVersion);

        String template = ThisAppUtils.readWeexJsFile(this, jsVersion);
        if (template == null) {
            Log.w(TAG, "onDataChanged: JS缓存读取失败，使用App中绑定的js");
            template = WXFileUtils.loadAsset(Config.WEEX_FILE_ASSERTS_PATH, this);
        }

        preLoadWeex(template);
    }

    /**
     * 3秒后调用该方法
     * <p>
     * 与onDataChanged方法互斥
     * <p>
     * 非UI线程
     *
     * @see #onDataChanged(String)
     */
    @Override
    public void onTimeout() {
        if (isLoaded_IfNotLoadedSetLoaded("onTimeout")) {
            Log.i(TAG, "onDataChanged: 更新线程已完成了JS文件的更新");
            return;
        }

        String jsVersion =
                CommonUtils.getPreference(this,
                        MagicString.JS_VERSION_KEY,
                        MagicString.JS_VERSION_DEFAULT_VALUE_IN_PREFERENCE);

        if (Config.DEBUG) {
            CommonUtils.toastOnNonUiThread(this, "当前网络不佳，采用的旧版JS版本为：" + jsVersion);
        }
        Log.i(TAG, "onDataChanged: 更新线程超时，故采用了旧版JS：" + jsVersion);

        String template = ThisAppUtils.readWeexJsFile(this, jsVersion);
        if (template == null) {
            Log.w(TAG, "onDataChanged: JS缓存读取失败，使用App中绑定的js");
            template = WXFileUtils.loadAsset(Config.WEEX_FILE_ASSERTS_PATH, this);
        }

        preLoadWeex(template);
    }

    /**
     * 预加载 weex in shopActivity
     * <p>
     * next OnViewCreated
     *
     * @see #onViewCreated(WXSDKInstance, View)
     */
    public void preLoadWeex(String template) {
        WXSDKInstance mInstance = new WXSDKInstance(this);
        Bus.putWxsdkInstance(MagicString.MAIN_WEEX_SDK_INSTANCE, mInstance);
        mInstance.registerRenderListener(this);
        mInstance.render(getPackageName(), template, null, null, WXRenderStrategy.APPEND_ASYNC);
    }


    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        Log.d(TAG, "view 创建了");
        Bus.putView(MagicString.JS_VIEW_KEY, view);

        if (!loadSuccess) {
            viewCreated = true;
            return;
        }

        nextActivity();
    }

    /*
     * 以下非关键代码
     */
    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
    }

    @Override
    protected void onDestroy() {
        activities[0] = null;
        super.onDestroy();
    }

    /**
     * 获得了所有权限后
     * 最主要是定位权限
     */
    @Override
    protected void onAllPermissionsGetted() {
    }

}
