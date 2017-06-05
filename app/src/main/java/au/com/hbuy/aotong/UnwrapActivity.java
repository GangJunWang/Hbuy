package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespSendCodeCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class UnwrapActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_hint)
    ImageView ivHint;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_name_hint)
    TextView tvNameHint;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.tv_chat)
    TextView tvChat;
    @Bind(R.id.tv_hint01)
    TextView tvHint01;
    @Bind(R.id.tv_hint02)
    TextView tvHint02;
    private Activity mActivity = UnwrapActivity.this;
    private String mPhone, mPhoneCode, mEmail;
    private CustomProgressDialog progressDialog;
    private UMShareAPI mShareAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unwrap);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if ("1".equals(title)) {
            ivHint.setImageResource(R.drawable.bind_img_phone);
            btNext.setText("重新绑定手机");
            tvTitle.setText("手机号信息");
            tvName.setText("您已绑定手机号码");
            mPhone = intent.getStringExtra("phone");
            mPhoneCode = intent.getStringExtra("phoneCode");
            tvNameHint.setText(mPhoneCode + " " + mPhone.substring(0, 3) + "****" + mPhone.substring(7, mPhone.length()));
            tvHint01.setText("绑定手机号是账号安全的基本");
            tvHint02.setText("如您绑定的手机号已不使用, 请及时更换");
        } else if ("2".equals(title)) {
            ivHint.setImageResource(R.drawable.bind_img_email);
            btNext.setText("重新绑定邮箱");
            tvName.setText("您已绑定邮箱");
            tvTitle.setText("邮箱信息");
            mEmail = intent.getStringExtra("email");
            tvNameHint.setText(mEmail.substring(0, 3) + "****" + mEmail.substring(7, mEmail.length()));
            tvHint01.setText("绑定邮箱能给账户多重保障");
            tvHint02.setText("如您绑定的邮箱已不使用, 请及时更换");
        } else if ("3".equals(title)) {
            tvTitle.setText("微信信息");
            ivHint.setImageResource(R.drawable.bind_img_weixin);
            btNext.setText("重新绑定微信");
            tvName.setText("您已绑定微信");
            tvNameHint.setText("微信号: " + "****");
            tvHint01.setText("绑定微信号后支持快捷登录哦");
            tvHint02.setText("如需绑定新微信号:微信端登录——>点击按钮授权绑定");
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_next, R.id.tv_chat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_chat:
                AppUtils.goChat(mActivity);
                break;
            case R.id.bt_next:
                List<String> list = new ArrayList<>();
                list.add("换绑");
                new SuperDialog.Builder(this)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                        String tmpHint = AppUtils.getTextStr(tvTitle);
                                        if (NetUtils.isNetworkAvailable(mActivity)) {
                                            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

                                            if ("手机号信息".equals(tmpHint)) {
                                                HashMap param = new HashMap();
                                                param.put("phone", mPhone);
                                                param.put("phonecode", mPhoneCode);
                                                KLog.d(mPhone + mPhoneCode);
                                                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_PHONE_CODE_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity) {
                                                    @Override
                                                    public void onSuccess(String str, String msg) {
                                                        AppUtils.stopProgressDialog(progressDialog);

                                                        if (null != str) {
                                                            KLog.d(str);
                                                            Intent intent = new Intent(mActivity, AuthUserActivity.class);
                                                            intent.putExtra("phoneCode", mPhoneCode);
                                                            intent.putExtra("phone", mPhone);
                                                            intent.putExtra("hint", "1");
                                                            if ("1".equals(str)) {
                                                                intent.putExtra("msg", "1");
                                                            } else if ("0".equals(str)) {
                                                                intent.putExtra("msg", "2");
                                                            }
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFail(User o) {
                                                        AppUtils.stopProgressDialog(progressDialog);

                                                    }
                                                });

                                            } else if ("邮箱信息".equals(tmpHint)) {
                                                HashMap param = new HashMap();
                                                param.put("email", mEmail);
                                                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_EMAIL_CODE_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity) {
                                                    @Override
                                                    public void onSuccess(String str, String msg) {
                                                        AppUtils.stopProgressDialog(progressDialog);

                                                        if (null != str) {
                                                            KLog.d(str);
                                                            Intent intent = new Intent(mActivity, AuthUserActivity.class);
                                                            intent.putExtra("email", mEmail);
                                                            intent.putExtra("hint", "2");
                                                            if ("1".equals(str)) {
                                                                intent.putExtra("msg", "1");
                                                            } else if ("0".equals(str)) {
                                                                intent.putExtra("msg", "2");
                                                            }
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFail(User o) {
                                                        AppUtils.stopProgressDialog(progressDialog);

                                                    }
                                                });
                                            } else if ("微信信息".equals(tmpHint)) {
                                                mShareAPI = UMShareAPI.get(mActivity);
                                                mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener);
                                            }
                                        } else {
                                            ShowToastUtils.toast(mActivity, getString(R.string.net_hint));
                                        }
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            final String unionid = map.get("unionid");
            if (null == unionid) {
                ShowToastUtils.toast(mActivity, "换绑微信失败", 2);
                return;
            }
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("wxid", unionid);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.BOUNDWEIXIN, params, new RespModifyMsgCallback(mActivity, null) {
                @Override
                public void onSuccess() {
                    ShowToastUtils.toast(mActivity, "绑定微信成功", 1);
                    SharedUtils.putString(mActivity, "unionid", unionid);
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(mActivity, "失败", 2);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
