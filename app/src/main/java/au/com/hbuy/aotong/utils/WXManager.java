/**
 * Project Name:WechatLoginDemo
 * File Name:WXManager.java
 * Package Name:com.example.chenzhengjun.wechatlogindemo
 * Date:11/5/2015
 * Copyright (c) 2015, iczjun@gmail.com All Rights Reserved.
 */
package au.com.hbuy.aotong.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import java.io.File;

/**
 *
 * @author chenzhengjun
 *
 */
public class WXManager {
	private static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
	private static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
	private static final int THUMB_SIZE = 150;
	private static WXManager mWXManager = new WXManager();

	private IWXAPI api;

	public static WXManager instance() {
		return mWXManager;
	}

	/**
	 * @param api
	 *            the api to set
	 */
	public void setApi(IWXAPI api) {
		this.api = api;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	public boolean handleIntent(Intent arg0, IWXAPIEventHandler arg1) {
		if (api == null)
			return false;
		return api.handleIntent(arg0, arg1);
	}

	public boolean isWXAppInstalled() {
		if (api == null)
			return false;
		return api.isWXAppInstalled();
	}

	public boolean isWXAppSupportAPI() {
		if (api == null)
			return false;
		return api.isWXAppSupportAPI();
	}

	public boolean sendReq(BaseReq arg0) {
		if (api == null)
			return false;
		return api.sendReq(arg0);
	}

	/*public void getAccessToken(String code, WxAccessTokenResp resp) {
		WxAccessTokenReq req = new WxAccessTokenReq(resp);
		StringBuilder sb = new StringBuilder();
		sb.append(URL_ACCESS_TOKEN).append("appid=")
				.append(DemoApplication.WX_APP_ID).append("&secret=")
				.append(DemoApplication.WX_APP_SECRET).append("&code=")
				.append(code).append("&grant_type=authorization_code");
		NetClient.instance().getWxAccessToken(sb.toString(), req);
	}

	public void refreshAccessToken(WxAccessTokenResp resp) {
		WxAccessTokenReq req = new WxAccessTokenReq(resp);
		StringBuilder sb = new StringBuilder();
		sb.append(URL_REFRESH_TOKEN).append("appid=")
				.append(DemoApplication.WX_APP_ID)
				.append("&grant_type=refresh_token&refresh_token=REFRESH_TOKEN");
		NetClient.instance().getWxAccessToken(sb.toString(), req);
	}

	public void getWxUserInfo(WxAccessTokenResp resp) {
		WxAccessTokenReq req = new WxAccessTokenReq(resp);
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weixin.qq.com/sns/userinfo?access_token=")
				.append(DemoApplication.getIns().getWxAccessToken().access_token)
				.append("&openid=")
				.append(DemoApplication.getIns().getWxAccessToken().getOpenId());
		NetClient.instance().getWxAccessToken(sb.toString(), req);
	}*/
}
