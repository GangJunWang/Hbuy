package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.robin.lazy.cache.CacheLoaderManager;
import com.robin.lazy.cache.disk.naming.HashCodeFileNameGenerator;
import com.socks.library.KLog;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.WXManager;
import au.com.hbuy.aotong.view.rong.IExtensionModuleImpl;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import im.fir.sdk.FIR;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MyApplication extends MultiDexApplication {

	private static Context mContext;

	private List<Activity> mList = new LinkedList();

	public void setMyExtensionModule(IExtensionModuleImpl iExtensionModule) {
		KLog.d();
		List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
		IExtensionModule defaultModule = null;
		if (moduleList != null) {
			for (IExtensionModule module : moduleList) {
				if (module instanceof DefaultExtensionModule) {
					defaultModule = module;
					break;
				}
			}
			if (defaultModule != null) {
				RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
				RongExtensionManager.getInstance().registerExtensionModule(iExtensionModule);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		KLog.init(BuildConfig.LOG_DEBUG, "yw");

		//初始化极光推
		//JPush init
		JPushInterface.setDebugMode(true);//如果时正式版就改成false
		JPushInterface.init(this);

		RongIM.init(this);
		KLog.d();
		FIR.init(this);       //初始化bug收集工具
		mContext = getApplicationContext();
		CacheLoaderManager.getInstance().init(this, new HashCodeFileNameGenerator(), 1024 * 1024 * 8, 50, 20);
		regToWx();
		UMShareAPI.get(this);      //友盟分享初始化
		PlatformConfig.setWeixin(ConfigConstants.APP_ID, ConfigConstants.APP_SECRET); //微信

		setMyExtensionModule(new IExtensionModuleImpl());
	}
	/**
	 * 将应用的注册到微信
	 */
	private void regToWx() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		IWXAPI wxApi = WXAPIFactory.createWXAPI(this, ConfigConstants.APP_ID, true);
		// 将应用的注册到微信
		wxApi.registerApp(ConfigConstants.APP_ID);
		WXManager.instance().setApi(wxApi);
	}

	/**
	 * 获得当前进程的名字
	 *
	 * @param context
	 * @return 进程号
	 */
	public static String getCurProcessName(Context context) {

		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {

			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public static Context getContext() {
		return mContext;
	}
}
