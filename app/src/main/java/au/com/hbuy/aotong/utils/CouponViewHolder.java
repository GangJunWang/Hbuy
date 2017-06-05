package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.CouponBean;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import butterknife.Bind;

public class CouponViewHolder extends BaseViewHolder<CouponBean> {
    private TextView tvTitle;
    private TextView tvType, mTvTypeOrder;
    private TextView tvContent;
    private TextView tvValidity;
    private TextView mTvTime;
    private TextView mTvNumber;
    private FrameLayout mLayout;
    private Context mContext;
    private ImageView mTitleType, mTypeImg;

    public CouponViewHolder(ViewGroup parent, Context mContext) {
        super(parent, R.layout.my_coupon_list_item);
        this.mContext = mContext;
        tvTitle = $(R.id.tv_title);
        tvType = $(R.id.tv_type);
        tvContent = $(R.id.tv_content);
        mTvTypeOrder = $(R.id.tv_type_order);
        mTvNumber = $(R.id.tv_number);
        tvValidity = $(R.id.tv_validity);
        mTitleType = $(R.id.iv_title_type);
        mTvTime = $(R.id.tv_time);
        mTypeImg = $(R.id.iv_type);
        mLayout = $(R.id.layout);
    }

    @Override
    public void setData(final CouponBean o) {
        String tmp = o.getIs_used();
        String ordertype = o.getOrdertype();

        if (ordertype.equals("0")) {
            //通用
            mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.coupon_bg_common));
            mTvTypeOrder.setText("通用");

            mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_common_01));
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_common_01));
            tvType.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_common_02));
            tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_common_02));

            if (o.getIs_expire().equals("1")) {
                //已过期
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_guoqi_img);
                mTypeImg.setImageResource(R.drawable.used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("1")) {
                //已使用
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.yes_used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("0")) {
                //未使用
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.no_used);
            }

        } else if (ordertype.equals("1")) {
            //转运
            mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.coupon_bg_transfer));
            mTvTypeOrder.setText("转运");

            mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_transfer_01));
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_transfer_01));
            tvType.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_transfer_02));
            tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_transfer_02));

            if (o.getIs_expire().equals("1")) {
                //已过期
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_guoqi_img);
                mTypeImg.setImageResource(R.drawable.used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("1")) {
                //已使用
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.yes_used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("0")) {
                //未使用
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.no_used);

                if (o.getIs_expire().equals("1")) {
                    //已过期
                    mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                    mTitleType.setImageResource(R.drawable.coupon_guoqi_img);
                    mTypeImg.setImageResource(R.drawable.used);

                    mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                } else if (tmp.equals("1")) {
                    //已使用
                    mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                    mTitleType.setImageResource(R.drawable.coupon_can_img);
                    mTypeImg.setImageResource(R.drawable.yes_used);

                    mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                    tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                } else if (tmp.equals("0")) {
                    //未使用
                    mTitleType.setImageResource(R.drawable.coupon_can_img);
                    mTypeImg.setImageResource(R.drawable.no_used);
                }
            }

        } else if (ordertype.equals("2")) {
            //代购
            mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.coupon_bg_daigou));
            mTvTypeOrder.setText("代购");

            mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_daigou_01));
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_daigou_01));
            tvType.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_daigou_02));
            tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.coupon_bg_daigou_02));

            if (o.getIs_expire().equals("1")) {
                //已过期
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_guoqi_img);
                mTypeImg.setImageResource(R.drawable.used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("1")) {
                //已使用
                mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.guoqi_bg));
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.yes_used);

                mTvTypeOrder.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvType.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                mTvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvValidity.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.used_text_color));
            } else if (tmp.equals("0")) {
                //未使用
                mTitleType.setImageResource(R.drawable.coupon_can_img);
                mTypeImg.setImageResource(R.drawable.no_used);
            }
        }


        tvTitle.setText(o.getTitle());
        tvContent.setText(o.getContent());
        String expire = o.getExpire();
        if (StringUtils.toInt(expire) == 0) {
            tvValidity.setText("永不过期");
        } else {
            tvValidity.setText("有效期: " + o.getExpire() + "天");
        }
        mTvTime.setText(o.getTime());

        String tmpType = o.getType();
        if ("1".equals(tmpType)) {
            tvType.setText("折  扣  卷");
            mTvNumber.setText(o.getValue() + "折");

        } else if ("2".equals(tmpType)) {
            tvType.setText("减  免  卷");
            mTvNumber.setText(o.getValue() + "元");

        } else if ("3".equals(tmpType)) {
            tvType.setText("满  减  卷");
            mTvNumber.setText(o.getValue() + "元");
        }
    }
}
