package au.com.hbuy.aotong.utils.okhttp;

/**
 * temporary 错误信息提示!!!
 * <p/>
 * 这是根据服务器返回的各种错误消息封装体
 */
public class ErrorMsg {
    public String msg;

    @Override
    public String toString() {
        return "ErrorMsg{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
