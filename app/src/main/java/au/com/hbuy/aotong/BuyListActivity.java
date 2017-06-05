package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

import java.util.List;

import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.gen.BuyDbBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class BuyListActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
   /* @Bind(R.id.iv_add)
    ImageView ivAdd;*/
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.lv_list)
    ListView lvList;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.tv_add)
    Button tvAdd;
    private List<BuyDbBean> mLists = null;
    private MyAdapter mMyAdapter = null;
    private BuyDbBeanDao mMessageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buylist);
        ButterKnife.bind(this);

        mMessageDao = GreenDaoManager.getInstance().getSession().getBuyDbBeanDao();
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
                Intent intent = new Intent(BuyListActivity.this, AddBuyActivity.class);
                intent.putExtra("bean", mLists.get(i));
                if (null != mLists && mLists.size() > 0) {
                    intent.putExtra(AddBuyActivity.HINTNUM, mLists.size());
                }
                startActivityForResult(intent, 1);
            }
        });
    }

    @OnClick({R.id.iv_back,/* R.id.iv_add, */R.id.iv_cart, R.id.bt_add_pkg, R.id.tv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
         /*   case R.id.iv_add:
                Intent intent = new Intent(this, AddBuyActivity.class);
                if (null != mLists && mLists.size() > 0) {
                    intent.putExtra(AddBuyActivity.HINTNUM, mLists.size());
                }
                startActivityForResult(intent, 1);
                break;*/
            case R.id.iv_cart:
                AppUtils.goChatByBuy(this);
                break;
            case R.id.tv_add:
                KLog.d();
                startActivityForResult(new Intent(this, AddBuyActivity.class), 1);
                break;
            case R.id.bt_add_pkg:
                if (mLists.size() == 0) {
                    ShowToastUtils.toast(this, "当前没有订单提交", 3);
                    return;
                }
                startActivityForResult(new Intent(this, BuyNoticeActivity.class), 2);
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<BuyDbBean> lists;
        private LayoutInflater mInflater;

        public MyAdapter(List<BuyDbBean> lists) {
            KLog.d();
            this.lists = lists;
            this.mInflater = LayoutInflater.from(BuyListActivity.this);
        }

        public void setLists(List<BuyDbBean> lists) {
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
                convertView = mInflater.inflate(R.layout.activity_buylist_item, null);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_img);
                viewHolder.size = (TextView) convertView.findViewById(R.id.tv_size);
                viewHolder.num = (TextView) convertView.findViewById(R.id.tv_num);
                viewHolder.mk = (TextView) convertView.findViewById(R.id.tv_mk);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BuyDbBean bean = (BuyDbBean) getItem(i);
            KLog.d(bean.getLink() + "---" + bean.getSize() + "---" + bean.getTitle() + "----" + bean.getNum() + "---" + bean.getMk() + "---" + bean.getImg());
            viewHolder.title.setText(bean.getLink());
            viewHolder.num.setText("数量:" + bean.getNum());
            viewHolder.size.setText("尺寸:" + bean.getSize());
            viewHolder.mk.setText("备注:" + bean.getMk());
            /*Picasso.with(BuyListActivity.this)
                    .load(bean.getImg())
                    .placeholder(R.drawable.buy_default_hint)
                    .error(R.drawable.buy_default_hint)
                    .fit()
                    .tag(this)
                    .into(viewHolder.img);*/
            viewHolder.img.setImageResource(R.drawable.buy_default_hint);
            return convertView;
        }

        private class ViewHolder {
            private ImageView img;
            private TextView title, size, mk, num;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == 1) {
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
}
