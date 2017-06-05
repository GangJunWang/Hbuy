package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.zcw.togglebutton.ToggleButton;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomDialog;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddressEditActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_name)
    ClearEditText mName;
    @Bind(R.id.et_nation)
    TextView mNation;
    @Bind(R.id.tv_select_code)
    TextView mSelectCode;
    @Bind(R.id.et_phone)
    ClearEditText mPhone;
    @Bind(R.id.et_city)
    ClearEditText mCity;
    @Bind(R.id.et_details)
    ClearEditText mDetails;
    @Bind(R.id.et_zip)
    ClearEditText mZip;
    @Bind(R.id.cb_default)
    ToggleButton mDefault;
    @Bind(R.id.bt_save)
    TextView mSave;
    @Bind(R.id.tv_hint_title)
    TextView mHintTitle;
    @Bind(R.id.iv_hint_title)
    ImageView ivHintTitle;
    @Bind(R.id.tv_address_hint_bottom)
    TextView tvAddressHintBottom;
    @Bind(R.id.hint)
    RelativeLayout hint;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_look_hint)
    TextView tvLookHint;
    @Bind(R.id.tv_name_layout)
    RelativeLayout tvNameLayout;
    @Bind(R.id.tv_look_name)
    TextView tvLookName;
    @Bind(R.id.tv_look_phone)
    TextView tvLookPhone;
    @Bind(R.id.tv_city_hint_bottom)
    TextView tvCityHintBottom;
    @Bind(R.id.tv_address_hint)
    TextView tvAddressHint;
    @Bind(R.id.tv_zip_hint_bottom)
    TextView tvZipHintBottom;
    @Bind(R.id.tv_go_other)
    TextView tvGoOther;
    @Bind(R.id.tv_name)
    TextView tvName;
    private CustomProgressDialog progressDialog;
    private String mUrl = ConfigConstants.addAddress;
    private Address mAddress;
    private String mIsDefault = "";
    private Activity mActivity;
    private String mCountryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        ButterKnife.bind(this);
        mActivity = AddressEditActivity.this;
        mAddress = (Address) getIntent().getSerializableExtra("address");
        if (null != mAddress) {
            mIsDefault = mAddress.getIs_default();
            mName.setText(mAddress.getReceiver());
            mNation.setText(mAddress.getCountry());
            mPhone.setText(mAddress.getPhone());
            mCity.setText(mAddress.getCity());
            mDetails.setText(mAddress.getAddress());
            mZip.setText(mAddress.getZip());
            if ("1".equals(mAddress.getIs_default())) {
                mDefault.setToggleOn();
            } else {
                mDefault.setToggleOff();
            }
            mUrl = ConfigConstants.updateAddress;

            mHintTitle.setText("修改收货地址");
            //   mSave.setText("确认修改");
        }
       /* mName.addTextChangedListener(new ITextWatcher(new View[]{mPhone, mCity, mZip, mNation, mDetails, mSelectCode}, mSave, this));
        mPhone.addTextChangedListener(new ITextWatcher(new View[]{mName, mCity, mZip, mNation, mDetails, mSelectCode}, mSave, this));
        mNation.addTextChangedListener(new ITextWatcher(new View[]{mPhone, mCity, mZip, mName, mDetails, mSelectCode}, mSave, this));
        mCity.addTextChangedListener(new ITextWatcher(new View[]{mName, mPhone, mZip, mNation, mDetails, mSelectCode}, mSave, this));
        mZip.addTextChangedListener(new ITextWatcher(new View[]{mName, mPhone, mCity, mNation, mDetails, mSelectCode}, mSave, this));
        mDetails.addTextChangedListener(new ITextWatcher(new View[]{mName, mPhone, mCity, mNation, mZip, mSelectCode}, mSave, this));
        mSelectCode.addTextChangedListener(new ITextWatcher(new View[]{mName, mPhone, mCity, mNation, mZip, mDetails}, mSave, this));*/

        boolean isEnable = getIntent().getBooleanExtra("num", false);
        if (!isEnable) {
            //    mDefault.setVisibility(View.GONE);
        }

        mDefault.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    mIsDefault = "1";
                } else {
                    mIsDefault = "0";
                }
            }
        });

        setOnFocus(mName, tvLookName);
        setOnFocus(mPhone, tvLookPhone);
        setOnFocus(mCity, tvCityHintBottom);
        setOnFocus(mDetails, tvAddressHintBottom);
        setOnFocus(mZip, tvZipHintBottom);

        Spannable span = new SpannableString(tvGoOther.getText());
        span.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.red_color));  //设置下划线颜色
                ds.setUnderlineText(true);  // 显示下划线
            }

            @Override
            public void onClick(View view) {     // TextView点击事件
                Intent i = new Intent(mActivity, GuideActivity.class);
                i.putExtra("type", "3");
                mActivity.startActivity(i);
            }
        }, 48, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvGoOther.setText(span);
    }

    private void setOnFocus(final EditText edit, final TextView hintView) {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText textView = (EditText) v;
                String hint1;
                if (v.hasFocus()) {
                    hint1 = textView.getHint().toString();
                    textView.setTag(hint1);
                    String nation = mNation.getText().toString().trim();
                    if (nation.equals("")) {
                        hint.setVisibility(View.VISIBLE);
                        edit.setEnabled(false);
                        return;
                    }
                    hint.setVisibility(View.GONE);
                    hintView.setVisibility(View.VISIBLE);
                    textView.setHint("");
                } else {
                    hint1 = textView.getTag().toString();
                    textView.setHint(hint1);
                    hintView.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.et_nation, R.id.tv_select_code, R.id.bt_save, R.id.iv_back, R.id.iv_hint_title, R.id.tv_look_hint, R.id.tv_go_other})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.et_nation:
                Intent intent = new Intent();
                intent.setClass(AddressEditActivity.this, SelectNationAndCodeActivity.class);
                intent.putExtra("hint", mNation.getText().toString().trim());
                intent.putExtra("type", "2");
                startActivityForResult(intent, 0);
                break;
            case R.id.bt_save:
                if (AppUtils.isNotFastClick()) {
                    toSave();
                }
                break;
            case R.id.tv_select_code:
                // StringUtils.selectCode(0, null, mSelectCode, mActivity);
                Intent i = new Intent();
                i.setClass(AddressEditActivity.this, SelectNationAndCodeActivity.class);
                i.putExtra("type", "1");
                startActivityForResult(i, 1);
                break;
            case R.id.iv_hint_title:
                hint.setVisibility(View.GONE);
                break;
            case R.id.tv_look_hint:
                ImageView imageView = new ImageView(mContext);
                String nation = mNation.getText().toString().trim();
                if ("中国大陆".equals(nation)) {
                    imageView.setImageResource(R.drawable.edit_address_title_hint_china);
                } else if ("日本".equals(nation)) {
                    imageView.setImageResource(R.drawable.edit_address_title_hint_japan);
                } else {
                    imageView.setImageResource(R.drawable.edit_address_title_hint);
                }

                Dialog dialog = CustomDialog.create(mContext, imageView);
                dialog.show();
                break;
            case R.id.tv_go_other:
                Intent other = new Intent(mActivity, GuideActivity.class);
                other.putExtra("type", "3");
                mActivity.startActivity(other);
                break;
        }
    }

    private void toSave() {
        final String phone = AppUtils.getTextStr(mPhone);
        final String zip = AppUtils.getTextStr(mZip);
        final String name = AppUtils.getTextStr(mName);
        final String nation = AppUtils.getTextStr(mNation);
        final String city = AppUtils.getTextStr(mCity);
        final String details = AppUtils.getTextStr(mDetails);
        String tmp = AppUtils.getTextStr(mSelectCode);
        boolean is04 = false;

        final String code = tmp.substring(1, tmp.length());
        if ("".equals(phone) || "".equals(name) || "".equals(nation) || "".equals(city) || "".equals(details) || "".equals(code) || "".equals(zip)) {
            ShowToastUtils.toast(this, "请补全信息后再提交", 3);
            return;
        }

        if (null != mAddress) {
            KLog.d(mAddress.getReceiver() + mAddress.getPhone() + mAddress.getPhonecode() + mAddress.getIs_default() + mAddress.getZip() + mAddress.getAddress() + mAddress.getCity());

            if (mAddress.getPhone().equals(phone) && mAddress.getReceiver().equals(name) && mAddress.getCountry().equals(nation) && mAddress.getCity().equals(city) && mAddress.getAddress().equals(details) &&
                    mAddress.getPhonecode().equals(code) && mAddress.getZip().equals(zip) && mAddress.getIs_default().equals(mIsDefault)) {
                ShowToastUtils.toast(this, "亲, 你没有修改信息", 3);
                return;
            }
        }

        if (nation.equals("澳大利亚")) {
            KLog.d(phone.substring(0, 2).equals("04"));
            if (!phone.substring(0, 2).equals("04")) {
                is04 = true;
            }
        }

        if (NetUtils.hasAvailableNet(mActivity)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            if (null != mAddress) {
                params.put("id", mAddress.getId());
            }
            params.put("receiver", name);
            KLog.d(phone);
            if (is04) {
                params.put("phone", "04" + phone);
            } else {
                params.put("phone", phone);
            }
            params.put("phonecode", code);
            params.put("country", mCountryId + "," + nation);
            params.put("city", city);
            params.put("zip", zip);
            params.put("address", details);
            params.put("is_default", mIsDefault);

            ApiClient.getInstance(getApplicationContext()).postForm(mUrl, params, new RespModifyMsgCallback(AddressEditActivity.this) {
                @Override
                public void onSuccess() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(AddressEditActivity.this, "成功", 1);
                    //(String receiver, String phone, String address, String city, String country, String id, String is_default, String zip)
                    if (null != mAddress) {
                        Address a = new Address(name, phone, details, city, nation, mAddress.getId(), mIsDefault, zip, code);
                        Intent intent = new Intent(mActivity, AddressDetailsActivity.class);
                        intent.putExtra("address", a);
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_OK);
                    }
                    AddressEditActivity.this.finish();
                }

                @Override
                public void onFail(String status) {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(AddressEditActivity.this, "失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint), 3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            Bundle bunde = data.getExtras();
            String nation = bunde.getString("nation");
            mCountryId = bunde.getString("id");
            switch (requestCode) {
                case 0:
                    mNation.setText(nation);
                    setEnable(mName, mCity, mDetails, mPhone, mZip);
                    switch (nation) {
                        case "中国大陆":
                            hint.setVisibility(View.GONE);
                            tvGoOther.setVisibility(View.GONE);
                            tvName.setVisibility(View.GONE);
                            break;
                        case "日本":
                            hint.setVisibility(View.VISIBLE);
                            tvTitle.setText("寄往日本请使用日语填写，否则会派送不到哦！");
                            tvGoOther.setVisibility(View.GONE);
                            tvName.setVisibility(View.GONE);
                            break;
                        case "澳大利亚":
                            hint.setVisibility(View.GONE);
                            tvNameLayout.setVisibility(View.VISIBLE);
                            tvGoOther.setVisibility(View.VISIBLE);
                            break;
                    }
                    tvLookHint.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    String code = bunde.getString("code");
                    mSelectCode.setText("+" + code);
                    break;
            }
        }
    }

    private void setEnable(View... view) {
        for (View tmp : view) {
            tmp.setEnabled(true);
        }
    }
}
