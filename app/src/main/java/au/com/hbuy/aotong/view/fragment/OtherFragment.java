package au.com.hbuy.aotong.view.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.GetAddressRepoActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.utils.ConfigConstants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2017/4/26--16:11.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class OtherFragment extends Fragment {
    @Bind(R.id.tv_look)
    TextView tvLook;
    @Bind(R.id.tv_hint01)
    TextView tvHint01;
    @Bind(R.id.tv_hint02)
    TextView tvHint02;
    private FragmentActivity mContext;

    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
      /*  Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);*/
        return fragment;
    }

    public void setActivity(FragmentActivity context) {
        this.mContext = context;
    }

    public OtherFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, rootView);

        tvHint01.setText("禁运物品须知：亲~邮寄物品分国际管制和国内管制哟!“禁运物品”寄到Hbuy中转仓也没法帮您转运出去呢!请在“服务”→“常见问题”查看。");
        Spannable span = new SpannableString(tvHint01.getText());
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.default_color)), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.button_color)), 53, 57, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.button_color)), 58, 64, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvHint01.setText(span);

        tvHint02.setText("关于推送：亲~您的包裹到达Hbuy我们会及时通知您哦！请勿关闭推送。");
        Spannable span2 = new SpannableString(tvHint02.getText());
        span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.default_color)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvHint02.setText(span2);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_look)
    public void onClick() {
        Intent i = new Intent(mContext, FaqActivity.class);
        i.putExtra("url", ConfigConstants.faq_url);
        i.putExtra("type", 1);
        mContext.startActivityForResult(i, GetAddressRepoActivity.GOSERVICE);
    }
}
