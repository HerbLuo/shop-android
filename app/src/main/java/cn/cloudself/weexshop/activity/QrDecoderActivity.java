package cn.cloudself.weexshop.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import cn.cloudself.weexshop.R;
import cn.cloudself.weexshop.weex.module.WXQrCode;


public class QrDecoderActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    /*
     * 传来传去的权限标志
     */
    public static final int PERMISSION_CODE_OF_CAMERA = 0;

    private ViewGroup mainLayout;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_decoder);
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }

    }

    /**
     * 当获得了某个权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {

        if (requestCode != PERMISSION_CODE_OF_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            Toast.makeText(this, "您拒绝了摄像头权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求摄像头权限（安卓6.0以上好像就要这么干）
     */
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // 用户已经拒绝过一次权限了，威胁，恐吓，花言巧语什么的随便来
            Toast.makeText(this,
                    "为了扫描二维码，您需要允许我们使用摄像头权限",
                    Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(QrDecoderActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, PERMISSION_CODE_OF_CAMERA);
        } else {
            // 第一次请求摄像头权限，先试试用户给不给
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, PERMISSION_CODE_OF_CAMERA);
        }
    }

    /**
     * 初始化 那个什么什么view
     * 调用前，必须确保获得了摄像头权限
     */
    private void initQRCodeReaderView() {

        // 直观上的理解就是加载 content_qr_decoder
        getLayoutInflater().inflate(R.layout.content_qr_decoder, mainLayout, true);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);

        // 传入实现了OnQRCodeReadListener接口的对象，便于回调
        qrCodeReaderView.setOnQRCodeReadListener(this);
        // 自动聚焦时间
        qrCodeReaderView.setAutofocusInterval(500L);
        // 后置摄像头
        qrCodeReaderView.setBackCamera();
        // 开启摄像头
        qrCodeReaderView.startCamera();
    }

    /**
     * 识别成功的回调
     */
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        WXQrCode.onFinished(text);
        backToShopActivity();
    }

    /**
     * 返回主界面
     */
    private void backToShopActivity() {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

}
