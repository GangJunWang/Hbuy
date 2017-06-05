package au.com.hbuy.aotong.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.util.List;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.ContentData;
import au.com.hbuy.aotong.domain.InformMesBean;
import au.com.hbuy.aotong.domain.TakeMsgBean;

public class InformMsgViewHolder extends BaseViewHolder<InformMesBean> {
    private TextView tvTitle, tvTime;
    private TextView tvContent;
    private ImageView mImgHint;
    private Context context;

    public InformMsgViewHolder(ViewGroup parent, Context activity) {
        super(parent, R.layout.informmsg_list_item);
        tvTitle = $(R.id.tv_title);
        // tvTime = $(R.id.tv_time);
        tvContent = $(R.id.tv_content);
        mImgHint = $(R.id.iv_hint);
        this.context = activity;
    }

    @Override
    public void setData(final InformMesBean o) {
        //  2为物流消息3为工单消息4为付款消息
        if (null != o) {
            String type = o.getType();
            if ("2".equals(type)) {
                mImgHint.setImageResource(R.drawable.wuliu_inform);
            } else if ("3".equals(type)) {
                mImgHint.setImageResource(R.drawable.gongdan_inform);
            } else if ("4".equals(type)) {
                mImgHint.setImageResource(R.drawable.fukuan_inform);
            }
            tvTitle.setText(o.getTitle());
            tvContent.setText(o.getContent());
            //w tvTime.setText(o.getTime());
        }
    }
}
