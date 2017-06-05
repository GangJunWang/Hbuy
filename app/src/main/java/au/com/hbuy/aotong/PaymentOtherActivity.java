package au.com.hbuy.aotong;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.UrlSafeBase64;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.OtherBeanZhifb;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.position.OnLeftPosCallback;
import zhy.com.highlight.position.OnRightPosCallback;
import zhy.com.highlight.position.OnTopPosCallback;
import zhy.com.highlight.shape.CircleLightShape;
import zhy.com.highlight.shape.RectLightShape;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 * 选择付款方式界面
 */
public class PaymentOtherActivity extends BaseFragmentActivity implements TakePhoto.TakeResultListener, InvokeListener {
    private static final int SDK_PAY_FLAG = 1;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    /*  @Bind(R.id.tv_order_name)
      TextView tvOrderName;*/
    @Bind(R.id.tv_order_money)
    TextView tvOrderMoney;
    @Bind(R.id.tv_nation)
    TextView tvNation;
    @Bind(R.id.tv_bank)
    TextView tvBank;
    @Bind(R.id.bt_ok_payment)
    Button btOkPayment;
    @Bind(R.id.tv_issue)
    TextView tvIssue;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_bank_name)
    TextView tvBankName;
    @Bind(R.id.tv_bank_num)
    TextView tvBankNum;
    @Bind(R.id.tv_bank_no)
    TextView tvBankNo;
    @Bind(R.id.iv_upload)
    ImageView ivUpload;
    @Bind(R.id.tv_select_img)
    TextView tvSelectImg;
    private HighLight mHightLight;
    private String mNo, mMoney, mNum, mPrice;
    private int mPayStyle = 0;
    private CustomProgressDialog progressDialog;
    private List<OtherBeanZhifb> mList;
    private PaymentOtherActivity mActivity = PaymentOtherActivity.this;
    private String mBankId;
    private PopupWindow popupWindow;
    public TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String avater = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_other);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mNo = intent.getStringExtra("no");
        mMoney = intent.getStringExtra("money");

//        tvOrderName.setText(mNo);
        tvOrderMoney.setText(mMoney);
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("no", mNo);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GETDETAILS, params, new RespTakeMessageCallback<List<OtherBeanZhifb>>(this, null, progressDialog) {
                @Override
                public void onSuccess(List<OtherBeanZhifb> otherBeanZhifbs) {
                    AppUtils.stopProgressDialog(progressDialog);
                    if (null != otherBeanZhifbs) {
                        mList = otherBeanZhifbs;
                    }
                }

                @Override
                public void onFail(List<OtherBeanZhifb> otherBeanZhifbs) {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, "获取服务器信息失败");
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    /**
     * 显示next模式提示布局
     */
    public void showNextTipView(){
        mHightLight = new HighLight(mActivity)//
                .addHighLight(R.id.tv_nation,R.layout.info_gravity_left_down,new OnLeftPosCallback(45),new RectLightShape())
                .addHighLight(R.id.layout_other,R.layout.info_gravity_right_up,new OnTopPosCallback(205),new CircleLightShape())
                .addHighLight(R.id.iv_upload,R.layout.info_down,new OnTopPosCallback(),new CircleLightShape())
                .autoRemove(false)
                .enableNext()
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        mHightLight.next();
                    }
                });
        mHightLight.show();
    }

    private void selectNation(final List<OtherBeanZhifb> list, String title, final TextView tv) {

        String tmp = "";
        for (int i = 0; i < list.size(); i++) {
            String country = list.get(i).getCountry_name();
            KLog.d(tmp.contains(country));
            if (!tmp.contains(country)) {
                KLog.d(tmp);
                tmp += list.get(i).getCountry_name() + ",";
                //   items[i] = list.get(i).getCountry_name();
            }
        }
        String[] tmparr = tmp.split(",");
        final String[] items = new String[tmparr.length];
        if (null == tmp) {
            return;
        } else {
            for (int i = 0; i < tmparr.length; i++) {
                items[i] = tmparr[i];
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tvNation.setText(items[which]);
                tvBank.setText("请选择");
            }
        });
        builder.create().show();
    }

    private void selectBank(final String nation, String title, final TextView tv) {
        List<String> list = new ArrayList();
        for (OtherBeanZhifb bean : mList) {
            if (bean.getCountry_name().equals(nation)) {
                list.add(bean.getBank_name());
            }
        }
        KLog.d(list.size());
        final String items[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tvBank.setText(items[which]);
                for (OtherBeanZhifb bean : mList) {
                    if (items[which].equals(bean.getBank_name()) && nation.equals(bean.getCountry_name())) {
                        //    tvAobiMoney.setText(bean.getMoney());
                       /* tvAobi.setText(bean.getMoney_name());
                        tvBankDetails.setText("account_name: " +
                                bean.getAccount_name() + "\n" + "account_bsb: " +
                                bean.getAccount_bsb() + "\n" + "account_no: " + bean.getAccount_no());*/
                        tvMoney.setText("Transfer Money: " + bean.getMoney_name() + bean.getMoney());
                        tvBankName.setText("Account Name: " + bean.getAccount_name());
                        tvBankNum.setText("Account Bsb: " + bean.getAccount_bsb());
                        tvBankNo.setText("Account No: " + bean.getAccount_no());
                    }
                }
            }
        });
        builder.create().show();
    }

    @OnClick({R.id.iv_back, R.id.tv_nation, R.id.tv_bank, R.id.tv_select_img, R.id.bt_ok_payment, R.id.tv_issue, R.id.iv_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_nation:
                if (null != mList)
                    selectNation(mList, "请选择国家", tvNation);
                break;
            case R.id.tv_bank:
                String nation = tvNation.getText().toString().trim();
                if ("".equals(nation)) {
                    ShowToastUtils.toast(this, "请选择国家");
                    return;
                }
                selectBank(nation, "请选择银行", tvBank);
                break;
            case R.id.tv_select_img:
                View v = LayoutInflater.from(mActivity).inflate(R.layout.fragment_image_detail, null);
                popupWindow = new PopupWindow(v,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                popupWindow.setFocusable(true);
                ColorDrawable dw = new ColorDrawable(0x00000000);
                popupWindow.setBackgroundDrawable(dw);
                popupWindow.setWidth(800);
                popupWindow.setHeight(1200);
                ImageView imageView = (ImageView) v.findViewById(R.id.image);
                imageView.setImageResource(R.drawable.payment_other_hint_img);
                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                popupWindow.showAtLocation(ivUpload, Gravity.CENTER, 0, 0);
                break;
            case R.id.bt_ok_payment:
                String bankName = tvBank.getText().toString().trim();
                String bankId = "";
                if ("".equals(bankName) || "请选择".equals(bankName)) {
                    ShowToastUtils.toast(mActivity, "请选择银行");
                    return;
                }
                for (OtherBeanZhifb bean : mList) {
                    if (bankName.equals(bean.getBank_name())) {
                        bankId = bean.getId();
                    }
                }
                if (null == avater || (null != avater && "".equals(avater))) {
                    ShowToastUtils.toast(mActivity, "亲, 请上传凭证图片");
                    return;
                }
                KLog.d("no " + mNo + "---" + bankId);
                if ("".equals(mNo) || "".equals(bankId)) {
                    ShowToastUtils.toast(mActivity, "亲补齐信息再提交");
                    return;
                }
                if (NetUtils.hasAvailableNet(this)) {
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("picture", avater);
                    params.put("bcid", bankId);
                    params.put("order", mNo);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.UPLOAD, params, new RespModifyMsgCallback(this, progressDialog) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(progressDialog);
                            Intent intent = new Intent(mActivity, PaymentActivity.class);
                            intent.putExtra("no", mNo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFail(String status) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if ("0".equals(status)) {
                                ShowToastUtils.toast(mActivity, "失败", 2);
                            } else if ("-1".equals(status)) {
                                ShowToastUtils.toast(mActivity, "参数错误");
                            }
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint));
                }
                break;
            case R.id.tv_issue:
                showNextTipView();
                break;
            case R.id.iv_upload:
                AppUtils.showUploadAvater(mActivity, takePhoto, 0);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImages().get(0).getCompressPath();
        if (NetUtils.hasAvailableNet(this) && null != path) {
            final File file = new File(path);
                  /*  long size = FileUtils.getFileSize(file);
                    if (!StringUtils.isImageFile(path)) {
                        ShowToastUtils.toast(this, "头像不支持这个文件格式");
                        return;
                    }
                    if (!FileUtils.isOkAvatar(size)) {
                        ShowToastUtils.toast(this, "头像大小不能超过2M");
                        return;
                    }*/
            try {
                // 1 构造上传策略
                JSONObject _json = new JSONObject();
                long _dataline = System.currentTimeMillis() / 1000 + 3600;
                _json.put("deadline", _dataline);// 有效时间为一个小时
                _json.put("scope", ConfigConstants.QINIU_BUCKET);
                String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json
                        .toString().getBytes());
                byte[] _sign = AppUtils.HmacSHA1Encrypt(_encodedPutPolicy, ConfigConstants.QINIU_SECRETKEY);
                String _encodedSign = UrlSafeBase64.encodeToString(_sign);
                String _uploadToken = ConfigConstants.QINIU_ACCESSKEY + ':' + _encodedSign + ':'
                        + _encodedPutPolicy;
                final String fileName = AppUtils.getFileName(mActivity);
                UploadManager uploadManager = new UploadManager();
                uploadManager.put(file, fileName, _uploadToken,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info,
                                                 JSONObject response) {
                                KLog.d(info.toString() + "---" + response + "---info = " + info.isOK() + "info=" + info);
                                if (info.isOK()) {
                                    avater = fileName;
                                    ShowToastUtils.toast(mActivity, "上传图片成功", 1);
                                    if (null != file)
                                        Picasso.with(mActivity)
                                                .load(file)
                                                .placeholder(R.drawable.hbuy)
                                                .error(R.drawable.hbuy)
                                                .fit()
                                                .tag(this)
                                                .into(ivUpload);
                                    KLog.d(avater + "---" + "---fileName = " + fileName);
                                } else {
                                    ShowToastUtils.toast(mActivity, "上传图片失败,请稍后再试", 2);
                                }
                            }
                        }, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        ShowToastUtils.toast(this, "上传图片失败", 2);
    }

    @Override
    public void takeCancel() {
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }
}
