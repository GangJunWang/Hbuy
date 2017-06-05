package au.com.hbuy.aotong.base.impl;

import android.content.Intent;
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
import au.com.hbuy.aotong.SelectNationAndCodeActivity;
import au.com.hbuy.aotong.UserdealActivity;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespSendCodeCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CountTimer;
import au.com.hbuy.aotong.view.CustomProgressDialog;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class RegisterPage extends BasePage {
    private ClearEditText mPhone, mRegistPwd, mRegistVerifyPwd, mCode, mName;
    private Button mRegisteButton;
    private View mView;
    private TextView mSelectCode, mGetCode;
    private CountTimer mTimeCount;
    private CustomProgressDialog progressDialog;
    private TextView codeTv;

    public RegisterPage(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.register_page, null);
        flContent.addView(mView);
        mPhone = (ClearEditText) mView.findViewById(R.id.et_phone);
        mCode = (ClearEditText) mView.findViewById(R.id.et_code);
        mRegistPwd = (ClearEditText) mView.findViewById(R.id.pwd_edit);
        mRegistVerifyPwd = (ClearEditText) mView.findViewById(R.id.pwd_edit_verify);
        mSelectCode = (TextView) mView.findViewById(R.id.tv_select_code);
        mName = (ClearEditText) mView.findViewById(R.id.tv_name);

        mRegisteButton = (Button) mView.findViewById(R.id.register_agree_button);
        mGetCode = (TextView) mView.findViewById(R.id.bt_get_code);

        mView.findViewById(R.id.tv_user_deal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.showActivity(mActivity, UserdealActivity.class);
            }
        });

        TextView hintName = (TextView) mView.findViewById(R.id.tv_name_hint);
        hintName.setText("请先选择国家码，手机号(如澳洲:\"04xxxx\")不加\"0\"");
        Spannable span2 = new SpannableString(hintName.getText());
        span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.used_text_color)), 12, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span2.setSpan(new AbsoluteSizeSpan(24), 11, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        hintName.setText(span2);
    }

    public View setCode () {
        return mSelectCode;
    }
    @Override
    public void initData() {
        mRegisteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isNotFastClick()) register();
            }
        });

        mGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String numStr = mPhone.getText().toString().trim();
                if (!StringUtils.isNumber(numStr)) {
                    ShowToastUtils.toast(mActivity, "请输入正确的手机号码");
                    return;
                }
                final String code = mSelectCode.getText().toString().trim();
                if (null == code || StringUtils.isEmpty(code)) {
                    ShowToastUtils.toast(mActivity, "国家码不能为空");
                    return;
                }
                    if (NetUtils.isNetworkAvailable(mActivity)) {
                        progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("phonecode", code.substring(1, code.length()));
                        params.put("phone", numStr);
                        ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.send_phone_code_url, params, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);
                                if (null != str && str.equals("1")) {
                                    if (msg.equals("1")) {
                                        //发送成功
                                        mTimeCount = new CountTimer(mGetCode, 0xfff30008, 0xff969696);//传入了文字颜色值
                                        mTimeCount.start();
                                    } else {
                                        ShowToastUtils.toast(mActivity, "验证码发送失败", 2);
                                        return;
                                    }
                                    //发送成功
                                } else if (null != str && str.equals("0")) {
                                    ShowToastUtils.toast(mActivity, "该手机号码已注册,请更换手机号码");
                                    return;
                                }
                            }

                            @Override
                            public void onFail(User o) {
                                AppUtils.stopProgressDialog(progressDialog);
                            }
                        });
                    } else {
                        ShowToastUtils.toast(mActivity, mActivity.getString(R.string.net_hint));
                    }
            }
        });

        mSelectCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   StringUtils.selectCode(0, null, mSelectCode, mActivity);
                Intent i = new Intent();
                i.setClass(mActivity, SelectNationAndCodeActivity.class);
                i.putExtra("type", "1");
               // i.putExtra("hint", mSelectCode.getText().toString().trim());
                mActivity.startActivityForResult(i, 1);
            }
        });
    }

    private void register() {
        String name = mName.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String pwd = mRegistPwd.getText().toString().trim();
        String verifyPwd = mRegistVerifyPwd.getText().toString();
        int userNameLength = StringUtils.getStringLength(phone);
        int pwdLength = StringUtils.getStringLength(pwd);
        String code = mCode.getText().toString().trim();

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(pwd) || StringUtils.isEmpty(verifyPwd) || StringUtils.isEmpty(code)) {
            ShowToastUtils.toast(mActivity, "您输入的信息不全");
            return;
        } else if (userNameLength < 6 || userNameLength > 20) {
            ShowToastUtils.toast(mActivity, "用户名长度为6-19");
            return;
        } else if (pwdLength < 6 || pwdLength > 20) {
            ShowToastUtils.toast(mActivity, "密码的长度为6-19");
            return;
        } else if (!pwd.equals(verifyPwd)) {
            ShowToastUtils.toast(mActivity, "二次输入的密码不相等");
            return;
        }
        if (null != name && name.length() < 2) {
            ShowToastUtils.toast(mActivity, "用户名长度不能小于2");
            return;
        }
        if (null != name && name.length() > 4) {
            ShowToastUtils.toast(mActivity, "用户名长度不能大于4");
            return;
        }
        if (null != name && name.contains("@")) {
            ShowToastUtils.toast(mActivity, "用户名不能包含@符号");
            return;
        }
        if (null != name && !StringUtils.isContainChinese(name)) {
            ShowToastUtils.toast(mActivity, "用户名必须为中英文");
            return;
        }
        if (NetUtils.hasAvailableNet(mActivity)) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("new", "1");
            params.put("code", code);
            params.put("passwd", pwd);
            params.put("username", name);
            params.put("repasswd", verifyPwd);
            progressDialog = AppUtils.startProgressDialog(mActivity, "正在注册", progressDialog);
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.weixinBound, params, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                @Override
                public void onSuccess(String str, String msg) {
                    AppUtils.stopProgressDialog(progressDialog);

                    if (null != str) {
                        if ("-1".equals(str)) {
                            ShowToastUtils.toast(mActivity, "验证码过期");
                            mTimeCount.cancel();
                            mTimeCount.onFinish();
                            return;
                        } else if ("0".equals(str)) {
                            ShowToastUtils.toast(mActivity, "发送失败,请稍后再试", 2);
                            return;
                        } else if ("-2".equals(str)) {
                            ShowToastUtils.toast(mActivity, "手机号已存在");
                            return;
                        } else if ("-3".equals(str)) {
                            ShowToastUtils.toast(mActivity, "验证码错误");
                            mTimeCount.cancel();
                            mTimeCount.onFinish();
                            return;
                        } else if ("-4".equals(str)) {
                            ShowToastUtils.toast(mActivity, "两次密码不一致");
                            return;
                        } else if (StringUtils.toInt(str) > 0) {
                            AppUtils.getUseInfo(mActivity, 0);
                        }
                    }
                }

                @Override
                public void onFail(User user) {
                    AppUtils.stopProgressDialog(progressDialog);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
        }
    }
}
