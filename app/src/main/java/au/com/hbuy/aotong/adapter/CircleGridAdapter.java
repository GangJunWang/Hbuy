package au.com.hbuy.aotong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.utils.ScreenUtils;

public class CircleGridAdapter extends BaseAdapter {

    private String[] mFiles;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CircleGridAdapter(String[] files, Context context) {
        this.mFiles = files;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mFiles == null ? 0 : mFiles.length;
    }

    @Override
    public String getItem(int position) {
        return mFiles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_gridview_circle, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.album_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = getItem(position);
        int width = ScreenUtils.getScreenWidth(mContext);
        int imageWidth = (width / 3 - 40);

        if (position == 0) {
            holder.imageView.setLayoutParams(new LayoutParams(imageWidth, imageWidth));
            if (!"".equals(url))
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.hbuy_img)
                    .resize(imageWidth - 30, imageWidth - 30)
                    .onlyScaleDown()
                    .error(R.drawable.hbuy_img)
                    .tag(this)

                    .into(holder.imageView);
        } else {
            // 根据屏幕宽度动态设置图片宽高
            holder.imageView.setLayoutParams(new LayoutParams(imageWidth, imageWidth));
            KLog.d(url);
            if (!"".equals(url))
            Picasso.with(mContext)
                    .load(url)
                    .resize(imageWidth, imageWidth)
                    .placeholder(R.drawable.hbuy_img)
                    .error(R.drawable.hbuy_img)
                    .tag(this)
                    .into(holder.imageView);
        }
        KLog.d(holder.imageView.getHeight());
        // 解决getview多次调用问题
    /*	if(((NoScrollGridView)parent).isOnMeasure) {
            return convertView;
		} else {
			return mLayoutInflater.inflate(R.layout.item_gridview_circle,parent, false);
		}*/
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

}
