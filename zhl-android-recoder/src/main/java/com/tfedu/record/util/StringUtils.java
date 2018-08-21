package com.tfedu.record.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Panyy
 * @ClassName: StringUtils
 * @Description: TODO字符串操作工具包
 * @date 2014 2014年3月14日 下午1:05:00
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
            "yyyy-MM-dd");

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        if (isEmpty(sdate))
            return null;
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.format(cal.getTime());
        String paramDate = dateFormater2.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime())
                    / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime())
                    / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.format(today);
            String timeDate = dateFormater2.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

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
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
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
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
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
     * 字符串转布尔值
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

    public static String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    /**
     * @param size
     * @return
     * @Title: btoKM
     * @Description:把b转成Kb或者MB
     */
    public static String btoKM(String s) {
        String sizeStr = "";
        if (s != null && !"".equals(s)) {
            float size = Float.parseFloat(s);
            DecimalFormat df = new DecimalFormat(".00");
            if (size < 1024) {
                sizeStr = String.valueOf(size) + "B";
            } else if (size >= 1024 && size < 1024 * 1024) {
                sizeStr = String.valueOf(df.format(size / 1024)) + "KB";
            } else if (size >= 1024 * 1024) {
                sizeStr = String.valueOf(df.format(size / 1024 / 1024)) + "MB";
            }
        }
        return sizeStr;
    }

    public static String btoM(long size) {
        DecimalFormat df = new DecimalFormat("");
        String sizeStr = String.valueOf(df.format(size / 1024 / 1024)) + "MB";
        return sizeStr;
    }

    /**
     * 验证手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile(
                "^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static SpannableStringBuilder getStringBuidler(String start,
                                                          String middle, String end, String color) {
        SpannableStringBuilder builder = new SpannableStringBuilder(
                start + middle + end);
        ForegroundColorSpan span = new ForegroundColorSpan(
                Color.parseColor(color));
        builder.setSpan(span, start.length(), start.length() + middle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * @throws
     * @Title: cutWorkPath
     * @Description: 截取作业的相对路径
     * @param: @param workPath
     * @param: @return
     * @return: String
     */
    public static String cutWorkPath(String workPath) {
        String cutstr = "";
        String[] cut = workPath.split("/");
        for (int i = 0; i < cut.length; i++) {
            if (i <= 4) {
                cutstr += ("/" + cut[i]).trim();
            }
        }
        return cutstr;
    }

    public static boolean isPhoto(String content) {
        if (content.contains("[photo]") && content.contains("[/photo]")) {
            return true;
        }
        return false;
    }

    public static boolean isPenbox(String content) {
        if (content.contains("[penbox]") && content.contains("[/penbox]")) {
            return true;
        }
        return false;
    }

    public static boolean isRecord(String content) {
        if (content.contains("[record]") && content.contains("[/record]")) {
            return true;
        }
        return false;
    }

    public static String string2Json(String content) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        content = sb.toString();
        content = content.replace("\\n", "<br/>");
        content = content.replace(" ", "&nbsp;");
        return content;
    }

}