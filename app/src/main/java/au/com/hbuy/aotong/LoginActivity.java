package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.hbuy.aotong.domain.Android;
import au.com.hbuy.aotong.domain.Images;
import au.com.hbuy.aotong.domain.Version;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.Md5Utils;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WXManager;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByUpdateCallback;
import au.com.hbuy.aotong.utils.okhttp.RespGetAvaterback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CircleImageView;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements OnClickListener {
    @Bind(R.id.username_edit)
    ClearEditText mUserName;
    @Bind(R.id.pwd_edit)
    ClearEditText mLoginPwd;
    @Bind(R.id.bt_login)
    Button mLogin;
    @Bind(R.id.tv_forget_pwd)
    TextView mForgetPwd;
    @Bind(R.id.tv_to_register)
    TextView mToRegiter;
    @Bind(R.id.iv_weixin)
    Button mWeiXin;
    @Bind(R.id.civ_img)
    CircleImageView civImg;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    @Bind(R.id.tv_name)
    TextView tvName;
    private CustomProgressDialog progressDialog;
    private SendAuth.Req req;
    private static final String WEIXIN_SCOPE = "snsapi_userinfo";// 用于请求用户信息的作用域
    private static final String WEIXIN_STATE = "login_state"; // 自定义
    private Activity mActivity = LoginActivity.this;
    private boolean mIsBeform = false;
    private UMShareAPI mShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        KLog.d();

      /*  if (NetUtils.hasAvailableNet(this)) {
            checkUpdate();  //检查更新版本
        }*/

        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //得到焦点
                } else {
                    KLog.d();
                    String username = mUserName.getText().toString().trim();
                    if (!StringUtils.isEmpty(username)) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("account", username);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GETHEADPORTRAIT, params, new RespGetAvaterback(LoginActivity.this) {
                            @Override
                            public void onSuccess(String img) {
                                KLog.d(img);
                                if (null != img && !"".equals(img))
                                    Picasso.with(LoginActivity.this)
                                            .load(img)
                                            .placeholder(R.drawable.hbuy)
                                            .error(R.drawable.hbuy)
                                            .fit()
                                            .tag(this)
                                            .into(civImg);
                                mIsBeform = true;
                            }

                            @Override
                            public void onFail(Object o) {
                                ShowToastUtils.toast(mActivity, "登录验证头像失败", 2);
                            }
                        });
                    }
                }
            }
        });

        tvName.setText("用户名手机号(如澳洲:\"04xxxx\")不加\"0\"");
        Spannable span2 = new SpannableString(tvName.getText());
        span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.used_text_color)), 6, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span2.setSpan(new AbsoluteSizeSpan(24), 6, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvName.setText(span2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d("request =" + requestCode + "result=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    //手机号注册
                    AppUtils.getUseInfo(mActivity, 0);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.d();
        if (NetUtils.hasAvailableNet(this)) {
            checkUpdate();  //检查更新版本
        }
    }

    private void checkUpdate() {
        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.startDetails, new RespByUpdateCallback<List<Images>>(this) {
            @Override
            public void onSuccess(List<Images> t, final Version v) {
            /*	if (null != t) {
                    mUrl = new ArrayList();
					String[] imgs = new String[t.size()];
					for (int i = 0; i < imgs.length; i++) {
						Images images = t.get(i);
						imgs[i] = images.getImg();
						mUrl.add(i, images.getUrl());
					}
					//      mCview.setImagesUrl(imgs, UserGuideActivity.this);
				}*/
                //    KLog.d(v.getAndroid().getV() + "-" + AppUtils.getVersionCode(mActivity) + "|");
                //    KLog.d(AppUtils.getVersionCode(mActivity) == StringUtils.toInt(v.getAndroid().getV()));
                if (null != v) {
                    final Android a = v.getAndroid();
                    if (AppUtils.getVersionCode(mActivity) == StringUtils.toInt(a.getV())) {
                        return;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(R.string.android_auto_update_dialog_title);
                        builder.setCancelable(false);
                        builder.setMessage(Html.fromHtml(a.getLog()))
                                .setPositiveButton(R.string.android_auto_update_dialog_btn_download, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (!NetUtils.isWIFI(mActivity)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                            builder.setTitle(R.string.warm_hint);
                                            builder.setCancelable(false);
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

            }
        });
    }

    /**
     * 申请授权
     */
    private void sendAuth() {
        if (WXManager.instance().isWXAppInstalled()) {
           /* final SendAuth.Req req = new SendAuth.Req();
            req.scope = WEIXIN_SCOPE;
            req.state = WEIXIN_STATE;
            //拉起微信授权，授权结果在WXEntryActivity中接收处理
            WXManager.instance().sendReq(req);
            finish();*/
            mShareAPI = UMShareAPI.get(this);
            mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, umAuthListener);
            KLog.d();
        } else {
            ShowToastUtils.toast(mActivity, "没有安装微信");
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            mShareAPI.getPlatformInfo(mActivity, share_media, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    KLog.d();
                    String unionid = map.get("unionid");
                    String sex = map.get("gender");
                    String nickname = map.get("screen_name");
                    String headimgurl = map.get("iconurl");
                    if (null == unionid || null == sex || null == nickname || null == headimgurl) {
                        ShowToastUtils.toast(mActivity, "授权失败", 2);
                        return;
                    }
                    for (String str : map.keySet()) {
                        KLog.d(str);
                    }
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("headimgurl", headimgurl);
                    params.put("nickname", nickname);
                    if ("男".equals(sex)) {
                        params.put("sex", "1");
                    } else {
                        params.put("sex", "2");
                    }
                    params.put("unionid", unionid);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.weixinLogin, params, new RespModifyMsgCallback(mActivity, progressDialog) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(progressDialog);
                        }

                        @Override
                        public void onFail(String str) {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "失败", 2);
                        }

                        @Override
                        public void onBusiness(String str) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if ("1".equals(str)) {
                                //之前绑定了
                                AppUtils.getUseInfo(mActivity, 3);
                            } else if ("2".equals(str)) {
                                //创建账号
                                AppUtils.showActivity(mActivity, CompleteActivity.class);
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    KLog.d();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    KLog.d();
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    @OnClick({R.id.bt_login, R.id.tv_forget_pwd, R.id.tv_to_register, R.id.iv_weixin, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                // 登录
                login();
                break;
            case R.id.tv_forget_pwd:
                // 忘记密码
                AppUtils.showActivity(mActivity, FindPwdActivity.class);
                break;
            case R.id.tv_to_register:
                // 点击注册
                Intent intent = new Intent(mActivity, PhoneRegisterActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.iv_weixin:
                sendAuth();
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
        }
    }

    private void login() {
        mUserName.clearFocus();
        String pwd = mLoginPwd.getText().toString().trim();
        String username = mUserName.getText().toString().trim();
        int pwdLength = StringUtils.getStringLength(pwd);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(pwd)) {
            ShowToastUtils.toast(this, "用户名或密码为空了，请重新输入...");
            return;
        } else if (pwdLength < 6 || pwdLength > 20) {
            ShowToastUtils.toast(this, "密码的长度为6-20");
            return;
        } else if (!mIsBeform) {
            ShowToastUtils.toast(this, "亲, 再登录一次");
            mIsBeform = true;
            return;
        } else {
            if (NetUtils.hasAvailableNet(this)) {
                //go login
                progressDialog = AppUtils.startProgressDialog(this, "正在登陆...", progressDialog);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("passwd", Md5Utils.MD5(pwd));
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.LOGINURL, params, new RespModifyMsgCallback(LoginActivity.this) {
                    @Override
                    public void onSuccess() {
                        AppUtils.stopProgressDialog(progressDialog);
                        AppUtils.getUseInfo(mActivity, 1);
                    }

                    @Override
                    public void onFail(String str) {
                        ShowToastUtils.toast(LoginActivity.this, "用户名或密码错误");
                        AppUtils.stopProgressDialog(progressDialog);
                    }
                });
            } else {
                ShowToastUtils.toast(LoginActivity.this, getString(R.string.net_hint));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
