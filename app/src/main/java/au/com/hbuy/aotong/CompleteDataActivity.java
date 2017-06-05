package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import au.com.hbuy.aotong.domain.KefuBean;
import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespClassTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompleteDataActivity extends FragmentActivity implements TakePhoto.TakeResultListener, InvokeListener {
    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.tv_selete_sex)
    TextView mSeleteSex;
    @Bind(R.id.bt_save)
    Button mSave;
    @Bind(R.id.tv_name)
    ClearEditText mName;
    @Bind(R.id.tv_select_nation)
    TextView mSelectNation;
    @Bind(R.id.civ_img)
    ImageView mCivImg;
    @Bind(R.id.toolbar_bottom)
    ToggleButton toolbarBottom;
    @Bind(R.id.tv_kf)
    TextView tvKf;
    @Bind(R.id.layout_kf)
    RelativeLayout layoutKf;
    @Bind(R.id.kf_layout)
    RelativeLayout kfLayout;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private CustomProgressDialog progressDialog;
    private final int CAMERA_WITH_DATA = 1;
    public static final int FROM_SELECT_NATION = 4;
    private static final int FLAG_CHOOSE_IMG = 2;
    private static final int FLAG_MODIFY_FINISH = 3;
    private String avater = "";
    private boolean mIsOpen = false;
    private Activity mActivity = CompleteDataActivity.this;
    private List<KefuBean> mList;
    private String mId = "0";
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    private String mCountryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_data);
        ButterKnife.bind(this);
       /* mSeleteSex.addTextChangedListener(new ITextWatcher(new View[]{mName, mSelectNation}, mSave, this));
        mName.addTextChangedListener(new ITextWatcher(new View[]{mSeleteSex, mSelectNation}, mSave, this));
        mSelectNation.addTextChangedListener(new ITextWatcher(new View[]{mName, mSeleteSex}, mSave, this));*/

        //客服id大于0 不显示
        if (StringUtils.toLong(SharedUtils.getString(mActivity, "kf_id", "0")) > 0) {
            kfLayout.setVisibility(View.GONE);
        }

        toolbarBottom.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    //得到客服列表
                    if (NetUtils.hasAvailableNet(mActivity)) {
                        progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GET_KEFU_LIST, new RespTakeMessageCallback<List<KefuBean>>(mActivity, null) {
                            @Override
                            public void onSuccess(List<KefuBean> bean) {
                                AppUtils.stopProgressDialog(progressDialog);
                                if (null != bean) {
                                    int size = bean.size();
                                    if (size == 0) {
                                        ShowToastUtils.toast(mActivity, "获取客服信息失败");
                                        toolbarBottom.setToggleOff();
                                    } else if (size > 0) {
                                        mList = bean;
                                        layoutKf.setVisibility(View.VISIBLE);
                                        selectKefu(mList, 0);
                                        mIsOpen = true;
                                    }
                                }
                            }

                            @Override
                            public void onFail(List<KefuBean> bean) {
                                AppUtils.stopProgressDialog(progressDialog);
                            }
                        });
                    } else {
                        ShowToastUtils.toast(CompleteDataActivity.this, "请连接网络");
                    }
                } else {
                    layoutKf.setVisibility(View.GONE);
                    mIsOpen = false;
                }
            }
        });


    }

    private void selectKefu(final List<KefuBean> list, int item) {
        int size = list.size();
        final String items[] = new String[size];
        for (int i = 0; i < size; i++) {
            items[i] = list.get(i).getName();
        }
        //   final String items[] = {"1号客服", "2号客服", "3号客服", "4号客服", "5号客服", "6号客服"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("请选择客服:"); //设置标题
        builder.setIcon(R.drawable.select_kefu);//设置图标，图片id即可
        builder.setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tmp = items[which];
                tvKf.setText(tmp);
                for (KefuBean k : list) {
                    if (k.getName().equals(tmp)) {
                        mId = k.getId();
                        KLog.d(mId);
                    }
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static Boolean isExit = false;

    //双击退出
    private void exitBy2Click() {
        Timer exit = null;
        if (!isExit) {
            isExit = true;
            ShowToastUtils.toast(this, "请完善资料再退出", 3);
            exit = new Timer();
            exit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //    RongIM.getInstance().disconnect();
            //  SharedUtils.putBoolean(this, "use_is_login", false);
            //   RongIM.getInstance().logout();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitBy2Click();
                break;
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.tv_selete_sex, R.id.bt_save, R.id.tv_select_nation, R.id.civ_img, R.id.tv_kf, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_kf:
                selectKefu(mList, 0);
                break;
            case R.id.iv_back:
                exitBy2Click();
                break;
            case R.id.tv_selete_sex:
                selectSex(0);
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.bt_save:
                String name = mName.getText().toString().trim();
                String nation = mSelectNation.getText().toString().trim();
                final String sex = mSeleteSex.getText().toString().trim();
                KLog.d(name + "nation=" + nation + "sex=" + sex);
                if (null != nation && "".equals(nation)) {
                    ShowToastUtils.toast(this, "请选择国家", 3);
                    return;
                }
                if (null != sex && "".equals(sex)) {
                    ShowToastUtils.toast(this, "请选择性别");
                    return;
                }
                if (null != name && "".equals(name)) {
                    ShowToastUtils.toast(this, "用户名不能为空");
                    return;
                }
                if (null != name && name.length() < 2) {
                    ShowToastUtils.toast(this, "用户名长度不能小于2");
                    return;
                }
                if (null != name && name.length() > 4) {
                    ShowToastUtils.toast(this, "用户名长度不能大于4");
                    return;
                }
                if (null != name && name.contains("@")) {
                    ShowToastUtils.toast(this, "用户名不能包含@符号");
                    return;
                }
                if (null != name && !StringUtils.isContainChinese(name)) {
                    ShowToastUtils.toast(this, "用户名必须为中英文");
                    return;
                }
                String kf = tvKf.getText().toString().trim();
                if (mIsOpen) {
                    if (StringUtils.isEmpty(kf)) {
                        ShowToastUtils.toast(CompleteDataActivity.this, "请选择Hbuy客服");
                        return;
                    }
                }
              /*  if (null != name && !hanzi || !zhongyingwen) {
                    ShowToastUtils.toast(this, "用户名必须含有中文");
                    return;
                }*/
                if (NetUtils.hasAvailableNet(this)) {
                    //go login
                  /*  if (StringUtils.isEmpty(avater)) {
                        ShowToastUtils.toast(this, "亲 你还没上传头像呢");
                        return;
                    }*/
                    progressDialog = AppUtils.startProgressDialog(this, "正在提交", progressDialog);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("avator", avater);
                    params.put("username", name);
                    if (sex.equals("男")) {
                        params.put("gender", "1");
                    } else if (sex.equals("女")) {
                        params.put("gender", "2");
                    }
                    if (!mIsOpen) {
                        params.put("kf_id", "0");
                    } else {
                        params.put("kf_id", mId);
                    }
                    params.put("country", nation);
                    params.put("country_id", mCountryId);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.COMPLETEDATA, params, new RespClassTCallback<User>(this) {
                        @Override
                        public void onSuccess(User user) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != user) {
                                AppUtils.stopProgressDialog(progressDialog);
                                SharedUtils.putBoolean(CompleteDataActivity.this, "use_is_login", true);
                                AppUtils.getUseInfo(CompleteDataActivity.this, 0);
                              /*  SharedUtils.putString(CompleteDataActivity.this, "gender", user.getGender());
                                SharedUtils.putString(CompleteDataActivity.this, "country", user.getCountry());
                                SharedUtils.putString(CompleteDataActivity.this, "username", user.getUsername());
                                SharedUtils.putString(CompleteDataActivity.this, "avatar", user.getAvator());
                                AppUtils.showActivity(CompleteDataActivity.this, MainActivity.class);*/
                            }
                        }

                        @Override
                        public void onFail(String str) {
                            if ("-1".equals(str)) {
                                ShowToastUtils.toast(CompleteDataActivity.this, "该用户名已存在");
                            } else if ("-2".equals(str)) {
                                ShowToastUtils.toast(CompleteDataActivity.this, "请求异常");
                            }
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(CompleteDataActivity.this, "请连接网络");
                }
                break;
            case R.id.civ_img:
                AppUtils.showUploadAvater(this, takePhoto, 1);
                break;
            case R.id.tv_select_nation:
                Intent intent = new Intent();
                intent.setClass(CompleteDataActivity.this, SelectNationAndCodeActivity.class);
                intent.putExtra("type", "2");
                intent.putExtra("hint", mSelectNation.getText().toString().trim());
                startActivityForResult(intent, FROM_SELECT_NATION);
                break;
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImages().get(0).getCompressPath();
        if (NetUtils.hasAvailableNet(this) && null != path) {
            final File file = new File(path);
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
                final String fileName = AppUtils.getFileName(CompleteDataActivity.this);
                UploadManager uploadManager = new UploadManager();
                uploadManager.put(file, fileName, _uploadToken,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info,
                                                 JSONObject response) {
                                if (info.isOK()) {
                                    avater = fileName;
                                    if (null != fileName)
                                        Picasso.with(CompleteDataActivity.this)
                                                .load(ConfigConstants.QINIU_CDN + "/" + fileName)
                                                .placeholder(R.drawable.hbuy)
                                                .error(R.drawable.hbuy)
                                                .fit()
                                                .tag(this)
                                                .into(mCivImg);
                                } else {
                                    ShowToastUtils.toast(CompleteDataActivity.this, "上传图片失败,请稍后再试", 2);
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
        ShowToastUtils.toast(this, "上传头像失败", 2);
    }

    @Override
    public void takeCancel() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FROM_SELECT_NATION:
                if (null != data) {
                    Bundle bunde = data.getExtras();
                    String nation = bunde.getString("nation");
                    mCountryId = bunde.getString("id");
                    mSelectNation.setText(nation);
                }
                break;
        }
    }

    private void selectSex(int item) {
        final String items[] = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("请选择性别:"); //设置标题
        builder.setIcon(R.drawable.sex_hint);//设置图标，图片id即可
        builder.setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSeleteSex.setText(items[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
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
