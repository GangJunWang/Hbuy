package au.com.hbuy.aotong.base.impl;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.BuyListActivity;
import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.FreightBean;
import au.com.hbuy.aotong.domain.ParityBean;
import au.com.hbuy.aotong.domain.TakeMsgBean;
import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class FreightPage extends BasePage {
    private TextView mName, mNameHint;
    private EditText mEdit;
    private ImageView mImgHint;
    private ListView mListView;
    private List<FreightBean> mList;
    private MyAdapter mAdapter;
    private FreightBean mBean;
    private boolean mIsReset = false;

    public FreightPage(FragmentActivity activity) {
        super(activity);
    }

    private View mView;

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.freight_page, null);
        flContent.addView(mView);
        KLog.d();
        mName = (TextView) mView.findViewById(R.id.tv_name);
        mNameHint = (TextView) mView.findViewById(R.id.tv_name_hint);
        mEdit = (EditText) mView.findViewById(R.id.et_edit);
        mImgHint = (ImageView) mView.findViewById(R.id.iv_hint);
        mListView = (ListView) mView.findViewById(R.id.lv_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIsReset = true;
                FreightBean bean = mList.get(position);
                KLog.d(bean.getType());
                setHint(bean.getType(), mImgHint, mName, mNameHint);
                mEdit.setText("0.00");

                mList.set(position, mBean);
                mBean = bean;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        mBean = new FreightBean(17, 0.00, 0.00, 0.00);

        if (NetUtils.hasAvailableNet(mActivity)) {
            HashMap<String, String> params = new HashMap<String, String>();
            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GET_FOREX, params, new RespAddTCallback<ParityBean>(mActivity) {
                @Override
                public void onSuccess(ParityBean bean) {
                    if (null != bean) {
                        mList = new ArrayList();
                        mList.add(new FreightBean(0, StringUtils.toDouble(bean.getAud().getFbp()), StringUtils.toDouble(bean.getAud().getSp()), StringUtils.toDouble(bean.getAud().getMp())));
                        mList.add(new FreightBean(1, StringUtils.toDouble(bean.getUsd().getFbp()), StringUtils.toDouble(bean.getUsd().getSp()), StringUtils.toDouble(bean.getUsd().getMp())));
                        mList.add(new FreightBean(2, StringUtils.toDouble(bean.getNzd().getFbp()), StringUtils.toDouble(bean.getNzd().getSp()), StringUtils.toDouble(bean.getNzd().getMp())));
                        mList.add(new FreightBean(3, StringUtils.toDouble(bean.getJpy().getFbp()), StringUtils.toDouble(bean.getJpy().getSp()), StringUtils.toDouble(bean.getJpy().getMp())));
                        mList.add(new FreightBean(4, StringUtils.toDouble(bean.getKrw().getFbp()), StringUtils.toDouble(bean.getKrw().getSp()), StringUtils.toDouble(bean.getKrw().getMp())));
                        mList.add(new FreightBean(5, StringUtils.toDouble(bean.getThb().getFbp()), StringUtils.toDouble(bean.getThb().getSp()), StringUtils.toDouble(bean.getThb().getMp())));
                        mList.add(new FreightBean(6, StringUtils.toDouble(bean.getPhp().getFbp()), StringUtils.toDouble(bean.getPhp().getSp()), StringUtils.toDouble(bean.getPhp().getMp())));
                        mList.add(new FreightBean(7, StringUtils.toDouble(bean.getMop().getFbp()), StringUtils.toDouble(bean.getMop().getSp()), StringUtils.toDouble(bean.getMop().getMp())));
                        mList.add(new FreightBean(8, StringUtils.toDouble(bean.getEur().getFbp()), StringUtils.toDouble(bean.getEur().getSp()), StringUtils.toDouble(bean.getEur().getMp())));
                        mList.add(new FreightBean(9, StringUtils.toDouble(bean.getCad().getFbp()), StringUtils.toDouble(bean.getCad().getSp()), StringUtils.toDouble(bean.getCad().getMp())));
                        mList.add(new FreightBean(10, StringUtils.toDouble(bean.getNok().getFbp()), StringUtils.toDouble(bean.getNok().getSp()), StringUtils.toDouble(bean.getNok().getMp())));
                        mList.add(new FreightBean(11, StringUtils.toDouble(bean.getDkk().getFbp()), StringUtils.toDouble(bean.getDkk().getSp()), StringUtils.toDouble(bean.getDkk().getMp())));
                        mList.add(new FreightBean(12, StringUtils.toDouble(bean.getSek().getFbp()), StringUtils.toDouble(bean.getSek().getSp()), StringUtils.toDouble(bean.getSek().getMp())));
                        mList.add(new FreightBean(13, StringUtils.toDouble(bean.getSgd().getFbp()), StringUtils.toDouble(bean.getSgd().getSp()), StringUtils.toDouble(bean.getSgd().getMp())));
                        mList.add(new FreightBean(14, StringUtils.toDouble(bean.getChf().getFbp()), StringUtils.toDouble(bean.getChf().getSp()), StringUtils.toDouble(bean.getChf().getMp())));
                        mList.add(new FreightBean(15, StringUtils.toDouble(bean.getHkd().getFbp()), StringUtils.toDouble(bean.getHkd().getSp()), StringUtils.toDouble(bean.getHkd().getMp())));
                        mList.add(new FreightBean(16, StringUtils.toDouble(bean.getGbp().getFbp()), StringUtils.toDouble(bean.getGbp().getSp()), StringUtils.toDouble(bean.getGbp().getMp())));
                       /* mList.add(bean.getAud());  //澳元
                        mList.add(bean.getUsd());  //美元
                        mList.add(bean.getNzd());  //纽币/新西兰币
                        mList.add(bean.getJpy());  //日元
                        mList.add(bean.getKrw());  //韩元
                        mList.add(bean.getThb());   //泰铢
                        mList.add(bean.getPhp());   //菲律宾比索
                        mList.add(bean.getMop());   //中国澳门币
                        mList.add(bean.getEur());   //欧元
                        mList.add(bean.getCad());   //加元/加拿大元
                        mList.add(bean.getNok());   //挪威克朗
                        mList.add(bean.getDkk());   //丹麦克朗
                        mList.add(bean.getSek());   //瑞典克朗
                        mList.add(bean.getSgd());    //新加坡元
                        mList.add(bean.getChf());   //瑞士法郎
                        mList.add(bean.getHkd());   //中国港币
                        mList.add(bean.getGbp());   //英镑*/
                        mAdapter = new MyAdapter(mList);
                        mListView.setAdapter(mAdapter);
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

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double tmp = StringUtils.toDouble(s.toString());
                KLog.d(s);
                if (tmp >= 0) {
                    mIsReset = false;
                    KLog.d(s);
                    if (null != mList) {
                        if (mBean.getType() == 17) {
                            //人民币转外币：RMB/汇出价*100=外币
                            for (FreightBean bean : mList) {
                                double waibi = tmp / bean.getMp() * 100;
                                bean.setResult(waibi);
                            }
                        } else {
                            // 外币转人民币：外币*汇入价/100=RMB
                            //外币转外币: 先转成人民币 再人民币转成外币
                            for (FreightBean bean : mList) {
                                double rmb = tmp * mBean.getMp() / 100;
                                KLog.d(bean.getType() + "--" + rmb);
                                if (bean.getType() == 17) {
                                    bean.setResult(rmb);
                                } else {
                                    double waibi = rmb / bean.getMp() * 100;
                                    bean.setResult(waibi);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * 只能输入二位小数
         */
        mEdit.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt)
            {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
    }

    private void setHint(int type, ImageView img, TextView name, TextView nameHint) {
        switch (type) {
            case 0:
                img.setImageResource(R.drawable.currency_aud);
                name.setText("AUD");
                nameHint.setText("澳元");
                break;
            case 1:
                img.setImageResource(R.drawable.currency_usd);
                name.setText("USD");
                nameHint.setText("美元");
                break;
            case 2:
                img.setImageResource(R.drawable.currency_nzd);
                name.setText("NZD");
                nameHint.setText("新西兰币");
                break;
            case 3:
                img.setImageResource(R.drawable.currency_jpy);
                name.setText("JPY");
                nameHint.setText("日元");
                break;
            case 4:
                img.setImageResource(R.drawable.currency_krw);
                name.setText("KRW");
                nameHint.setText("韩元");
                break;
            case 5:
                img.setImageResource(R.drawable.currency_thb);
                name.setText("THB");
                nameHint.setText("泰铢");
                break;
            case 6:
                img.setImageResource(R.drawable.currency_php);
                name.setText("PHP");
                nameHint.setText("比索");
                break;
            case 7:
                img.setImageResource(R.drawable.currency_mop);
                name.setText("MOP");
                nameHint.setText("澳门币");
                break;
            case 8:
                img.setImageResource(R.drawable.currency_eur);
                name.setText("EUR");
                nameHint.setText("欧元");
                break;
            case 9:
                img.setImageResource(R.drawable.currency_cad);
                name.setText("CAD");
                nameHint.setText("加拿大元");
                break;
            case 10:
                img.setImageResource(R.drawable.currency_nok);
                name.setText("NOK");
                nameHint.setText("挪威克朗");
                break;
            case 11:
                img.setImageResource(R.drawable.currency_dkk);
                name.setText("DKK");
                nameHint.setText("丹麦克朗");
                break;
            case 12:
                img.setImageResource(R.drawable.currency_sek);
                name.setText("SEK");
                nameHint.setText("瑞典克朗");
                break;
            case 13:
                img.setImageResource(R.drawable.currency_sgd);
                name.setText("SGD");
                nameHint.setText("新加坡元");
                break;
            case 14:
                img.setImageResource(R.drawable.currency_hkd);
                name.setText("CHF");
                nameHint.setText("瑞士法郎");
                break;
            case 15:
                img.setImageResource(R.drawable.currency_hkd);
                name.setText("HKD");
                nameHint.setText("中国港币");
                break;
            case 16:
                img.setImageResource(R.drawable.currency_gbp);
                name.setText("GBP");
                nameHint.setText("英镑");
                break;
            case 17:
                img.setImageResource(R.drawable.china);
                name.setText("RMB");
                nameHint.setText("人民币");
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List lists;
        private LayoutInflater mInflater;
        public MyAdapter() {

        }

        public MyAdapter(List lists) {
            KLog.d();
            this.lists = lists;
            this.mInflater = LayoutInflater.from(mActivity);
        }

        public void setLists(List lists) {
            this.lists = lists;
            //   notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            MyAdapter.ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.freight_page_list_item, null);
                viewHolder = new MyAdapter.ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_hint);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.namehint = (TextView) convertView.findViewById(R.id.tv_name_hint);
                viewHolder.result = (TextView) convertView.findViewById(R.id.tv_result);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyAdapter.ViewHolder) convertView.getTag();
            }
            FreightBean bean = mList.get(i);
            setHint(bean.getType(), viewHolder.img, viewHolder.name, viewHolder.namehint);
            if (mIsReset) {
                viewHolder.result.setText("0.00");
            } else {
                DecimalFormat df = new DecimalFormat("######0.00");
                viewHolder.result.setText(df.format(bean.getResult()) + "");
            }
            return convertView;
        }

        private class ViewHolder {
            private ImageView img;
            private TextView name, namehint, result;
        }
    }
}
