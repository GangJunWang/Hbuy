package au.com.hbuy.aotong.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.utils.StringUtils;

/**
 * Created by yangwei on 2016/9/1--17:53.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MasonryView> {
    private final List<Order> list;

    public OrderAdapter(List<Order> list) {
        this.list=list;
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item,parent,false);
        return new MasonryView(view);
    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {
        Order o = list.get(position);
        holder.name.setText(o.getCarrier_id());
        holder.number.setText(o.getMail_no());
        holder.content.setText(o.getContent());
        StringUtils.setTextAndImg(o.getCarrier_id(), holder.name, holder.nameLogo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MasonryView extends RecyclerView.ViewHolder {
        public CheckBox select;
        public TextView name;
        public TextView number;
        public TextView content;
        public ImageView nameLogo;
        public TextView status;
        public TextView address;
        public TextView details;

        public MasonryView(View convertView) {
            super(convertView);
            name = (TextView) convertView.findViewById(R.id.tv_name);
            number = (TextView) convertView.findViewById(R.id.tv_number);
            content = (TextView) convertView.findViewById(R.id.tv_content);
            nameLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
            status = (TextView) convertView.findViewById(R.id.tv_status);
            address = (TextView) convertView.findViewById(R.id.tv_address);
            details = (TextView) convertView.findViewById(R.id.tv_details);
        }
    }
}
