package au.com.hbuy.aotong;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class GuideActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_hint)
    ImageView ivHint;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        String type = getIntent().getStringExtra("type");
        if ("1".equals(type)) {
            //包裹
            ivHint.setImageResource(R.drawable.transfer_guide);
            tvTitle.setText("转运指南");
        } else if ("2".equals(type)){
            //代购
            ivHint.setImageResource(R.drawable.buy_guide);
            tvTitle.setText("代购指南");
        } else if ("3".equals(type)) {
            ivHint.setImageResource(R.drawable.jiyun_address_hint);
            tvTitle.setText("澳洲集运中心");
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

 /*   *//**
     * 根据图片大小按比例适配全屏
     * @param imageView
     * @param picWidth
     * @param picHeight
     *//*
    public static void fitImage(ImageView imageView, float picWidth, float picHeight) {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        float height = (float) width / picWidth * picHeight;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = (int) height;
        imageView.setLayoutParams(layoutParams);
    }*/
}
