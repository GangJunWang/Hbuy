package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.util.List;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.ContentData;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.WaitPlaceBean;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;

public class WaitPlaceOrderViewHolder extends BaseViewHolder<WaitPlaceBean> {
    private NestFullListView tvContent;
    private TextView mTitle;
    private Activity mActivity;

    public WaitPlaceOrderViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.item_waitplace_order);
        mTitle = $(R.id.tv_title);
        tvContent = $(R.id.lv_content);
        this.mActivity = activity;
    }

    @Override
    public void setData(final WaitPlaceBean o) {
        mTitle.setText("寄往" + StringUtils.getCountryNameFormId(o.getDestination_country_id()));
        List<Order> lists = o.getList();
        if (null != lists) {
            tvContent.setAdapter(new NestFullListViewAdapter<Order>(R.layout.item_waitplace_order_item, lists) {
                @Override
                public void onBind(int pos, final Order o, final NestFullViewHolder holder) {
                    KLog.d(o.getContent() + "---" + o.getOpen_pay());
                    holder.setText(R.id.tv_content, o.getContent());

                    holder.setText(R.id.tv_price, "价格: ¥" + StringUtils.keepDouble(o.getFreight_pay()));
                    holder.setText(R.id.tv_num, "件数:" + o.getPkg_number());
                    holder.setText(R.id.tv_weight, o.getWeight() + "kg");
                    holder.setText(R.id.tv_time, o.getTime());
                    final String img = o.getWeight_img();
                    if (null != img && !"".equals(img))
                    holder.setOnClickListener(R.id.tv_img, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          /*  KLog.d(o.getWeight_img());
                            View vv = LayoutInflater.from(mActivity).inflate(R.layout.fragment_image_detail, null);
                            final PopupWindow popupWindow = new PopupWindow(vv,
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                            popupWindow.setFocusable(true);
                            popupWindow.setAnimationStyle(R.style.popwin_anim_style);
                            ColorDrawable dw = new ColorDrawable(0x00000000);
                            popupWindow.setBackgroundDrawable(dw);
                            popupWindow.setWidth(800);
                            popupWindow.setHeight(1000);
                            ImageView imageView = (ImageView) vv.findViewById(R.id.image);
                            KLog.d(imageView);
                                Picasso.with(mActivity)
                                        .load(img)
                                        .placeholder(R.drawable.hbuy)
                                        .error(R.drawable.hbuy)
                                        .fit()
                                        .tag(this)
                                        .into(imageView);
                            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                        popupWindow.dismiss();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            popupWindow.showAtLocation(holder.getView(R.id.tv_img), Gravity.CENTER, 0, 0);*/
                            String[] tmp = new String[] {o.getWeight_img()};
                            KLog.d(tmp);
                            AppUtils.enterPhotoDetailed(mActivity, tmp, 0);
                        }
                    });

                    StringUtils.setTextAndImg(o.getCarrier_id(), null, (ImageView) holder.getView(R.id.iv_name));

                    holder.setOnClickListener(R.id.cb_select, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((CheckBox) view).isChecked()) {
                                o.setIsChecked(true);
                                KLog.d(1);
                            } else {
                                o.setIsChecked(false);
                                KLog.d(2);
                            }
                        }
                    });
                }
            });
        }
    }
}
