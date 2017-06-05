package au.com.hbuy.aotong.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.AddressRepoActivity;
import au.com.hbuy.aotong.BuyPageDetailsActivity;
import au.com.hbuy.aotong.PayOrderBuyDetailsActivity;
import au.com.hbuy.aotong.PayOrderDetailsActivity;
import au.com.hbuy.aotong.PkgDetailsActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.WorkOrderDetailsActivity;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Good;
import au.com.hbuy.aotong.domain.OrderDetails;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.domain.TakeMsgContent;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.Message;
import au.com.hbuy.aotong.greenDao.gen.MessageDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByPayBuyDetailsCallback;
import au.com.hbuy.aotong.utils.okhttp.RespGetTypeback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;
import cn.jpush.android.api.JPushInterface;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by HFS on 2016/7/30.
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        KLog.d(extra);
        Log.d(TAG, "onReceive - " + intent.getAction());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        //    KLog.d("收到了自定义消息。消息内容是" + bundle.getString(JPushInterface.EXTRA_MESSAGE) + "---" + bundle.getString(JPushInterface.EXTRA_EXTRA) + "---" + bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE));
            String type =  bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String msg_type = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            final String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            KLog.d(type + "---" + msg_type + "--" + msg + "---" + null != type);
        //    if (null != type) {
                KLog.d(type + "datacache".equals(type));
                if ("datacache".equals(type)) {
                    switch (msg_type) {
                        case "user":
                            if (null != msg) {
                                int length = msg.length();
                                KLog.d(length);
                                if (length > 12) {
                                    String kf_id = msg.substring(10, 10 + length - 12);
                                    KLog.d(kf_id);
                                    SharedUtils.putString(context, "kf_id", kf_id);
                                    if (NetUtils.hasAvailableNet(context)) {
                                        ApiClient.getInstance(context.getApplicationContext()).postForm(ConfigConstants.getRepoAddress02, new RespRepoAddressCallback<Address>(null) {
                                            @Override
                                            public void onSuccess(Address address) {
                                                KLog.d(address.getCountry());
                                                SharedUtils.putString(context, "repo_name", address.getName());
                                                SharedUtils.putString(context, "repo_address", address.getAddress());
                                                SharedUtils.putString(context, "repo_zip", address.getZip());
                                                SharedUtils.putString(context, "repo_phone", address.getPhone());
                                            }

                                            @Override
                                            public void onFail(String str) {
                                                //      ShowToastUtils.toast(AddressRepoActivity.this, "获取地址失败", 2);
                                            }
                                        });
                                    } else {
                                        //    ShowToastUtils.toast(context, context.getString(R.string.no_net_hint), 3);
                                    }
                                }
                            }
                            break;
                    }
                } else if ("function".equals(type)) {
                    switch (msg_type) {
                        case "getTransferAdsNetData":
                            if (NetUtils.hasAvailableNet(context)) {
                                ApiClient.getInstance(context.getApplicationContext()).postForm(ConfigConstants.getRepoAddress02, new RespRepoAddressCallback<Address>(null) {
                                    @Override
                                    public void onSuccess(Address address) {
                                        KLog.d(address.getCountry());
                                        SharedUtils.putString(context, "repo_name", address.getName());
                                        SharedUtils.putString(context, "repo_address", address.getAddress());
                                        SharedUtils.putString(context, "repo_zip", address.getZip());
                                        SharedUtils.putString(context, "repo_phone", address.getPhone());
                                    }

                                    @Override
                                    public void onFail(String str) {
                                  //      ShowToastUtils.toast(AddressRepoActivity.this, "获取地址失败", 2);
                                    }
                                });
                            } else {
                            //    ShowToastUtils.toast(context, context.getString(R.string.no_net_hint), 3);
                            }
                            break;
                    }
                }
          //  }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //add db
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String uid = SharedUtils.getString(context, "uid", "");

            // new Date()为获取当前系统时间
            MessageDao messageDao = GreenDaoManager.getInstance().getSession().getMessageDao();
            Message message = new Message(null, uid, title, date, content, 0);
            messageDao.insert(message);
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // 在这里可以自己写代码去定义用户点击后的行为
            try {
                Intent in = null;
                TakeMsgContent bean = JSON.parseObject(extra, TakeMsgContent.class);
                KLog.d(bean.getData());
                String[] tepArray = bean.getData().split(":");
                KLog.d(tepArray[0] + "--" + tepArray[1] + "size =" + tepArray.length);
              if (tepArray.length > 1) {
                  String name = tepArray[0];
                  String value = tepArray[1];
                  if ("package".equals(name)) {
                      //包裹详情  "value": "package://mailno/425001693883",
                      String[] pkgids = value.split("/");
                      KLog.d(pkgids);
                      if (pkgids.length > 3) {
                          KLog.d(pkgids[0] + "--" + pkgids[1] + pkgids[2] + "--" + pkgids[3] + "size =" + pkgids.length);

                          String type = pkgids[2];
                          in = new Intent(context, PkgDetailsActivity.class);
                          if ("id".equals(type)) {
                              //id 查询物流
                              in.putExtra("type", "1");
                          } else {
                              //单号查询
                              in.putExtra("type", "2");
                          }
                          in.putExtra("id", pkgids[3]);
                      }
                  } else if ("ticket".equals(name)) {
                      //工单详情"value": "ticket://1011000000000100",
                      in = new Intent(context, WorkOrderDetailsActivity.class);
                      in.putExtra("no", value.substring(2, value.length()));
                      KLog.d(value.substring(2, value.length()));
                  } else if ("order".equals(name)) {
                      //订单详情
                      final String tmp = value.substring(2, value.length());
                      HashMap<String, String> params = new HashMap<String, String>();
                      params.put("no", tmp);
                      if (NetUtils.hasAvailableNet(context)) {
                          ApiClient.getInstance(context.getApplicationContext()).postForm(ConfigConstants.getItemOrder, params, new RespGetTypeback(context) {
                              @Override
                              public void onSuccess() {

                              }
                              @Override
                              public void onFail(String str) {

                              }
                              @Override
                              public void onBusiness(String str) {
                                  Intent i = null;
                                  if ("1".equals(str)) {
                                      //转运
                                      i = new Intent(context, PayOrderDetailsActivity.class);
                                  } else {
                                      //代购
                                      i = new Intent(context, PayOrderBuyDetailsActivity.class);
                                  }
                                  i.putExtra("no", tmp);
                                  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                  context.startActivity(i);
                                  return;
                              }
                          });
                      }
                      KLog.d(value.substring(2, value.length()));
                  } else if ("helpbuy".equals(name)) {
                      //代购订单详情
                      in = new Intent(context, BuyPageDetailsActivity.class);
                      in.putExtra("no", value.substring(2, value.length()));
                      KLog.d(value.substring(2, value.length()));
                  }
                  if (null != in) {
                      in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                      context.startActivity(in);
                  }
              }
            } catch (Exception e) {
                KLog.d(e.getMessage());
                Toast.makeText(context, "信息异常", Toast.LENGTH_LONG);
            }
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
