package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.WorkOrderBean;
public class WorkOrderViewHolder extends BaseViewHolder<WorkOrderBean> {
    private TextView mTvTime, mContent01;
    private ImageView mIvStatus;
    private TextView mTvNumber;
    private Activity mActivity;

    public WorkOrderViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.work_order_list_item);
        mTvNumber = $(R.id.tv_number);
        mIvStatus = $(R.id.iv_status);
        mTvTime = $(R.id.tv_time);
        mContent01 = $(R.id.tv_content01);
        this.mActivity = activity;
    }
    @Override
    public void setData(final WorkOrderBean o) {
        mTvNumber.setText("NO: " + o.getNo());
        mTvTime.setText(o.getTime());
        mContent01.setText(o.getList());

        String status = o.getStatus();
        if ("0".equals(status) || "1".equals(status) || "2".equals(status)) {
            mIvStatus.setImageResource(R.drawable.workorder_chulizhong);
        } else if ("3".equals(status)) {
            mIvStatus.setImageResource(R.drawable.workorder_daiqueren);
        } else if ("4".equals(status)) {
            mIvStatus.setImageResource(R.drawable.workorder_daigoutong);
        } else if ("5".equals(status)) {
            mIvStatus.setImageResource(R.drawable.workorder_wancheng);
        }  else if ("6".equals(status)) {
            mIvStatus.setImageResource(R.drawable.workorder_cancel);
        } else {
            mIvStatus.setImageResource(R.drawable.workorder_wancheng);
        }
    }
}
