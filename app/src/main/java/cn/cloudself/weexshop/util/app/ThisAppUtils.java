package cn.cloudself.weexshop.util.app;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.cloudself.weexshop.util.FileUtils;
import cn.cloudself.weexshop.util.UrlUtils;

public class ThisAppUtils {

    private static final String TAG = "ThisAppUtils";

    public static String readWeexJsFile(Context context, String version) {

        try {
            return FileUtils.readWeexJsFile(context, Config.WEEX_JS_FILE_PATH(version));

        } catch (FileNotFoundException e) {
            Log.e(TAG, "js文件未找到", e);
            return null;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("不支持的编码协议");

        } catch (IOException e) {
            Log.e(TAG, "文件读取失败", e);
            return null;
        }
    }

    /**
     * 将服务端的weex file保存到本地
     *
     * @return file 的内容
     */
    public static boolean saveWeexJsFile(Context context, String version) {
        try {
            UrlUtils.saveUrlData2File(
                    context,
                    Config.WEEX_SHOP_INDEX_FILE_URL(version),
                    Config.WEEX_JS_FILE_PATH(version)
            );
            return true;
        } catch (IOException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }



}
