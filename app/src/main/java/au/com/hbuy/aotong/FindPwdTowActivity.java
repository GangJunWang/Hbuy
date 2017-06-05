package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.HashMap;

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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class FindPwdTowActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.layout_select)
    RelativeLayout layoutSelect;
    @Bind(R.id.auto_edit)
    ClearEditText autoEdit;
    @Bind(R.id.code_text)
    TextView codeText;
    @Bind(R.id.bt_find_pwd)
    Button btFindPwd;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private CustomProgressDialog progressDialog;
    private String phone, email;
    private CountTimer mTimeCount;
    private Activity mActivity = FindPwdTowActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd_two);
        ButterKnife.bind(this);
        mTimeCount = new CountTimer(codeText, 0xfff30008, 0xff969696);//传入了文字颜色值

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        email = intent.getStringExtra("email");

        KLog.d(phone + "---" + email);

        if (!phone.equals("")) {
            tvHint.setText(phone);
        } else {
            tvHint.setText(email);
        }
    }

    @OnClick({R.id.iv_back, R.id.layout_select, R.id.code_text, R.id.bt_find_pwd, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.layout_select:
                if (!"".equals(phone) && !"".equals(email)) {
                    final String items[] = {phone, email};
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
                    builder.setTitle("请选择找回方式:"); //设置标题
                    //  builder.setIcon(R.drawable.sex_hint);//设置图标，图片id即可
                    builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    ShowToastUtils.toast(mActivity, "亲,你其他的方式不可用哦...");
                }
                break;

            case R.id.code_text:
                if (NetUtils.isNetworkAvailable(this)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap params = new HashMap();
                    String tep = tvHint.getText().toString().trim();
                    if (tep.indexOf("@") != -1) {
                        params.put("type", 2 + "");
                    } else
                        params.put("type", 1 + "");
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.FINDPWDCODE, params, new RespSendCodeCallback<User>(this, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str.equals("1")) {
                                    mTimeCount.start();
                                    ShowToastUtils.toast(mActivity, "发送成功", 1);
                                    return;
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(mActivity, "发送失败,请稍后再试", 2);
                                    return;
                                } else if (str.equals("-2")) {
                                    ShowToastUtils.toast(mActivity, "60秒后再试");
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(mActivity, "拒绝请求");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.net_hint));
                    AppUtils.stopProgressDialog(progressDialog);
                }
                break;
            case R.id.bt_find_pwd:
                if (NetUtils.isNetworkAvailable(this)) {
                    String code = autoEdit.getText().toString().trim();
                    if (StringUtils.isEmpty(code)) {
                        ShowToastUtils.toast(mActivity, "请输入验证码后再试");
                        return;
                    }
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap params = new HashMap();

                    params.put("code", code);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.FINDPWDTWO, params, new RespSendCodeCallback<User>(this, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str.equals("1")) {
                                    AppUtils.showActivity(mActivity, FindPwdInputActivity.class);
                                    return;
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(mActivity, "验证码错误");
                                    return;
                                } else if (str.equals("-2")) {
                                    ShowToastUtils.toast(mActivity, "拒绝请求");
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(mActivity, "验证码过期");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.net_hint));
                    AppUtils.stopProgressDialog(progressDialog);
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
