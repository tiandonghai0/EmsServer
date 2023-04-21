package com.shmet.api;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class MyHttpClient {
  public static JSONObject postStr(String url, String paramsStr) {
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost httppost = new HttpPost(url);
      //添加http头信息
      //httppost.addHeader("Authorization", "your token"); //认证token
      httppost.addHeader("Content-Type", "application/json");
      httppost.addHeader("User-Agent", "imgfornote");
      httppost.setEntity(new StringEntity(paramsStr));
      HttpResponse response = client.execute(httppost);
      //检验状态码，如果成功接收数据
      int code = response.getStatusLine().getStatusCode();
      if (code == 200) {
        //String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "","name": ""}
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          return JSONObject.parseObject(EntityUtils.toString(entity,
              "UTF-8"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
