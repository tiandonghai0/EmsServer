package com.shmet.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class EncryptUtil {
  /**
   * md5 加密验证
   *
   * @param source      源
   * @param toUpperCase 是否转为大写
   * @return md5 加密串
   */
  public static String md5(String source, boolean toUpperCase) {
    StringBuilder sb = new StringBuilder(32);
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] array = md.digest(source.getBytes(StandardCharsets.UTF_8));
      for (byte b : array) {
        if (toUpperCase) {
          sb.append(Integer.toHexString((b & 0xFF) | 0x100).toUpperCase(), 1, 3);
        } else {
          sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return sb.toString();
  }
  public static void main(String[] args) {
    String a="2020-10-09";
    System.out.println(a.substring(0,4));
  }
}
