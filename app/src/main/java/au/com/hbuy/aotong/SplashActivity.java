package au.com.hbuy.aotong;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;

import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Android;
import au.com.hbuy.aotong.domain.Images;
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
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;

public class SplashActivity extends BaseActivity {
	private Activity mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = SplashActivity.this;
	//	setContentView(R.layout.activity_splash);
		MobclickAgent.enableEncrypt(true);  //友盟日志加密
		MobclickAgent.setDebugMode( true );  //开启测试模式
		final boolean isLogin = SharedUtils.getBoolean(this, "use_is_login", false);
		final boolean isFirst = SharedUtils.getBoolean(this, "use_first", false);
		KLog.d(isLogin);

	/*	long beformTime = SharedUtils.getLong(this, "time", 0);
		long currentTiem = System.currentTimeMillis();
		KLog.d(beformTime + "---" + currentTiem);
		SharedUtils.putLong(this, "time", currentTiem);
	//	if (currentTiem - beformTime > 24 * 60 * 60 * 1000) {
			if (NetUtils.hasAvailableNet(mContext)) {
				ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(this) {
					@Override
					public void onSuccess(Address address) {
						KLog.d(address.getCountry());
						SharedUtils.putString(mContext, "repo_name", address.getName());
						SharedUtils.putString(mContext, "repo_address", address.getAddress());
						SharedUtils.putString(mContext, "repo_zip", address.getZip());
						SharedUtils.putString(mContext, "repo_phone", address.getPhone());
					}

					@Override
					public void onFail(String str) {
						      ShowToastUtils.toast(mContext, "初始化失败", 2);
					}
				});
			} else {
				//    ShowToastUtils.toast(context, context.getString(R.string.no_net_hint), 3);
			}
	//	}*/

		// 延迟进入
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isFirst) {
					startActivity(new Intent(SplashActivity.this, UserGuideActivity.class));
				} else {
					if (!isLogin) {
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					} else {
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
					}
				}
				finish();
			}
		}, 2000);
	}
}
