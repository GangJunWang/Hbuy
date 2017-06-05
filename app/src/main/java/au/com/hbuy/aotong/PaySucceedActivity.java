package au.com.hbuy.aotong;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.ShareBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.view.CustomDialog;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaySucceedActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_hint)
    ImageView ivHint;
    @Bind(R.id.tv_result)
    TextView tvResult;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.bt_01)
    Button bt01;
    @Bind(R.id.bt_02)
    Button bt02;
    @Bind(R.id.layout_bg)
    LinearLayout layoutBg;
    private String mNo;
    private CustomProgressDialog progressDialog;
    private PaySucceedActivity mActivity = PaySucceedActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_succeed);
        ButterKnife.bind(this);

        String type = getIntent().getStringExtra("type");
        mNo = getIntent().getStringExtra("no");
        tvNo.setText("订单号: " + mNo);
        if ("1".equals(type)) {
            //付款成功
            tvTitle.setText("付款成功");
            tvResult.setText("支付成功");
            bt01.setText("查看订单");
            bt02.setText("返回首页");
            bt01.setBackgroundResource(R.drawable.bg_left_and_right_angle_button_pay_secceed);
            bt02.setBackgroundResource(R.drawable.bg_left_and_right_angle_button_pay_secceed);
            ivHint.setImageResource(R.drawable.result_ok);
            layoutBg.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.button_color));
            final Dialog dialog = CustomDialog.create(mActivity, R.layout.pay_succeed_dialog);
            dialog.findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (NetUtils.hasAvailableNet(mActivity)) {
                        if (null == mNo) {
                            ShowToastUtils.toast(mActivity, "分享关闭");
                            return;
                        }
                        progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("no", mNo);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SHARE, params, new RespAddTCallback<ShareBean>(mActivity) {
                            @Override
                            public void onSuccess(ShareBean shareBean) {
                                AppUtils.stopProgressDialog(progressDialog);
                                if (Build.VERSION.SDK_INT >= 23) {
                                    String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW,
                                            Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
                                    ActivityCompat.requestPermissions(mActivity, mPermissionList, 123);
                                }
                                UMImage image = new UMImage(mActivity, shareBean.getImg());
                                new ShareAction(mActivity)
                                        .withTargetUrl(shareBean.getUrl())
                                        .withTitle(shareBean.getTitle())
                                        .withText(shareBean.getDesc())
                                        .withMedia(image)
                                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
                                        .setCallback(umShareListener)
                                        .open();
                            }

                            @Override
                            public void onFail(ShareBean shareBean) {
                                ShowToastUtils.toast(mActivity, "分享关闭");
                                AppUtils.stopProgressDialog(progressDialog);

                            }
                        });
                    } else {
                        ShowToastUtils.toast(mActivity, getString(R.string.net_hint));
                    }
                }
            });
            dialog.show();
        } else {
            //付款失败
            bt01.setBackgroundResource(R.drawable.bg_left_and_right_angle_button_pay_red);
            bt02.setBackgroundResource(R.drawable.bg_left_and_right_angle_button_pay_red);
            tvTitle.setText("付款失败");
            tvResult.setText("支付失败");
            bt01.setText("重新支付");
            bt02.setText("联系客服");
            ivHint.setImageResource(R.drawable.result_error);
            layoutBg.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.pay_failure));
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_01, R.id.bt_02})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_01:
                if ("重新支付".equals(bt01.getText().toString().trim())) {
                    finish();
                } else {
                   finish();
                }
                break;
            case R.id.bt_02:
                if ("返回首页".equals(bt02.getText().toString().trim())) {
                    finish();
                } else {
                    //联系客服
                    AppUtils.goChat(mActivity);
                    finish();
                }
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
