package au.com.hbuy.aotong.view.rong;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;
import java.util.Iterator;

import au.com.hbuy.aotong.R;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FileManagerActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.plugin.image.PictureSelectorActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Created by yangwei on 2017/3/10--17:12.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class ImagePluginImpl extends ImagePlugin {
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.chat_image);
    }
}
