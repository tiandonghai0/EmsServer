package com.shmet.helper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Requst请求类
 */
public class RequestUtils {

    /**
     * 使用post请求，请求默认文本类型
     *
     * @param clazz
     * @param url
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, Object> map, Class<T> clazz) {

        return post(url, map, MediaType.APPLICATION_FORM_URLENCODED, clazz,null);
    }

    /**
     * POST MediaType 类型 json
     *
     * @param clazz
     * @param url
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T postJson(String url, Map<String, Object> map, Class<T> clazz) {

        return post(url, map, MediaType.APPLICATION_JSON, clazz,null);
    }

    public static <T> T postJson(String url, Map<String, Object> map,MultiValueMap<String, String> headersMap, Class<T> clazz) {

        return post(url, map, MediaType.APPLICATION_JSON,clazz,headersMap);
    }

    /**
     *
     * @param url
     * @param map
     * @param mediaType
     * @param clazz  返回类型
     * @param <T>
     * @return
     */
    public static <T> T postJson(String url, Map<String, Object> map,MediaType mediaType, Class<T> clazz) {

        return post(url, map,mediaType, clazz,null);
    }

    /**
     * 使用post请求
     *
     * @param clazz     返回类型
     * @param url       路径
     * @param dataMap   请求参数
     * @param mediaType 请求类型
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, Object> dataMap, MediaType mediaType, Class<T> clazz ,MultiValueMap<String, String> headersMap) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            //默认文本
            headers.setContentType(mediaType);
            if (headersMap != null) {
                headers.addAll(headersMap);
            }
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataMap, headers);
            ResponseEntity<T> response = restTemplate.postForEntity(url, request, clazz);
            System.out.println(response.getBody());
            return response.getBody();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    public static <T> T post(String url, Map<String, Object> dataMap, MediaType mediaType, Class<T> clazz) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            //默认文本
            headers.setContentType(mediaType);
            //HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(dataMap, headers);
            HttpEntity<Map<String,Object>> request= new HttpEntity<>(dataMap, headers);
            ResponseEntity<T> response = restTemplate.postForEntity(url, request, clazz);
            System.out.println(response.getBody());
            return response.getBody();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

}
