package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.Order;

public class PkgOrderViewHolderBy extends BaseViewHolder<Order> {
    TextView tvContent, tvStatus, tvNameAndNum, tvTime;
    ImageView ivName, ivStatus;
    private Activity mActivity;

    public PkgOrderViewHolderBy(ViewGroup parent, Activity activity) {
        super(parent, R.layout.inland_item_page);
        KLog.d();
      //  tvContent = $(R.id.tv_content);
        tvStatus = $(R.id.tv_status);
        tvNameAndNum = $(R.id.tv_name_num);
        tvTime = $(R.id.tv_time);
        ivName = $(R.id.iv_name);
        ivStatus = $(R.id.iv_status);
        this.mActivity = activity;
    }

    @Override
    public void setData(final Order o) {
      //  this.tvContent.setText(o.getContent());
        /*Order.ExpressDetails details = o.getDetail();
        KLog.d(details);
        if (null != details) {
            Order.OriginInfo info = details.getOriginal_info();
            KLog.d(info);
            if (null != info) {
                List<Order.OriginInfo.DataInfo> infos = info.getData();
                if (null != infos) {
                    Order.OriginInfo.DataInfo d = infos.get(0);
                    this.tvStatus.setText(d.getContent());
                }
            } else {
                this.tvStatus.setText("暂无物流信息");
            }
        }*/
        this.tvStatus.setText(o.getMail_no());
        this.tvNameAndNum.setText(o.getContent());
        this.tvTime.setText(o.getTime());
        String code = o.getCarrier_id();
        StringUtils.setTextAndImg(code, null, ivName);
        StringUtils.setStatusImg(o.getStatus(), ivStatus);
    }
}
