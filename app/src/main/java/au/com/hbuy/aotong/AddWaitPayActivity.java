package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.WaitPayBean;
import au.com.hbuy.aotong.greenDao.gen.WaitPayBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.view.BadgeView;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddWaitPayActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_name)
    ClearEditText etName;
    @Bind(R.id.et_num)
    ClearEditText etNum;
    @Bind(R.id.tv_issue)
    TextView tvIssue;
    @Bind(R.id.iv_buy_cart)
    ImageView ivBuyCart;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    private Long mId;
    public final static String HINTNUM = "waitpayhintnum";
    private BadgeView badge3;
    private Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwaitpay);
        ButterKnife.bind(this);
        mContext = AddWaitPayActivity.this;
        KLog.d();
        WaitPayBean bean = (WaitPayBean) getIntent().getSerializableExtra("bean");
        if (null != bean) {
            mId = bean.getId();
            tvTitle.setText("修改信息");
            btAddPkg.setText("确认修改");
            etName.setText(bean.getName());
            etNum.setText(bean.getNum());
        }

        int po3 = getIntent().getIntExtra(HINTNUM, 0);
        if (po3 > 0) {
            //   mSubmit.setPadding(30, 0, 0, 0);
            if (null == badge3) {
                badge3 = new BadgeView(this, ivBuyCart);
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

        /**
         * 只能输入二位小数
         */
        etNum.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.bt_add_pkg, R.id.tv_issue, R.id.iv_buy_cart, R.id.iv_cart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_issue:
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.waitpay_show_dialog_img);
                Dialog dialog = CustomDialog.create(mContext, imageView);
                dialog.show();
                break;
            case R.id.bt_add_pkg:
                if (StringUtils.isEmpty(AppUtils.getTextStr(etName)) || StringUtils.isEmpty(AppUtils.getTextStr(etNum))) {
                    ShowToastUtils.toast(this, "亲,请补全信息", 3);
                    return;
                }
                WaitPayBeanDao messageDao = GreenDaoManager.getInstance().getSession().getWaitPayBeanDao();
                String name = AppUtils.getTextStr(etName);
                String num = AppUtils.getTextStr(etNum);
                if ("确认修改".equals(AppUtils.getTextStr(btAddPkg))) {
                    WaitPayBean b = messageDao.queryBuilder()
                            .where(WaitPayBeanDao.Properties.Id.eq(mId)).build().unique();
                    b.setNum(num);
                    b.setName(name);
                    messageDao.update(b);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    WaitPayBean bean = new WaitPayBean();
                    bean.setNum(num);
                    bean.setName(name);
                    messageDao.insert(bean);
                    new SuperDialog.Builder(this)
                            .setAlpha(1f)
                            .setRadius(20)
                            .setWidth(0.8f)
                            .setTitle("提示").setMessage("亲，添加成功,是否继续添加")
                            .setPositiveButton("继续", new SuperDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick(View v) {
                                    etName.setText("");
                                    etNum.setText("");
                                }
                            })
                            .setNegativeButton("否", new SuperDialog.OnClickNegativeListener() {
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(new Intent(mContext, WaitPayListActivity.class));
                                    finish();
                                }
                            })
                            .build();
                }
                break;
            case R.id.iv_buy_cart:
                AppUtils.showActivity(this, WaitPayListActivity.class);
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChatByBuy(this);
                break;
        }
    }
}
