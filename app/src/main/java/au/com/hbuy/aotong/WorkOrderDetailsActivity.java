package au.com.hbuy.aotong;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.adapter.CircleGridAdapter;
import au.com.hbuy.aotong.domain.WordBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.GifView;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;
import au.com.hbuy.aotong.view.NoScrollGridView;
import au.com.hbuy.aotong.view.OverScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkOrderDetailsActivity extends BaseDetailsActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_touch_us)
    ImageView tvTouchUs;
    @Bind(R.id.title_tab)
    RelativeLayout titleTab;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.tv_time_01)
    TextView tvTime01;
    @Bind(R.id.layout_hint_transfer)
    RelativeLayout layoutHintTransfer;
    @Bind(R.id.lv_transter_list)
    NestFullListView lvTransterList;
    @Bind(R.id.tv_service_charge)
    TextView tvServiceCharge;
    @Bind(R.id.tv_time_03)
    TextView tvTime03;
    @Bind(R.id.layout_hint_pic)
    RelativeLayout layoutHintPic;
    @Bind(R.id.gridView)
    NoScrollGridView gridView;
    @Bind(R.id.tv_time_02)
    TextView tvTime02;
    @Bind(R.id.layout_hint_puck)
    RelativeLayout layoutHintPuck;
    @Bind(R.id.lv_unpack_list)
    NestFullListView lvUnpackList;
    @Bind(R.id.tv_other)
    TextView tvOther;
    @Bind(R.id.tv_yunfei)
    TextView tvYunfei;
    @Bind(R.id.layout_unpack)
    LinearLayout layoutUnpack;
    @Bind(R.id.bt_ok)
    Button btOk;
    @Bind(R.id.other_layout)
    RelativeLayout otherLayout;
    @Bind(R.id.gridView_beform)
    View gridViewBeform;
    @Bind(R.id.layout_hint_puck_beform)
    RelativeLayout layoutHintPuckBeform;
    @Bind(R.id.layout_01)
    RelativeLayout layout01;
    @Bind(R.id.layout_02)
    RelativeLayout layout02;
    @Bind(R.id.layout_03)
    RelativeLayout layout03;
    @Bind(R.id.tv_hint_gif)
    GifView tvHintGif;
    @Bind(R.id.view_transfer)
    View viewTransfer;
    @Bind(R.id.layout_transfer)
    LinearLayout layoutTransfer;
    @Bind(R.id.tv_hint_puck)
    TextView tvHintPuck;
    @Bind(R.id.iv_puck_img)
    LinearLayout ivPuckImg;
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    private String mId, mStatus;
    private CustomProgressDialog progressDialog;
    private FragmentActivity mActivity;
    private int mPicHeight = 0, mTransferHeight = 0;
    private boolean mIsFirst = true;
    private CircleGridAdapter mCircleGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_details);
        mActivity = WorkOrderDetailsActivity.this;
        ButterKnife.bind(this);
        layout = (OverScrollView) findViewById(R.id.layout);
        Intent mIntent = getIntent();
        mId = mIntent.getStringExtra("no");
        initData();
        setListener();
    }

    public void initData() {
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("no", mId);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getItemWorkOrder, params, new RespTakeMessageCallback<WordBean>(mActivity, null, progressDialog) {
                @Override
                public void onSuccess(WordBean o) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if (null != o) {
                        tvNo.setText("打包号: " + o.getNo());
                        mStatus = o.getStatus();
                        if ("0".equals(mStatus)) {
                            layoutHintPuck.setEnabled(false);
                            tvHintPuck.setEnabled(false);
                            tvHintGif.setVisibility(View.VISIBLE);
                            tvCancel.setVisibility(View.VISIBLE);
                            tvHint.setVisibility(View.VISIBLE);
                            //        tvStatus.setText("待处理");
                            String tmpWaitting = o.getWaiting();
                            if (StringUtils.toInt(tmpWaitting) > 0) {
                                tvHint.setText("还有" + tmpWaitting + "人, 马上到您啦");
                            } else {
                                tvHint.setText("马上到您咯！~");
                            }
                        } else if ("1".equals(mStatus)) {
                            tvCancel.setVisibility(View.VISIBLE);
                            //         tvStatus.setText("取包裹");
                            layoutHintPuck.setEnabled(false);
                            tvHintPuck.setEnabled(false);
                        } else if ("2".equals(mStatus)) {
                            tvCancel.setVisibility(View.VISIBLE);
                            //       tvStatus.setText("取包完成");
                            layoutHintPuck.setEnabled(false);
                            tvHintPuck.setEnabled(false);
                        } else if ("3".equals(mStatus)) {
                            tvHintGif.setVisibility(View.GONE);
                            tvHint.setVisibility(View.GONE);
                            btOk.setVisibility(View.VISIBLE);
                            //     tvStatus.setText("拆包拍照完成");
                            layoutHintPuck.setEnabled(false);
                            tvHintPuck.setEnabled(false);
                        } else if ("4".equals(mStatus)) {
                            tvHint.setVisibility(View.GONE);
                            tvHintGif.setVisibility(View.GONE);
                            btOk.setVisibility(View.VISIBLE);
                            btOk.setText("沟通客服");
                            //   tvStatus.setText("已确认");
                            layoutHintPuck.setEnabled(false);
                            tvHintPuck.setEnabled(false);
                            SharedUtils.putBoolean(mActivity, "isVisible", true);
                        } else if ("5".equals(mStatus)) {
                            tvHint.setVisibility(View.GONE);
                            tvHintGif.setVisibility(View.GONE);
                            //    tvStatus.setText("打包完成");
                            btOk.setVisibility(View.VISIBLE);
                            btOk.setText("去下单");
                            SharedUtils.putBoolean(mActivity, "isVisible", true);
                        } else if ("6".equals(mStatus)) {
                            tvHint.setVisibility(View.GONE);
                            tvHintGif.setVisibility(View.GONE);
                            //    tvStatus.setText("已取消");
                        } else {
                            tvHint.setVisibility(View.GONE);
                            tvHintGif.setVisibility(View.GONE);
                            //      tvStatus.setText("打包完成");
                            btOk.setVisibility(View.VISIBLE);
                            btOk.setText("去下单");
                            SharedUtils.putBoolean(mActivity, "isVisible", true);
                        }
                        List<WordBean.BeanBy> pkgs = o.getPkgs();
                        if (null != pkgs) {
                            layoutTransfer.setVisibility(View.VISIBLE);
                            lvTransterList.setAdapter(new NestFullListViewAdapter<WordBean.BeanBy>(R.layout.orderdetails_item_list_item_transfer, pkgs) {
                                @Override
                                public void onBind(int pos, WordBean.BeanBy nestBean, NestFullViewHolder holder) {
                                    holder.setText(R.id.tv_name, nestBean.getMail_no());
                                    holder.setText(R.id.tv_tiji, nestBean.getCarrier_name());
                                    holder.setText(R.id.tv_yufei, StringUtils.keepDouble(nestBean.getArrive_pay()));
                                }
                            });

                            double allMoney = 0.00;
                            DecimalFormat df = new DecimalFormat("######0.00");
                            for (WordBean.BeanBy bean : pkgs) {
                                allMoney += StringUtils.toDouble(bean.getArrive_pay()) + StringUtils.toDouble(bean.getOpen_pay());
                            }
                            tvServiceCharge.setText("服务费: ¥ " + df.format(allMoney));
                        }

                        List<WordBean.UpckBean> puck = o.getPack_pkgs();
                        KLog.d(puck);
                        if (null != puck) {
                            layoutUnpack.setVisibility(View.VISIBLE);
                            ivPuckImg.setVisibility(View.GONE);
                            lvUnpackList.setAdapter(new NestFullListViewAdapter<WordBean.UpckBean>(R.layout.orderdetails_item_list_item, puck) {
                                @Override
                                public void onBind(int pos, WordBean.UpckBean nestBean, NestFullViewHolder holder) {
                                    holder.setText(R.id.tv_name, nestBean.getCarrier_name());
                                    holder.setText(R.id.tv_tiji, nestBean.getVolume());
                                    holder.setText(R.id.tv_zhongliang, nestBean.getWeight());
                                    holder.setText(R.id.tv_yufei, StringUtils.keepDouble(nestBean.getFreight_pay()));
                                }
                            });

                            double allMoney = 0.00, freight = 0.00;
                            DecimalFormat df = new DecimalFormat("######0.00");
                            for (WordBean.UpckBean bean : puck) {
                                freight += StringUtils.toDouble(bean.getFreight_pay());
                                allMoney += StringUtils.toDouble(bean.getExtra_pay()) + StringUtils.toDouble(bean.getPackage_pay()) + StringUtils.toDouble(bean.getDegauss_pay());
                            }
                            tvYunfei.setText("运费: ¥ " + df.format(freight));
                            tvOther.setText("其他费: ¥ " + df.format(allMoney));
                        } else {
                            ivPuckImg.setVisibility(View.VISIBLE);
                            layoutUnpack.setVisibility(View.GONE);
                        }

                        KLog.d(o.getPictures());
                        if (null != o.getPictures() && o.getPictures().length > 0) {
                            gridView.setVisibility(View.VISIBLE);
                            tvHintGif.setVisibility(View.GONE);
                            final String[] tmp = o.getPictures();
                            gridView.setAdapter(mCircleGridAdapter = new CircleGridAdapter(tmp, mActivity));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    KLog.d(i);
                                    AppUtils.enterPhotoDetailed(mActivity, tmp, i);
                                }
                            });
                        } else {
                            gridView.setVisibility(View.GONE);
                            tvHintGif.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFail(WordBean o) {
                    AppUtils.stopProgressDialog(progressDialog);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mIsFirst) {
            mIsFirst = false;
            mTransferHeight = layoutTransfer.getMeasuredHeight();
            setViewHeight(mTransferHeight, viewTransfer);

            mPicHeight = layout02.getMeasuredHeight();
            setViewHeight(mPicHeight, gridViewBeform);
        }
        KLog.d("beformGrid=" + gridView.getHeight() + "--after=" + gridViewBeform.getHeight());
        KLog.d("beformTransfer=" + layoutTransfer.getHeight() + "--after=" + viewTransfer.getHeight());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.iv_back, R.id.tv_cancel, R.id.tv_touch_us, R.id.layout_hint_transfer, R.id.layout_hint_pic, R.id.layout_hint_puck, R.id.bt_ok, R.id.tv_copy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_cancel:
                new SuperDialog.Builder(mActivity).setRadius(10)
                        .setAlpha(1f)
                        .setTitle("温馨提示").setMessage("您确定取消该单吗？")
                        .setPositiveButton("取消", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetUtils.hasAvailableNet(mActivity)) {
                                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                                    HashMap params = new HashMap();
                                    params.put("no", mId);
                                    ApiClient.postForm(ConfigConstants.CANCELWORKORDER, params, new RespModifyMsgCallback(mActivity, progressDialog) {
                                        @Override
                                        public void onSuccess() {
                                            AppUtils.stopProgressDialog(progressDialog);
                                            ShowToastUtils.toast(mActivity, "取消成功", 1);
                                            //           tvStatus.setText("已取消");
                                            tvCancel.setVisibility(View.GONE);
                                        }
                                        @Override
                                        public void onFail(String str) {
                                            //0失败-1状态不对无法取消-2工单不存在
                                            if ("0".equals(str)) {
                                                ShowToastUtils.toast(mActivity, "失败", 2);
                                            } else if ("-1".equals(str)) {
                                                ShowToastUtils.toast(mActivity, "状态不对无法取消");
                                            } else if ("-2".equals(str)) {
                                                ShowToastUtils.toast(mActivity, "不存在");
                                            }
                                            AppUtils.stopProgressDialog(progressDialog);
                                        }
                                    });
                                } else {
                                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                                }
                            }
                        }).setNegativeButton("不取消了", new SuperDialog.OnClickNegativeListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).build();
                break;
            case R.id.tv_touch_us:
                AppUtils.goChat(this);
                break;
            case R.id.layout_hint_transfer:
                if (layout01.getVisibility() == View.VISIBLE) {
                    layout01.setVisibility(View.GONE);

                    KLog.d(mTransferHeight + "--" + layout01.getHeight() + "--view=" + viewTransfer.getHeight());
                } else if (layout01.getVisibility() == View.GONE) {
                    layout01.setVisibility(View.VISIBLE);

                    setViewHeight(mTransferHeight, viewTransfer);
                    KLog.d(mTransferHeight + "--" + layout01.getHeight());
                }
                KLog.d("beformTransfer=" + layoutTransfer.getHeight() + "--after=" + layout01.getHeight());
                break;
            case R.id.layout_hint_pic:
                if (layout02.getVisibility() == View.VISIBLE) {
                    layout02.setVisibility(View.GONE);
                } else if (layout02.getVisibility() == View.GONE) {
                    layout02.setVisibility(View.VISIBLE);
                    setViewHeight(mPicHeight, gridViewBeform);
                    KLog.d(mPicHeight + "--" + gridViewBeform.getHeight());
                }
                KLog.d("beformGrid=" + gridView.getHeight() + "--after=" + gridViewBeform.getHeight());
                break;
            case R.id.layout_hint_puck:
                if (layout03.getVisibility() == View.VISIBLE) {
                    layout03.setVisibility(View.GONE);
                } else if (layout03.getVisibility() == View.GONE) {
                    layout03.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bt_ok:
                String btStr = btOk.getText().toString().trim();
                if ("去下单".equals(btStr)) {
                    AppUtils.showActivity(mActivity, WaitPlaceOrderActivity.class);
                    finish();
                    return;
                }
                if (("沟通客服").equals(btStr)) {
                    AppUtils.goChat(mActivity);
                    return;
                }
                if (AppUtils.isNotFastClick()) {
                    if (NetUtils.hasAvailableNet(this)) {
                        progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("no", mId);
                        ApiClient.postForm(ConfigConstants.CONFIRMPKG, params, new RespModifyMsgCallback(this, progressDialog) {
                            @Override
                            public void onSuccess() {
                                AppUtils.stopProgressDialog(progressDialog);
                                ShowToastUtils.toast(mActivity, "提交成功", 1);
                                //        tvStatus.setText("已确认");
                                btOk.setVisibility(View.VISIBLE);
                                btOk.setText("沟通客服");
                            }

                            @Override
                            public void onFail(String str) {
                                AppUtils.stopProgressDialog(progressDialog);
                                ShowToastUtils.toast(mActivity, "提交失败", 2);
                            }
                        });
                    } else {
                        ShowToastUtils.toast(this, getString(R.string.no_net_hint));
                    }
                }
                break;
            case R.id.tv_copy:
                ClipboardManager myClipboard = (ClipboardManager)mActivity.getSystemService(mActivity.CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", mId);
                myClipboard.setPrimaryClip(myClip);
                ShowToastUtils.toast(mActivity, "复制单号成功", 1);
                break;
        }
    }

    @Override
    public void headerScroll() {
        initData();
    }

    @Override
    public void footerScroll() {

    }

    @Override
    public void scrollDistance(int tinyDistance, int totalDistance) {

    }

    @Override
    public void scrollLoosen() {

    }

    private void setViewHeight(int height, View view) {
        RelativeLayout.LayoutParams lp;
        lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.height = height;
        view.setLayoutParams(lp);
    }
}
