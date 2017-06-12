package cn.cloudself.weexshop.thread;

import android.content.Context;
import android.util.Log;

import cn.cloudself.weexshop.util.NetworkUtils;
import cn.cloudself.weexshop.util.SingleMap;
import cn.cloudself.weexshop.util.app.AppError;
import cn.cloudself.weexshop.util.app.Bus;
import cn.cloudself.weexshop.util.CommonUtils;
import cn.cloudself.weexshop.util.app.Config;
import cn.cloudself.weexshop.util.app.MagicString;
import cn.cloudself.weexshop.util.app.ThisAppUtils;
import cn.cloudself.weexshop.util.app.Version;

/**
 * 更新操作
 * <p>
 * app更新：检查app的版本，提示用户进行更新
 * 热更新：检查weex shop的 js native 的版本，并判断是否更新
 *
 * 该Runnable可能执行相当久的时间
 * 网络良好的情况下200Kb/s，该线程1s内完成
 * 该Runnable执行成功后，Bus.jsVersionWatcher 不为null
 *
 */
public class UpdateRunnable implements Runnable {

    private static final String TAG = "UpdateRunnable";
    private Context context;

    public UpdateRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();

        Version version = new Version(context);

        /*
         * 检查app的版本,
         * 当前版本和服务器最新版本不一致时,
         * 调用needUpdate提示用户更新
         */
        String isAppVersionLatest = version.isAppVersionLatest();

        switch (isAppVersionLatest) {
            case "true": // 最新版本
                Log.i(TAG, "run: App为最新版本");
                break;
            case "false": // 非最新
                Log.i(TAG, "run: App不是最新版本");
                appNeedUpdate();
                break;
            case AppError.NetworkError: // 网络链接错误
                Log.i(TAG, "run: 网络链接失败");
                networkError();
                break;
            default: // 未知异常
                Log.w(TAG, "run: isAppVersionLatest" + isAppVersionLatest);
                AppError.defaultErrorHandler(isAppVersionLatest);
        }


        /*
         * 检查js的版本
         */
        SingleMap<String, String> isJsVersionLatest = version.isJsVersionLatest();

        switch (isJsVersionLatest.getKey()) {
            case "true": // 最新版本
                Log.i(TAG, "run: js为最新版本");
                Bus.jsVersionWatcher.setObject(null);
                break;
            case "false": // 非最新js版本，需要更新
                Log.i(TAG, "run: js非最新版本");
                long duration = System.currentTimeMillis() - time;
                Log.i(TAG, "run: version检测耗时" + duration + "毫秒");
                updateJs(isJsVersionLatest.getValue());
                break;
            case AppError.NetworkError: // 网络链接异常
                Log.i(TAG, "run: 网络链接失败");
                networkError();
                break;
            default: // 未知异常
                Log.w(TAG, "run: isJsVersionLatest" + isJsVersionLatest);
                AppError.defaultErrorHandler(isJsVersionLatest.getKey());
        }

        Log.i(TAG, "run: 更新线程总共消耗了" + (System.currentTimeMillis() - time) + "毫秒");
    }



    /**
     * 更新App
     * TODO 该方法未实现
     */
    private void appNeedUpdate() {
        Log.w(TAG, "App需要更新");
    }

    /**
     * 更新JS
     * 保存JS版本到配置文件
     */
    private void updateJs(String jsVersion) {
        // 保存文件
        boolean isSaved = ThisAppUtils.saveWeexJsFile(context, jsVersion);

        // 保存失败
        if (!isSaved) {
            if (Config.DEBUG) {
                CommonUtils.toastOnNonUiThreadx(context, "调试：js文件保存失败");
            }
            Log.w(TAG, "updateJs: js文件保存失败");
            return;
        }

        // 保存js版本信息
        CommonUtils.savePreference(context, MagicString.JS_VERSION_KEY, jsVersion);
        Log.i(TAG, "updateJs: js文件保存成功, version: " + jsVersion);
        Bus.jsVersionWatcher.setObject(jsVersion);
    }

    /**
     * 网络存在问题
     */
    private void networkError() {
        Log.i(TAG, "networkError: 网络可能存在问题，开始检测");
        if (NetworkUtils.isNetworkAvailable()) {
            Log.i(TAG, "networkError: 外网连接正常。");
        } else {
            Log.i(TAG, "networkError: 无法连通百度或链接不稳定");
            CommonUtils.toastOnNonUiThreadx(context, "目测不能联网");
        }

        if (NetworkUtils.isNetworkAvailable(Config.ip)) {
            Log.i(TAG, "networkError: 网络链接正常，可以ping通" + Config.ip);
        } else {
            Log.i(TAG, "networkError: 外网连接正常但无法连通: " + Config.ip);
            CommonUtils.toastOnNonUiThreadx(context, "外网连接正常但无法连通: " + Config.ip);
        }

    }

}
