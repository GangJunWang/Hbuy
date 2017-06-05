package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.BuyListActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.WorkOrderDetailsActivity;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.ContentData;
import au.com.hbuy.aotong.domain.ParityBean;
import au.com.hbuy.aotong.domain.TakeMsgBean;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoViewHolder;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.TakeMsgViewHolder;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMsgCallback;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */

public class ParityPage extends BasePage {
    private GridView mGridView;
    public ParityPage(FragmentActivity activity) {
        super(activity);
    }
    private View mView;
    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.parity_page, null);
        flContent.addView(mView);
        mGridView = (GridView) mView.findViewById(R.id.gridView);
    }

    @Override
    public void initData() {
        if (NetUtils.hasAvailableNet(mActivity)) {
            HashMap<String, String> params = new HashMap<String, String>();
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GET_FOREX, params, new RespAddTCallback<ParityBean>(mActivity) {
                @Override
                public void onSuccess(ParityBean bean) {
                    if (null != bean) {
                        List list = new ArrayList();
                        list.add(bean.getAud());  //澳元
                        list.add(bean.getUsd());  //美元
                        list.add(bean.getNzd());  //纽币/新西兰币
                        list.add(bean.getJpy());  //日元
                        list.add(bean.getKrw());  //韩元
                        list.add(bean.getThb());   //泰铢
                        list.add(bean.getPhp());   //菲律宾比索
                        list.add(bean.getMop());   //中国澳门币
                        list.add(bean.getEur());   //欧元
                        list.add(bean.getCad());   //加元/加拿大元
                        list.add(bean.getNok());   //挪威克朗
                        list.add(bean.getDkk());   //丹麦克朗
                        list.add(bean.getSek());   //瑞典克朗
                        list.add(bean.getSgd());    //新加坡元
                        list.add(bean.getChf());   //瑞士法郎
                        list.add(bean.getHkd());   //中国港币
                        list.add(bean.getGbp());   //英镑
                        mGridView.setAdapter(new GridViewAdapter(list));
                    }
                }

                @Override
                public void onFail(ParityBean workOrderBean) {
                    ShowToastUtils.toast(mActivity, "获取失败", 3);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
        }

    }

    public class GridViewAdapter extends BaseAdapter {
        private List mList;
        private LayoutInflater mInflater;
        private GridViewAdapter() {

        }
        private GridViewAdapter(List list) {
            this.mList = list;
            this.mInflater = LayoutInflater.from(mActivity);
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            KLog.d(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.parity_page_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_name);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mbp = (TextView) convertView.findViewById(R.id.tv_mbp);
                viewHolder.fbp = (TextView) convertView.findViewById(R.id.tv_fbp);
                viewHolder.sp = (TextView) convertView.findViewById(R.id.tv_sp);
                viewHolder.mp = (TextView) convertView.findViewById(R.id.tv_mp);
                viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            switch (position) {
                case 0:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.aud_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_aud);
                    viewHolder.name.setText("澳元\nAUD");
                    break;
                case 1:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, (R.color.usd_color)));
                    viewHolder.imageView.setImageResource(R.drawable.currency_usd);
                    viewHolder.name.setText("美元\nUSD");
                    break;
                case 2:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.nzd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_nzd);
                    viewHolder.name.setText("新西兰币\nNZD");
                    break;
                case 3:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.jpy_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_jpy);
                    viewHolder.name.setText("日元\nJPY");
                    break;
                case 4:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.krw_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_krw);
                    viewHolder.name.setText("韩元\nKRW");
                    break;
                case 5:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.thb_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_thb);
                    viewHolder.name.setText("泰铢\nTHB");
                    break;
                case 6:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.php_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_php);
                    viewHolder.name.setText("比索\nPHP");
                    break;
                case 7:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.mop_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_mop);
                    viewHolder.name.setText("澳门币\nMOP");
                    break;
                case 8:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.eur_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_eur);
                    viewHolder.name.setText("欧元\nEUR");
                    break;
                case 9:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.cad_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_cad);
                    viewHolder.name.setText("加拿大元\nCAD");
                    break;
                case 10:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.nok_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_nok);
                    viewHolder.name.setText("挪威克朗\nNOK");
                    break;
                case 11:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.usd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_dkk);
                    viewHolder.name.setText("丹麦克朗\nDKK");
                    break;
                case 12:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.hkd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_sek);
                    viewHolder.name.setText("瑞典克朗\nSEK");
                    break;
                case 13:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.sgd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_sgd);
                    viewHolder.name.setText("新加坡元\nSGD");
                    break;
                case 14:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.usd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_hkd);
                    viewHolder.name.setText("瑞士法郎\nCHF");
                    break;
                case 15:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.hkd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_hkd);
                    viewHolder.name.setText("中国港币\nHKD");
                    break;
                case 16:
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gbp_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_gbp);
                    viewHolder.name.setText("英镑\nGBP");
                    break;
            }
            ParityBean.Parity bean = (ParityBean.Parity) getItem(position);
            viewHolder.mbp.setText(bean.getMbp());
            viewHolder.fbp.setText(bean.getFbp());
            viewHolder.sp.setText(bean.getSp());
            viewHolder.mp.setText(bean.getMp());
            return convertView;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        private class ViewHolder {
            private RelativeLayout layout;
            private ImageView imageView;
            private TextView name, fbpname, fbp, mbp, mbpname, sp, spname, mp, mpname;
        }
    }


   /* public class GridViewAdapter extends BaseAdapter {
        private List mList;
        private LayoutInflater mInflater;
        private GridViewAdapter() {

        }
        private GridViewAdapter(List list) {
            this.mList = list;
            this.mInflater = LayoutInflater.from(mActivity);
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            KLog.d(position);
            GridViewAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new GridViewAdapter.ViewHolder();
                convertView = mInflater.inflate(R.layout.parity_page_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_name);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mbp = (TextView) convertView.findViewById(R.id.tv_mbp);
                viewHolder.fbp = (TextView) convertView.findViewById(R.id.tv_fbp);
                viewHolder.sp = (TextView) convertView.findViewById(R.id.tv_sp);
                viewHolder.mp = (TextView) convertView.findViewById(R.id.tv_mp);
                viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GridViewAdapter.ViewHolder) convertView.getTag();
            }
            switch (position) {
                case 0:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.aud_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_aud);
                    viewHolder.name.setText("澳元\nAUD");
                    break;
                case 1:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.usd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_usd);
                    viewHolder.name.setText("美元\nUSD");
                    break;
                case 2:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.nzd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_nzd);
                    viewHolder.name.setText("新西兰币\nNZD");
                    break;
                case 3:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.jpy_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_jpy);
                    viewHolder.name.setText("日元\nJPY");
                    break;
                case 4:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.krw_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_krw);
                    viewHolder.name.setText("韩元\nKRW");
                    break;
                case 5:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.thb_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_thb);
                    viewHolder.name.setText("泰铢\nTHB");
                    break;
                case 6:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.php_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_php);
                    viewHolder.name.setText("比索\nPHP");
                    break;
                case 7:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.mop_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_mop);
                    viewHolder.name.setText("澳门币\nMOP");
                    break;
                case 8:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.eur_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_eur);
                    viewHolder.name.setText("欧元\nEUR");
                    break;
                case 9:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.cad_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_cad);
                    viewHolder.name.setText("加拿大元\nCAD");
                    break;
                case 10:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.nok_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_nok);
                    viewHolder.name.setText("挪威克朗\nNOK");
                    break;
                case 11:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.usd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_dkk);
                    viewHolder.name.setText("丹麦克朗\nDKK");
                    break;
                case 12:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.hkd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_sek);
                    viewHolder.name.setText("瑞典克朗\nSEK");
                    break;
                case 13:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.sgd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_sgd);
                    viewHolder.name.setText("新加坡元\nSGD");
                    break;
                case 14:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.usd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_chf);
                    viewHolder.name.setText("瑞士法郎\nCHF");
                    break;
                case 15:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.hkd_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_hkd);
                    viewHolder.name.setText("中国港币\nHKD");
                    break;
                case 16:
                    viewHolder.layout.setBackgroundColor(mActivity.getColor(R.color.gbp_color));
                    viewHolder.imageView.setImageResource(R.drawable.currency_gbp);
                    viewHolder.name.setText("英镑\nGBP");
                    break;
            }
            ParityBean.Parity bean = (ParityBean.Parity) getItem(position);
            viewHolder.mbp.setText(bean.getMbp());
            viewHolder.fbp.setText(bean.getFbp());
            viewHolder.sp.setText(bean.getSp());
            viewHolder.mp.setText(bean.getMp());
            return convertView;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        private class ViewHolder {
            private RelativeLayout layout;
            private ImageView imageView;
            private TextView name, fbpname, fbp, mbp, mbpname, sp, spname, mp, mpname;
        }
    }


   /* mGridView = (GridView) findViewById(R.id.gridView);
    if (NetUtils.hasAvailableNet(mActivity)) {
        HashMap<String, String> params = new HashMap<String, String>();
        ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GET_FOREX, params, new RespAddTCallback<ParityBean>(mActivity) {
            @Override
            public void onSuccess(ParityBean bean) {
                if (null != bean) {
                    List list = new ArrayList();
                    list.add(bean.getAud());  //澳元
                    list.add(bean.getUsd());  //美元
                    list.add(bean.getNzd());  //纽币/新西兰币
                    list.add(bean.getJpy());  //日元
                    list.add(bean.getKrw());  //韩元
                    list.add(bean.getThb());   //泰铢
                    list.add(bean.getPhp());   //菲律宾比索
                    list.add(bean.getMop());   //中国澳门币
                    list.add(bean.getEur());   //欧元
                    list.add(bean.getCad());   //加元/加拿大元
                    list.add(bean.getNok());   //挪威克朗
                    list.add(bean.getDkk());   //丹麦克朗
                    list.add(bean.getSek());   //瑞典克朗
                    list.add(bean.getSgd());    //新加坡元
                    list.add(bean.getChf());   //瑞士法郎
                    list.add(bean.getHkd());   //中国港币
                    list.add(bean.getGbp());   //英镑
                    mGridView.setAdapter(new GridViewAdapter(list));
                }
            }

            @Override
            public void onFail(ParityBean workOrderBean) {
                ShowToastUtils.toast(mActivity, "获取失败", 3);
            }
        });
    } else {
        ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
    }
*/
}
