package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.WorkOrderBean;

public class HistoryOrderViewHolder extends BaseViewHolder<WorkOrderBean> {
    private TextView mTvDetails;
    private TextView mTvTime;
    private TextView mTvStatus;
    private ImageView mImgLogo;
    private TextView mTvNumber;
    private Activity mActivity;

    public HistoryOrderViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.waitpayment_order_list_item);
        mTvNumber = $(R.id.tv_number);
        mTvStatus = $(R.id.tv_status);
        mTvDetails = $(R.id.tv_details);
        mTvTime = $(R.id.tv_time);
        mImgLogo = $(R.id.iv_logo);
        this.mActivity = activity;
    }
    @Override
    public void setData(final WorkOrderBean o){
        final String number = o.getNo();
        final String status = o.getStatus();
        final String time = o.getTime();
        mTvNumber.setText("订单 " + number);
        mTvStatus.setText("已处理");
        mTvTime.setText(time);
        mTvDetails.setText("查看");
    }
}
