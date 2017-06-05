package au.com.hbuy.aotong.adapter;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.utils.StringUtils;

/**
 * Created by yangwei on 2016/12/2--10:28.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class TransferAdapter extends BaseAdapter {
    private Context mContext;

    private List<Order> mDatas;

    private LayoutInflater mInflater;

    public boolean flage = false;

    public Map<Integer, String> selected;

    private List<String> dataIds;

    public List<Order> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<Order> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public TransferAdapter(Context mContext, List<Order> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;

        mInflater = LayoutInflater.from(this.mContext);

        selected = new HashMap<>();
        dataIds = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        KLog.d();
        ViewHolder holder = null;
        if (convertView == null) {
            // 下拉项布局
            convertView = mInflater.inflate(R.layout.item_slide_relativelayout, null);
            holder = new ViewHolder();
            holder.checkboxOperateData = (CheckBox) convertView.findViewById(R.id.item_checkbox);
            holder.context = (TextView) convertView.findViewById(R.id.tv_content);
            holder.nameAndNum = (TextView) convertView.findViewById(R.id.tv_name_num);
            holder.status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
            holder.ivName = (ImageView) convertView.findViewById(R.id.iv_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Order itemBean = mDatas.get(position);
        if (itemBean != null) {
            holder.context.setText(itemBean.getContent());
            holder.nameAndNum.setText(itemBean.getCarrier_name() + ":" + itemBean.getMail_no());
            Order.OriginInfo details = itemBean.getDetail().getOriginal_info();
            if (null != details) {
                holder.status.setText(details.getData().get(0).getContent());
            } else {
                holder.status.setText("暂无物流信息");
            }
            holder.time.setText("时间: " + itemBean.getTime());
            String code = itemBean.getCarrier_id();
            StringUtils.setTextAndImg(code, null, holder.ivName);
            StringUtils.setStatusImg(itemBean.getStatus(), holder.ivStatus);

            // 根据isSelected来设置checkbox的显示状况
            KLog.d(flage);
            if (flage) {
                holder.checkboxOperateData.setVisibility(View.VISIBLE);
            } else {
                holder.checkboxOperateData.setVisibility(View.GONE);
            }

            if (selected.containsKey(position))
                holder.checkboxOperateData.setChecked(true);
            else
                holder.checkboxOperateData.setChecked(false);

            holder.checkboxOperateData.setChecked(itemBean.isChecked());
            //注意这里设置的不是onCheckedChangListener，还是值得思考一下的
            holder.checkboxOperateData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemBean.isChecked()) {
                        itemBean.setIsChecked(false);
                    } else {
                        itemBean.setIsChecked(true);
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        private CheckBox checkboxOperateData;
        private TextView context, nameAndNum, status, time;
        private ImageView ivStatus, ivName;
    }
}
