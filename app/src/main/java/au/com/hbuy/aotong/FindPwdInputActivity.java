package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 * 找回密码 填写新密码
 */
public class FindPwdInputActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_new_pwd)
    ClearEditText etNewPwd;
    @Bind(R.id.et_tow_pwd)
    ClearEditText etTowPwd;
    @Bind(R.id.bt_save)
    Button btSave;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private CustomProgressDialog progressDialog;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd_input);
        ButterKnife.bind(this);
        mActivity = FindPwdInputActivity.this;
    }

    // 捕获返回键的方法1
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 按下BACK，同时没有重复
            finish();
            AppUtils.showActivity(mActivity, FindPwdActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.iv_back, R.id.bt_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                AppUtils.showActivity(mActivity, FindPwdActivity.class);
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.bt_save:
                String newPwd = AppUtils.getTextStr(etNewPwd);
                String towPwd = AppUtils.getTextStr(etTowPwd);
                if (StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(towPwd)) {
                    ShowToastUtils.toast(this, "密码不能为空");
                    return;
                }

                if (!newPwd.equals(towPwd)) {
                    ShowToastUtils.toast(this, "重置的二次密码不同");
                    return;
                }

                progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                if (NetUtils.hasAvailableNet(this)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("passwd", newPwd);
                    params.put("repeat_passwd", towPwd);

                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.FINDPWD3, params, new RespModifyMsgCallback(this) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "修改密码成功", 1);
                            //    SharedUtils.putBoolean(mActivity, "use_is_login", false);
                            startActivity(new Intent(mActivity, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFail(String status) {
                            if ("-1".equals(status)) {
                                ShowToastUtils.toast(mActivity, "两次密码不一致");
                                return;
                            } else if ("0".equals(status)) {
                                ShowToastUtils.toast(mActivity, "修改失败", 2);
                                return;
                            } else if ("-2".equals(status)) {
                                ShowToastUtils.toast(mActivity, "你近期使用过该密码");
                            }
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
