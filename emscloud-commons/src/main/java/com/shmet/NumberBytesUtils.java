package com.shmet;

import java.util.regex.Pattern;

public class NumberBytesUtils {
    public static int bytesToInt(byte[] i) {
        byte[] src = new byte[4];
        System.arraycopy(i, 0, src, 4 - i.length, i.length);
        int value;
        value =
                (int) ((src[3] & 0xFF) | ((src[2] & 0xFF) << 8) | ((src[1] & 0xFF) << 16) | ((src[0] & 0xFF) << 24));
        return value;
    }

    public static byte[] intToBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte intToOneByte(int num) {
        return (byte) (num & 0x000000ff);
    }

    public static byte[] intToTwoBytes(int i) {
        byte[] result = new byte[2];
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

    // byte数组转成long
    public static long bytesToLong(byte[] data) {
        byte[] b = new byte[8];
        System.arraycopy(data, 0, b, 8 - data.length, data.length);
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    public static byte[] longToBytes(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static byte[] doubleToBytes(double d) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    public static double bytesToDouble(byte[] b) {
        return Double.longBitsToDouble(bytesToLong(b));
    }

    public static float bytesToFloat(byte[] b) {
        return Float.intBitsToFloat(bytesToInt(b));
    }

    public static byte[] floatToBytes(float f) {
        return intToBytes(Float.floatToIntBits(f));
    }

    public static Object bytesToObject(byte[] bytes, String type) {
        if ("string".equalsIgnoreCase(type)) {
            return new String(bytes);
        } else if ("int".equalsIgnoreCase(type)) {
            return bytesToInt(bytes);
        } else if ("double".equalsIgnoreCase(type)) {
            return bytesToDouble(bytes);
        } else if ("float".equalsIgnoreCase(type)) {
            return bytesToFloat(bytes);
        } else if ("long".equalsIgnoreCase(type)) {
            return bytesToLong(bytes);
        } else {
            return bytes;
        }
    }

    // 判断是否为纯数字
    public static boolean isNumber(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    // 判断浮点数（double和float）
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void main(String agrs[]) {
        System.out.println(bytesToFloat(floatToBytes(12.232321F)));
    }
}
