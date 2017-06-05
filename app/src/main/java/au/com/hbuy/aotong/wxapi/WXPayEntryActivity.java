package au.com.hbuy.aotong.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.socks.library.KLog;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import au.com.hbuy.aotong.PaySucceedActivity;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.WXManager;
/**
 * Created by yangwei on 2016/8/22--15:09.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d(getIntent());
        KLog.d();
        getIntent().getStringExtra("no");
        WXManager.instance().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.d();
        WXManager.instance().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    /**
     * 得到支付结果回调
     */
    @Override
    public void onResp(BaseResp resp) {
        Intent intent = new Intent(this, PaySucceedActivity.class);
        KLog.d(resp.errCode);
        KLog.d(resp.transaction + ((PayResp) resp).extData);
        //  Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);// 支付结果码
        switch (resp.errCode) {
            case 0:
                intent.putExtra("type", "1");
                intent.putExtra("no", ((PayResp) resp).extData);
                startActivity(intent);
                break;
            case -2:
                //付款取消
                finish();
                break;
            default:
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
        }
        finish();
    }
}