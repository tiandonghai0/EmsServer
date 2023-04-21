package com.shmet.bean.waterMeter;

/**
 * 表计剩余量清零
 * http://ip:port/userInfo/clearIntefer
 * {"code":1,"meter_id":"14840052","msg":"提交成功", "tra_id":"123455" }
 * 说明：code=0 认证失败，code=1 提交成功，code=2 找不到该表号，code=3 提交失败 ,msg=结果描述
 */
public class ClearInteferInfo extends WaterMeterBaseInfo {
    private String tra_id;
}
