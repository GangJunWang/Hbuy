package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.hbuy.aotong.domain.Nation;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespSelectNationCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class SelectNationAndCodeActivity extends BaseActivity {
    private ListView mSortListView;
    private TextView mTitle;
    private ImageView mBack;
    private CustomProgressDialog progressDialog;
    private String type;
    private List<Nation> mList;
    private Activity mContext;
    private String mHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_nation_code);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        mHint = intent.getStringExtra("hint");
        mContext = this;
        initView();
        if (null != type) {
            if ("1".equals(type)) {
                mTitle.setText("请选择国家码");
            } else if ("2".equals(type)) {
                mTitle.setText("请选择国家");
            } else if ("3".equals(type)) {
                mTitle.setText("请选择国家码");
            }
        }

        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);

            ApiClient.getInstance(this).postForm(ConfigConstants.GETLISTNATION, new RespSelectNationCallback<List<Nation>>(this, progressDialog) {
                @Override
                public void onSuccess(List<Nation> list) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if (null != list && list.size() > 0) {
                        mList = list;
                        mSortListView.setAdapter(new NationAdapter(mContext, list));
                    }
                }

                @Override
                public void onFail(List<Nation> list) {
                    AppUtils.stopProgressDialog(progressDialog);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
        }
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.tv_title);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSortListView = (ListView) findViewById(R.id.country_lvcountry);
        mSortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                if ("1".equals(type)) {
                    intent.putExtra("code", mList.get(position).getPhone_code());
                } else if ("2".equals(type)){
                    intent.putExtra("nation", mList.get(position).getName());
                    intent.putExtra("id", mList.get(position).getId());
                } else if ("3".equals(type)) {
                    intent.putExtra("nation", mList.get(position).getName());
                    intent.putExtra("code", mList.get(position).getPhone_code());
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private class NationAdapter extends BaseAdapter {
        private List<Nation> list = null;

        private Context mContext;

        public NationAdapter(Context mContext, List<Nation> list) {
            this.mContext = mContext;
            this.list = list;
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.nation_list_item, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Nation nation = list.get(position);
            if (nation.getName().equals(mHint) || nation.getPhone_code().equals(mHint)) {
                viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.nation_select_color));
            } else {
                viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.default_color));
            }
            viewHolder.tvTitle.setText(nation.getName() + "-" + nation.getEn() + "+" + nation.getPhone_code());
            return convertView;
        }

        class ViewHolder {
            TextView tvTitle;
        }
    }
}
