package com.shmet.helper.crypt;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESCrypt {
  // 加密算法
  private String ALGO = "AES";

  private final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

  private IvParameterSpec ivParameterSpec;

  private byte[] keyValue;

  public AESCrypt(String keyValue, String iv) {
    this.setKeyValue(keyValue.getBytes());
    try {
      this.setIvParameterSpec(iv);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 用来进行加密的操作
   */
  public String encrypt(String Data) throws Exception {
    IvParameterSpec ivParameterSpec = this.getIvParameterSpec();
    Key key = generateKey();
    Cipher c = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
    c.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
    byte[] encVal = c.doFinal(Data.getBytes());
    String encryptedValue = Base64.encodeBase64String(encVal);
    return encryptedValue;
  }

  /**
   * 用来进行解密的操作
   */
  public String decrypt(String encryptedData) throws Exception {
    IvParameterSpec ivParameterSpec = this.getIvParameterSpec();
    Key key = generateKey();
    Cipher c = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
    c.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
    byte[] decordedValue = Base64.decodeBase64(encryptedData);
    byte[] decValue = c.doFinal(decordedValue);
    String decryptedValue = new String(decValue);
    return decryptedValue;
  }

  /**
   * 根据密钥和算法生成Key
   */
  private Key generateKey() throws Exception {
    Key key = new SecretKeySpec(keyValue, ALGO);
    return key;
  }

  public byte[] getKeyValue() {
    return keyValue;
  }

  public void setKeyValue(byte[] keyValue) {
    this.keyValue = keyValue;
  }

  /**
   * @return the ivParameterSpec
   */
  public IvParameterSpec getIvParameterSpec() {
    return ivParameterSpec;
  }

  /**
   * @param iv the ivParameterSpec to set
   * @throws UnsupportedEncodingException
   */
  public void setIvParameterSpec(String iv) throws UnsupportedEncodingException {
    this.ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));
  }

  public static void main(String[] args) {
    AESCrypt aes = new AESCrypt("1234567890abcdef", "1234567890abcdef");
    try {
      String data = aes.encrypt("{\"total\":1,\"stationStatusInfo\":{\"operationID\":\"123456789\",\"stationID\":\"111111111111111\",\"connectorStatusInfos\":{\"connectorID\":1,\"equipmentID\":\"10000000000000000000001\",\"status\":4,\"currentA\":0,\"currentB\":0,\"currentC\":0,\"voltageA\":0,\"voltageB\":0,\"voltageC\":0,\"soc\":10,}}}");
      System.out.println(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
