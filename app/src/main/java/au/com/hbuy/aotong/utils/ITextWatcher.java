package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yangwei on 2016/7/29--17:03.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class ITextWatcher implements TextWatcher {
    private View[] ids;
    private View tv;
    private Activity context;

    public ITextWatcher(View[] ids, View tv, Activity context) {
        this.ids = ids;
        this.tv = tv;
        this.context = context;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
            tv.setEnabled(true);
        } else {
            tv.setEnabled(false);
            return;
        }
        for (int i = 0; i < ids.length; i++) {
            if (StringUtils.isEmpty(AppUtils.getTextStr(ids[i]))) {
                tv.setEnabled(false);
                return;
            } else {
                tv.setEnabled(true);
            }
        }
    }
}
