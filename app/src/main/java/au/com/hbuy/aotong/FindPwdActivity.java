package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.PhoneBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.ITextWatcher;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespClassTCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class FindPwdActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mBack;
    private EditText mNumber;
    private Button mAddPkg;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mNumber = (EditText)findViewById(R.id.et_number);
        mAddPkg = (Button)findViewById(R.id.bt_find_pwd);

        mAddPkg.setOnClickListener(this);
        mBack.setOnClickListener(this);
        findViewById(R.id.tv_feedback).setOnClickListener(this);

        TextView hint = (TextView) findViewById(R.id.tv_title_hint);
        hint.setText("手机号(如澳洲:\"04xxxx\")不加\"0\"和国家码");
        Spannable span2 = new SpannableString(hint.getText());
        span2.setSpan(new AbsoluteSizeSpan(24), 4, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //back
                finish();
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.bt_find_pwd:
                if (AppUtils.isNotFastClick()) {
                    if (NetUtils.hasAvailableNet(this)) {
                        String tmp = mNumber.getText().toString().trim();
                        if (StringUtils.isEmpty(tmp)) {
                            ShowToastUtils.toast(this, "请输入用户名/邮箱/手机号后再试~");
                            return;
                        }
                        progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("account", tmp);
                        ApiClient.getInstance(this).postForm(ConfigConstants.findPwd, params, new RespClassTCallback<PhoneBean>(FindPwdActivity.this) {
                            @Override
                            public void onSuccess(PhoneBean phoneBean) {
                                AppUtils.stopProgressDialog(progressDialog);
                                String email = phoneBean.getEmail();
                                String phone = phoneBean.getPhone();
                                if (!"".equals(email) || !"".equals(phone)) {
                                    Intent i = new Intent(FindPwdActivity.this, FindPwdTowActivity.class);
                                    i.putExtra("email", email);
                                    i.putExtra("phone", phone);
                                    startActivity(i);
                                } else {
                                    //提示用户用微信登录WeixinFindPwdActivity
                                    AppUtils.showActivity(FindPwdActivity.this, WeixinFindPwdActivity.class);
                                }
                            }
                            @Override
                            public void onFail(String s) {
                                if ("0".equals(s)) {
                                    ShowToastUtils.toast(FindPwdActivity.this, "用户不存在");
                                } else if ("-1".equals(s)) {
                                    ShowToastUtils.toast(FindPwdActivity.this, "错误请求");
                                }
                                AppUtils.stopProgressDialog(progressDialog);
                            }
                        });
                    } else {
                        AppUtils.stopProgressDialog(progressDialog);
                        ShowToastUtils.toast(this, getString(R.string.no_net_hint));
                    }
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
