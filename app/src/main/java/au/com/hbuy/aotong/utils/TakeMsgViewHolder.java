package au.com.hbuy.aotong.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.util.List;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.ContentData;
import au.com.hbuy.aotong.domain.TakeMsgBean;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;

public class TakeMsgViewHolder extends BaseViewHolder<TakeMsgBean> {
    private TextView tvTitle;
    private NestFullListView tvContent;
    private ImageView mImgHint;
    private Context context;
    private LinearLayout layout;
    private LayoutInflater mInflater = null;
    List<ContentData> lists = null;

    public TakeMsgViewHolder(ViewGroup parent, Context activity) {
        super(parent, R.layout.takemsg_list_item);
        tvTitle = $(R.id.tv_title);
        tvContent = $(R.id.lv_content);
        layout = $(R.id.layout);
        mImgHint = $(R.id.iv_hint);
        this.context = activity;
        this.mInflater = LayoutInflater.from(context);
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
        layout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                //访问
                Intent i = new Intent(context, FaqActivity.class);
                i.putExtra("url", o.getValue());
                context.startActivity(i);
            }
        });

        lists = o.getContent().getListdata();
        KLog.d(lists);
        if (null != lists) {
            tvContent.setAdapter(new NestFullListViewAdapter<ContentData>(R.layout.take_content_data_list_item, lists) {
                @Override
                public void onBind(int pos, final ContentData testBean, NestFullViewHolder holder) {
                    holder.setText(R.id.tv_content, testBean.getTitle());
                    holder.setOnClickListener(R.id.layout, new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View view) {
                            KLog.d(testBean.getImg());
                            Intent i = new Intent(context, FaqActivity.class);
                            i.putExtra("url", testBean.getValue());
                            context.startActivity(i);
                        }
                    });
                    String img = testBean.getImg();
                    if (null != img && !StringUtils.isEmpty(img))
                    Picasso.with(context)
                            .load(img)
                            .placeholder(R.drawable.hbuy)
                            .error(R.drawable.hbuy)
                            .fit()
                            .tag(this)
                            .into((ImageView) holder.getView(R.id.iv_hint));
                }
            });
        }

        /*tvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    KLog.d(lists.get(i).getTitle());
            }
        });*/
    }

   /* private class ContentAdapter extends BaseAdapter {
        private List<ContentData> datas;

        public ContentAdapter(List<ContentData> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            KLog.d(datas + "datas = "+ datas.size());
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.take_content_data_list_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.iv_hint);
                holder.title = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                ContentData data = datas.get(i);
                holder.title.setText(data.getTitle());
                String img = data.getImg();
                if (null != img && !StringUtils.isEmpty(img))
                Picasso.with(context)
                        .load(img)
                        .placeholder(R.drawable.me_default)
                        .error(R.drawable.me_default)
                        .fit()
                        .tag(this)
                        .into(holder.img);
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView title;
            private ImageView img;
        }
    }*/
}
