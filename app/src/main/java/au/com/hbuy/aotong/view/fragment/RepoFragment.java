package au.com.hbuy.aotong.view.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;

import au.com.hbuy.aotong.MainActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2017/4/26--16:11.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class RepoFragment extends Fragment {
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    @Bind(R.id.tv_name)
    TextView mName;
    @Bind(R.id.tv_phone)
    TextView mPhone;
    @Bind(R.id.tv_address)
    TextView mAddress;
    @Bind(R.id.tv_zip)
    TextView mZip;
    private FragmentActivity mContext;

    public static RepoFragment newInstance() {
        RepoFragment fragment = new RepoFragment();
      /*  Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);*/
        return fragment;
    }

    public void setActivity(FragmentActivity context) {
        this.mContext = context;


    }

    public RepoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_repo, container, false);
        ButterKnife.bind(this, rootView);

        String name = SharedUtils.getString(mContext, "repo_name", "服务器异常");
        if (name.equals("服务器异常")) {
              getData();
        } else {
            mName.setText("姓 名 : " + name);
            mAddress.setText("仓库地址: " + SharedUtils.getString(mContext, "repo_address", "服务器异常"));
            mZip.setText("邮 编 : " + SharedUtils.getString(mContext, "repo_zip", "服务器异常"));
            mPhone.setText("电 话 : " + SharedUtils.getString(mContext, "repo_phone", "服务器异常"));
        }
        return rootView;
    }

    private void getData() {
        if (NetUtils.hasAvailableNet(mContext)) {
            ApiClient.getInstance(mContext.getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(mContext) {
                @Override
                public void onSuccess(Address address) {
                    if (null != address) {
                        mName.setText(address.getName());
                        mAddress.setText(address.getAddress());
                        mZip.setText(address.getZip());
                        mPhone.setText(address.getPhone());

                        mName.setText("姓 名 : " + address.getName());
                        mAddress.setText("仓库地址: " + address.getAddress());
                        mZip.setText("邮 编 : " + address.getZip());
                        mPhone.setText("电 话 : " + address.getPhone());
                    }
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(mContext, "获取地址失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(mContext, getString(R.string.no_net_hint), 3);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_copy)
    public void onClick() {
        String name = mName.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String zip = mZip.getText().toString().trim();
        String tmp = name + "\n" + phone + "\n" + address + "\n" + zip;
        ClipboardManager myClipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", tmp);
        myClipboard.setPrimaryClip(myClip);
     /*   new SuperDialog.Builder(mContext).setRadius(10)
                .setAlpha(1f)
                .setTitle("温馨提示").setMessage(" 亲~已为您复制好Hbuy中转地址信息哟,快将您购买的物品寄到Hbuy吧!在首页“添加包裹”能及时更新物流哟！")
                .setPositiveButton("好的", new SuperDialog.OnClickPositiveListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.showActivity(mContext, MainActivity.class);
                        mContext.finish();
                    }
                }).build();*/
        ShowToastUtils.toast(mContext, "复制成功", 1);
    }
}
