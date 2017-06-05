package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.WaitPayBean;
import au.com.hbuy.aotong.greenDao.gen.WaitPayBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * 代付列表
 * E-Mail:yangwei199402@gmail.com
 */
public class WaitPayListActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
 /*   @Bind(R.id.iv_add)
    ImageView ivAdd;*/
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.lv_list)
    ListView lvList;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    @Bind(R.id.tv_add)
    Button tvAdd;
    @Bind(R.id.layout)
    LinearLayout layout;
    private List<WaitPayBean> mLists = new ArrayList<>();
    private MyAdapter mMyAdapter = null;
    private WaitPayBeanDao mMessageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitpay_list);
        ButterKnife.bind(this);

        mMessageDao = GreenDaoManager.getInstance().getSession().getWaitPayBeanDao();
        mLists = mMessageDao.queryBuilder().build().list();
        KLog.d(mLists + "== " + mLists.size() + "--" + mMyAdapter);
        //  KLog.d(mLists.size() + "----" + mLists.get(0).getNum());
        if (null != mLists && mLists.size() > 0) {
            if (null == mMyAdapter) {
                mMyAdapter = new MyAdapter(mLists);
                lvList.setAdapter(mMyAdapter);
            } else {
                mMyAdapter.setLists(mLists);
            }
            layout.setVisibility(View.GONE);
        } else {
            lvList.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            btAddPkg.setVisibility(View.GONE);
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WaitPayListActivity.this, AddWaitPayActivity.class);
                intent.putExtra("bean", mLists.get(i));
                if (null != mLists && mLists.size() > 0) {
                    intent.putExtra(AddWaitPayActivity.HINTNUM, mLists.size());
                }
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d(requestCode + "---" + resultCode);
        if (resultCode == RESULT_OK)
            if (requestCode == 1) {
                KLog.d();
                mLists = mMessageDao.queryBuilder().build().list();
                if (null == mMyAdapter) {
                    mMyAdapter = new MyAdapter(mLists);
                    lvList.setAdapter(mMyAdapter);
                } else {
                    mMyAdapter.setLists(mLists);
                }
            } else if (requestCode == 2) {
                setResult(RESULT_OK);
                finish();
            }
    }

    @OnClick({R.id.iv_back, /*R.id.iv_add, */R.id.iv_cart, R.id.bt_add_pkg, R.id.tv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
           /* case R.id.iv_add:
                Intent intent = new Intent(this, AddWaitPayActivity.class);
                if (null != mLists && mLists.size() > 0) {
                    intent.putExtra(AddWaitPayActivity.HINTNUM, mLists.size());
                }
                startActivityForResult(intent, 1);
                break;*/
            case R.id.iv_cart:
                AppUtils.goChatByBuy(this);
                break;
            case R.id.tv_add:
                startActivityForResult(new Intent(this, AddWaitPayActivity.class), 1);
                break;
            case R.id.bt_add_pkg:
                if (mLists.size() == 0) {
                    ShowToastUtils.toast(this, "当前代付订单提交");
                    return;
                }
                startActivityForResult(new Intent(this, WaitPayNoticeActivity.class), 2);
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<WaitPayBean> lists;
        private LayoutInflater mInflater;

        public MyAdapter(List<WaitPayBean> lists) {
            KLog.d();
            this.lists = lists;
            this.mInflater = LayoutInflater.from(WaitPayListActivity.this);
        }

        public void setLists(List<WaitPayBean> lists) {
            this.lists = lists;
            notifyDataSetChanged();
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
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.activity_waitpaylist_item, null);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_img);
                viewHolder.num = (TextView) convertView.findViewById(R.id.tv_num);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            WaitPayBean bean = (WaitPayBean) getItem(i);
            KLog.d(bean.getName() + "----" + bean.getNum());
            viewHolder.title.setText(bean.getName());
            viewHolder.num.setText("代付金额 : " + StringUtils.keepDouble(bean.getNum()));
            viewHolder.img.setImageResource(R.drawable.buy_default_hint);
            return convertView;
        }

        private class ViewHolder {
            private ImageView img;
            private TextView title, num;
        }
    }
}
