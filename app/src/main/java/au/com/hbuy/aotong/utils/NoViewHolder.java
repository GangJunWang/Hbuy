package au.com.hbuy.aotong.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.TakeMsgBean;
import au.com.hbuy.aotong.domain.TakeMsgContent;

public class NoViewHolder extends BaseViewHolder<TakeMsgBean> {
    private TextView tvTitle, tvContent, tvLook;
    private ImageView mImgHint;
    private Context context;
    private LinearLayout layout;

    public NoViewHolder(ViewGroup parent, Context activity) {
        super(parent, R.layout.no_takemsg_list_item);
        tvTitle = $(R.id.tv_title);
        tvContent = $(R.id.tv_content);
        mImgHint = $(R.id.iv_hint);
        layout = $(R.id.layout);
        this.context = activity;
    }
    @Override
    public void setData(final TakeMsgBean o) {
        String img = o.getImg();
        if (null != img && !StringUtils.isEmpty(img)) {
            Picasso.with(context)
                    .load(img)
                    .placeholder(R.drawable.hbuy)
                    .error(R.drawable.hbuy)
                    .fit()
                    .tag(this)
                    .into(mImgHint);
        }
        tvTitle.setText(o.getTitle());
        TakeMsgContent content = o.getContent();
        if (null != content) {
            tvContent.setText(content.getData());
        }
        layout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                Intent i = new Intent(context, FaqActivity.class);
                i.putExtra("url", o.getValue());
                context.startActivity(i);
            }
        });

    }
}
