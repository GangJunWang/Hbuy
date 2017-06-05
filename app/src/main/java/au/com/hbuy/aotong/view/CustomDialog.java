package au.com.hbuy.aotong.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.socks.library.KLog;

import au.com.hbuy.aotong.R;

/**
 * Created by yangwei on 2017/5/3--17:32.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class CustomDialog extends Dialog {
    private static CustomDialog customDialog = null;
    private ImageView mHint;
    private View mId;
    private Context mContext;
    public static FrameLayout flContent;// 内容
    public CustomDialog(Context context, View id) {
        super(context);
        KLog.d();
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static CustomDialog create(Context context, View view) {
        customDialog = new CustomDialog(context, R.style.dialog);
        customDialog.setContentView(R.layout.custom_dialog);
        flContent = (FrameLayout) customDialog.findViewById(R.id.top_layout);
        flContent.addView(view);
       /* WindowManager.LayoutParams params = customDialog.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        customDialog.getWindow().setAttributes(params);
        customDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);*/
        customDialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        return customDialog;
    }

    public static CustomDialog create(Context context, int id) {
        View view = LayoutInflater.from(context).inflate(id, null);
        customDialog = new CustomDialog(context, R.style.dialog);
        customDialog.setContentView(R.layout.custom_dialog);
        flContent = (FrameLayout) customDialog.findViewById(R.id.top_layout);
        flContent.addView(view);
        customDialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        return customDialog;
    }
}
