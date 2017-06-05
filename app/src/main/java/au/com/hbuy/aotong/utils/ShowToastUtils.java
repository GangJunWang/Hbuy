package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import au.com.hbuy.aotong.R;
import es.dmoral.toasty.Toasty;

public class ShowToastUtils {
	public static void toast(Activity context, String Str, int style) {
		//Toast.makeText(context, Str, 1).show();
		/*TastyToast.Style s = new TastyToast.Style(2000, R.color.toast_color);
		TastyToast.makeText(context, Str, s).show();*/

		if (style == 1) {
			Toasty.success(context, Str, Toast.LENGTH_SHORT, true).show();
		} else if (style == 2) {
			Toasty.error(context, Str, Toast.LENGTH_SHORT, true).show();
		} else if (style == 3) {
			Toasty.info(context, Str, Toast.LENGTH_SHORT, true).show();
		}
	}
	public static void toast(Activity context, String Str) {
			Toasty.info(context, Str, Toast.LENGTH_SHORT, true).show();
	}
}
