package au.com.hbuy.aotong.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.greenDao.Message;

/**
 * Created by yangwei on 2016/8/11--10:24.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class MessageAdapter extends BaseAdapter {
    private List<Message> mMessageList;
    private Context mContext;

    public MessageAdapter(List<Message> list, Context context) {
        this.mMessageList = list;
        this.mContext = context;
    }

    public void setmMessageList(List<Message> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public int getCount() {
        return mMessageList == null ?  0 : mMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_message, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.status = (ImageView) convertView.findViewById(R.id.iv_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Message msg = mMessageList.get(i);
        viewHolder.name.setText(msg.getName());
        viewHolder.time.setText(msg.getTime());
        viewHolder.content.setText(msg.getContent());
        if (msg.getStatus() == 1) {
            viewHolder.status.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder{
        private TextView name;
        private TextView time;
        private TextView content;
        private ImageView status;
    }
}
