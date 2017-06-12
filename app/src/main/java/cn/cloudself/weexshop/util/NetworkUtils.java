package cn.cloudself.weexshop.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;

/**
 * 网络相关工具类
 *
 * @author unascribed
 * @author sichard
 * @author HerbLuo
 * @version 1.0.1.d
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkUtils {

    /**
     * 检测网络是否连接
     * <p>
     * this code snippets is copied from internet
     *
     * @return .
     */
    public static boolean isNetworkAvailable(Context context) {
        // 得到网络连接信息
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isAvailable();
    }

    /**
     * 判断是否由网络连接
     * 通过是否能ping通百度来判断
     * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     *
     * @return .
     * @author sichard
     */
    @SuppressWarnings("JavaDoc")
    public static boolean isNetworkAvailable() {
        return isNetworkAvailable("www.baidu.com");
    }

    /**
     * 判断是否由网络连接
     * 通过是否能ping通百度来判断
     * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     *
     * @return .
     * @author sichard
     */
    @SuppressWarnings("JavaDoc")
    public static boolean isNetworkAvailable(String ipOrHost) {

        String result = null;
        try {
            // ping 的地址，可以换成任何一种可靠的外网
            // ping网址3次
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ipOrHost);
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("util", "result = " + result);
        }
        return false;
    }

}
