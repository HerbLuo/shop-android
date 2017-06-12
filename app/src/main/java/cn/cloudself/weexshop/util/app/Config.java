package cn.cloudself.weexshop.util.app;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/9 HerbLuo 首次创建
 */
@SuppressWarnings("WeakerAccess")
public class Config {

    public static final boolean DEBUG = true;

    /**
     * 服务端地址
     */
    public static final String ip = "192.168.137.1";

    /**
     * 当前App绑定的JS文件的版本
     */
    public final static String JS_VERSION_CURRENT = "002c9ee3";


    /**
     * 获取 js 版本的 url
     */
    public final static String WEEX_SHOP_JS_VERSION_FILE_URL =
            "http://" + ip + ":89/dist/app.version.json";

    /**
     * 获取 app 版本的 url
     */
    final static String WEEX_SHOP_APP_VERSION_URL =
            "http://" + ip + ":8080/app/native/info/";

    /**
     * 获取 js 文件的 url
     */
    public static String WEEX_SHOP_INDEX_FILE_URL(String version) {
        return "http://" + ip + ":89/dist/app.weex." + version + ".js";
    }

    /**
     * 当前App内置的JS文件的位置
     */
    public final static String WEEX_FILE_ASSERTS_PATH = "app.weex." + JS_VERSION_CURRENT + ".js";

    /**
     * 保存JS文件的位置
     */
    public static String WEEX_JS_FILE_PATH(String version) {
        return "weexshop.weex." + version + ".js";
    }

}
