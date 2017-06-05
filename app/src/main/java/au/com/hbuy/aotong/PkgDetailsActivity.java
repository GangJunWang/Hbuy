package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Data;
import au.com.hbuy.aotong.domain.PkgDetail;
import au.com.hbuy.aotong.domain.PkgDetailsBean;
import au.com.hbuy.aotong.domain.PkgDetailsInfo;
import au.com.hbuy.aotong.domain.ViewHolder;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;
import au.com.hbuy.aotong.view.OverScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PkgDetailsActivity extends BaseDetailsActivity {
    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.iv_menu)
    ImageView mMenu;
    @Bind(R.id.iv_express_logo)
    ImageView mExpressLogo;
    @Bind(R.id.tv_content)
    TextView mContent;
    @Bind(R.id.tv_time)
    TextView mTime;
    @Bind(R.id.iv_edit_content)
    ImageView mEditContent;
    @Bind(R.id.iv_status)
    ImageView mStatus;
    @Bind(R.id.lv_logistics_msg)
    NestFullListView lvLogisticsMsg;
    /*  @Bind(R.id.tv_look_pkg)
      Button mLookPkg;*/
/*    @Bind(R.id.tv_name)
    TextView mName;*/
    @Bind(R.id.tv_number)
    TextView mNumber;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.tv_name_address)
    TextView tvNameAddress;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_details_address)
    TextView tvDetailsAddress;
    @Bind(R.id.tv_chaibao)
    TextView tvChaibao;
    @Bind(R.id.tv_daofu)
    TextView tvDaofu;
    @Bind(R.id.address)
    LinearLayout address;
    @Bind(R.id.feiyong_hint)
    TextView feiyongHint;
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    private String mId, mStatusId, mOpenPay;
    private List<Data> mList;
    private CustomProgressDialog progressDialog;
    private PkgDetailsBean mBean;
    private String mStatusMsg = null;
    private String mType = null; //1  id查询包裹  2 单号查询包裹

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_pkg);
        ButterKnife.bind(this);
        layout = (OverScrollView) findViewById(R.id.layout1);
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        mType = intent.getStringExtra("type");
        mStatusId = intent.getStringExtra("status");
        mOpenPay = intent.getStringExtra("openPay");
        KLog.d(mStatusId + "---" + mOpenPay);
        if ("0".equals(mOpenPay) && "7".equals(mStatusId)) {
            //    mLookPkg.setVisibility(View.VISIBLE);
        } else if ("3".equals(mStatusId) || "9".equals(mStatusId)) {
            //历史包裹详情
            mEditContent.setVisibility(View.GONE);
        }
        initData();
        setListener();
    }

    public void initData() {
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(PkgDetailsActivity.this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", mId);
            if (null != mType && "2".equals(mType)) {
                params.put("type", "1");
            }
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getItemPkg, params, new RespTakeMessageCallback<PkgDetailsBean>(this, null, progressDialog) {
                @Override
                public void onSuccess(PkgDetailsBean bean) {
                    mId = bean.getId();
                    AppUtils.stopProgressDialog(progressDialog);
                    mStatusId = bean.getStatus();
                    mOpenPay = bean.getOpen_pay();
                    if ("0".equals(mOpenPay) && "7".equals(mStatusId)) {
                        //         mLookPkg.setVisibility(View.VISIBLE);
                    } else if ("3".equals(mStatusId) || "9".equals(mStatusId)) {
                        //历史包裹详情
                        mEditContent.setVisibility(View.INVISIBLE);
                    }
                    mTime.setText(bean.getTime());

                    double chaibao = StringUtils.toDouble(bean.getOpen_pay());
                    double daofu = StringUtils.toDouble(bean.getArrive_pay());
                    if (chaibao == 0 && daofu == 0) {
                        feiyongHint.setVisibility(View.GONE);
                        tvChaibao.setVisibility(View.GONE);
                        tvDaofu.setVisibility(View.GONE);
                    } else if (chaibao == 0 && daofu > 0) {
                        tvDaofu.setVisibility(View.GONE);
                        tvChaibao.setText("邮费到付: ¥" + StringUtils.keepDouble(daofu + "") + "");
                    } else if (chaibao > 0 && daofu == 0) {
                        tvDaofu.setVisibility(View.GONE);
                        tvChaibao.setText("提前拆包: ¥" + StringUtils.keepDouble(chaibao + "") + "");
                    } else if (chaibao > 0 && daofu > 0) {
                        tvChaibao.setText("提前拆包: ¥" + StringUtils.keepDouble(chaibao + "") + "");
                        tvDaofu.setText("邮费到付: ¥" + StringUtils.keepDouble(daofu + "") + "");
                    }
                    //  int address_id = StringUtils.toInt(bean.getAddress_id());
                    //    if (address_id > 0) {
                    Address a = bean.getAddress();
                    KLog.d("a=" + a + "-=" + a.getCity());
                    if (null != a && null != a.getCity()) {
                        address.setVisibility(View.VISIBLE);
                        tvNameAddress.setText(a.getReceiver());
                        tvPhone.setText("+" + a.getPhonecode() + " " + a.getPhone());
                        tvDetailsAddress.setText(a.getCountry() + a.getCity() + a.getAddress());
                    }
                    //   }
                    mBean = bean;
                    mStatusMsg = bean.getStatus();
                    StringUtils.setTextAndImg(bean.getCarrier_id(), null, mExpressLogo);
                    mNumber.setText(bean.getMail_no());
                    mContent.setText(bean.getContent());
                    PkgDetail detail = bean.getDetail();
                    StringUtils.setStatusImg(bean.getStatus(), mStatus);
                    if (null != detail) {
                        PkgDetailsInfo ori = detail.getOriginal_info();
                        List<Data> origin = null;
                        List<Data> des = null;
                        if (null != ori) {
                            origin = detail.getOriginal_info().getData();
                        }
                        PkgDetailsInfo d = detail.getDestination_info();
                        if (null != d) {
                            des = detail.getDestination_info().getData();
                        }
                        if (null != origin && null != des) {
                            mList = origin;
                            mList.addAll(des);
                        } else if (null != origin) {
                            mList = des;
                        }

                        if (null != mList) {
                            lvLogisticsMsg.setAdapter(new NestFullListViewAdapter<Data>(R.layout.list_item_express_message, mList) {
                                @Override
                                public void onBind(int pos, Data nestBean, NestFullViewHolder holder) {
                                    if (pos == 0) {
              /*  在oppo 5.1系统显示 java.lang.NoSuchMethodError: No virtual method getColor(I)
               viewHolder.time.setTextColor(getColor(R.color.default_color));
                viewHolder.date.setTextColor(getColor(R.color.default_color));
                viewHolder.content.setTextColor(getColor(R.color.default_color));*/
                                        holder.setTextColor(R.id.tv_time, ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                                        holder.setTextColor(R.id.tv_date, ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                                        holder.setTextColor(R.id.tv_content, ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                                        if (null != mStatusMsg) {
                                            if ("2".equals(mStatusMsg) || "6".equals(mStatusMsg)) {
                                                holder.setImageResource(R.id.iv_icon, R.drawable.error_pkg_details);
                                            } else if ("3".equals(mStatusMsg)) {
                                                holder.setImageResource(R.id.iv_icon, R.drawable.yes_pkg_details);
                                            }
                                        }
                                        holder.setImageResource(R.id.iv_icon, R.drawable.landmark);
                                    } else {
              /*  viewHolder.time.setTextColor(getColor(R.color.wuliu_default));
                viewHolder.date.setTextColor(getColor(R.color.wuliu_default));
                viewHolder.content.setTextColor(getColor(R.color.wuliu_default));*/
                                        holder.setTextColor(R.id.tv_time, ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                                        holder.setTextColor(R.id.tv_date, ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                                        holder.setTextColor(R.id.tv_content, ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                                        holder.setImageResource(R.id.iv_icon, R.drawable.default_landmark);
                                    }
                                    String[] tmp = nestBean.getTime().split(" ");
                                    if (null != tmp) {
                                        holder.setText(R.id.tv_time, tmp[1]);
                                        holder.setText(R.id.tv_date, tmp[0]);
                                    }
                                    holder.setText(R.id.tv_content, nestBean.getContent());
                                }
                            });
                        } else {
                            tvHint.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvHint.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFail(PkgDetailsBean pkgDetailsBean) {
                    tvHint.setVisibility(View.VISIBLE);
                    AppUtils.stopProgressDialog(progressDialog);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_menu, R.id.iv_edit_content/*, R.id.tv_look_pkg*/, R.id.tv_copy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                StringUtils.copyText(mNumber.getText().toString().trim(), PkgDetailsActivity.this, "复制单号成功");
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_menu:
                List<String> list = new ArrayList<>();
                list.add("复制单号");
                KLog.d(mStatusId);
                if (!"3".equals(mStatusId) && !"9".equals(mStatusId)) {
                    //历史包裹详情
                    list.add("编辑备注");
                }
                new SuperDialog.Builder(this)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                if (position == 0) {
                                    StringUtils.copyText(mNumber.getText().toString().trim(), PkgDetailsActivity.this, "复制单号成功");
                                } else if (position == 1) {
                                    showEditView();
                                }
                                KLog.d(position);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;
            case R.id.iv_edit_content:
                showEditView();
                break;
           /* case R.id.tv_look_pkg:
                new SuperDialog.Builder(this).setRadius(10)
                        .setAlpha(1f)
                        .setTitle("提示").setMessage("亲，提前看包裹图片, 需要2块费用。")
                        .setPositiveButton("确定", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetUtils.hasAvailableNet(PkgDetailsActivity.this)) {
                                    progressDialog = AppUtils.startProgressDialog(PkgDetailsActivity.this, "", progressDialog);

                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("id", mId);
                                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.aheadLookPkg, params, new RespModifyMsgCallback(PkgDetailsActivity.this, progressDialog) {
                                        @Override
                                        public void onSuccess() {
                                            AppUtils.stopProgressDialog(progressDialog);
                                            AppUtils.goChat(PkgDetailsActivity.this);
                                            finish();
                                        }

                                        @Override
                                        public void onFail(String str) {
                                            ShowToastUtils.toast(PkgDetailsActivity.this, "查看失败", 2);
                                            AppUtils.stopProgressDialog(progressDialog);
                                        }
                                    });
                                } else {
                                    ShowToastUtils.toast(PkgDetailsActivity.this, getString(R.string.no_net_hint));
                                }
                            }
                        })
                        .setNegativeButton("取消", new SuperDialog.OnClickNegativeListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        }).build();
                break;*/
        }
    }

    class ExpressAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(PkgDetailsActivity.this).inflate(R.layout.list_item_express_message, null);
                viewHolder = new ViewHolder();
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.nameLogo = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Data msg = mList.get(i);
            if (i == 0) {
              /*  在oppo 5.1系统显示 java.lang.NoSuchMethodError: No virtual method getColor(I)
               viewHolder.time.setTextColor(getColor(R.color.default_color));
                viewHolder.date.setTextColor(getColor(R.color.default_color));
                viewHolder.content.setTextColor(getColor(R.color.default_color));*/
                viewHolder.time.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                viewHolder.date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                viewHolder.content.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.default_color));
                if (null != mStatusMsg) {
                    if ("2".equals(mStatusMsg) || "6".equals(mStatusMsg)) {
                        viewHolder.nameLogo.setImageResource(R.drawable.error_pkg_details);
                    } else if ("3".equals(mStatusMsg)) {
                        viewHolder.nameLogo.setImageResource(R.drawable.yes_pkg_details);
                    }
                }
                viewHolder.nameLogo.setImageResource(R.drawable.landmark);
            } else {
              /*  viewHolder.time.setTextColor(getColor(R.color.wuliu_default));
                viewHolder.date.setTextColor(getColor(R.color.wuliu_default));
                viewHolder.content.setTextColor(getColor(R.color.wuliu_default));*/
                viewHolder.time.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                viewHolder.date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                viewHolder.content.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                viewHolder.nameLogo.setImageResource(R.drawable.default_landmark);
            }
            String[] tmp = msg.getTime().split(" ");
            if (null != tmp) {
                viewHolder.time.setText(tmp[1]);
                viewHolder.date.setText(tmp[0]);
            }
            viewHolder.content.setText(msg.getContent());
            return convertView;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        KLog.d();
        if (keyCode == event.KEYCODE_ENTER) {
            String tmpContext = mContent.getText().toString().trim();
            if (StringUtils.isEmpty(tmpContext)) {
                ShowToastUtils.toast(PkgDetailsActivity.this, "请输入你要备注的内容");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showEditView() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("输入备注")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String tmpContext = et.getText().toString().trim();
                        if (StringUtils.isEmpty(tmpContext)) {
                            ShowToastUtils.toast(PkgDetailsActivity.this, "请输入你要备注的内容");
                            return;
                        }
                        final Activity activity = PkgDetailsActivity.this;
                        if (NetUtils.hasAvailableNet(activity)) {
                            progressDialog = AppUtils.startProgressDialog(activity, "", progressDialog);

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("id", mId);
                            params.put("content", tmpContext);
                            params.put("mail_no", mBean.getMail_no());
                            params.put("carrier", mBean.getCarrier_id());
                            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.updatePkg, params, new RespModifyMsgCallback(activity, progressDialog) {
                                @Override
                                public void onSuccess() {
                                    AppUtils.stopProgressDialog(progressDialog);
                                    ShowToastUtils.toast(activity, "备注成功", 1);
                                    mContent.setText(tmpContext);
                                }

                                @Override
                                public void onFail(String str) {
                                    AppUtils.stopProgressDialog(progressDialog);
                                    ShowToastUtils.toast(activity, "备注失败", 2);
                                }
                            });
                        } else {
                            ShowToastUtils.toast(activity, getString(R.string.no_net_hint));
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
