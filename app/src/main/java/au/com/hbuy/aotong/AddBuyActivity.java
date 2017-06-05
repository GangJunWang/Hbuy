package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.gen.BuyDbBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.view.BadgeView;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomDialog;
import au.com.hbuy.aotong.view.OverScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddBuyActivity extends BaseFragmentActivity implements View.OnClickListener {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_name)
    ClearEditText etName;
    @Bind(R.id.et_num)
    ClearEditText etNum;
    @Bind(R.id.et_size)
    ClearEditText etSize;
    @Bind(R.id.et_mk)
    ClearEditText etMk;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    @Bind(R.id.tv_issue)
    TextView tvIssue;
    @Bind(R.id.iv_buy_cart)
    ImageView ivBuyCart;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.layout)
    OverScrollView layout;
    @Bind(R.id.tv_hint)
    ImageView tvHint;
    private Long buyId;
    private Activity mContext;
    private BadgeView badge3;
    public final static String HINTNUM = "buyhintnum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbuy);
        mContext = AddBuyActivity.this;
        ButterKnife.bind(this);
        BuyDbBean buyBean = (BuyDbBean) getIntent().getSerializableExtra("bean");
        if (null != buyBean) {
            buyId = buyBean.getId();
            tvTitle.setText("修改信息");
            btAddPkg.setText("确认修改");
            etName.setText(buyBean.getLink());
            etSize.setText(buyBean.getSize());
            etMk.setText(buyBean.getMk());
            etNum.setText(buyBean.getNum());
        }

        int po3 = getIntent().getIntExtra(HINTNUM, 0);
        if (po3 > 0) {
            //   mSubmit.setPadding(30, 0, 0, 0);
            if (null == badge3) {
                badge3 = new BadgeView(mContext, ivBuyCart);
                badge3.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                badge3.setBadgeMargin(80, 0);
                badge3.setTextSize(10);
            }
            if (po3 > 99) {

                badge3.setText("99+");
            } else {

                badge3.setText(po3 + "");
            }
            badge3.show();
        } else {
            if (null != badge3) {
                badge3.hide();
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_add_pkg, R.id.tv_issue, R.id.iv_buy_cart, R.id.iv_cart, R.id.tv_hint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_issue:
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.show_dialog_img);
                Dialog dialog = CustomDialog.create(mContext, imageView);
                dialog.show();
                break;
            case R.id.bt_add_pkg:
                String url = AppUtils.getTextStr(etName);
                String link = null;
                KLog.d(url.indexOf("http"));
                if (null != url && url.length() > 5) {
                    if (url.substring(0, 4).equals("http")) {
                        link = url;
                    } else if (url.indexOf("http") > 0) {
                        link = url.substring(url.indexOf("http"), url.length());
                    } else {
                        ShowToastUtils.toast(this, "亲,请输入正确的商品网址", 3);
                        return;
                    }
                }
                if (null == link) {
                    ShowToastUtils.toast(this, "亲,请输入正确的商品网址", 3);
                    return;
                }
                if (StringUtils.isEmpty(url) || StringUtils.isEmpty(AppUtils.getTextStr(etNum)) || StringUtils.isEmpty(AppUtils.getTextStr(etSize))) {
                    ShowToastUtils.toast(this, "亲,请补全信息", 3);
                    return;
                }
                BuyDbBeanDao messageDao = GreenDaoManager.getInstance().getSession().getBuyDbBeanDao();
                String num = AppUtils.getTextStr(etNum);
                String size = AppUtils.getTextStr(etSize);
                String mk = AppUtils.getTextStr(etMk);
                BuyDbBean result = new BuyDbBean();
                if ("确认修改".equals(AppUtils.getTextStr(btAddPkg))) {
                    BuyDbBean b = messageDao.queryBuilder()
                            .where(BuyDbBeanDao.Properties.Id.eq(buyId)).build().unique();
                    b.setSize(size);
                    b.setLink(link);
                    b.setNum(num);
                    b.setMk(mk);
                    messageDao.update(b);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    KLog.d(result.getLink() + "---" + result.getTitle() + "---" + result.getSize());
                    result.setNum(num);
                    result.setTitle("");
                    result.setImg("");
                    result.setLink(link);
                    result.setMk(mk);
                    result.setSize(size);
                    messageDao.insert(result);
                    new SuperDialog.Builder(this)
                            .setAlpha(1f)
                            .setRadius(20)
                            .setWidth(0.8f)
                            .setTitle("提示").setMessage("亲，添加成功,是否继续添加")
                            .setPositiveButton("继续", new SuperDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick(View v) {
                                    etNum.setText("");
                                    etSize.setText("");
                                    etMk.setText("");
                                    etName.setText("");
                                }
                            })
                            .setNegativeButton("否", new SuperDialog.OnClickNegativeListener() {
                                @Override
                                public void onClick(View v) {
                                    setResult(RESULT_OK);
                                    mContext.startActivity(new Intent(mContext, BuyListActivity.class));
                                    finish();
                                }
                            })
                            .build();
                }
                //   KLog.d();
                /*if (NetUtils.hasAvailableNet(this)) {
                    String uid = SharedUtils.getString(this, "uid", "");
                    new MyTask().execute(AppUtils.getTextStr(etName), uid);
                } else {
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint));
                }*/
                break;
            case R.id.iv_buy_cart:
                AppUtils.showActivity(mContext, BuyListActivity.class);
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChatByBuy(this);
                break;
            case R.id.tv_hint:
                final Dialog d = CustomDialog.create(mContext, R.layout.issue_layout);
                d.findViewById(R.id.tv_gochat).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.goChat(mContext);
                        d.dismiss();
                    }
                });
                d.findViewById(R.id.tv_issue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, FaqActivity.class);
                        i.putExtra("url", ConfigConstants.faq_url);
                        mContext.startActivity(i);
                        d.dismiss();
                    }
                });
                d.show();
                break;
        }
    }
    /*//get 要解析的html
    public BuyDbBean doGet(String urlStr, String uid) throws Exception {
        final BuyDbBean bean = new BuyDbBean();
        Document d = Jsoup.connect(urlStr).get();
        Elements titleElement = d.select("title");
        String title = titleElement.get(0).text();
        bean.setTitle(title);
        Elements imgElement = d.select("img[src$=.jpg]");
        String img = imgElement.get(0).attr("src");
        String path = "http:" + img;
        byte[] tmp = AppUtils.getImageFromURL(path);
        final String fileName = AppUtils.getFileName(uid, path);
        try {
            // 1 构造上传策略
            JSONObject _json = new JSONObject();
            long _dataline = System.currentTimeMillis() / 1000 + 3600;
            _json.put("deadline", _dataline);// 有效时间为一个小时
            _json.put("scope", "test2");
            String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json
                    .toString().getBytes());
            byte[] _sign = AppUtils.HmacSHA1Encrypt(_encodedPutPolicy, ConfigConstants.QINIU_SECRETKEY);
            String _encodedSign = UrlSafeBase64.encodeToString(_sign);
            String _uploadToken = ConfigConstants.QINIU_ACCESSKEY + ':' + _encodedSign + ':'
                    + _encodedPutPolicy;
            UploadManager uploadManager = new UploadManager();
            KLog.d(fileName + "   path = " + path + " tmp = " + tmp);
            uploadManager.put(tmp, fileName, _uploadToken,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info,
                                             JSONObject response) {
                            KLog.d(info.toString() + "---" + response + "---info = " + info.isOK());
                            if (info.isOK()) {
                                KLog.d("fileName = " + fileName);
                                bean.setImg("http://ogmbfybyn.bkt.clouddn.com/" + fileName);
                            }
                        }
                    }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(img)) {
            return bean;
        } else {
            return null;
        }
    }

    class MyTask extends AsyncTask<String, Void, BuyDbBean>{
        @Override
        protected BuyDbBean doInBackground(String... params) {
            try {
                KLog.d(params[0]);
                return doGet(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BuyDbBean result) {
            super.onPostExecute(result);
            BuyDbBeanDao messageDao = GreenDaoManager.getInstance().getSession().getBuyDbBeanDao();

            String num = AppUtils.getTextStr(etNum);
            String size = AppUtils.getTextStr(etSize);
            String mk = AppUtils.getTextStr(etMk);
            String link = AppUtils.getTextStr(etName);

            if (null != result) {
                if ("确认修改".equals(AppUtils.getTextStr(btAddPkg)))  {
                    BuyDbBean b = messageDao.queryBuilder()
                            .where(BuyDbBeanDao.Properties.Id.eq(buyId)).build().unique();
                    b.setSize(size);
                    b.setImg(result.getImg());
                    b.setTitle(result.getTitle());
                    b.setLink(link);
                    b.setNum(num);
                    b.setMk(mk);
                    messageDao.update(b);
                } else {
                    KLog.d(result.getLink() + "---" + result.getTitle() + "---" + result.getSize());
                    result.setNum(num);
                    result.setLink(link);
                    result.setMk(mk);
                    result.setSize(size);
                    messageDao.insert(result);
                }
                KLog.d();
                finish();
            } else {
                ShowToastUtils.toast(AddBuyActivity.this, "宝贝链接错误, 请检查后输入");
            }
        }
    }
    */
}
