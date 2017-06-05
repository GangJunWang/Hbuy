package au.com.hbuy.aotong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AboutNetTestActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.bt_net_test)
    Button btNetTest;
    @Bind(R.id.pb)
    ProgressBar pb;
    private Activity mContext;
    private final String[] mNets = {
            "www.weibo.com",
            "upload.qiniu.com",
            "user.hbuy.com.au",
            "168.1.40.39",
            "api.cn.ronghub.com",
            "www.facebook.com"
    };
    @Bind(R.id.tv_show_num)
    TextView tvShowNum;
    @Bind(R.id.tv_content)
    TextView tvContent;
    private Subscription mSubscription;
    private int progress = 1;
    private String mUploadStr = "";
    private String mHintStr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_test_activity);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick({R.id.iv_back, R.id.bt_net_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_net_test:
                testNet();
                break;
        }
    }

    public String getNetStatus(String net) {
        KLog.d();
        Process p;
        StringBuffer buffer = null;
        try {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + net);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
//            if (status == 0) {
//                result = "success";
//            } else {
//                result = "faild";
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private void testNet() {
        for (int i = 0; i < mNets.length; i++) {
            final int j = i;
            KLog.d(mNets[i]);
            Observable o = Observable.create(new Observable.OnSubscribe<String>() {

                @Override
                public void call(Subscriber<? super String> subscriber) {
                    subscriber.onNext(getNetStatus(mNets[j]));
                    subscriber.onCompleted();
                }
            });
            pb.setVisibility(View.VISIBLE);

            mSubscription = o
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            KLog.d();
                        }

                        @Override
                        public void onError(Throwable e) {
                            KLog.d(e);
                        }

                        @Override
                        public void onNext(String o) {
                            int p = progress * (100 / 6);
                            KLog.d(progress);
                            KLog.d(p);
                            mUploadStr += o;
                            KLog.d(mUploadStr);
                            mHintStr += "Ping :" + mNets[progress - 1] + "...\n\n\n";
                            KLog.d(mHintStr);

                            KLog.d(progress == 6);
                            if (progress == 6) {
                                p = 100;
                                if (NetUtils.hasAvailableNet(mContext)) {
                                    //go login
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("content", mUploadStr);
                                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.FEEKBACK, params, new RespModifyMsgCallback(mContext) {
                                        @Override
                                        public void onSuccess() {
                                            ShowToastUtils.toast(mContext, "我们已经收到你的反馈", 1);
                                            mHintStr += "我们已经收到你的反馈";
                                            tvContent.setText(mHintStr);
                                        }

                                        @Override
                                        public void onFail(String str) {
                                            ShowToastUtils.toast(mContext, "提交反馈失败");
                                            mHintStr += "提交反馈失败";
                                            tvContent.setText(mHintStr);
                                        }
                                    });
                                } else {
                                    ShowToastUtils.toast(mContext, getString(R.string.net_hint));
                                }
                            }
                            pb.setProgress(p);
                            tvShowNum.setText(p + "%");
                            progress++;
                            tvContent.setText(mHintStr);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSubscription)
            mSubscription.unsubscribe();
    }
}
