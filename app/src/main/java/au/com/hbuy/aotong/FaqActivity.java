package au.com.hbuy.aotong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import au.com.hbuy.aotong.utils.ConfigConstants;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class FaqActivity extends BaseActivity{
    private WebView webview;
    private TextView mTitle;
    private ImageView mBack;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        webview = (WebView) findViewById(R.id.webview);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.reload();
                finish();//结束退出程序
            }
        });
        String url = getIntent().getStringExtra("url");
        if (null != url)
        if (url.equals(ConfigConstants.faq_url)) {
            mTitle.setText("常见问题");
        } else if (url.equals(ConfigConstants.flow_url)) {
            mTitle.setText("流程介绍");
        } else if (url.equals(ConfigConstants.us_url)) {
            mTitle.setText("关于我们");
        } else if (url.equals(ConfigConstants.SHIPING)) {
            mTitle.setText("视频简介");
        } else if (url.equals(ConfigConstants.FREIGHT_URL)) {
            mTitle.setText("运费估算");
        } else if (url.equals(ConfigConstants.DOWNLOAD_URL)) {
            mTitle.setText("下载更新");
        }  else if (url.equals(ConfigConstants.TRACK_URL)) {
            mTitle.setText("查询物流");
        }   else if (url.equals(ConfigConstants.FREIGHT_IMG_URL)) {
            mTitle.setText("价格表");
        } else {
            mTitle.setText("最新资讯");
        }
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        webview.loadUrl(url);
        //设置Web视图
        webview.setWebViewClient(new webViewClient());

    }

    @Override
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webview.reload();
            String type = getIntent().getStringExtra("type");
            if (null != type && type.equals("1")) {
                KLog.d(type);
                setResult(RESULT_OK);
                finish();
            }
            finish();
            return true;
        }
        return false;
    }

    //Web视图
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
