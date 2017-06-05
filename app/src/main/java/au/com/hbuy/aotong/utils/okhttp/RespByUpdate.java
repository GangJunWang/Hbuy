package au.com.hbuy.aotong.utils.okhttp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import au.com.hbuy.aotong.domain.Version;

/**
 * Created by yangwei on 2016/7/28--09:53.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */
public class RespByUpdate<T> extends Resp implements Serializable {
    @SerializedName("version")
    public Version version;

    @SerializedName("slide")
    public T slide;
}
