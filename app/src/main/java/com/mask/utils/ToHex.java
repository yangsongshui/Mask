package com.mask.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ToHex {
    public static final int MESSAGE_DEVICE_NAME = 4;
    private static final char[] bcdLookup = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static final String MTA_COOPERATION_TAG = "";


    public static String bytesToHex(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * REQUEST_ENABLE_BT);
        for (int i = 0; i < bcd.length; i += REQUEST_CONNECT_DEVICE) {
            s.append(bcdLookup[(bcd[i] >>> MESSAGE_DEVICE_NAME) & 15]);
            s.append(bcdLookup[bcd[i] & 15]);

        }
        return s.toString();
    }

    public static String replaceBlank(String str) {
        String dest = MTA_COOPERATION_TAG;
        if (str != null) {
            return Pattern.compile("\\s*|\t|\r|\n").matcher(str).replaceAll(MTA_COOPERATION_TAG);
        }

        return dest;
    }

    public static String StringToHex(String string) {
        String hex = "";
        hex = Integer.toHexString(Integer.parseInt(string));
        if (hex.length() == 1) {
            hex = "000" + hex;

        } else if (hex.length() == 2) {
            hex = "00" + hex;
        } else if (hex.length() == 3) {
            hex = '0' + hex;
        }

        return hex;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] StringFilter(String str) throws PatternSyntaxException {
        return hexToBytes(replaceBlank(Pattern.compile("[^a-fA-F0-9]").matcher(str).replaceAll(MTA_COOPERATION_TAG).trim()));
    }

    public static final byte[] hexToBytes(String s) {
        byte[] bytes = new byte[(s.length() / REQUEST_ENABLE_BT)];
        for (int i = 0; i < bytes.length; i += REQUEST_CONNECT_DEVICE) {
            bytes[i] = (byte) Integer.parseInt(s.substring(i * REQUEST_ENABLE_BT, (i * REQUEST_ENABLE_BT) + REQUEST_ENABLE_BT), 16);
        }
        return bytes;
    }

    public static String stringHexToStringInt(String stirng) {
        String str = "";
        for (int i = 0; i < stirng.length(); i = i + REQUEST_ENABLE_BT) {
            String parseInt = Integer.parseInt(stirng.substring(i, i + REQUEST_ENABLE_BT), 16) + "";
            str += parseInt;
        }
        return str;
    }

    public static String stringIntToStringHex(String stirng) {
        String str = "";
        for (int i = 0; i < stirng.length(); i = i + REQUEST_ENABLE_BT) {
            String parseInt = StringToHex(stirng.substring(i, i + REQUEST_ENABLE_BT)) + "";
            if (parseInt.length() == 1) {
                parseInt = "0" + parseInt;
            }

            str += parseInt;
        }

        return str;
    }

    /**
     * 累加和公式
     *
     * @param para 字符串
     **/
    public static String appendSpace(String para) {
        if (para == null || para.equals("")) {
            return "";
        }
        int total = 0;
        int len = para.length();
        int num = 0;
        while (num < len) {
            String s = para.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    } public static String StringToHex3(String string) {
        String hex = "";
        hex = Integer.toHexString(Integer.parseInt(string));
        if (hex.length() == 1) {
            hex = "000" + hex;

        } else if (hex.length() == 2) {
            hex = "00" + hex;

        } else if (hex.length() == 3) {
            hex = "0" + hex;

        }

        return hex;
    }
}
