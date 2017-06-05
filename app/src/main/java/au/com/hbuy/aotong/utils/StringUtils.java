package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.PkgBean;

/**
 * 字符串操作工具包<br>
 */
public class StringUtils {
    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern phone = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    /**
     * 如果内容为空的话，返回true
     *
     * @param content
     * @return
     */
    public static boolean isNull(String content) {
        if (TextUtils.isEmpty(content)) {
            return true;
        } else {
            if ("null".equals(content.trim().toLowerCase())) {
                return true;
            } else {
                return false;
            }
        }

    }

    public static boolean isContainChinese(String str) {
        int j = str.length();
        boolean isZhongwei = false;
        for (int i = 0; i < str.length(); i++) {
            String tmp = str.charAt(i) + "";
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(tmp);
            if (m.find()) {
                KLog.d();
                --j;
                isZhongwei = true;
            }
            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(tmp);
            if (m.find()) {
                KLog.d();
                --j;
            }
            KLog.d(tmp + "--" + "j = " + j);
        }
        if (isZhongwei && j == 0) {
            return true;
        } else {
            return false;
        }
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input)) return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(CharSequence email) {
        if (isEmpty(email)) return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(CharSequence phoneNum) {
        if (isEmpty(phoneNum)) return false;
        return phone.matcher(phoneNum).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * String转long
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * String转double
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0D;
    }

    /**
     * 字符串转布尔
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断一个字符串是不是数字
     */
    public static boolean isNumber(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }

    /**
     * 判断一个是不是邮编
     */
    public static boolean isZip(CharSequence str) {
        try {
            Integer.parseInt(str.toString());
        } catch (Exception e) {
            return false;
        }

        if (str.length() == 6)
            return true;
        return false;
    }

    /**
     * 判断一个是不是默认地址
     */
    public static boolean isDefault(CharSequence str) {
        if (str.equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * byte[]数组转换为16进制的字符串。
     *
     * @param data 要转换的字节数组。
     * @return 转换后的结果。
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组。
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return d;
    }

    public static int getStringLength(String str) {
        if (null != str) {
            return str.length();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(isNull(null));
    }

    public static String getCountry(String str) {
        if (null == str || StringUtils.isEmpty(str)) {
            return "";
        } else {
            switch (str) {
                case "中国大陆":
                    return "1,中国大陆";
                case "澳大利亚":
                    return "2,澳大利亚";
                case "香港":
                    return "3,香港";
                case "台湾":
                    return "4,台湾";
                case "韩国":
                    return "5,韩国";
                case "澳门":
                    return "6,澳门";
                case "美国":
                    return "7,美国";
                case "英国":
                    return "8,英国";
                case "日本":
                    return "9,日本";
                case "俄罗斯":
                    return "10,俄罗斯";
                case "意大利":
                    return "11,意大利";
                case "马来西亚":
                    return "12,马来西亚";
                case "法国":
                    return "13,法国";
                case "加拿大":
                    return "14,加拿大";
                case "新加坡":
                    return "15,新加坡";
                case "新西兰":
                    return "16,新西兰";
                case "印度尼西亚":
                    return "17,印度尼西亚";
                case "匈牙利":
                    return "18,匈牙利";
                case "葡萄牙":
                    return "19,葡萄牙";
                case "爱尔兰":
                    return "20,爱尔兰";
                case "德国":
                    return "21,德国";
                case "越南":
                    return "22,越南";
                case "泰国":
                    return "23,泰国";
                case "瑞典":
                    return "24,瑞典";
                case "荷兰":
                    return "25,荷兰";
                case "西班牙":
                    return "26,西班牙";
            }
            return "";
        }
    }

    public static String getCountryId(String str) {
        if (null == str || StringUtils.isEmpty(str)) {
            return "";
        } else {
            switch (str) {
                case "中国大陆":
                    return "1";
                case "澳大利亚":
                    return "2";
                case "香港":
                    return "3";
                case "台湾":
                    return "4";
                case "韩国":
                    return "5";
                case "澳门":
                    return "6";
                case "美国":
                    return "7";
                case "英国":
                    return "8";
                case "日本":
                    return "9";
                case "俄罗斯":
                    return "10";
                case "意大利":
                    return "11";
                case "马来西亚":
                    return "12";
                case "法国":
                    return "13";
                case "加拿大":
                    return "14";
                case "新加坡":
                    return "15";
                case "新西兰":
                    return "16";
                case "印度尼西亚":
                    return "17";
                case "匈牙利":
                    return "18";
                case "葡萄牙":
                    return "19";
                case "爱尔兰":
                    return "20";
                case "德国":
                    return "21";
                case "越南":
                    return "22";
                case "泰国":
                    return "23";
                case "瑞典":
                    return "24";
                case "荷兰":
                    return "25";
                case "西班牙":
                    return "26";
            }
            return "";
        }
    }

    public static String getCountryNameFormId(String str) {
        if (null == str || StringUtils.isEmpty(str)) {
            return "";
        } else {
            switch (str) {
                case "1":
                    return "中国大陆";
                case "2":
                    return "澳大利亚";
                case "3":
                    return "香港";
                case "4":
                    return "台湾";
                case "5":
                    return "韩国";
                case "6":
                    return "澳门";
                case "7":
                    return "美国";
                case "8":
                    return "英国";
                case "9":
                    return "日本";
                case "10":
                    return "俄罗斯";
                case "11":
                    return "意大利";
                case "12":
                    return "马来西亚";
                case "13":
                    return "法国";
                case "14":
                    return "加拿大";
                case "15":
                    return "新加坡";
                case "16":
                    return "新西兰";
                case "17":
                    return "印度尼西亚";
                case "18":
                    return "匈牙利";
                case "19":
                    return "葡萄牙";
                case "20":
                    return "爱尔兰";
                case "21":
                    return "德国";
                case "22":
                    return "越南";
                case "23":
                    return "泰国";
                case "24":
                    return "瑞典";
                case "25":
                    return "荷兰";
                case "26":
                    return "西班牙";
            }
            return "";
        }
    }

    public static String getPhoneCode(String str) {
        if (null == str || StringUtils.isEmpty(str)) {
            return "";
        } else {
            switch (str) {
                case "中国大陆":
                    return "86";
                case "澳大利亚":
                    return "61";
                case "香港":
                    return "852";
                case "台湾":
                    return "886";
                case "韩国":
                    return "82";
                case "澳门":
                    return "853";
                case "美国":
                    return "1";
                case "英国":
                    return "44";
                case "日本":
                    return "81";
                case "俄罗斯":
                    return "7";
                case "意大利":
                    return "39";
                case "马来西亚":
                    return "60";
                case "法国":
                    return "33";
                case "加拿大":
                    return "1";
                case "新加坡":
                    return "65";
                case "新西兰":
                    return "64";
                case "印度尼西亚":
                    return "62";
                case "匈牙利":
                    return "36";
                case "葡萄牙":
                    return "351";
            }
            return "";
        }
    }

    //back express id
    public static int getExpressId(String str) {
        if (null == str) {
            return 0;
        } else {
            switch (str) {
                case "申通快递":
                    return 361;
                case "韵达快递":
                    return 362;
                case "圆通快递":
                    return 363;
                case "顺丰快递":
                    return 364;
                case "百世快递":
                    return 365;
                case "中通快递":
                    return 366;
                case "邮政快递":
                    return 367;
                case "德邦物流":
                    return 368;
                case "EMS快递":
                    return 369;
                case "天天快递":
                    return 370;
                case "宅急送":
                    return 371;
                case "国通快递":
                    return 372;
                case "全峰快递":
                    return 373;
            }
            return 0;
        }
    }

    //back express id
    public static int getKefu(String str) {
        if (null == str) {
            return 0;
        } else {
            switch (str) {
                case "1号客服":
                    return 1;
                case "2号客服":
                    return 2;
                case "3号客服":
                    return 3;
                case "4号客服":
                    return 4;
                case "5号客服":
                    return 5;
                case "6号客服":
                    return 6;
            }
            return 0;
        }
    }

    //back express id
    public static void setStatusImg(String str, ImageView img) {
            switch (str) {
                case "0":
                    img.setImageResource(R.drawable.zaitu);
                    break;
                case "1":
                    img.setImageResource(R.drawable.lanjian);
                    break;
                case "2":
                    img.setImageResource(R.drawable.yinan);
                    break;
                case "3":
                    img.setImageResource(R.drawable.qianshou);
                    break;
                case "4":
                    img.setImageResource(R.drawable.tuiqian);
                    break;
                case "5":
                    img.setImageResource(R.drawable.paijian);
                    break;
                case "6":
                    img.setImageResource(R.drawable.tuihui);
                    break;
                case "7":
                    img.setImageResource(R.drawable.daozhongzhuang);
                    break;
                case "8":
                    img.setImageResource(R.drawable.dabaozhong);
                    break;
                case "9":
                    img.setImageResource(R.drawable.yizhuangyun);
                    break;
                case "10":
                    img.setImageResource(R.drawable.daixiadan);
                    break;
                case "11":
                    img.setImageResource(R.drawable.daifukuan);
                    break;
                case "12":
                    img.setImageResource(R.drawable.yifukuan);
                    break;
                case "13":
                    img.setImageResource(R.drawable.yichuku);
                    break;
                default:
                    img.setImageResource(R.drawable.zanwu);
                    break;
        }
    }

    public static String splitString(String str, String split) {
        String a[] = str.split(split);
        return a[a.length - 1];
    }

    public static boolean isImageFile(String str) {
        if (str.isEmpty()) {
            return false;
        }
        String suffix = str.substring(str.length() - 3, str.length());
        if ("jpg".equals(suffix) || "png".equals(suffix) || "jpeg".equals(suffix) || "gif".equals(suffix)) {
            return true;
        }
        return false;
    }

    //back express id
    public static void setTextAndImg(String id, TextView tv, ImageView iv) {
        if (null == id) {
            return;
        } else {
            switch (id) {
                case "2":
                    if (null != tv)
                        tv.setText("UPS");
                    if (null != iv)
                        iv.setImageResource(R.drawable.ups);
                    break;
                case "4":
                    if (null != tv)
                        tv.setText("TNT");
                    if (null != iv)
                        iv.setImageResource(R.drawable.tnt);
                    break;
                case "271":
                    if (null != tv)
                        tv.setText("TOLL");
                    if (null != iv)
                        iv.setImageResource(R.drawable.toll);
                    break;
                case "361":
                    if (null != tv)
                        tv.setText("申通快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.shentong);
                    break;
                case "362":
                    if (null != tv)
                    tv.setText("韵达快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.yunda);
                    break;
                case "363":
                    if (null != tv)
                    tv.setText("圆通快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.yuantong);
                    break;
                case "364":
                    if (null != tv)
                    tv.setText("顺丰快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.shunfeng);
                    break;
                case "365":
                    if (null != tv)
                    tv.setText("百世快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.baishi);
                    break;
                case "366":
                    if (null != tv)
                    tv.setText("中通快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.zhongtong);
                    break;
                case "367":
                    if (null != tv)
                    tv.setText("邮政快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.youzheng);
                    break;
                case "368":
                    if (null != tv)
                    tv.setText("德邦物流");
                    if (null != iv)
                        iv.setImageResource(R.drawable.debang);
                    break;
                case "369":
                    if (null != tv)
                    tv.setText("邮政快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.ems);
                    break;
                case "5":
                    if (null != tv)
                        tv.setText("EMS快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.ems);
                    break;
                case "6":
                    if (null != tv)
                        tv.setText("EMS快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.ems);
                    break;
                case "370":
                    if (null != tv)
                    tv.setText("天天快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.tiantian);
                    break;
                case "371":
                    if (null != tv)
                    tv.setText("宅急送");
                    if (null != iv)
                        iv.setImageResource(R.drawable.zaijisong);
                    break;
                case "372":
                    if (null != tv)
                    tv.setText("国通快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.guotong);
                    break;
                case "373":
                    if (null != tv)
                    tv.setText("全峰快递");
                    if (null != iv)
                        iv.setImageResource(R.drawable.quanfeng);
                    break;
                case "374":
                    if (null != tv)
                    tv.setText("中外运");
                    if (null != iv)
                        iv.setImageResource(R.drawable.zhongwaiyun);
                    break;
                default:
                    if (null != tv)
                        tv.setText("其他");
                    if (null != iv)
                        iv.setImageResource(R.drawable.other);
                    break;
            }
        }
    }

    public static void selectCode(int item, final TextView view, final TextView view2, Activity activity) {
        final String items[] = {"中国", "澳大利亚", "香港", "台湾", "韩国","澳门", "美国", "英国", "日本", "俄罗斯", "意大利", "马来西亚","法国","加拿大","新加坡","新西兰"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);  //先得到构造器
        builder.setTitle("请选择区号:"); //设置标题
        builder.setIcon(R.drawable.phonecode);//设置图标，图片id即可
        builder.setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (items[which]) {
                    case "中国":
                        view2.setText("+86");
                        break;
                    case "澳大利亚":
                        view2.setText("+61");
                        break;
                    case "香港":
                        view2.setText("+852");
                        break;
                    case "台湾":
                        view2.setText("+886");
                        break;
                    case "韩国":
                        view2.setText("+82");
                        break;
                    case "澳门":
                        view2.setText("+853");
                        break;
                    case "美国":
                        view2.setText("+1");
                        break;
                    case "英国":
                        view2.setText("+44");
                        break;
                    case "日本":
                        view2.setText("+81");
                        break;
                    case "俄罗斯":
                        view2.setText("+7");
                        break;
                    case "意大利":
                        view2.setText("+39");
                        break;
                    case "马来西亚":
                        view2.setText("+60");
                        break;
                    case "法国":
                        view2.setText("+33");
                        break;
                    case "加拿大":
                        view2.setText("+1");
                        break;
                    case "新加坡":
                        view2.setText("+65");
                        break;
                    case "新西兰":
                        view2.setText("+64");
                        break;
                }
                if (null != view) {
                    view.setText(items[which]);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //copy text
    public static void copyText(String copyText, Activity context, String hint) {
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", copyText);
        myClipboard.setPrimaryClip(myClip);
        ShowToastUtils.toast(context, hint, 1);
    }

    //保留2位小数  double
    public static String keepDouble(String doubleStr) {
        double allMoney = 0.00;
        DecimalFormat df  = new DecimalFormat("######0.00");
        allMoney = StringUtils.toDouble(doubleStr);
        return df.format(allMoney);
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
