package cn.cloudself.weexshop.util;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Ghosted on 2017/4/11
 */

@SuppressWarnings("WeakerAccess")
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String readWeexJsFile(Context context, String filename) throws IOException {

        FileInputStream inStream = null;
        try {

            inStream = context.openFileInput(filename);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);  ///获取buffer数组中从0-len范围的数据，将数据读入内存
            }
            return new String(outStream.toByteArray(), "UTF-8");

        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "文件流无法关闭", e);
            }
        }
    }

}
