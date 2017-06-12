package cn.cloudself.weexshop.util.app;

import android.view.View;

import com.taobao.weex.WXSDKInstance;

import java.util.HashMap;
import java.util.Map;

import cn.cloudself.weexshop.util.Watcher;

/**
 * runtime variable
 */
public class Bus {

    /**
     * App主界面 初始化时，
     * 1. 判断该变量是否为空，该变量代表了js的版本
     * 2. 如果不为空，进入5
     * 3. 如果为空，表明线程2请求服务端js版本出了问题，尝试获取配置文件中的js版本，进入4
     * 4. 获取配置文件中的js版本，如果成功，进入5；如果失败，进入6
     * 5. 根据获取到的js版本，读取app data内的js版本并转换为String，成功 => ok；失败，进入6
     * 6. 使用App内置的js文件，其中版本为Global.JS_VERSION_CURRENT，文件在assets下
     */
//    public static String jsVersionWatcher = null;
    public static Watcher<String> jsVersionWatcher = new Watcher<>();

    /**
     * 缓存weex view
     */
    private static Map<String, View> views = new HashMap<>();

    public static void putView(String name, View view) {
        views.put(name, view);
    }

    public static View getView(String name) {
        return views.get(name);
    }

    /**
     * 缓存weex sdk instance
     */
    private static Map<String, WXSDKInstance> wxsdkInstanceMap = new HashMap<>();

    public static void putWxsdkInstance(String name, WXSDKInstance wxsdkInstance) {
        wxsdkInstanceMap.put(name, wxsdkInstance);
    }

    public static WXSDKInstance getWxsdkInstance(String name) {
        return wxsdkInstanceMap.get(name);
    }


}
