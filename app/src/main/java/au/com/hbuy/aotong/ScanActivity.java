package au.com.hbuy.aotong;

import android.*;
import android.content.Intent;
import android.os.Bundle;

import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class ScanActivity extends BaseFragmentActivity implements QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {
    @Bind(R.id.zbarview)
    QRCodeView mZbarview;
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        mZbarview.setDelegate(this);
        mZbarview.changeToScanBarcodeStyle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();

        mZbarview.startCamera();
        mZbarview.showScanRect();
        mZbarview.startSpot();
    }

    @Override
    protected void onDestroy() {
        mZbarview.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mZbarview.stopCamera();
        super.onStop();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        KLog.d(result);
        Intent intent = new Intent(this, AddPkgActivity.class);
        intent.putExtra(AddPkgActivity.MSCANRESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ShowToastUtils.toast(this, "打开相机出错", 3);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        KLog.d(requestCode);

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        KLog.d(requestCode);
    }
}
