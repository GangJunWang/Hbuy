package au.com.hbuy.aotong;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.socks.library.KLog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.List;

import au.com.hbuy.aotong.domain.Android;
import au.com.hbuy.aotong.domain.Images;
import au.com.hbuy.aotong.domain.Version;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByUpdateCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutHbuyActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_us_share)
    TextView tvUsShare;
    @Bind(R.id.tv_us_check)
    TextView tvUsCheck;
    @Bind(R.id.tv_us_new_version)
    TextView tvUsNewVersion;
    @Bind(R.id.tv_us_hbuy)
    TextView tvUsHbuy;

    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    @Bind(R.id.tv_net)
    TextView tvNet;
    private CustomProgressDialog progressDialog;
    private AboutHbuyActivity mActivity = AboutHbuyActivity.this;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_hbuy_activity);
        ButterKnife.bind(this);
        tvUsNewVersion.setText("V" + AppUtils.getVersionName(mActivity));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.iv_back, R.id.tv_us_share, R.id.tv_us_check, R.id.tv_us_hbuy, R.id.tv_feedback, R.id.tv_net})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_net:
                AppUtils.showActivity(this, AboutNetTestActivity.class);
                break;
            case R.id.tv_us_share:
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
                    ActivityCompat.requestPermissions(mActivity, mPermissionList, 123);
                }
                UMImage image = new UMImage(mActivity, ConfigConstants.SHARE_IMG);
                new ShareAction(mActivity)
                        .withTargetUrl(ConfigConstants.SHARE_URL)
                        .withTitle(ConfigConstants.SHARE_TITLE)
                        .withText(ConfigConstants.SHARE_DESC)
                        .withMedia(image)
                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.tv_us_check:
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.startDetails, new RespByUpdateCallback<List<Images>>(this) {
                    @Override
                    public void onSuccess(List<Images> t, final Version v) {
                        KLog.d(v.getAndroid().getV() + "-" + AppUtils.getVersionCode(mActivity) + "|");
                        KLog.d(AppUtils.getVersionCode(mActivity) == StringUtils.toInt(v.getAndroid().getV()));
                        if (null != v) {
                            final Android a = v.getAndroid();
                            if (AppUtils.getVersionCode(mActivity) == StringUtils.toInt(a.getV())) {
                                ShowToastUtils.toast(mActivity, "当前已经是最新版本");
                                return;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                builder.setTitle(R.string.android_auto_update_dialog_title);
                                builder.setMessage(Html.fromHtml(a.getLog()))
                                        .setPositiveButton(R.string.android_auto_update_dialog_btn_download, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (!NetUtils.isWIFI(mActivity)) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                                    builder.setTitle(R.string.warm_hint);
                                                    builder.setMessage(R.string.warm_hint_nowifi);
                                                    builder.setPositiveButton(R.string.android_auto_update_dialog_btn_download, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            AppUtils.goToDownload(mActivity, a.getDownload());
                                                        }
                                                    }).setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            return;
                                                        }
                                                    });
                                                    AlertDialog d = builder.create();
                                                    //点击对话框外面,对话框不消失
                                                    d.setCanceledOnTouchOutside(false);
                                                    d.show();
                                                } else {
                                                    AppUtils.goToDownload(mActivity, a.getDownload());
                                                }
                                            }
                                        });
                                if ("1".equals(v.getAndroid().getIsforce())) {

                                } else {
                                    builder.setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            return;
                                        }
                                    });
                                }
                                AlertDialog dialog = builder.create();
                                //点击对话框外面,对话框不消失
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                            }
                        }
                    }

                    @Override
                    public void onFail() {
                        ShowToastUtils.toast(mActivity, "获取版本信息失败", 3);
                    }
                });
                break;
            case R.id.tv_us_hbuy:
                AppUtils.showActivity(this, AboutUsActivity.class);
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            KLog.d();
            ShowToastUtils.toast(mActivity, "分享成功", 1);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            KLog.d();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            KLog.d();
        }
    };
}
