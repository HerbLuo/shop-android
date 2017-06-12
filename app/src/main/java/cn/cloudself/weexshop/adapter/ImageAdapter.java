package cn.cloudself.weexshop.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

import java.io.IOException;
import java.io.InputStream;

/**
 * url-image 适配器
 * <p>
 * 分别在assets web下查看是否存在该图片
 */
public class ImageAdapter implements IWXImgLoaderAdapter {

    public ImageAdapter() {
    }

    @Override
    public void setImage(final String url, final ImageView view,
                         WXImageQuality quality, WXImageStrategy strategy) {

        WXSDKManager.getInstance().postOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (view == null || view.getLayoutParams() == null) {
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    view.setImageBitmap(null);
                    return;
                }

                // 尝试从assets下寻找资源
                if (url.contains("//closx-shop.oss-cn-qingdao.aliyuncs.com/app/v1/")
                        && renderImgByAssetsImg(url, view)) {

                    return;
                }

                // 使用http资源
                renderImgByHttpResource(url, view);
            }
        }, 0);

    }

    /**
     * 从assets下寻找图片，
     * 如找到，放入view下，返回true
     * 否则，返回false
     */
    private boolean renderImgByAssetsImg(final String url, final ImageView view) {

        int indexStart = url.lastIndexOf('/') + 1;
        int indexEnd = url.indexOf('?');

        String imgName = indexEnd < 0
                ? url.substring(indexStart)
                : url.substring(indexStart, indexEnd);

        InputStream inputStream;
        try {
            inputStream = WXEnvironment.getApplication().getAssets().open("imgs/" + imgName);
        } catch (IOException e) {
            // 这个异常是很常见的，代表assert下不存在某个文件，不必做任何事
            return false;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        view.setImageBitmap(bitmap);
        return true;
    }

    /**
     * 从服务端下载图片
     */
    private boolean renderImgByHttpResource(final String url, final ImageView view) {
        String temp = url;
        if (url.startsWith("//")) {
            temp = "http:" + url;
        }
        if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
            return false;
        }
        Picasso.with(WXEnvironment.getApplication())
                .load(temp)
                .into(view);
        return true;
    }

}