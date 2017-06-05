package au.com.hbuy.aotong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeekBackActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_feekback)
    ClearEditText etFeekback;
    @Bind(R.id.bt_ok)
    Button btOk;
    @Bind(R.id.et_contact)
    ClearEditText etContact;
    @Bind(R.id.tv_question)
    TextView tvQuestion;
    private Activity mContext;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feekback_view);
        ButterKnife.bind(this);
        mContext = this;

        Spannable span2 = new SpannableString(tvQuestion.getText());
        span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.used_text_color)), 4, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span2.setSpan(new AbsoluteSizeSpan(24), 4, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvQuestion.setText(span2);
    }

    @OnClick({R.id.iv_back, R.id.bt_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_ok:
                String context = etFeekback.getText().toString().trim();
                String contact = etContact.getText().toString().trim();
                if (contact.equals("")) {
                    ShowToastUtils.toast(this, "请输入联系方式");
                    return;
                }
                if (context.length() < 5) {
                    ShowToastUtils.toast(this, "提交的反馈信息不能少于5个字符");
                    return;
                }
                if (NetUtils.hasAvailableNet(this)) {
                    //go login
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("content", context);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.FEEKBACK, params, new RespModifyMsgCallback(mContext) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mContext, "我们已经收到你的反馈", 1);
                            // 延迟进入
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2000);
                        }

                        @Override
                        public void onFail(String str) {
                            ShowToastUtils.toast(mContext, "提交反馈失败");
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mContext, getString(R.string.net_hint));
                }
                break;
        }
    }
}
