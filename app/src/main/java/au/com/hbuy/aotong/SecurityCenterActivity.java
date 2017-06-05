package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class SecurityCenterActivity extends BaseFragmentActivity implements View.OnClickListener {

    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.tv_mofity_pwd)
    TextView mMofityPwd;
    @Bind(R.id.bt_logout)
    Button mLogout;
    @Bind(R.id.bt_phone)
    TextView btPhone;
    @Bind(R.id.bt_weixin)
    TextView btWeixin;
    @Bind(R.id.bt_email)
    TextView btEmail;
    private static int PHONECODE = 1;
    private static int EMAILCODE = 2;
    @Bind(R.id.tv_phone_hint)
    TextView tvPhoneHint;
    @Bind(R.id.tv_weixin_hint)
    TextView tvWeixinHint;
    @Bind(R.id.tv_email_hint)
    TextView tvEmailHint;
    @Bind(R.id.modify_phone_layout)
    RelativeLayout modifyPhoneLayout;
    @Bind(R.id.modify_weixin_layout)
    RelativeLayout modifyWeixinLayout;
    @Bind(R.id.modify_email_layout)
    RelativeLayout modifyEmailLayout;
    @Bind(R.id.modify_pwd_layout)
    LinearLayout modifyPwdLayout;
    private String phone;
    private String phoneCode;
    private String email;
    private String weixin;
    public final static int QUITAPP = 4;
    private UMShareAPI mShareAPI;
    private SecurityCenterActivity mActivity = SecurityCenterActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_center);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        phone = SharedUtils.getString(this, "phone", "");
        phoneCode = SharedUtils.getString(this, "phonecode", "");
        if (StringUtils.isEmpty(phone)) {
            btPhone.setText("去绑定");
        } else {
            btPhone.setText(phone);
            tvPhoneHint.setText("已绑定, 如需更换请点击修改");
        }

        email = SharedUtils.getString(this, "email", "");
        if (StringUtils.isEmpty(email)) {
            btEmail.setText("去绑定");
            tvEmailHint.setText("未绑定, 绑定邮箱给账户多重保护!");
        } else {
            btEmail.setText(email);
            tvPhoneHint.setText("已绑定, 如需更换请点击修改");
        }

        weixin = SharedUtils.getString(this, "unionid", "");
        if (StringUtils.isEmpty(weixin)) {
            btWeixin.setText("去绑定");
            tvWeixinHint.setText("未绑定, 绑定微信可快捷登录哦!");
        } else {
            btWeixin.setText("已绑定");
            tvWeixinHint.setText("已绑定, 如需换绑请联系客服");
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            final String unionid = map.get("unionid");
            if (null == unionid) {
                ShowToastUtils.toast(mActivity, "绑定微信失败", 2);
                return;
            }
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("wxid", unionid);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.BOUNDWEIXIN, params, new RespModifyMsgCallback(mActivity, null) {
                @Override
                public void onSuccess() {
                    ShowToastUtils.toast(mActivity, "绑定微信成功", 1);
                    btWeixin.setText("已绑定");
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

    @OnClick({R.id.iv_back, R.id.bt_logout, R.id.modify_phone_layout, R.id.modify_weixin_layout, R.id.modify_email_layout, R.id.modify_pwd_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_logout:
                new SuperDialog.Builder(this)
                        .setAlpha(1f)
                        .setRadius(20)
                        .setWidth(0.8f)
                        .setTitle("退出").setMessage("亲，确定要退出当前账号吗?")
                        .setPositiveButton("确定", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                KLog.d(CacheLoaderManager.getInstance().size());
                                SharedUtils.putBoolean(SecurityCenterActivity.this, "use_is_login", false);
                                //    AppUtils.showActivity(PersonalDataActivity.this, LoginActivity.class);
                                CacheLoaderManager.getInstance().clear();   //清空缓存
                                RongIM.getInstance().logout();
                                //   MyApplication.getInstance().exit();
                                MobclickAgent.onProfileSignOff();  //退出友盟的账户统计
                                SharedUtils.putString(mActivity, "repo_name", "服务器异常");
                                JPushInterface.setAlias(SecurityCenterActivity.this, "", new TagAliasCallback() {          //极光推退出
                                    @Override
                                    public void gotResult(int i, String s, Set<String> set) {
                                    }
                                });
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new SuperDialog.OnClickNegativeListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .build();
                break;
            case R.id.modify_phone_layout:
                String tmp = AppUtils.getTextStr(btPhone);
                if ("去绑定".equals(tmp)) {
                    Intent intent = new Intent(this, PhoneBoundActivity.class);
                    intent.putExtra("hint", "1");
                    startActivityForResult(intent, PHONECODE);
                } else {
                    Intent intent = new Intent(this, UnwrapActivity.class);
                    intent.putExtra("title", "1");
                    intent.putExtra("phoneCode", phoneCode);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
                break;
            case R.id.modify_weixin_layout:
                String weixinHint = AppUtils.getTextStr(btWeixin);
                if ("去绑定".equals(weixinHint)) {
                    mShareAPI = UMShareAPI.get(this);
                    mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);
                } else {
                 //   ShowToastUtils.toast(this, "不能换绑微信");
                    Intent intent = new Intent(this, UnwrapActivity.class);
                    intent.putExtra("title", "3");
                    startActivity(intent);
                }
                break;
            case R.id.modify_email_layout:
                String emailtmp = AppUtils.getTextStr(btEmail);
                if ("去绑定".equals(emailtmp)) {
                    Intent intent = new Intent(this, EmailBoundActivity.class);
                    intent.putExtra("hint", "1");
                    startActivityForResult(intent, EMAILCODE);
                } else {
                    Intent intent = new Intent(this, UnwrapActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("title", "2");
                    startActivity(intent);
                }
                break;
            case R.id.modify_pwd_layout:
                AppUtils.showActivity(this, ModifyPwdActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            initData();
        }
    }
}
