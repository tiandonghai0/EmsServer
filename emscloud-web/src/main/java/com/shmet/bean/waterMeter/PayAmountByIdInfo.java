package com.shmet.bean.waterMeter;

/**
 * 根据 token 和设备 id 金额充值 金额 、唯一交易号，对某个设备 id 充值
 * http://ip:port/userInfo/payAmountById
 * amount=充值金额;meter_id=电表号;tra_id=交易号(不能重复，提交成功后，电表系统是唯一 id)
 * 说明：code=0 认证失败，code=1 提交成功，code=2 数据不存在，code=3 提交失败
 * msg=结果描述
 */
public class PayAmountByIdInfo extends WaterMeterBaseInfo {

}
