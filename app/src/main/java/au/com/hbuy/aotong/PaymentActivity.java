package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.socks.library.KLog;
import com.tencent.mm.sdk.modelpay.PayReq;

import java.util.HashMap;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.zcw.togglebutton.ToggleButton;

import au.com.hbuy.aotong.domain.UpdateOrderBean;
import au.com.hbuy.aotong.domain.WeixinPayBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.PayResult;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WXManager;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAliPayCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespOrderUpdateCallback;
import au.com.hbuy.aotong.utils.okhttp.RespWeixinCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 * 选择付款方式界面
 */
public class PaymentActivity extends BaseActivity implements View.OnClickListener {
    // 商户PID
    public static final String PARTNER = "2088911693107483";
    // 商户收款账号
    public static final String SELLER = "aotongxinxikeji@gmail.com";
    /*支付宝支付结果码*/
    private static final String PAY_OK = "9000";// 支付成功
    private static final String PAY_WAIT_CONFIRM = "8000";// 交易待确认
    private static final String PAY_NET_ERR = "6002";// 网络出错
    private static final String PAY_CANCLE = "6001";// 交易取消
    private static final String PAY_FAILED = "4000";// 交易失败
    private static final int SDK_PAY_FLAG = 1;

    private ImageView mBack;
    public static final int FROM_SELECT_NATION = 1;
    private TextView mOrderMoney, mOrderName, mMoneyTv, mNumTv;    //余额
    private Button mPayment;
    private String mNo, mMoney, mNum, mPrice;
    private Intent mIntent;
    private RadioGroup mRadioGroup;
    private int mPayStyle = 0;
    private CustomProgressDialog progressDialog;
    ToggleButton toolbarBottom;
    private PaymentActivity mActivity = PaymentActivity.this;
    private String mCoupon_id = "0", mUse_balance = "0", mCoupon = "0", mbalance = "0"; //判断开关是否开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        mNo = intent.getStringExtra("no");
        mMoney = intent.getStringExtra("money");
        mNum = intent.getStringExtra("num");
        mCoupon = intent.getStringExtra("coupon");
        mbalance = intent.getStringExtra("Use_balance");
        mPrice = intent.getStringExtra("price");
        initView();

        if ("1".equals(intent.getStringExtra("type"))) {
            //转运单
            mRadioGroup.check(R.id.rb_weixin);
            mPayStyle = 0;
        } else {
            //代购 代付单
          //  findViewById(R.id.rb_weixin).setVisibility(View.GONE);
            findViewById(R.id.rb_zfb).setVisibility(View.GONE);
            mRadioGroup.check(R.id.rb_other);
            mPayStyle = 2;
        }
    }

    private void initView() {
        KLog.d(Thread.currentThread());
        mIntent = getIntent();
        mBack = (ImageView) findViewById(R.id.iv_back);
        mMoneyTv = (TextView) findViewById(R.id.tv_money);
        toolbarBottom = (ToggleButton) findViewById(R.id.toolbar_bottom);
        mOrderName = (TextView) findViewById(R.id.tv_order_name);
        mOrderMoney = (TextView) findViewById(R.id.tv_order_money);
        mNumTv = (TextView) findViewById(R.id.tv_num);
        mOrderMoney = (TextView) findViewById(R.id.tv_order_money);
        mPayment = (Button) findViewById(R.id.bt_ok_payment);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_group);

        mOrderName.setText(mNo);
        mOrderMoney.setText("¥" + StringUtils.keepDouble(mPrice));
        mMoneyTv.setText("账号余额: ¥" + StringUtils.keepDouble(mMoney));
        KLog.d(mNum);
        if (StringUtils.toInt(mCoupon) > 0) {
            //已使用优惠卷
            mNumTv.setText("已使用优惠卷");
        } else {
            mNumTv.setText(mNum + "张可用");
        }
        mNumTv.setOnClickListener(this);
        if (StringUtils.toInt(mbalance) > 0) {
            toolbarBottom.setToggleOn();
        } else {
            toolbarBottom.setToggleOff();
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = findViewById(checkedId);
                if (!checkView.isPressed()) {
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_weixin:
                        mPayStyle = 0;
                        break;
                    case R.id.rb_zfb:
                        mPayStyle = 1;
                        break;
                    case R.id.rb_other:
                        mPayStyle = 2;
                        break;
                }
            }
        });

        mBack.setOnClickListener(this);
        mPayment.setOnClickListener(this);

        toolbarBottom.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    mUse_balance = "1";
                } else {
                    mUse_balance = "0";
                }
                if (StringUtils.toDouble(mMoney) == 0) {
                    KLog.d(StringUtils.toDouble(mMoney) + "--" + (StringUtils.toDouble(mMoney) == 0));
                    return;
                }
                updateOrder(mUse_balance, mNo, mCoupon_id);
            }
        });
    }

    public void updateOrder(String mUse_balance, String mNo, String mCoupon_id) {
        if (NetUtils.hasAvailableNet(PaymentActivity.this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("use_balance", mUse_balance);
            params.put("no", mNo);
            params.put("coupon_id", mCoupon_id);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.UPDATEORDER, params, new RespOrderUpdateCallback<UpdateOrderBean>(mActivity, progressDialog) {
                @Override
                public void onSuccess(UpdateOrderBean updateOrderBean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    String money = StringUtils.keepDouble(updateOrderBean.getReal_money());
                   /* if (StringUtils.toDouble(money) == 0) {
                        findViewById(R.id.rb_other).setVisibility(View.GONE);
                    }*/
                    mMoneyTv.setText("账号余额: ¥" + StringUtils.keepDouble(updateOrderBean.getUser_balance()));
                    mOrderMoney.setText("¥" + money);
                    mPrice = money;
                   /* if ("0".equals(updateOrderBean.getCoupon_id())) {
                        ShowToastUtils.toast(mActivity, "使用优惠券失败");
                    } else if ("0".equals(updateOrderBean.getUse_balance())) {
                        ShowToastUtils.toast(mActivity, "没有使用余额");
                    }*/
                }

                @Override
                public void onFail(String status) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if ("-1".equals(status)) {
                        ShowToastUtils.toast(mActivity, "订单过期");
                    } else if ("-2".equals(status)) {
                        ShowToastUtils.toast(mActivity, "订单状态不对");
                    }
                }

                @Override
                public void onBusiness() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, "失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_ok_payment:
                toSave();
                break;
            case R.id.tv_num:
                Intent i = new Intent(this, MyCouponActivity.class);
                i.putExtra("type", "4");
                i.putExtra("no", mNo);
                i.putExtra("use_balance", mUse_balance);
                startActivityForResult(i, 1);
                break;
        }
    }

   /* private void backPayment() {
        Intent i = new Intent(this, WaitPaymentActivity.class);
        i.putExtra("style", 6);
        startActivity(i);
        finish();
    }*/

    private void toSave() {
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("no", mNo);
            if (mPayStyle == 0) {
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.weixinPay, params, new RespWeixinCallback<WeixinPayBean>(this) {
                    @Override
                    public void onSuccess(WeixinPayBean weixinPayBean) {
                        //weixin payment
                        PayReq req = new PayReq();
                        //req 配置参数
                        req.appId = ConfigConstants.APP_ID;
                        req.sign = weixinPayBean.getSign();
                        req.nonceStr = weixinPayBean.getNoncestr();
                        req.prepayId = weixinPayBean.getPrepayid();
                        req.timeStamp = weixinPayBean.getTimestamp();
                        req.partnerId = ConfigConstants.APP_WEIXIN_ID;
                        req.packageValue = "Sign=WXPay";
                        req.extData = mNo;
                        KLog.d(weixinPayBean.toString());
                        KLog.d(req);
                        WXManager.instance().sendReq(req);
                        AppUtils.stopProgressDialog(progressDialog);
                    }

                    @Override
                    public void onCreateAccount(String status) {
                        AppUtils.stopProgressDialog(progressDialog);
                       /* -4	订单超时关闭
                                -3	订单已取消
                                -2	订单已支付
                                -1	订单不存在或无效
                        0	支付异常*/
                        if ("-4".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单超时关闭");
                        } else if ("-3".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已取消");
                        } else if ("-2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已支付");
                        } else if ("-1".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单不存在或无效");
                        } else if ("2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "余额支付成功", 1);
                            Intent intent = new Intent(PaymentActivity.this, PaySucceedActivity.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("no", mNo);
                            startActivity(intent);
                            finish();
                        } else if ("0".equals(status)) {
                            ShowToastUtils.toast(mActivity, "支付异常");
                        }
                    }

                    @Override
                    public void onFail(WeixinPayBean weixinPayBean) {
                        ShowToastUtils.toast(PaymentActivity.this, "支付异常");
                        AppUtils.stopProgressDialog(progressDialog);
                    }
                });
            } else if (mPayStyle == 1) {
                //支付宝
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.aliPay, params, new RespAliPayCallback(this) {

                    @Override
                    public void onSuccess(final String msg) {
                        KLog.d();
                        AppUtils.stopProgressDialog(progressDialog);
                        if (null != msg) {
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // 构造PayTask 对象
                                    PayTask alipay = new PayTask(PaymentActivity.this);
                                    // 调用支付接口，获取支付结果
                                    String result = alipay.pay(msg, true);
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }
                    }

                    @Override
                    public void onFail(String status) {
                        AppUtils.stopProgressDialog(progressDialog);
                       /* -3	订单超时关闭
                                -2	订单已取消
                                -1	订单已支付
                        0	订单不存在或无效*/
                        if ("-3".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单超时关闭");
                        } else if ("-2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已取消");
                        } else if ("-1".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已支付");
                        } else if ("0".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单不存在或无效");
                        } else if ("2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "余额支付成功", 1);
                            Intent intent = new Intent(PaymentActivity.this, PaySucceedActivity.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("no", mNo);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
               /* *//**
                 * 完整的符合支付宝参数规范的订单信息
                 *//*
                String orderInfo = getOrderInfo("test1000000000000049-运费支付", "爱去买Hbuy包裹运费支付", "0.01", "1000000000000049");
                String sign = "IXyl1ANKdzAgRvgAYBOxvQWijPY%2FbzAapffPluobQJt6Fb%2FLEjpZcAEchEeo1ddqfacUdBqoxjHyYiFPx%2FLJ%2BpI3xrouX05JFQRW29%2BkUS67TehlFlKe2pLYlWIdSyoUQqBWgwTcqFduZysx3PJGGWDJRO8jnqsOgE8M9hgQfiM%3D";
                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
                KLog.d(sign);*/
            } else if (mPayStyle == 2) {
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.otherPay, params, new RespModifyMsgCallback(this) {
                    @Override
                    public void onSuccess() {
                        AppUtils.stopProgressDialog(progressDialog);
                    }

                    @Override
                    public void onBusiness(String status) {
                        AppUtils.stopProgressDialog(progressDialog);
                        if ("1".equals(status)) {
                            Intent i = new Intent(mActivity, PaymentOtherActivity.class);
                            i.putExtra("no", mNo);
                            i.putExtra("money", mPrice);
                            startActivityForResult(i, 2);
                        } else if ("2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "余额支付成功", 1);
                            Intent intent = new Intent(PaymentActivity.this, PaySucceedActivity.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("no", mNo);
                            startActivity(intent);
                            finish();
                        }
                    }

                    public void onFail(String status) {
                        AppUtils.stopProgressDialog(progressDialog);
                        if ("0".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单不存在或无效");
                        } else if ("-1".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已支付");
                        } else if ("-2".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单已取消");
                        } else if ("-3".equals(status)) {
                            ShowToastUtils.toast(mActivity, "订单超时关闭");
                        }
                    }
                });
            }
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d(requestCode + "--" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (null != data) {
                    //使用优惠卷
                    mCoupon_id = data.getStringExtra("id");
                    mPrice = data.getStringExtra("money");
                    mOrderMoney.setText(mPrice);
                    if ("0".equals(mCoupon_id)) {
                        //没有使用优惠卷
                        mNumTv.setText(mNum + "张可用");
                    } else {
                        mNumTv.setText("已使用优惠卷");
                    }
                }
            } else if (requestCode == 2) {
                KLog.d();
                //其他付款方式来关闭该界面
                Intent intent = new Intent(PaymentActivity.this, PaySucceedActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            KLog.d(msg.toString());
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    KLog.d(payResult);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent(PaymentActivity.this, PaySucceedActivity.class);
                                intent.putExtra("type", "1");
                        intent.putExtra("no", mNo);
                                startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            //   Toast.makeText(PayDemoActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            // Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


   /* private String getOrderInfo(String subject, String body, String price, String outTradeNo) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    private String getOrderInfo(String subject, String body, String price, String outTradeNo) {
        // 参数编码， 固定值
        String orderInfo = "_input_charset=\"utf-8\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"20m\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://192.168.99.132:8080/notify/pay" + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

        // 签约合作者身份ID
        orderInfo += "&partner=" + "\"" + PARTNER + "\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
