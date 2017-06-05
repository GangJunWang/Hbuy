package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.zcw.togglebutton.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.SettingBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.FileUtils;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class PreSettingActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;

    @Bind(R.id.tv_sex)
    TextView mSex;
    private String mTmpId = "", mCarrierId = "";

    @Bind(R.id.toolbar_bottom)
    ToggleButton toolbarBottom;
    private FragmentActivity mActivity = PreSettingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_setting);
        ButterKnife.bind(this);

      /*  String carrier_priority = SharedUtils.getString(mActivity, "carrier_priority", "");
        String pack_box = SharedUtils.getString(mActivity, "pack_box", "");
        if (StringUtils.isEmpty(carrier_priority) && StringUtils.isEmpty(pack_box)) {*/
            if (NetUtils.hasAvailableNet(PreSettingActivity.this)) {
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GETSETTING, new RespTakeMessageCallback<SettingBean>(mActivity, null) {
                    @Override
                    public void onSuccess(SettingBean settingBean) {
                        String tmp = settingBean.getCarrier_priority();
                        String str = "";
                        String tmp_box = settingBean.getPack_box();

                        if ("0".equals(tmp)) {
                            str = "默认";
                        } else if ("1".equals(tmp)) {
                            str = "时效优先";
                        } else if ("2".equals(tmp)) {
                            str = "经济优先";
                        } else {
                            mSex.setText("请选择");
                        }

                        if (!StringUtils.isEmpty(str)) {
                            mSex.setText(str);
                            //SharedUtils.putString(mActivity, "carrier_priority", str);
                        }
                      /*  SharedUtils.putString(mActivity, "pack_box", tmp_box);*/
                        if ("0".equals(tmp_box)) {
                            toolbarBottom.setToggleOff();
                        } else {
                            toolbarBottom.setToggleOn();
                        }
                    }

                    @Override
                    public void onFail(SettingBean settingBean) {
                        ShowToastUtils.toast(mActivity, "获取失败", 2);
                    }
                });
            } else {
                ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
            }
        /*} else {
            mSex.setText(carrier_priority);
            if ("0".equals(pack_box)) {
                toolbarBottom.setToggleOff();
            } else {
                toolbarBottom.setToggleOn();
            }
        }*/

        toolbarBottom.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {

                if (NetUtils.hasAvailableNet(PreSettingActivity.this)) {

                    HashMap<String, String> params = new HashMap<String, String>();
                    if (on) {
                        mTmpId = "1";
                        params.put("pack_box", mTmpId);
                    } else {
                        mTmpId = "0";
                        params.put("pack_box", mTmpId);
                    }
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SETSETTING, params, new RespModifyMsgCallback(PreSettingActivity.this) {
                        @Override
                        public void onSuccess() {
                            SharedUtils.putString(mActivity, "pack_box", mTmpId);
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
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_sex})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sex:
                List<String> list = new ArrayList<>();
                list.add("默认");
                list.add("时效优先");
                list.add("经济优先");
                new SuperDialog.Builder(mActivity)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(final int position) {

                                if (NetUtils.hasAvailableNet(PreSettingActivity.this)) {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("carrier_priority", position + "");
                                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SETSETTING, params, new RespModifyMsgCallback(PreSettingActivity.this) {
                                        @Override
                                        public void onSuccess() {
                                            if (position == 0) {
                                                mCarrierId = "默认";
                                            } else if (position == 1) {
                                                mCarrierId = "时效优先";
                                            } else if (position == 2) {
                                                mCarrierId = "经济优先";
                                            }
                                            mSex.setText(mCarrierId);
                                            SharedUtils.putString(mActivity, "carrier_priority", mCarrierId);
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
                        .build();
                break;
        }
    }
}
