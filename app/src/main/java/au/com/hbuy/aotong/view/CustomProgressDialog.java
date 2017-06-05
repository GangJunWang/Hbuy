package au.com.hbuy.aotong.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.hbuy.aotong.R;

/**
 * Created by yangwei on 2016/7/29--09:48.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class CustomProgressDialog extends Dialog {
    private Activity context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;
    }

    // 创建一个ProgressDialog，当然参数你也可以传多个，以便设置相关属性
    public static CustomProgressDialog createDialog(Activity context,
                                                    String message) {
        customProgressDialog = new CustomProgressDialog(context,
                R.style.CustomProgressDialog);// 引入样式
        customProgressDialog.setContentView(R.layout.custom_progressdialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;// 设置在中间显示
        customProgressDialog.setMessage(message);// 设置展示的文字，如：正在加载中
        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (customProgressDialog == null) {
            return;
        }

        ImageView imageView = (ImageView) customProgressDialog
                .findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                .getBackground();
        animationDrawable.start();
    }

    /**
     *
     * setMessage 提示内容，这是我们自定义的dialog布局
     *
     */
    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) customProgressDialog
                .findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }
}