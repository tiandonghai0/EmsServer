package com.shmet.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.*;

public class JsonUtils {


  // 对象转字符串
  public static <T> String objToString(T obj) {
    if (obj == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  // 字符串转对象
  public static <T> T stringToObj(String str, Class<T> clazz) {
    if (StringUtils.isEmpty(str) || clazz == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 对象转byte[]数组
   *
   * @param obj obj
   * @return byte[]
   */
  public static byte[] toByteArray(Object obj) {
    byte[] bytes = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.flush();
      bytes = bos.toByteArray();
      oos.close();
      bos.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return bytes;
  }

  /**
   * byte[]数组转对象
   *
   * @param bytes bytes
   * @return Object
   */
  public static Object toObject(byte[] bytes) {
    Object obj = null;
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bis);
      obj = ois.readObject();
      ois.close();
      bis.close();
    } catch (IOException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return obj;
  }

}
