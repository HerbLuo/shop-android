package cn.cloudself.weexshop.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 简单的url解析工具
 *
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/4/8 HerbLuo 首次创建
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UrlUtils {


    public static void saveUrlData2File(Context context, String url, String filename)
            throws IOException {

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            resolveURL(url, fileOutputStream, 1024 * 4);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * 解析url
     *
     * @param url      符合规范的url
     * @param encoding response的编码
     * @return response
     * @throws IOException 网络问题或磁盘已满
     */
    public static String resolveURL(String url, String encoding) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resolveURL(url, outputStream, 1024 * 4);
        /*
         * outputStream转string并 return
         */
        try {
            return new String(outputStream.toByteArray(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("不支持的编码规则");
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 解析url
     *
     * @param url        规范的url
     * @param bufferSize . 单位 B
     * @throws IOException 网络问题或磁盘已满
     */
    public static void resolveURL(String url, OutputStream outputStream,
                                  int bufferSize) throws IOException {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("bufferSize 不能小于1");
        }

        /*
         * 根据url参数创建url对象
         */
        URL urlObj;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url 不符合规范");
        }

        /*
         * 连接 连接成功后取得inputStream对象
         */
        URLConnection connection = urlObj.openConnection();
        connection.setConnectTimeout(3000);

        try (InputStream inputStream = connection.getInputStream()) {
            /*
             * inputStream转outputStream
             */
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }

    }


}
