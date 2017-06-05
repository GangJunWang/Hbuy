package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.Order;

public class OrderViewHolder extends BaseViewHolder<Order> {
    private TextView mTvName;
    private TextView mTvDetails;
    private TextView mTvContent, mTvTime;
    private TextView mTvStatus;
    private ImageView mImgLogo;
    private TextView mTvNumber;
    private Activity mActivity;

    public OrderViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.order_list_item);
        mTvName = $(R.id.tv_name);
        mTvNumber = $(R.id.tv_number);
        mTvContent = $(R.id.tv_content);
        mTvStatus = $(R.id.tv_status);
        mTvDetails = $(R.id.tv_details);
        mImgLogo = $(R.id.iv_logo);
        mTvTime = $(R.id.tv_time);
        this.mActivity = activity;
    }

    @Override
    public void setData(final Order o){
        mTvNumber.setText(o.getMail_no());
        mTvContent.setText(o.getContent());
        String status = o.getStatus();
        if ("-1".equals(status)) {
            mTvStatus.setText("暂无物流信息");
        } else if ("0".equals(status)) {
            mTvStatus.setText("在途");
        } else if ("1".equals(status)) {
            mTvStatus.setText("揽件");
        } else if ("2".equals(status)) {
            mTvStatus.setText("疑难");
        } else if ("3".equals(status)) {
            mTvStatus.setText("签收");
        } else if ("4".equals(status)) {
            mTvStatus.setText("退签");
        } else if ("5".equals(status)) {
            mTvStatus.setText("派件");
        } else if ("6".equals(status)) {
            mTvStatus.setText("退回");
        } else if ("7".equals(status)) {
            mTvStatus.setText("到Hbuy中转");
        } else if ("8".equals(status)) {
            mTvStatus.setText("打包中");
        }

        mTvTime.setText(o.getTime());
        StringUtils.setTextAndImg(o.getCarrier_id(), mTvName, mImgLogo);
    }
}
