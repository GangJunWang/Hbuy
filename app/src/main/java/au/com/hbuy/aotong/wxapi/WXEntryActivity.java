package au.com.hbuy.aotong.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.socks.library.KLog;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.weixin.callback.WXCallbackActivity;


public class WXEntryActivity extends WXCallbackActivity  {  //友盟分享

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        KLog.d();
        super.onCreate(savedInstanceState);
   //     WXManager.instance().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
  //      WXManager.instance().handleIntent(intent, this);
    }
*/
   /* @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        KLog.d(resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp instanceof SendAuth.Resp) {
                    SendAuth.Resp aures = (SendAuth.Resp) resp;
                    String code = aures.code;
                    //微信授权返回结果，处理授权后操作,获取微信token
                    KLog.d(code);
                    getTokenAndOpenId(code);
                    return;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
             //   AppUtils.showActivity(this, LoginActivity.class);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                ShowToastUtils.toast(this, "微信登录失败");
                // 延迟进入
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.showActivity(WXEntryActivity.this, LoginActivity.class);
                        finish();
                    }
                }, 2000);
                break;
        }
        //finish();
    }

    private void getTokenAndOpenId(String code) {
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "正在授权...", progressDialog);
            String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + ConfigConstants.APP_ID + "&secret=" + ConfigConstants.APP_SECRET
                    + "&code=" + code + "&grant_type=authorization_code";
            ApiClient.get(path, new RespCallback(WXEntryActivity.this) {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(String str) {
                }

                @Override
                public void sendToken(String access_token, String openid) {
                    String path = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
                    ApiClient.get(path, new RespCallback(WXEntryActivity.this) {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(String str) {

                        }

                        @Override
                        public void onBusiness(String headimgurl, String nickname, String sex, String unionid) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("headimgurl", headimgurl);
                            params.put("nickname", nickname);
                            params.put("sex", sex);
                            params.put("unionid", unionid);

                            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.weixinLogin, params, new RespWeixinCallback<User>(WXEntryActivity.this) {
                                @Override
                                public void onSuccess(User user) {
                                    if (null != user) {
                                        SharedUtils.putString(WXEntryActivity.this, "user_token", user.getImtoken());
                                        SharedUtils.putString(WXEntryActivity.this, "kf_id", user.getKf_id());
                                        SharedUtils.putBoolean(WXEntryActivity.this, "use_is_login", true);
                                        SharedUtils.putString(WXEntryActivity.this, "username", user.getUsername());
                                        SharedUtils.putString(WXEntryActivity.this, "city", user.getCity());
                                        SharedUtils.putString(WXEntryActivity.this, "avatar", user.getAvator());
                                        SharedUtils.putString(WXEntryActivity.this, "country", user.getCountry());
                                        SharedUtils.putString(WXEntryActivity.this, "gender", user.getGender());
                                        SharedUtils.putString(WXEntryActivity.this, "uid", user.getUid());
                                    }
                                    AppUtils.stopProgressDialog(progressDialog);

                                    ShowToastUtils.toast(WXEntryActivity.this, "登录成功");
                                    startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCreateAccount(String s) {
                                    AppUtils.stopProgressDialog(progressDialog);

                                    AppUtils.showActivity(WXEntryActivity.this, CompleteActivity.class);
                                    finish();
                                }

                                @Override
                                public void onFail(User user) {
                                    AppUtils.stopProgressDialog(progressDialog);

                                    ShowToastUtils.toast(WXEntryActivity.this, "登录失败");
                                    startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                        }
                    });
                }
            });

        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }*/
}
