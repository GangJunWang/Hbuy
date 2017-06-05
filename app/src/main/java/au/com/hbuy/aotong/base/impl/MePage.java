package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import au.com.hbuy.aotong.AboutHbuyActivity;
import au.com.hbuy.aotong.AddressRepoActivity;
import au.com.hbuy.aotong.HandingActivity;
import au.com.hbuy.aotong.HistoryPkgActivity;
import au.com.hbuy.aotong.MainActivity;
import au.com.hbuy.aotong.MessageManagerActivity;
import au.com.hbuy.aotong.MyCouponActivity;
import au.com.hbuy.aotong.PersonalDataActivity;
import au.com.hbuy.aotong.PreSettingActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.ReceiverAddressActivity;
import au.com.hbuy.aotong.SecurityCenterActivity;
import au.com.hbuy.aotong.TransferActivity;
import au.com.hbuy.aotong.WaitPaymentActivity;
import au.com.hbuy.aotong.WaitPlaceOrderActivity;
import au.com.hbuy.aotong.base.BaseMainPage;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.BuyWaitPayBean;
import au.com.hbuy.aotong.domain.MeBean;
import au.com.hbuy.aotong.listener.AppBarStateChangeListener;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ScreenUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import au.com.hbuy.aotong.view.BadgeView;
import au.com.hbuy.aotong.view.CircleImageView;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.OverScrollView;
import au.com.hbuy.aotong.view.gaussimg.StackBlurManager;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class MePage extends BaseMainPage implements View.OnClickListener, OverScrollView.OverScrollListener, OverScrollView.OverScrollTinyListener {
    ImageView mMyMessage;
    CircleImageView mCivImg, mCivImgLittle;
    TextView mUsername;
    ImageView mPhone, mTopBg;
    ImageView mEmail;
    ImageView mWeixin;
    private String avatar;
    private TextView mTransfer, mInHandle, mSubmit, mWaitPayment, tv_balance, tv_coupon_num;
    BadgeView badge1;
    BadgeView badge2;
    BadgeView badge3;
    BadgeView badge4;
    private View mView;
    private Toolbar toolbar;
    private CustomProgressDialog progressDialog;
    private StackBlurManager _stackBlurManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    public MePage(AppCompatActivity activity) {
        super(activity);
    }
    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.me_page, null);
        mView.findViewById(R.id.iv_my_message).setOnClickListener(this);
        mCivImg = (CircleImageView) mView.findViewById(R.id.civ_img);
        mCivImgLittle = (CircleImageView) mView.findViewById(R.id.civ_img_little);
        mUsername = (TextView) mView.findViewById(R.id.tv_username);
        mPhone = (ImageView) mView.findViewById(R.id.iv_phone);
        mEmail = (ImageView) mView.findViewById(R.id.iv_email);
        mWeixin = (ImageView) mView.findViewById(R.id.iv_weixin);
        mTopBg = (ImageView) mView.findViewById(R.id.iv_top_bg);
        tv_balance = (TextView) mView.findViewById(R.id.tv_balance);
        tv_coupon_num = (TextView) mView.findViewById(R.id.tv_coupon_num);
        mView.findViewById(R.id.iv_setting).setOnClickListener(this);

        mTransfer = (TextView) mView.findViewById(R.id.tv_start_transfer);
        mTransfer.setOnClickListener(this);

        mInHandle = (TextView) mView.findViewById(R.id.tv_in_hand);
        mInHandle.setOnClickListener(this);

        mSubmit = (TextView) mView.findViewById(R.id.tv_to_submit);
        mSubmit.setOnClickListener(this);

        mWaitPayment = (TextView) mView.findViewById(R.id.tv_wait_payment);
        mWaitPayment.setOnClickListener(this);
        toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) mView.findViewById(R.id.collapsing_toolbar);
        mView.findViewById(R.id.tv_work_order).setOnClickListener(this);
        mView.findViewById(R.id.tv_pkg).setOnClickListener(this);
        mView.findViewById(R.id.tv_order).setOnClickListener(this);
        mView.findViewById(R.id.layout_add).setOnClickListener(this);
        mView.findViewById(R.id.layout_repo).setOnClickListener(this);
        mView.findViewById(R.id.layout_transfer).setOnClickListener(this);
        mView.findViewById(R.id.tv_security_center).setOnClickListener(this);
        mView.findViewById(R.id.tv_online_customer_service).setOnClickListener(this);
        mView.findViewById(R.id.tv_online_customer_hbuy).setOnClickListener(this);
        mView.findViewById(R.id.coupon_layout).setOnClickListener(this);
        mView.findViewById(R.id.iv_setting).setOnClickListener(this);
      /*  OverScrollView layout = (OverScrollView) mView.findViewById(R.id.layout);
        layout.setOverScrollListener(this);
        layout.setOverScrollTinyListener(this);
        layout.setOverScrollTrigger(60);*/
        appBarLayout = (AppBarLayout) mView.findViewById(R.id.appbar);

        flContent.addView(mView);
    }

    @Override
    public void initData() {
        String username = SharedUtils.getString(mActivity, "username", "");
        if (username.equals("")) {
            mUsername.setText("没有用户名哦");
        } else
            mUsername.setText(username);

        toolbar.setTitle("");
        mActivity.setSupportActionBar(toolbar);

        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(mActivity, R.color.default_color));
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if( state == State.EXPANDED ) {
                    //展开状态
                    mCivImgLittle.setVisibility(View.GONE);
                }else if(state == State.COLLAPSED){
                    //折叠状态
                    mCivImgLittle.setVisibility(View.VISIBLE);
                }else {
                    //中间状态
                    mCivImgLittle.setVisibility(View.VISIBLE);
                }
            }
        });
        getData();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(ScreenUtils.getScreenWidth(mActivity), h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
    private void getData() {
        avatar = SharedUtils.getString(mActivity, "avatar", "");
        String weixin = SharedUtils.getString(mActivity, "unionid", "");
        String phone = SharedUtils.getString(mActivity, "phone", "");
        String email = SharedUtils.getString(mActivity, "email", "");
        String country = SharedUtils.getString(mActivity, "country", "");
        String ranktype = SharedUtils.getString(mActivity, "ranktype", "");
        if (null != avatar && !avatar.equals("")) {
            Picasso.with(mActivity)
                    .load(avatar)
                    .placeholder(R.drawable.hbuy)
                    .error(R.drawable.hbuy)
                    .fit()
                    .tag(this)
                    .into(mCivImg);

            if (mCivImgLittle.getVisibility() == View.VISIBLE) {
                Picasso.with(mActivity)
                        .load(avatar)
                        .placeholder(R.drawable.hbuy)
                        .error(R.drawable.hbuy)
                        .fit()
                        .tag(this)
                        .into(mCivImgLittle);
            }
            ImageLoader.getInstance().displayImage(avatar, mCivImgLittle,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                                   /* String message = null;
                                    switch (failReason.getType()) {
                                        case IO_ERROR:
                                            message = "下载错误";
                                            break;
                                        case DECODING_ERROR:
                                            message = "图片无法显示";
                                            break;
                                        case NETWORK_DENIED:
                                            message = "网络有问题，无法下载";
                                            break;
                                        case OUT_OF_MEMORY:
                                            message = "图片太大无法显示";
                                            break;
                                        case UNKNOWN:
                                            message = "未知的错误";
                                            break;
                                    }*/
                        }
                        @Override
                        public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
                            KLog.d(loadedImage);
                            int width = ScreenUtils.getScreenWidth(mActivity);
                            _stackBlurManager = new StackBlurManager(zoomBitmap(loadedImage, width, width));
                            mTopBg.setImageBitmap( _stackBlurManager.process(5));
                        }
                    });
        }

        if (phone.equals("")) {
            mPhone.setImageResource(R.drawable.phone_hint_img_default);
        } else {
            mPhone.setImageResource(R.drawable.phone_hint_img);
        }
        if (weixin.equals("")) {
            mWeixin.setImageResource(R.drawable.weixin_hint_img_default);
        } else {
            mWeixin.setImageResource(R.drawable.weixin_hint_img);
        }
        if (email.equals("")) {
            mEmail.setImageResource(R.drawable.email_hint_img_default);
        } else {
            mEmail.setImageResource(R.drawable.email_hint_img);
        }

        if (NetUtils.hasAvailableNet(mActivity)) {
            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETMEDATA, new RespTakeMessageCallback<MeBean>(mActivity, null) {
                        @Override
                        public void onSuccess(MeBean meBean) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != meBean) {
                                tv_coupon_num.setText(meBean.getValid_coupon() + "张");
                                tv_balance.setText("￥" + meBean.getUser_balance());

                                String pos1 = meBean.getPacking();
                                int po1 = StringUtils.toInt(pos1);
                                if (po1 > 0) {
                                    mTransfer.setPadding(20, 0, 0, 0);
                                    if (null == badge1) {
                                        badge1 = new BadgeView(mActivity, mTransfer);
                                        badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                                        badge1.setBadgeMargin(80, 0);
                                        badge1.setTextSize(10);
                                    }
                                    if (po1 > 99) {
                                        badge1.setText("99+");
                                    } else {
                                        badge1.setText(pos1);
                                    }
                                    badge1.show();
                                } else {
                                    if (null != badge1) {
                                        badge1.hide();
                                    }
                                }

                                String pos2 = meBean.getDealing();
                                int po2 = StringUtils.toInt(pos2);
                                if (po2 > 0) {
                                    mInHandle.setPadding(20, 0, 0, 0);
                                    if (null == badge2) {
                                        badge2 = new BadgeView(mActivity, mInHandle);
                                        badge2.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                                        badge2.setBadgeMargin(80, 0);
                                        badge2.setTextSize(10);
                                    }
                                    if (po2 > 99) {
                                        badge2.setText("99+");
                                    } else {
                                        badge2.setText(pos2);
                                    }
                                    badge2.show();
                                } else {
                                    if (null != badge2) {
                                        badge2.hide();
                                    }
                                }

                                String pos3 = meBean.getWait_order();
                                int po3 = StringUtils.toInt(pos3);
                                if (po3 > 0) {
                                    mSubmit.setPadding(30, 0, 0, 0);
                                    if (null == badge3) {
                                        badge3 = new BadgeView(mActivity, mSubmit);
                                        badge3.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                                        badge3.setBadgeMargin(80, 0);
                                        badge3.setTextSize(10);
                                    }
                                    if (po3 > 99) {
                                        badge3.setText("99+");
                                    } else {
                                        badge3.setText(pos3);
                                    }
                                    badge3.show();
                                } else {
                                    if (null != badge3) {
                                        badge3.hide();
                                    }
                                }

                                String pos4 = meBean.getWait_pay();
                                int po4 = StringUtils.toInt(pos4);
                                if (po4 > 0) {
                                    if (null == badge4) {
                                        badge4 = new BadgeView(mActivity, mWaitPayment);
                                        badge4.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                                        badge4.setBadgeMargin(80, 0);
                                        badge4.setTextSize(10);
                                    }
                                    if (po4 > 99) {
                                        badge4.setText("99+");
                                    } else {
                                        badge4.setText(pos4);
                                    }
                                    badge4.show();
                                } else {
                                    if (null != badge4) {
                                        badge4 .hide();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFail(MeBean meBean) {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "获取角标数据失败", 3);
                        }
                    });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint), 2);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_my_message:
                AppUtils.showActivity(mActivity, MessageManagerActivity.class);
                break;
            case R.id.coupon_layout:
                AppUtils.showActivity(mActivity, MyCouponActivity.class);
                break;
            case R.id.iv_setting:
                Intent intent = new Intent();
                intent.setClass(mActivity, PersonalDataActivity.class);
                mActivity.startActivityForResult(intent, MainActivity.FROM_MODIFY_PERSONAL_DATA);
                break;
            case R.id.tv_start_transfer:
                AppUtils.showActivity(mActivity, TransferActivity.class);
                break;
            case R.id.tv_in_hand:
                Intent i = new Intent(mActivity, HandingActivity.class);
                i.putExtra("id", "1");
                mActivity.startActivity(i);
                break;
            //代下单
            case R.id.tv_to_submit:
                AppUtils.showActivity(mActivity, WaitPlaceOrderActivity.class);
                break;
            case R.id.tv_wait_payment:
                //等待付款订单列表
                Intent waitList = new Intent(mActivity, WaitPaymentActivity.class);
                waitList.putExtra("style", "7");
                mActivity.startActivity(waitList);
                break;
            case R.id.tv_work_order:
                Intent ii = new Intent(mActivity, HandingActivity.class);
                ii.putExtra("id", "2");//
                mActivity.startActivity(ii);
                break;
            case R.id.tv_pkg:
                AppUtils.showActivity(mActivity, HistoryPkgActivity.class);
                break;
            case R.id.tv_order:
                //历史订单列表
                Intent orderlist = new Intent(mActivity, WaitPaymentActivity.class);
                orderlist.putExtra("style", "8");
                mActivity.startActivity(orderlist);
                break;
            case R.id.layout_add:
                AppUtils.showActivity(mActivity, ReceiverAddressActivity.class);
                break;
            case R.id.layout_repo:
                AppUtils.showActivity(mActivity, AddressRepoActivity.class);
                break;
            case R.id.layout_transfer:
                AppUtils.showActivity(mActivity, PreSettingActivity.class);
                break;
            case R.id.tv_security_center:
                mActivity.startActivityForResult(new Intent(mActivity, SecurityCenterActivity.class), SecurityCenterActivity.QUITAPP);
                break;
            case R.id.tv_online_customer_service:
                AppUtils.goChat(mActivity);
                break;
            case R.id.tv_online_customer_hbuy:
                AppUtils.showActivity(mActivity, AboutHbuyActivity.class);
                break;
           /* case R.id.civ_img:
                View v = LayoutInflater.from(mActivity).inflate(R.layout.fragment_image_detail, null);
                final PopupWindow popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                ImageView imageView = (ImageView)v.findViewById(R.id.image);
                KLog.d(avatar);
                if (null != imageView && null != avatar && !"".equals(avatar)) {
                    Picasso.with(mActivity)
                            .load(avatar)
                            .placeholder(R.drawable.hbuy)
                            .error(R.drawable.hbuy)
                            .fit()
                            .tag(this)
                            .into(imageView);
                }
                popupWindow.setFocusable(true);
                popupWindow.setWidth(600);
                popupWindow.setHeight(800);
                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
                break;*/
        }
    }

    @Override
    public void headerScroll() {
        getData();
    }

    @Override
    public void footerScroll() {

    }
    @Override
    public void scrollDistance(int tinyDistance, int totalDistance) {

    }
    @Override
    public void scrollLoosen() {

    }
}
