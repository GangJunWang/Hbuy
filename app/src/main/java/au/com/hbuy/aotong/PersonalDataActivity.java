package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.FileUtils;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyAddMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CircleImageView;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class PersonalDataActivity extends BaseFragmentActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {
    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.layout_avater)
    RelativeLayout layoutAvater;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_sex)
    TextView mSex;
    @Bind(R.id.tv_nation)
    TextView tvNation;
    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.civ_img)
    CircleImageView mCircleImageView;
    public static final int FROM_SELECT_NATION = 4;
    private CustomProgressDialog progressDialog;
    private Activity mActivity = PersonalDataActivity.this;
    private InvokeParam invokeParam;
    public TakePhoto takePhoto;
    private String mCountryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String username = SharedUtils.getString(mActivity, "username", "");
        String city = SharedUtils.getString(mActivity, "city", "");
        String avatar = SharedUtils.getString(mActivity, "avatar", "");
        String nation = SharedUtils.getString(mActivity, "country", "");
        String gender = SharedUtils.getString(mActivity, "gender", "");
        if (username.equals("")) {
            tvName.setText("亲,你还没有设置用户名哦");
        } else
            tvName.setText(username);
        tvName.setText(username);
        tvNation.setText(nation);
        tvCity.setText(city);
        if (gender.equals("1")) {
            mSex.setText("男");
        } else if (gender.equals("2")) {
            mSex.setText("女");
        }/* else if (gender.equals("0")) {
            mSex.setText("保密");
        }*/
        if (null != avatar && !avatar.isEmpty()) {
            Picasso.with(this)
                    .load(avatar)
                    .placeholder(R.drawable.hbuy)
                    .error(R.drawable.hbuy)
                    .fit()
                    .tag(this)
                    .into(mCircleImageView);
        }
    }


    private void toSave() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("输入城市")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = et.getText().toString().trim();
                        if (StringUtils.isEmpty(input)) {
                            ShowToastUtils.toast(PersonalDataActivity.this, "请输入城市");
                            return;
                        }
                        if (NetUtils.hasAvailableNet(PersonalDataActivity.this)) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("city", input);
                            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.modifyPersonalData, params, new RespModifyMsgCallback(PersonalDataActivity.this) {
                                @Override
                                public void onSuccess() {
                                    tvCity.setText(input);
                                    SharedUtils.putString(mActivity, "city", input);
                                    ShowToastUtils.toast(mActivity, "修改成功", 1);
                                }

                                @Override
                                public void onFail(String status) {
                                    ShowToastUtils.toast(mActivity, "修改失败", 2);
                                }
                            });
                        } else {
                            ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FROM_SELECT_NATION:
                if (null != data) {
                    Bundle bunde = data.getExtras();
                    final String nation = bunde.getString("nation");
                    mCountryId = bunde.getString("id");
                    if (null != nation) {
                        if (NetUtils.hasAvailableNet(PersonalDataActivity.this)) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("country", nation);
                            params.put("country_id", mCountryId);
                            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.modifyPersonalData, params, new RespModifyMsgCallback(PersonalDataActivity.this) {
                                @Override
                                public void onSuccess() {
                                    tvNation.setText(nation);
                                    ShowToastUtils.toast(mActivity, "修改成功", 1);
                                    SharedUtils.putString(mActivity, "country", nation);
                                }

                                @Override
                                public void onFail(String status) {
                                    ShowToastUtils.toast(PersonalDataActivity.this, "修改失败", 2);
                                }
                            });
                        } else {
                            ShowToastUtils.toast(PersonalDataActivity.this, getString(R.string.no_net_hint));
                        }
                    }
                }
                break;
        }
    }

    private void selectSex(final int item) {
        final String items[] = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("请选择性别:"); //设置标题
        builder.setIcon(R.drawable.sex_hint);//设置图标，图片id即可
        builder.setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                dialog.dismiss();
                if (NetUtils.hasAvailableNet(PersonalDataActivity.this)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("gender", (which + 1) + "");
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.modifyPersonalData, params, new RespModifyMsgCallback(PersonalDataActivity.this) {
                        @Override
                        public void onSuccess() {
                            ShowToastUtils.toast(PersonalDataActivity.this, "修改成功", 1);
                            mSex.setText(items[which]);
                            SharedUtils.putString(mActivity, "gender", (which + 1) + "");
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(PersonalDataActivity.this, "修改失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(PersonalDataActivity.this, getString(R.string.no_net_hint));
                }
            }
        });
        builder.show();
    }

    @OnClick({R.id.iv_back, R.id.layout_avater, R.id.tv_name, R.id.tv_sex, R.id.tv_nation, R.id.tv_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_city:
                toSave();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_nation:
                Intent intent = new Intent();
                intent.setClass(PersonalDataActivity.this, SelectNationAndCodeActivity.class);
                intent.putExtra("type", "2");
                startActivityForResult(intent, FROM_SELECT_NATION);
                break;
            case R.id.tv_sex:
                String gender = mSex.getText().toString();
                if (gender.equals("男")) {
                    selectSex(0);
                } else if (gender.equals("女")) {
                    selectSex(1);
                } else {
                    selectSex(0);
                }
                break;
            case R.id.tv_name:
                ShowToastUtils.toast(this, "亲，用户名不可修改哦");
                break;
            case R.id.layout_avater:
                AppUtils.showUploadAvater(this, takePhoto, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
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
    public void takeSuccess(TResult result) {
        String path = result.getImages().get(0).getCompressPath();
        if (NetUtils.hasAvailableNet(this) && null != path) {
            final File file = new File(path);
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
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
                KLog.d(_uploadToken);
                uploadManager.put(file, fileName, _uploadToken,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info,
                                                 JSONObject response) {
                                KLog.d(info.toString() + "---" + response + "---info = " + info.isOK());
                                if (info.isOK()) {
                                    KLog.d("---" + "---fileName = " + fileName);
                                    HashMap params = new HashMap();
                                    params.put("avator", fileName);
                                    ApiClient.getInstance(mActivity).postForm(ConfigConstants.modifyUserData, params, new RespModifyAddMsgCallback(mActivity) {
                                        @Override
                                        public void onSuccess(String msg) {
                                            AppUtils.stopProgressDialog(progressDialog);
                                            if (null != msg) {
                                                SharedUtils.putString(PersonalDataActivity.this, "avatar", msg);
                                                if (!"".equals(msg))
                                                    Picasso.with(PersonalDataActivity.this)
                                                            .load(msg)
                                                            .placeholder(R.drawable.hbuy)
                                                            .error(R.drawable.hbuy)
                                                            .fit()
                                                            .tag(this)
                                                            .into(mCircleImageView);
                                                AppUtils.stopProgressDialog(progressDialog);
                                                ShowToastUtils.toast(PersonalDataActivity.this, "修改头像成功", 1);
                                            }
                                        }

                                        @Override
                                        public void onFail(String str) {
                                            AppUtils.stopProgressDialog(progressDialog);
                                            ShowToastUtils.toast(PersonalDataActivity.this, "修改头像失败", 2);
                                        }
                                    });
                                } else {
                                    ShowToastUtils.toast(mActivity, "上传图片失败,请稍后再试", 2);
                                }
                            }
                        }, null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        ShowToastUtils.toast(this, "上传头像失败", 2);
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
}
