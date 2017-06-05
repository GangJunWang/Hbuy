package au.com.hbuy.aotong.view.rong;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.File;

import au.com.hbuy.aotong.R;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;

/**
 * Created by yangwei on 2017/3/10--17:12.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class FilePluginImpl extends FilePlugin {
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.chat_file);
    }
}
