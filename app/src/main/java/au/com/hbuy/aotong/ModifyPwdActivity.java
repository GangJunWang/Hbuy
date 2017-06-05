package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.util.HashMap;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.ITextWatcher;
import au.com.hbuy.aotong.utils.Md5Utils;
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
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.et_old_pwd)
    ClearEditText mOldPwd;
    @Bind(R.id.et_new_pwd)
    ClearEditText mNewPwd;
    @Bind(R.id.et_tow_pwd)
    ClearEditText mTowPwd;
    @Bind(R.id.bt_save)
    Button mSave;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);

        mOldPwd.addTextChangedListener(new ITextWatcher(new View[]{mNewPwd, mTowPwd}, mSave, this));
        mNewPwd.addTextChangedListener(new ITextWatcher(new View[]{mOldPwd, mTowPwd}, mSave, this));
        mTowPwd.addTextChangedListener(new ITextWatcher(new View[]{mNewPwd, mOldPwd}, mSave, this));
    }

    private void toSave() {
        String oldPwd = AppUtils.getTextStr(mOldPwd);
        String newPwd = AppUtils.getTextStr(mNewPwd);
        String towPwd = AppUtils.getTextStr(mTowPwd);
        int pwdLength = StringUtils.getStringLength(newPwd);

        if (!newPwd.equals(towPwd)) {
            ShowToastUtils.toast(this, "重置的二次密码不同");
            return;
        }
        if (oldPwd.equals(newPwd)) {
            ShowToastUtils.toast(this, "新密码不能和旧密码相同");
            return;
        }
        if (pwdLength < 6 || pwdLength > 20) {
            ShowToastUtils.toast(this, "密码的长度为6-20");
            return;
        }
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("old_passwd", Md5Utils.MD5(oldPwd));
            params.put("new_passwd", newPwd);
            params.put("repeat_passwd", towPwd);

            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.MODIFYPWD, params, new RespModifyMsgCallback(this) {
                @Override
                public void onSuccess() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(ModifyPwdActivity.this, "修改密码成功", 1);
                    // 延迟进入
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ModifyPwdActivity.this, MainActivity.class));
                            finish();
                        }
                    }, 2000);
                 //   SharedUtils.putBoolean(ModifyPwdActivity.this, "use_is_login", false);
                }

                @Override
                public void onFail(String status) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if ("-2".equals(status)) {
                        ShowToastUtils.toast(ModifyPwdActivity.this, "原密码错误");
                    } else if ("-3".equals(status)) {
                        ShowToastUtils.toast(ModifyPwdActivity.this, "两次密码不一致");
                    } else {
                        ShowToastUtils.toast(ModifyPwdActivity.this, "失败", 2);
                    }
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.iv_back, R.id.bt_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_save:
                toSave();
                break;
        }
    }
}
