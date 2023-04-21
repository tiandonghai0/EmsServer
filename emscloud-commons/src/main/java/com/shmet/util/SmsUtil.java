package com.shmet.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class SmsUtil {

  public static final String regionId = "cn-hangzhou";
  public static final String accessKeyId = "LTAI4G5XMsq2FBER1Ej4rnYB";
  public static final String accessSecret = "NIw2BjA99T03uEtvIVBYbMzFcxo0aP";
  public static final String sysDomain = "dysmsapi.aliyuncs.com";
  public static final String sysVersion = "2017-05-25";
  public static final String sysAction = "SendSms";
  public static final String signName = "聚震能源科技";
  public static final String templateCode = "SMS_203075412";


  public static void sendSms(String phone, String device, String failureterm, String failurecontent) {
    DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessSecret);
    IAcsClient client = new DefaultAcsClient(profile);

    CommonRequest request = new CommonRequest();
    request.setSysMethod(MethodType.POST);
    request.setSysDomain(sysDomain);
    request.setSysVersion(sysVersion);
    request.setSysAction(sysAction);
    request.putQueryParameter("RegionId", regionId);
    request.putQueryParameter("PhoneNumbers", phone);
    request.putQueryParameter("SignName", signName);
    request.putQueryParameter("TemplateCode", templateCode);
    request.putQueryParameter("TemplateParam", "{\"device\":\"" + device + "\",\"failureterm\":\"" + failureterm + "\",\"failurecontent\":\"" + failurecontent + "\"}");
    try {
      CommonResponse response = client.getCommonResponse(request);
      System.out.println(response.getData());
    } catch (ClientException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    //sendBatchSms();
    sendSms("15273379271", "aa", "bb", "cc");
    //sendSms("15273379272", "a", "b", "c");
  }
}
