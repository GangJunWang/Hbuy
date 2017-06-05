package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import au.com.hbuy.aotong.base.BaseMainPage;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.base.impl.BuyPage;
import au.com.hbuy.aotong.base.impl.MePage;
import au.com.hbuy.aotong.base.impl.OrderPage;
import au.com.hbuy.aotong.base.impl.ServicePage;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Android;
import au.com.hbuy.aotong.domain.Images;
import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.domain.UserInfoBen;
import au.com.hbuy.aotong.domain.Version;
import au.com.hbuy.aotong.service.DownloadService;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByUpdateCallback;
import au.com.hbuy.aotong.utils.okhttp.RespClassTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseMainActivity implements OnReceiveMessageListener {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private MyPagerAdapter myPagerAdapter;
    private RelativeLayout message_net;
    private long beformTime = 0;

    private List<BaseMainPage> mPagerList;
    public static final int FROM_MODIFY_PERSONAL_DATA = 1;
    //监听网络
    private NetBroadcast mNetBroadcast;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   MyApplication.getInstance().addActivity(this);
        mActivity = MainActivity.this;

        //获取剪贴板的数据
        ClipboardManager myClipboard = (ClipboardManager)this.getSystemService(this.CLIPBOARD_SERVICE);
        ClipData cd = myClipboard.getPrimaryClip();
        if(null != cd) {
            ClipData.Item item = cd.getItemAt(0);
            KLog.d(item.getText().toString());
            String tmp = item.getText().toString();
            int length = tmp.length();
            if (length > 5 && length < 25) {
                KLog.d();
                AppUtils.isLetterDigit(this, tmp);
            }
       }

        if (NetUtils.hasAvailableNet(mActivity)) {
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETUSEINFO, new RespClassTCallback<User>(mActivity) {
                @Override
                public void onSuccess(User user) {
                    if (null != user) {
                        MobclickAgent.onProfileSignIn(user.getUid());   //统计友盟用户数据
                        KLog.d(user.getImtoken());
                        SharedUtils.putString(mActivity, "user_token", user.getImtoken());
                        SharedUtils.putString(mActivity, "uid", user.getUid());
                        SharedUtils.putString(mActivity, "kf_id", user.getKf_id());
                        SharedUtils.putString(mActivity, "city", user.getCity());
                        SharedUtils.putString(mActivity, "gender", user.getGender());
                        SharedUtils.putString(mActivity, "avatar", user.getAvator());
                        SharedUtils.putString(mActivity, "country", user.getCountry());
                        SharedUtils.putString(mActivity, "phone", user.getPhone());
                        SharedUtils.putString(mActivity, "phonecode", user.getPhonecode());
                        SharedUtils.putString(mActivity, "email", user.getEmail());
                        SharedUtils.putString(mActivity, "unionid", user.getUnionid());
                        SharedUtils.putString(mActivity, "ranktype", user.getIsvip());
                    }
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(mActivity, "获取用户资料失败", 3);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.net_hint), 3);
        }

        if (NetUtils.hasAvailableNet(this)) {
            beformTime = SharedUtils.getLong(this, "time", 0);
            final long currentTiem = System.currentTimeMillis();
            KLog.d("currentTime=" + currentTiem + "--beformTime=" + beformTime);
            if (beformTime == 0 || currentTiem - beformTime > 24 * 60 * 60 * 1000) {
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(this) {
                        @Override
                        public void onSuccess(Address address) {
                            SharedUtils.putLong(mActivity, "time", currentTiem);
                            KLog.d(address.getCountry());
                            SharedUtils.putString(mActivity, "repo_name", address.getName());
                            SharedUtils.putString(mActivity, "repo_address", address.getAddress());
                            SharedUtils.putString(mActivity, "repo_zip", address.getZip());
                            SharedUtils.putString(mActivity, "repo_phone", address.getPhone());
                        }

                        @Override
                        public void onFail(String str) {
                          //  ShowToastUtils.toast(mActivity, "初始化失败", 2);
                        }
                    });
                } else {
                    //    ShowToastUtils.toast(context, context.getString(R.string.no_net_hint), 3);
                }
            }
        }
        final String uid = SharedUtils.getString(this, "uid", "");
        final String avatar = SharedUtils.getString(this, "avatar", "");
        final String kf_id = SharedUtils.getString(this, "kf_id", "");
        final String user_token = SharedUtils.getString(this, "user_token", "");
        KLog.d(user_token + getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext())));
//        KLog.d(user_token);
        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
        KLog.d();
            RongIM.connect(user_token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    KLog.d();
                    ShowToastUtils.toast(mActivity, "融云Token错误", 2);
                }
                @Override
                public void onSuccess(String userid) {
                    KLog.d(userid);
                    if (null != RongIM.getInstance()) {
                        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
                        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
                        RongIM.getInstance().setMessageAttachedUserInfo(true);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    KLog.d(errorCode);
                    ShowToastUtils.toast(mActivity, errorCode.getMessage(), 3);
                }
            });
        }
        RongIM.setOnReceiveMessageListener(this);
        KLog.d(uid);
        JPushInterface.setAlias(this, uid, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                KLog.d(uid);
            }
        });

        mNetBroadcast = new NetBroadcast();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetBroadcast, intentFilter);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if (NetUtils.hasAvailableNet(this)) {
            checkUpdate();  //检查更新版本
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetBroadcast);
    }

    private void init() {
        message_net = (RelativeLayout) findViewById(R.id.message_net);
        message_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSettingNet();
            }
        });
        mPagerList = new ArrayList<BaseMainPage>();
        mPagerList.add(new OrderPage(this));
        mPagerList.add(new BuyPage(this));
        mPagerList.add(new ServicePage(this));
        mPagerList.add(new MePage(this));
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View checkView = findViewById(i);
                if (!checkView.isPressed()) {
                    return;
                }
                switch (i) {
                    case R.id.rb_order:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_buy:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_service:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rb_me:
                        mViewPager.setCurrentItem(3, false);
                        break;
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.vp_content);

        myPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(myPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                KLog.d(arg0);
                if (arg0 == 0) {
                    mRadioGroup.check(R.id.rb_order);
                } else if (arg0 == 1) {
                    mRadioGroup.check(R.id.rb_buy);
                } else if (arg0 == 2){
                    mRadioGroup.check(R.id.rb_service);
                } else if (arg0 == 3) {
                    mRadioGroup.check(R.id.rb_me);
                }
                mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                KLog.d(arg0);
            }
        });
        mRadioGroup.check(R.id.rb_order);
        mPagerList.get(0).initData();// 获取当前被选中的页面, 初始化该页面数据
    }

    @Override
    public boolean onReceived(Message message, int i) {
     /*   KLog.d(Thread.currentThread());
        android.os.Message msg = new android.os.Message();
        msg.obj = message.getExtra();
     //   mHandler.sendMessage(msg);*/
        return false;
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

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagerList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseMainPage basePager = mPagerList.get(position);
            container.addView(basePager.mRootView);
            return basePager.mRootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.d("requestCode =" + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == OrderPage.FROM_ADD_PKG) {
                mPagerList.get(0).initData();
                KLog.d();
            } else if (requestCode == BuyPage.FROM_DEL) {
                KLog.d();
                mPagerList.get(1).initData();
            } else if (requestCode == SecurityCenterActivity.QUITAPP) {
                finish();
                AppUtils.showActivity(this, LoginActivity.class);
            }
        }
    }

    public class NetBroadcast extends BroadcastReceiver {
        private ConnectivityManager connectivityManager;
        private NetworkInfo info;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    //有网络
                    message_net.setVisibility(View.GONE);
                } else {
                    //没有可用网络
                    message_net.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    //setting net
    private void toSettingNet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        KLog.d(keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitBy2Click();
                break;
        }
        return false;
    }

    private static Boolean isExit = false;

    //双击退出
    private void exitBy2Click() {
        Timer exit = null;
        if (!isExit) {
            isExit = true;
            ShowToastUtils.toast(this, "再按一次退出应用");
            exit = new Timer();
            exit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
        //    RongIM.getInstance().disconnect();
          //  SharedUtils.putBoolean(this, "use_is_login", false);
         //   RongIM.getInstance().logout();
            finish();
        }
    }

    /*private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            KLog.d(msg.toString());
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, ConversationActivity.class), 0);
            Notification notify = new Notification.Builder(MainActivity.this)
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24， 这里也可以设置大图标
                    .setContentTitle("hbuy客服")// 设置显示的标题
                    .setContentText((String)msg.obj)// 消息的详细内容
                    .setContentIntent(pendingIntent)
                    .setNumber(100)
                    .getNotification(); // 关联PendingIntent
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, notify);
        }
    };*/
}
