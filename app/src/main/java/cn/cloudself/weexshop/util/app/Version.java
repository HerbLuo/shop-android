package cn.cloudself.weexshop.util.app;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cloudself.weexshop.util.CommonUtils;
import cn.cloudself.weexshop.util.SingleMap;
import cn.cloudself.weexshop.util.UrlUtils;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/9 HerbLuo 首次创建
 */
public class Version {

    private static final String TAG = "Version";

    private final Context context;

    public Version(Context context) {
        this.context = context;
    }

    /**
     * 检查App是否为最新的版本
     *
     * @return ['true', 'false', AppError.NetworkError, AppError.UnknownError]
     */
    public String isAppVersionLatest() {

        /*
         * 获取最新的App版本
         */
        Integer appVersion;

        try {
            appVersion = getAppVersionFromServer();

        } catch (IOException e) {
            Log.e(TAG, "isLatestVersion: catched error", e);
            return AppError.NetworkError;
        }

        Log.i(TAG, "isLatestVersion: 最新的App版本为：" + appVersion);

        /*
         * 未抛出异常的情况下返回了null version
         */
        if (appVersion == null) {
            Log.e(TAG, "isLatestVersion: 未知错误", new RuntimeException("App版本无法读取，返回了空值"));
            return AppError.UnknownError;
        }

        /*
         * 返回App是否为最新版本
         */
        return Objects.equals(appVersion, getAppVersionFromPreference(context))
                ? "true"
                : "false";

    }

    public SingleMap<String, String> isJsVersionLatest() {

        whenAppRunAtTheFistTimeInitIt();

        /*
         * 检查js的版本
         */
        String jsVersion;

        try {
            jsVersion = getJsVersionFromServer();

        } catch (IOException e) {
            Log.e(TAG, "isLatestVersion: catched error", e);
            return new SingleMap<>(AppError.NetworkError);
        }

        Log.i(TAG, "isLatestVersion: 最新的JS版本为：" + jsVersion);

        /*
         * 未抛出异常的情况下返回了null version
         */
        if (jsVersion == null) {
            Log.e(TAG, "isLatestVersion: 未知错误", new RuntimeException("JS版本无法读取，返回了空值"));
            return new SingleMap<>(AppError.UnknownError);
        }

        return Objects.equals(jsVersion, getJsVersionFromPreference())
                ? new SingleMap<String, String>("true")
                : new SingleMap<>("false", jsVersion);
    }

    /*
     * 私有方法
     */

    /**
     * 获取App最新版本号的 url
     */
    private static final String APP_VERSION_URL = Config.WEEX_SHOP_APP_VERSION_URL;
    /**
     * 获取Js最新版本号的 url
     */
    private static final String JS_VERSION_URL = Config.WEEX_SHOP_JS_VERSION_FILE_URL;

    /**
     * 从服务器获取当前 weex native js 的版本
     *
     * @return .
     */
    private String getJsVersionFromServer() throws IOException {
        return requestTemplate(
                JS_VERSION_URL,
                "\\{\"hash\":\"([\\da-zA-Z]+)\"\\}",
                1
        );
    }

    /**
     * 从服务器获取最新的app版本
     *
     * @return .
     */
    private Integer getAppVersionFromServer() throws IOException {
        String version = requestTemplate(
                APP_VERSION_URL,
                "^\\{[\\s\\S]*\"versionCode\":([\\d]+)[,\\}]",
                1
        );
        try {
            return Integer.parseInt(version);
        } catch (Exception e) {
            return null;
        }
    }


    private String requestTemplate(String url,
                                   String regexString, int regexGroup) throws IOException {

        // 请求服务器并保存返回结果到 responseData
        String responseData = UrlUtils.resolveURL(url, "UTF-8");
        if (responseData == null) {
            throw new RuntimeException("无法获取response, UrlUtils 返回了一个空值");
        }

        // 解析返回结果
        Matcher matcher = Pattern.compile(regexString).matcher(responseData);
        if (matcher.find()) {
            return matcher.group(regexGroup);
        }

        throw new IOException("无法识别的返回结果：" + responseData);
    }

    /**
     * 获取当前App的版本
     */
    private Integer getAppVersionFromPreference(Context context) {
        return CommonUtils.getVersionCode(context);
    }




    /**
     * 获取当前js的版本
     */
    private String getJsVersionFromPreference() {
        return CommonUtils.getPreference(
                context, MagicString.JS_VERSION_KEY, MagicString.JS_VERSION_DEFAULT_VALUE_IN_PREFERENCE);
    }

    /**
     * 保存
     */
    private void whenAppRunAtTheFistTimeInitIt() {
        if (Objects.equals(getJsVersionFromPreference(), MagicString.JS_VERSION_DEFAULT_VALUE_IN_PREFERENCE)) {
            Log.i(TAG, "init: App 第一次运行");
            CommonUtils.savePreference(
                    context, MagicString.JS_VERSION_KEY, Config.JS_VERSION_CURRENT);
        }
    }

}
