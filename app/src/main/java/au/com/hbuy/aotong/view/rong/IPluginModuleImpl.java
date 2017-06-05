package au.com.hbuy.aotong.view.rong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.socks.library.KLog;

import java.util.HashSet;
import java.util.Iterator;

import au.com.hbuy.aotong.AddPkgActivity;
import au.com.hbuy.aotong.HandingActivity;
import au.com.hbuy.aotong.ReceiverAddressActivity;
import au.com.hbuy.aotong.TransferActivity;
import au.com.hbuy.aotong.WaitPaymentActivity;
import au.com.hbuy.aotong.WaitPlaceOrderActivity;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FileManagerActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.image.PictureSelectorActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Created by yangwei on 2017/3/10--17:12.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class IPluginModuleImpl implements IPluginModule {
    private Conversation.ConversationType conversationType;
    private String targetId;
    private int mId;
    private String title;
    private String type;
    private Context mContext;
    private RongExtension extension;

    public IPluginModuleImpl() {

    }

    public IPluginModuleImpl(int mId, String title, String type) {
        this.mId = mId;
        this.title = title;
        this.type = type;
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        //适配低版本
        return ContextCompat.getDrawable(context, mId);
    }

    @Override
    public String obtainTitle(Context context) {
        this.mContext = context;
        return title;
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        this.extension = extension;
        if ("3".equals(type)) {
            //海外地址
            Intent intent = new Intent(mContext, ReceiverAddressActivity.class);
            intent.putExtra("type", "1");
            extension.startActivityForPluginResult(intent, 1, this);
        } else if ("4".equals(type)) {
            //开始打包
            AppUtils.showActivity((Activity) mContext, TransferActivity.class);
        } else if ("5".equals(type)) {
            //处理中
            Intent ii = new Intent(mContext, HandingActivity.class);
            ii.putExtra("id", "1");//
            mContext.startActivity(ii);
        } else if ("6".equals(type)) {
            //待提交
            AppUtils.showActivity((Activity) mContext, WaitPlaceOrderActivity.class);
        } else if ("7".equals(type)) {
            //待付订单
            Intent waitList = new Intent(mContext, WaitPaymentActivity.class);
            waitList.putExtra("style", "7");
            mContext.startActivity(waitList);
        } else if ("8".equals(type)) {
            //历史订单
            Intent orderlist = new Intent(mContext, WaitPaymentActivity.class);
            orderlist.putExtra("style", "8");
            mContext.startActivity(orderlist);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Address address = (Address) data.getSerializableExtra("address");
            String tmp = "收件人: " + address.getReceiver() + "\n号码: +" + address.getPhonecode() + " " + address.getPhone() +
                    "\n国家: " + address.getCountry() + "\n地址: " + address.getCountry() + address.getCity() + address.getAddress();
            extension.getInputEditText().getText().insert(0, tmp);
            KLog.d(address.getCountry());
        }
    }
}
