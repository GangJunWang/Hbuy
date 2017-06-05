package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/11/30--14:24.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class PkgDetail {
    private PkgDetailsInfo original_info;
    private PkgDetailsInfo destination_info;
    private String updated_time;

    public String getUpdated_time() {
        return updated_time;
    }

    public PkgDetailsInfo getDestination_info() {
        return destination_info;
    }

    public PkgDetailsInfo getOriginal_info() {
        return original_info;
    }
}
