package au.com.hbuy.aotong.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TakePhotoOptions;
import com.mylhyl.superdialog.SuperDialog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.UrlSafeBase64;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.apache.log4j.chainsaw.Main;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import au.com.hbuy.aotong.AddPkgActivity;
import au.com.hbuy.aotong.AddressRepoActivity;
import au.com.hbuy.aotong.BuildConfig;
import au.com.hbuy.aotong.CompleteDataActivity;
import au.com.hbuy.aotong.GetAddressRepoActivity;
import au.com.hbuy.aotong.ImagePagerActivity;
import au.com.hbuy.aotong.LoginActivity;
import au.com.hbuy.aotong.MainActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.SecurityCenterActivity;
import au.com.hbuy.aotong.UserOrderGuideActivity;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.domain.UserBean;
import au.com.hbuy.aotong.domain.UserInfoBen;
import au.com.hbuy.aotong.domain.WaitPlaceBean;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespClassTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class AppUtils {

    public static int getVersionCode(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    public static String getVersionName(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return "";
    }

    public static String getTextStr(View view) {
        if (view instanceof EditText || view instanceof TextView) {
            return ((TextView) view).getText().toString().trim();
        }
        return "";
    }

    public static void showActivity(Activity aty, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    public static void showActivityAddParams(Intent intent, Activity aty, Class<?> cls) {
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    public static List<WaitPlaceBean> WaitPlaceOrderSort(List<Order> lists) {
        //分类
        String ids = "";
        for (Order o : lists) {
            String newId = o.getDestination_country_id();
            KLog.d(newId);
            if (!ids.contains(newId)) {
                ids += newId + ",";
            }
        }

        KLog.d(ids);
        if ("".equals(ids)) {
            return null;
        }
        List<String> idlist = Arrays.asList(ids.substring(0, ids.length() - 1).split(","));
        List<WaitPlaceBean> waitPlaceBeens = new ArrayList<WaitPlaceBean>();
        for (String id : idlist) {
            WaitPlaceBean bean = new WaitPlaceBean();
            bean.setDestination_country_id(id);
            List<Order> list = new ArrayList<Order>();
            for (Order o : lists) {
                if (id.equals(o.getDestination_country_id())) {
                    list.add(o);
                }
            }
            bean.setList(list);
            waitPlaceBeens.add(bean);
        }
        return waitPlaceBeens;
    }

    public static List getClassifys(List<Order> orders, boolean isInterPkg) {
        List interPkg = new ArrayList();   //国际包裹
        List intandPkg = new ArrayList();  //国内包裹
        for (Order o : orders) {
            if (o.getIs_international().equals("1")) {
                //国际包裹
                interPkg.add(o);
            } else {
                //国内包裹
                intandPkg.add(o);
            }
        }
        if (isInterPkg) {
            return interPkg;
        } else {
            return intandPkg;
        }
    }

    /**
     * 开始加载
     */
    public static CustomProgressDialog startProgressDialog(Activity context, String message, CustomProgressDialog progressDialog) {
        if (null == progressDialog) {
            if (StringUtils.isEmpty(message)) {
                message = "数据加载中";
            }
            progressDialog = CustomProgressDialog.createDialog(context, message);
        }
        KLog.d(context);
        if (!context.isFinishing())
        progressDialog.show();
        return progressDialog;
    }

    public static void goToDownload(Activity mainActivity, String url) {
      /*  Intent intent = new Intent(mainActivity, DownloadService.class);
        intent.putExtra(ConfigConstants.APK_DOWNLOAD_URL, url);
        mainActivity.startService(intent);*/
        //访问
       /*
        Intent i = new Intent(mainActivity, FaqActivity.class);
        i.putExtra("url", url);
        mainActivity.startActivity(i);*/
        KLog.d(url);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mainActivity.startActivity(intent);
    }

    /**
     * 停止加载
     */
    public static void stopProgressDialog(CustomProgressDialog progressDialog) {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void goChat(final Activity mActivity) {
      /*
       RongIM.getInstance().setCurrentUserInfo(new UserInfo(uid, null,
                Uri.parse(avatar)));
       RongIM.getInstance().setCurrentUserInfo(new UserInfo(kf_id, null,
                Uri.parse("http://user.hbuy.com.au/public/images/server.jpg")));*/

       /* RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String uid) {
                return new UserInfo(uid, null,
                        Uri.parse(avatar));
            }
        }, true);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String kf_id) {
                KLog.d(kf_id);
                return new UserInfo(kf_id, null,
                        Uri.parse("http://user.hbuy.com.au/public/images/server.jpg"));
            }
        }, true);*/
        if (NetUtils.hasAvailableNet(mActivity)) {
            //go login
            final CustomProgressDialog progressDialog = AppUtils.startProgressDialog(mActivity, "", null);
            HashMap params = new HashMap();
            params.put("id", "-" + SharedUtils.getString(mActivity, "kf_id", ""));
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GET_COMUSER, params, new RespAddTCallback<UserBean>(mActivity) {
                @Override
                public void onSuccess(UserBean userBean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if (null == userBean) {
                        ShowToastUtils.toast(mActivity, "获取客户信息失败", 2);
                        return;
                    }
                    SharedUtils.putString(mActivity, "kfname", userBean.getUsername());

                    final String uid = SharedUtils.getString(mActivity, "uid", "");
                    final String avatar = SharedUtils.getString(mActivity, "avatar", "");
                    if (null != RongIM.getInstance()) {
                        final List<UserInfoBen> userInfoBenList = new ArrayList<>();
                        userInfoBenList.add(new UserInfoBen(uid, SharedUtils.getString(mActivity, "username", ""), avatar));
                        userInfoBenList.add(new UserInfoBen(userBean.getId(), userBean.getUsername(), userBean.getAvator()));
                        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                            @Override
                            public UserInfo getUserInfo(String s) {
                                for (UserInfoBen userinfo : userInfoBenList) {
                                    if (userinfo.getUserId().equals(s)) {
                                        KLog.d();
                                        return new UserInfo(userinfo.getUserId(), userinfo.getUserName(), Uri.parse(userinfo.getImageUrl()));
                                    }
                                }
                                return null;
                            }

                        }, true);
                        KLog.d(uid + avatar);
                        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(avatar)) {
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(uid, "", Uri.parse(avatar)));
                        }
                        RongIM.getInstance().startPrivateChat(mActivity, userBean.getId(), "hello word");
                    }
                }

                @Override
                public void onFail(UserBean userBean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, "获取客户id失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.net_hint), 3);
        }
    }

    public static void goChatByBuy(final Activity mActivity) {
        if (NetUtils.hasAvailableNet(mActivity)) {
            //go login
            final CustomProgressDialog progressDialog = AppUtils.startProgressDialog(mActivity, "", null);
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GET_ID, new RespAddTCallback<UserBean>(mActivity) {
                @Override
                public void onSuccess(UserBean userBean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if (null == userBean) {
                        ShowToastUtils.toast(mActivity, "获取客户信息失败", 2);
                        return;
                    }
                    SharedUtils.putString(mActivity, "kfname", userBean.getUsername());
                    final String uid = SharedUtils.getString(mActivity, "uid", "");
                    final String avatar = SharedUtils.getString(mActivity, "avatar", "");
                    if (null != RongIM.getInstance()) {
                        final List<UserInfoBen> userInfoBenList = new ArrayList<>();
                        userInfoBenList.add(new UserInfoBen(uid, SharedUtils.getString(mActivity, "username", ""), avatar));
                        userInfoBenList.add(new UserInfoBen(userBean.getId(), userBean.getUsername(), userBean.getAvator()));
                        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                            @Override
                            public UserInfo getUserInfo(String s) {
                                for (UserInfoBen userinfo : userInfoBenList) {
                                    if (userinfo.getUserId().equals(s)) {
                                        KLog.d();
                                        return new UserInfo(userinfo.getUserId(), userinfo.getUserName(), Uri.parse(userinfo.getImageUrl()));
                                    }
                                }
                                return null;
                            }

                        }, true);
                        KLog.d(uid + avatar);
                        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(avatar)) {
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(uid, "", Uri.parse(avatar)));
                        }
                        RongIM.getInstance().startPrivateChat(mActivity, userBean.getId(), "hello word");
                    }
                }

                @Override
                public void onFail(UserBean userBean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, "获取客户id失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.net_hint), 3);
        }
    }

    public static final int DELAY = 1000;
    private static long lastClickTime = 0;

    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    //判断字符串是否为json格式
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    public static void showUploadAvater(final FragmentActivity activity, final TakePhoto takePhoto, final int type) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        final Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        List<String> list = new ArrayList<>();
        list.add("拍照");
        list.add("从相册选择");
        new SuperDialog.Builder(activity)
                .setAlpha(1f)
                .setCanceledOnTouchOutside(false)
                .setItems(list, new SuperDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0:
                                if (type == 1) {
                                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                                } else {
                                    takePhoto.onPickFromCapture(imageUri);
                                }
                                break;
                            case 1:
                                if (type == 1) {
                                    takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                                } else {
                                    takePhoto.onPickFromGallery();
                                }
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .build();
    }


    private static void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400;
        int width = 600;
        int height = 600;
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(false)
                .create();

        takePhoto.onEnableCompress(config, true);

//        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
//        builder.setWithOwnGallery(false);
//        takePhoto.setTakePhotoOptions(builder.create());
    }

    private static CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(500).setAspectY(500);
        builder.setOutputX(500).setOutputY(500);
        builder.setWithOwnCrop(true);
        return builder.create();
    }

    /*  private static final int FLAG_CHOOSE_IMG = 2;
      private static final int CAMERA_WITH_DATA = 1;
      private static void startCamera(Activity activity) {
          //检查权限
          if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
              KLog.d();
              //如果没有授权，则请求授权
              ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, ConfigConstants.PERMISSIONS_REQUEST_CALL_CAMERA);
          } else {
              //有授权，直接开启摄像头
              KLog.d(Build.VERSION.SDK_INT + "--" + Build.VERSION.RELEASE);

              //适配7.0
              if (Build.VERSION.SDK_INT > 23) {
                  File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                  if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                  Uri imageUri = FileProvider.getUriForFile(activity, "com.jph.takephoto.fileprovider", file);//通过FileProvider创建一个content类型的Uri
                  Intent intent = new Intent();
                  intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                  intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                  intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                  activity.startActivityForResult(intent,CAMERA_WITH_DATA);
              } else {
                  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                          Environment.getExternalStorageDirectory(), ConfigConstants.TMP_PATH)));
                  activity.startActivityForResult(intent, CAMERA_WITH_DATA);
              }
          }
      }
      private static void startAlbum(Activity activity) {
          Intent intent = new Intent();
          intent.setAction(Intent.ACTION_PICK);
          intent.setType("image*//*");
        activity.startActivityForResult(intent, FLAG_CHOOSE_IMG);
    }
*/
    public static <T> T getSaveBeanClass(String saveStr, Class<T> t) {
        KLog.d(saveStr);
        String string = CacheLoaderManager.getInstance().loadString(saveStr);
        KLog.d(string);
        if (null != string) {
            /*if ("UTF".equals(string.substring(0, 3))) {
                string = string.substring(5, string.length());
            }*/
            try {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                return gson.fromJson(string, t);
            } catch (Exception e) {
                e.toString();
            }
        }
        return null;
    }

    /**
     * 进入图片详情页
     *
     * @param array    图片数组
     * @param position 角标
     */
    public static void enterPhotoDetailed(Activity activity, String[] array, int position) {
        Intent intent = new Intent(activity, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, array);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        activity.startActivity(intent);
    }

    //hbuy_goods/[uid]/[time[20160304]]_[随机字符5位][MD5源文件名6位].后缀
    public static String getFileName(String uid, String strName) {
        String name = "hbuy_goods/" + uid + "/" + getStringDate() + "_" + StringUtils.getRandomString(5) + Md5Utils.MD5(strName).substring(26, 32);
        return name;
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
            throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }

    public static byte[] getImageFromURL(String urlPath) {
        byte[] data = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            // conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            is = conn.getInputStream();
            if (conn.getResponseCode() == 200) {
                data = readInputStream(is);
            } else {
                data = null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return data;
    }

    public static void cookieInvalid(Activity mContext) {
        SharedUtils.putBoolean(mContext, "use_is_login", false);
        CacheLoaderManager.getInstance().clear();   //清空缓存
        RongIM.getInstance().logout();
        MobclickAgent.onProfileSignOff();  //退出友盟的账户统计
        JPushInterface.setAlias(mContext, "", new TagAliasCallback() {          //极光推退出
            @Override
            public void gotResult(int i, String s, Set<String> set) {
            }
        });

        ShowToastUtils.toast(mContext, "为了你的信息安全,请重新登录.");
        AppUtils.showActivity(mContext, LoginActivity.class);
        mContext.finish();
    }

    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void getUseInfo(final Activity activity, final int type) {
        if (NetUtils.hasAvailableNet(activity)) {
            // progressDialog = AppUtils.startProgressDialog(this, "正在登陆", progressDialog);
            ApiClient.getInstance(activity.getApplicationContext()).postForm(ConfigConstants.GETUSEINFO, new RespClassTCallback<User>(activity) {
                @Override
                public void onSuccess(User user) {
                    if (null != user) {
                        MobclickAgent.onProfileSignIn(user.getUid());   //统计友盟用户数据
                        KLog.d(user.getImtoken());
                        SharedUtils.putString(activity, "user_token", user.getImtoken());
                        SharedUtils.putString(activity, "uid", user.getUid());
                        SharedUtils.putString(activity, "kf_id", user.getKf_id());
                        SharedUtils.putString(activity, "city", user.getCity());
                        SharedUtils.putString(activity, "gender", user.getGender());
                        SharedUtils.putString(activity, "avatar", user.getAvator());
                        SharedUtils.putString(activity, "country", user.getCountry());
                        SharedUtils.putString(activity, "phone", user.getPhone());
                        SharedUtils.putString(activity, "phonecode", user.getPhonecode());
                        SharedUtils.putString(activity, "email", user.getEmail());
                        SharedUtils.putString(activity, "unionid", user.getUnionid());
                        SharedUtils.putString(activity, "ranktype", user.getIsvip());
                        String tmpName = user.getUsername();
                        if (tmpName.equals("")) {
                            AppUtils.showActivity(activity, CompleteDataActivity.class);
                        } else {
                            SharedUtils.putString(activity, "username", user.getUsername());
                            final boolean isUserGuide = SharedUtils.getBoolean(activity, "use_is_guide", false);
                            if (!isUserGuide) {
                                AppUtils.showActivity(activity, UserOrderGuideActivity.class);
                            } else {
                                Intent intent = new Intent(activity, GetAddressRepoActivity.class);
                                activity.startActivity(intent);
                                if (type == 1) {
                                    ShowToastUtils.toast(activity, "登录成功", 1);
                                } else if (type == 2) {
                                    ShowToastUtils.toast(activity, "绑定账号成功", 1);
                                } else if (type ==3) {
                                    ShowToastUtils.toast(activity, "成功", 1);
                                }
                                SharedUtils.putBoolean(activity, "use_is_login", true);
                            }
                        }
                        activity.finish();
                    }
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(activity, "获取用户资料失败", 3);
                }
            });
        } else {
            ShowToastUtils.toast(activity, activity.getString(R.string.net_hint), 3);
        }
    }

    public static String getFileName(Context context) {
        String uid = SharedUtils.getString(context, "uid", "");
        String name = "hbuy_user/avator/" + AppUtils.getStringDate() + uid + "_" + StringUtils.getRandomString(5);
        return name;
    }

    public static boolean isLetterDigit(final Activity activity, String str) {
    /*    boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
            KLog.d(isDigit + " " + isLetter);
        }*/
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = str.matches(regex);
        KLog.d(str.matches(regex) );
        if (isRight) {
            final String beform = SharedUtils.getString(activity, "code", "");

            SharedUtils.putString(activity, "code", str);
            if (beform.equals(str)) {
                KLog.d();
            } else {
                new SuperDialog.Builder((FragmentActivity) activity)
                        .setAlpha(1f)
                        .setRadius(20)
                        .setWidth(0.8f)
                        .setTitle("友情提示").setMessage("您是否需要跟踪以下快递单号:\n" + str)
                        .setPositiveButton("马上添加", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, AddPkgActivity.class);
                                intent.putExtra("code", SharedUtils.getString(activity, "code", ""));
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("我不需要", new SuperDialog.OnClickNegativeListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .build();
            }
        }
        return isRight;
    }
}
