package au.com.hbuy.aotong.utils;

import android.content.Context;

import au.com.hbuy.aotong.utils.okhttp.ApiClient;

/**
 * @description：Lib全局控制
 */
public final class ConfigConstants {
    //测试
    public static final String url = "https://user.test.hbuy.com.au/api";
    public static final String QINIU_ACCESSKEY = "HNDkC0q6aP2p8kFb_kX1G8lle3FRghtYTfH9ytvW";
    public static final String QINIU_SECRETKEY = "ajSYeKFwFZMuebyYIEBy1QVM7VCj8REIFjvbaJrO";
    public static final String QINIU_CDN = "http://oixd10e56.bkt.clouddn.com";
    public static final String QINIU_BUCKET = "test";

/* //线上地址
    public static final String url = "https://user.hbuy.com.au/api";
    public static final String QINIU_ACCESSKEY = "HNDkC0q6aP2p8kFb_kX1G8lle3FRghtYTfH9ytvW";
    public static final String QINIU_SECRETKEY = "ajSYeKFwFZMuebyYIEBy1QVM7VCj8REIFjvbaJrO";
    public static final String QINIU_CDN = "http://cdn.hbuy.com.au";
    public static final String QINIU_BUCKET = "hbuy";*/

    public static final String register_url = url + "/common/register";
    public static final String LOGINURL = url + "/common/login";
    public static final String COMPLETEDATA = url + "/user/completeinfo";
    public static final String FEEKBACK = url + "/feedback/submit";
    //客户列表
    public static final String GET_KEFU_LIST = url + "/common/get_kf_group";
    public static final String GET_ID = url + "/common/get_helpbuykf";
    public static final String GET_COMUSER = url + "/common/get_comuser";
    //得到个人资料
    public static final String GETUSEINFO = url + "/user/get_profile";
    //get address list
    public static final String ADDRESSLIST = url + "/common/getbatchcountry";
    //send phone code
    public static final String send_phone_code_url = url + "/common/phonecheck";
    //取消代购订单
    public static final String CANCELBUYORDER = url + "/helpbuy/cancel";
    //取消工单
    public static final String CANCELWORKORDER = url + "/ticket/cancel";
    //send phone code 安全界面
    public static final String SEND_PHONE_CODE_SECURITY_URL = url + "/security/send_sms_code";
    //send phone code 安全界面  绑定邮箱
    public static final String SEND_EMAIL_CODE_AUTH_SECURITY_URL = url + "/security/bindemail";
    //send phone code 安全界面  绑定手机
    public static final String SEND_PHONE_CODE_AUTH_SECURITY_URL = url + "/security/bindphone";
    //send email code 安全界面
    public static final String SEND_EMAIL_CODE_SECURITY_URL = url + "/security/send_email_code";
    //send email code 安全界面 手机验证码校验
    public static final String SEND_CHECK_PHONE_CODE_AUTH_SECURITY_URL = url + "/security/check_phone_code";
    //换绑
    public static final String SEND_CHECK_EMAIL_CODE_AUTH_SECURITY_URL = url + "/security/check_email_code";
    //重新发送验证码
    public static final String resend_phone_code_url = url + "/common/resend";
    public static final String FINDPWDCODE = url + "/common/findpwd_send";
    //手机号码注册
    public static final String PHONESIGNURL = url + "/common/phonesign";
    //找回密码第二步
    public static final String FINDPWDTWO = url + "/common/findpwd_step2";
    //找回密码第三步
    public static final String FINDPWD3 = url + "/common/findpwd_step3";
    //手机号码注册
    public static final String EMAILSIGNURL = url + "/common/emailsign";
    //email code get  注册的时候
    public static final String GETEMAILCODE = url + "/common/sendemail";
    //email code get  绑定的时候
    public static final String GETEMAILCODE_SECURITY = url + "/security/send_email_code";
    //获取所有包裹 n
    public static final String getbatchpackage_url = url + "/package/getbatchpackage";
    public static final String addpackage_url = url + "/package/addpackage";
    public static final String addAddress = url + "/useraddress/addaddress";
    public static final String getAddresses = url + "/useraddress/getbatchaddress";
    public static final String updateAddress = url + "/useraddress/putaddress";
    public static final String setDefaultAddress = url + "/useraddress/setdefault";
    public static final String deleteAddress = url + "/useraddress/deladdress";
    public static final String weixinLogin = url + "/common/wxlogin";
    public static final String modifyPersonalData = url + "/user/save";
    public static final String modifyUserData = url + "/user/save";

    //获取物流商
    public static final String GETLISTWULIU = url + "/carrier/getbatchcarriers";

    //获取物流商
    public static final String GETLISTNATION = url + "/common/getbatchcountry";
    public static final String SHARE = url + "/order/share";

    //查询物流
    public static final String TRACK_URL = "http://www.hbuy.com.au/track.html";

    //分享我们
    public static final String SHARE_IMG = "http://cdn.hbuy.com.au/wx/share/share_us_l.png";
    public static final String SHARE_URL = "http://www.hbuy.com.au";
    public static final String SHARE_TITLE = "关注Hbuy，购买国货无压力";
    public static final String SHARE_DESC = "身在海外，用外币轻松网购国货，用Hbuy就够了！";

    //微信绑定
    public static final String BOUNDWEIXIN = url + "/user/bindwx";
    //设置包裹地址
    public static final String SETPKGADDRESS = url + "/package/setaddress";

    //get 代购订单列表
    public static final String GETALLBUYLIST = url + "/helpbuy/getmyidents";

    //获取我的数据
    public static final String GETMEDATA = url + "/user/homecenter";

    //获取用户默认的设置
    public static final String GETSETTING = url + "/setting/get";

    //设置偏好
    public static final String SETSETTING = url + "/setting/submit";

    //设置偏好
    public static final String UPDATEORDER = url + "/order/update";
    //modify pwd
    public static final String MODIFYPWD = url + "/user/changepasswd";
    //首页信息
    public static final String startDetails = url + "/common/slider";
    //更新一个包裹
    public static final String updatePkg = url + "/package/update";
    //获取工单列表
    public static final String getAllworkList = url + "/ticket/getbatchtickets";
    //获取单个工单
    public static final String getItemWorkOrder = url + "/ticket/getitem";
    //获取单个代购
    public static final String getItemBUY = url + "/helpbuy/getitem";
    //确认打包
    public static final String CONFIRMPKG = url + "/ticket/confirm";
    //create order
    public static final String createOrder = url + "/order/create";
    //订单列表
    public static final String waitingPay = url + "/order/getbatchorder";

    //外币支付
    public static final String GETDETAILS = url + "/pay/getmoney";

    public static final String weixinPay = url + "/pay/wechat";
    public static final String aliPay = url + "/pay/ali";
    //获得地址详情
    public static final String getAddressDetails = url + "/useraddress/getaddress";
    //手动付款
    public static final String otherPay = url + "/pay/handwork";

    //上传凭证
    public static final String UPLOAD = url + "/pay/putproof";

    //增加代购订单
    public static final String ADDBUYORDER = url + "/helpbuy/add";

    //删除订单
    public static final String CANCELORDER = url + "/order/cancel";

    //获取单个订单
    public static final String getItemOrder = url + "/order/getitem";

    //获取消息列表  type为1时为通知消息，其他或不传为订阅消息
    public static final String GETMSGLIST = url + "/message/getmessage";

    //获取单个包裹
    public static final String getItemPkg = url + "/package/getitem";
    public static final String weixinBound = url + "/common/commit";
    public static final String submitWorkOrder = url + "/ticket/save";
    public static final String GET_FOREX = url + "/common/get_forex";

    //获取头像
    public static final String GETHEADPORTRAIT = url + "/common/getavator";

    //得到仓库地址
    public static final String getRepoAddress = url + "/user/getinfo";

    //得到仓库地址
    public static final String getRepoAddress02 = url + "/user/get_transfer_address";
    //找回密码
    public static final String findPwd = url + "/common/findpwd_step1";
    //优惠卷列表
    public static final String GETCOUPONLIST = url + "/coupon/getmycoupons";
    //提前看包裹
    public static final String aheadLookPkg = url + "/package/checknow";
    public static final String APK_DOWNLOAD_URL = "url";
    public static final String faq_url = "http://user.hbuy.com.au/home/faq";
    public static final String us_url = "http://www.hbuy.com.au/aboutus.html";
    //运算估算
    public static final String FREIGHT_URL = "http://www.hbuy.com.au/cost.html";

    //运费图
    public static final String FREIGHT_IMG_URL = "https://user.hbuy.com.au/static/freight/";

    //下载更新
    public static final String DOWNLOAD_URL = "http://user.hbuy.com.au/home/download";
    //视频简介
    public static final String SHIPING = "http://www.hbuy.com.au/";
    public static final String flow_url = "http://q.eqxiu.com/s/CypKHk";
    public static final String APP_ID = "wxd832cc9bf553d32b";
    public static final String APP_SECRET = "9896b4cd5be1c126093fcb9e48a57b12";
    public static final String APP_WEIXIN_ID = "1372720302";

//自己的环境
//public static final String QINIU_ACCESSKEY = "_5om6grksvLPnQVNJqlKKEkfEFJMf4FjdU228G6L";
//public static final String QINIU_SECRETKEY = "s9y2z8-krIVQDCIcT4ubUdmJVL8oEEh-Ui2fV91r";

    public static Context appContext;
    /**
     * 默认 SharePreferences文件名.
     */
    public static String SHARED_PATH = "app_share";
    public static String SHARED_MESSAGE_TIME = "message_time";
    /**
     * UI设计的基准宽度.
     */
    public static int UI_WIDTH = 720;
    /**
     * UI设计的基准高度.
     */
    public static int UI_HEIGHT = 1080;
    /**
     * 请求连接超时时间 产品需求:15s
     */
    public static final long CONN_TIMEOUT = 40 * 1000;
    /**
     * 响应超时时间
     */
    public static final long READ_TIMEOUT = 40 * 1000;
    /**
     * 写操作超时时间
     */
    public static final long WRITE_TIMEOUT = 40 * 1000;

   /* public static void initKJconfig(@NonNull Context mAppContext, @NonNull String sharePrefrenceName) {
        appContext = mAppContext;
        SHARED_PATH = sharePrefrenceName;
        UI_WIDTH = ScreenUtils.getScreenWidth(appContext);
        UI_HEIGHT = ScreenUtils.getScreenHeight(appContext);
        ImgCacheUtil.getInstance(mAppContext);
    }
*/

    /**
     * oauth系统key的配置列表
     */
    public static class Key {
        public static String BROKER = "374fa3ab6b1fae595db5382afe415bce";
        public static String ADMIN = "80b131d757c90282b802e3f9840bfd71";
    }

    /**
     * oauth系统APP的配置列表
     */
    public static class App {
        public static String BROKER = "app_broker";
        public static String ADMIN = "app_sales";
    }

//    *
//     * 获取校正后的时间。
//     * desc:当前手机时间加上与服务器时间差.
//     *
//     * @return 校正后的时间
//
//    public static String getCorrectionTime() {
//        Date phoneTime = new Date(System.currentTimeMillis());
//        long correctTime = phoneTime.getTime() + AbSharedUtil.getLong(ConfigConstants.appContext, "TimeInterval", 0);
//        phoneTime.setTime(correctTime);
//        return AbDateUtil.dateFormat7.format(phoneTime);
//    }
}
