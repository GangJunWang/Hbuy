package au.com.hbuy.aotong.base.impl;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.base.BasePage;
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

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class LoginPage extends BasePage {
    private Button mLogin;
    /**
     * username
     */
    private ClearEditText mUserName;
    /**
     * 密码
     */
    private ClearEditText mLoginPwd;
    private View mView;
    private CustomProgressDialog progressDialog;

    public LoginPage(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.login_page, null);
        flContent.addView(mView);
        mLogin = (Button) mView.findViewById(R.id.bt_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isNotFastClick()) {
                    login();
                }
            }
        });
        mUserName = (ClearEditText) mView.findViewById(R.id.username_edit);
        mLoginPwd = (ClearEditText) mView.findViewById(R.id.pwd_edit);

        TextView hintName = (TextView) mView.findViewById(R.id.tv_name_hint);
        hintName.setText("手机号(如澳洲:\"04xxxx\")不加\"0\"");
        Spannable span2 = new SpannableString(hintName.getText());
        span2.setSpan(new AbsoluteSizeSpan(24), 3, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        hintName.setText(span2);
    }

    @Override
    public void initData() {
        mUserName.addTextChangedListener(new ITextWatcher(new View[]{mLoginPwd}, mLogin, mActivity));
        mLoginPwd.addTextChangedListener(new ITextWatcher(new View[]{mUserName}, mLogin, mActivity));
    }

    private void login() {
        String username = mUserName.getText().toString().trim();
        String pwd = mLoginPwd.getText().toString().trim();
        if (StringUtils.isNull(username) || StringUtils.isNull(pwd)) {
            ShowToastUtils.toast(mActivity, "用户名或密码为空了，请重新输入...");
            return;
        }
            if (NetUtils.hasAvailableNet(mActivity)) {
                progressDialog = AppUtils.startProgressDialog(mActivity, "正在登陆...", progressDialog);
                //go login
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("account", username);
                params.put("passwd", Md5Utils.MD5(pwd));
                ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.weixinBound, params, new RespModifyMsgCallback(mActivity) {
                    @Override
                    public void onSuccess() {
                        AppUtils.stopProgressDialog(progressDialog);
                        AppUtils.getUseInfo(mActivity, 2);
                    }
                    @Override
                    public void onFail(String str) {
                        AppUtils.stopProgressDialog(progressDialog);
                        if ("0".equals(str)) {
                            ShowToastUtils.toast(mActivity, "失败", 2);
                        } else if ("-1".equals(str)) {
                            ShowToastUtils.toast(mActivity, "用户不存在");
                        }
                    }
                });
            } else {
                ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
            }
    }
}
