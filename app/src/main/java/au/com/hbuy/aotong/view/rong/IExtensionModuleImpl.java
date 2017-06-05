package au.com.hbuy.aotong.view.rong;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.R;
import io.rong.common.RLog;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.emoticon.EmojiTab;
import io.rong.imkit.emoticon.IEmojiItemClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.plugin.CombineLocationPlugin;
import io.rong.imkit.plugin.DefaultLocationPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by yangwei on 2017/3/10--17:03.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */
public class IExtensionModuleImpl extends DefaultExtensionModule {
    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        ArrayList pluginModuleList = new ArrayList();
        ImagePlugin image = new ImagePluginImpl();
        FilePlugin file = new FilePluginImpl();
        pluginModuleList.add(image);

        String e;
        Class cls;
        try {
            e = "com.amap.api.netlocation.AMapNetworkLocationClient";
            cls = Class.forName(e);
            if(cls != null) {
                CombineLocationPlugin constructor = new CombineLocationPlugin();
                DefaultLocationPlugin recognizer = new DefaultLocationPlugin();
                if(conversationType.equals(Conversation.ConversationType.PRIVATE)) {
                    pluginModuleList.add(constructor);
                } else {
                    pluginModuleList.add(recognizer);
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        if(conversationType.equals(Conversation.ConversationType.GROUP) || conversationType.equals(Conversation.ConversationType.DISCUSSION) || conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            pluginModuleList.addAll(InternalModuleManager.getInstance().getExternalPlugins(conversationType));
        }

        pluginModuleList.add(file);

        try {
            e = "com.iflytek.cloud.SpeechUtility";
            cls = Class.forName(e);
            if(cls != null) {
                cls = Class.forName("io.rong.recognizer.RecognizePlugin");
                Constructor constructor1 = cls.getConstructor(new Class[0]);
                IPluginModule recognizer1 = (IPluginModule)constructor1.newInstance(new Object[0]);
                pluginModuleList.add(recognizer1);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        IPluginModuleImpl address = new IPluginModuleImpl(R.drawable.chat_address, "海外地址", "3");
        IPluginModuleImpl transfer = new IPluginModuleImpl(R.drawable.chat_transfer, "开始打包", "4");
        IPluginModuleImpl handle = new IPluginModuleImpl(R.drawable.chat_handing, "处理中", "5");
        IPluginModuleImpl waitCommit = new IPluginModuleImpl(R.drawable.chat_wait, "待提交", "6");
        IPluginModuleImpl waitPayment = new IPluginModuleImpl(R.drawable.chat_wait_payment, "待付订单", "7");
        IPluginModuleImpl history = new IPluginModuleImpl(R.drawable.chat_history_order, "历史订单", "8");
        pluginModuleList.add(address);
        pluginModuleList.add(transfer);
        pluginModuleList.add(handle);
        pluginModuleList.add(waitCommit);
        pluginModuleList.add(waitPayment);
        pluginModuleList.add(history);
        return pluginModuleList;
    }
}
