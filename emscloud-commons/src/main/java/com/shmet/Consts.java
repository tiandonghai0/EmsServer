package com.shmet;

import io.netty.util.AttributeKey;

public class Consts {
    public static final String SESSION_USERINFO = "SESSION_USERINFO";

    /**
     * 尖峰电，比峰电高一级
     */
    public static final int ELECTRIC_PRICE_AIGUILLE = 4;

    /**
     * 峰电
     */
    public static final int ELECTRIC_PRICE_PEAK = 1;

    /**
     * 谷电
     */
    public static final int ELECTRIC_PRICE_VALLEY = 2;

    /**
     * 平
     */
    public static final int ELECTRIC_PRICE_FLAT = 3;

    /**
     * 充电
     */
    public static final int ELECTRIC_CHARGE = 1;

    /**
     * 放电
     */
    public static final int ELECTRIC_DISCHARGE = 2;

    /**
     * 停止
     */
    public static final int ELECTRIC_STOPPED = 3;


    public static final String KEY_SEP = ".";

    /**
     * 日志级别-INFO
     */
    public static final String LOG_LEVEL_INFO = "INFO";

    /**
     * 日志级别-ERROR
     */
    public static final String LOG_LEVEL_ERROR = "ERROR";
    /**
     * 日志分类-系统启动/停止
     */
    public static final int LOG_CATAGORY_SYSTEMBOOT = 1;

    /**
     * 日志分类-ACCONE主动上报错误
     */
    public static final int LOG_CATAGORY_ACCONE_ERROR_REPORT = 2;

    /**
     * 日志分类-系统充/放电
     */
    public static final int LOG_CATAGORY_CHARGE_DISCHARGE = 3;

    /**
     * 日志子分类-系统启动
     */
    public static final int LOG_SUB_CATAGORY_SYSTEMBOOT_START = 1;

    /**
     * 日志子分类-系统停止
     */
    public static final int LOG_SUB_CATAGORY_SYSTEMBOOT_STOP = 2;

    /**
     * 日志子分类-开始充电
     */
    public static final int LOG_SUB_CATAGORY_CHARGE = 1;

    /**
     * 日志子分类-系统停止
     */
    public static final int LOG_SUB_CATAGORY_DISCHARGE = 2;

    /**
     * 日志分类-ACCONE主动上报错误
     */
    public static final int LOG_SUB_CATAGORY_ACCONE_ERROR_REPORT = 1;

    /**
     * redis cache 过期时间--通用基础数据
     */
    public static final long REDIS_CACHE_MASTER_DATA_TIME_OUT = 30 * 60;

    /**
     * 命令字-一般响应
     */
    public static final int COMMAND_TYPE_COMMON_RESPONSE = 1000;

    /**
     * 命令字-更新Accone
     */
    public static final int COMMAND_TYPE_UPDATE_ACCONE = 4;

    /**
     * 命令字-系统启动
     */
    public static final int COMMAND_TYPE_SYSTEM_BOOT = 5;

    /**
     * 命令字-系统停止
     */
    public static final int COMMAND_TYPE_SYSTEM_STOP = 6;

    /**
     * 命令字-系统停止
     */
    public static final int COMMAND_TYPE_CHARGE_DISCHARGE = 8;

    /**
     * 命令字-获取电池系统详细数据
     */
    public static final int COMMAND_TYPE_GET_BAT_DATA = 9;

    /**
     * 命令字-获取电池系统详细数据
     */
    public static final int COMMAND_TYPE_SEND_TEXT_CMD = 10;

    /**
     * 命令字-下发并离网自动、手动
     */
    public static final int COMMAND_TYPE_SEND_POLICY_CMD = 11;
    /**
     * 命令字-下发空调控制
     */
    public static final int COMMAND_TYPE_SEND_AIR_CMD = 12;
    
    /**
     * 命令字-下发亨通岸电设备启动
     */
    public static final int COMMAND_TYPE_SHOREPOWER_START_CMD = 13;
    /**
     * 命令字-下发亨通岸电设备停止
     */
    public static final int COMMAND_TYPE_SHOREPOWER_STOP_CMD = 14;

    /**
     * 命令字--策略内容下发
     */
    public static final int COMMAND_TYPE_TACTIC=24;

    /**
     * 命令字--EMU运行模式
     */
    public static final int COMMAND_TYPE_RUN=25;
    /**
     * 命令字--风机控制
     */
    public static final int COMMAND_TYPE_FAN=26;

    /**
     * 命令字--下发调试
     */
    public static final int COMMAND_TYPE_DUGUG = 8888;

    /**
     * 更新Accone/系统启动/系统停止-状态码-0-正常
     */
    public static final int STATUS_COMMON_0 = 0;

    /**
     * 更新Accone/系统启动/系统停止-状态码-1-不成功
     */
    public static final int STATUS_COMMON_1 = 1;

    /**
     * 更新Accone-状态码-2-Accone未登录
     */
    public static final int STATUS_UPDATE_ACCONE_2 = 2;

    /**
     * Accone-RunningStatus-状态码-0-停止
     */
    public static final int RUNNING_STATUS_STOPPED = 0;

    /**
     * Accone-RunningStatus-状态码-1-运行
     */
    public static final int RUNNING_STATUS_RUNNING = 1;

    /**
     * Accone-RunningStatus-状态码-2-启动执行中
     */
    public static final int RUNNING_STATUS_STARTING = 2;

    /**
     * Accone-RunningStatus-状态码-3-停止执行中
     */
    public static final int RUNNING_STATUS_STOPPING = 3;

    /**
     * 一般响应-成功flag
     */
    public static final int RESPONSE_SUCCESS_0 = 0;

    /**
     * 一般响应-登录认证失败
     */
    public static final int RESPONSE_FAULT_101 = 101;

    /**
     * 一般响应-设备型号不在库
     */
    public static final int RESPONSE_FAULT_102 = 102;

    /**
     * 一般响应-预期外异常
     */
    public static final int RESPONSE_FAULT_103 = 103;

    /**
     * 一般响应-CRC校验失败
     */
    public static final int RESPONSE_FAULT_104 = 104;

    /**
     * 自动发送策略-YES-flag
     */
    public static final int AUTO_SEND_YES = 1;

    /**
     * 自动发送策略-NO-flag
     */
    public static final int AUTO_SEND_NO = 0;

    /**
     * 用户是否已登录的标志位
     */
    public static final AttributeKey<Boolean> NETTY_CHANNEL_ATTR_HAS_LOGIN = AttributeKey.valueOf("netty.channel.attr.has_login");

    /**
     * AccOneId
     */
    public static final AttributeKey<Long> NETTY_CHANNEL_ATTR_ACCONEID = AttributeKey.valueOf("netty.channel.attr.acconeid");

    /**
     * Uname
     */
    public static final AttributeKey<String> NETTY_CHANNEL_ATTR_UNAME = AttributeKey.valueOf("netty.channel.attr.uname");

    /**
     * login time
     */
    public static final AttributeKey<Long> NETTY_CHANNEL_ATTR_LOGIN_TIME = AttributeKey.valueOf("netty.channel.attr.logintime");


    /**
     *
     */
    public static final String REALRECORDMETHOD_LIST = "List";

    /**
     * 登录成功
     */
    public static final String MSG_CODE_I000001 = "I000001";

    /**
     * 预期外的异常
     */
    public static final String MSG_CODE_E010000 = "E010000";

    /**
     * 消息解析错误，如json解析错误等
     */
    public static final String MSG_CODE_E000001 = "E000001";

    /**
     * 格式化错误，如日期格式化等
     */
    public static final String MSG_CODE_E000002 = "E000002";

    /**
     * 登录失败-用户名密码错误
     */
    public static final String MSG_CODE_E000003 = "E000003";

    /**
     * 登录失败-设备型号不在数据库内
     */
    public static final String MSG_CODE_E000004 = "E000004";

    /**
     * 实时点位监测请求失败-设备id为空
     */
    public static final String MSG_CODE_E000005 = "E000005";

    /**
     * 公式配置错误
     */
    public static final String MSG_CODE_E000006 = "E000006";

    /**
     * 分组数据不全错误
     */
    public static final String MSG_CODE_E000007 = "E000007";
    /**
     * CRC校验错误
     */
    public static final String MSG_CODE_E000008 = "E000008";

    /**
     * 上传的设备实时数据中存在指标项，该指标项未在配置文件中配置
     */
    public static final String MSG_CODE_W000001 = "W000001";

    /**
     * 心跳超时，关闭连接
     */
    public static final String MSG_CODE_W000002 = "W000002";

    /**
     * 没有登录，请先登录
     */
    public static final String MSG_CODE_W000003 = "W000003";

    /**
     * 设备不在库
     */
    public static final String MSG_CODE_W000004 = "W000004";

    /**
     * 设备重复，acconeId+deviceModel+deviceId应该能够唯一确定一个设备
     */
    public static final String MSG_CODE_W000005 = "W000005";

    /**
     * 通讯处理器配置文件地址
     */
    public static final String COMMUNICATION_DEVICEPROCESSORCONFIG_PATH = "deviceProcessorConfig.json";

    /**
     * 通讯处理器配置-实时数据处理
     */
    public static final String COMMUNICATION_DEVICEPROCESSORCONFIG_REALDATA = "realdata";

    /**
     * 通讯处理器配置-异常数据处理
     */
    public static final String COMMUNICATION_DEVICEPROCESSORCONFIG_FAULTDATA = "faultdata";

    /**
     * 通讯处理器配置-统计处理
     */
    public static final String COMMUNICATION_DEVICEPROCESSORCONFIG_STATISTICS = "statistics";

    /**
     * 点位类型-0-正常点位
     */
    public static final int TAG_TYPE_NORM_0 = 0;

    /**
     * 点位类型-1-指示错误的点位
     */
    public static final int TAG_TYPE_FAULT_1 = 1;

    /**
     * 点位类型-2-未配置的点位
     */
    public static final int TAG_TYPE_UNCONFIG_2 = 2;

    /**
     * 默认统计-max-最大值
     */
    public static final String DEFAULT_STATISTICS_MAX = "max";

    /**
     * 默认统计-min-最大值
     */
    public static final String DEFAULT_STATISTICS_MIN = "min";

    /**
     * 默认统计-sum-和值-用于求平均
     */
    public static final String DEFAULT_STATISTICS_SUM = "sum";

    /**
     * 默认统计-sum-统计计数-用于求平均
     */
    public static final String DEFAULT_STATISTICS_COUNT = "count";

    /**
     * 默认统计-记录时间段内的当前计数值对应的时间戳
     */
    public static final String STATISTICS_ALL_LAST_TIMESTAMP = "lastTimestamp";

    /**
     * 默认统计-记录时间段内的首次计数值对应的时间戳
     */
    public static final String STATISTICS_ALL_FIRST_TIMESTAMP = "firstTimestamp";

    /**
     * diffsum统计-记录时间段内的第一个计数值
     */
    public static final String DIFFSUM_STATISTICS_FIRST = "first";

    /**
     * diffsum统计-记录时间段内的最后一个计数值
     */
    public static final String STATISTICS_ALL_LAST = "last";

    public static final String DIFFSUM_STATISTICS_DIFFSUM = "diffsum";
    public static final String DIFFSUM_STATISTICS_INCOME_PRICE = "incomePrice";
    public static final String DIFFSUM_STATISTICS_PRICE = "price";
    public static final String DIFFSUM_STATISTICS_ORIGIN_INCOME_PRICE = "originIncomePrice";
    public static final String DIFFSUM_STATISTICS_ORIGIN_PRICE = "originPrice";
    public static final String DIFFSUM_STATISTICS_UNIT_PRICE = "unitPrice";
    public static final String DIFFSUM_STATISTICS_INCOME_UNIT_PRICE = "incomeUnitPrice";
    public static final String DIFFSUM_STATISTICS_SERVICE_PRICE = "servicePrice";
    public static final String DIFFSUM_STATISTICS_EPRICE_TYPE = "priceType";
    public static final String DIFFSUM_STATISTICS_MultiplyingFactor = "multiplyingFactor";

    public static final String WEB_ASSEMBLE_CLASS = "assembleClass";

    public static final String WEB_ASSEMBLE_PUSH_REQ_MSG = "pushRequestMsg";

    /**
     * 查询统计数据的时间类型-1-实时数据
     */
    public static final int TIME_TYPE_REAL = 1;

    /**
     * 查询统计数据的时间类型-2-小时
     */
    public static final int TIME_TYPE_HOUR = 2;

    /**
     * 查询统计数据的时间类型-3-天
     */
    public static final int TIME_TYPE_DAY = 3;

    /**
     * 查询统计数据的时间类型-4-月
     */
    public static final int TIME_TYPE_MONTH = 4;

    /**
     * 空调关机
     */
    public static final int AIR_OFF = 1;
    /**
     * 空调开机-制冷
     */
    public static final int AIR_ON_COOL = 2;
    /**
     * 空调开机-制热
     */
    public static final int AIR_ON_HOT = 3;
    /**
     * 空调-除湿
     */
    public static final int AIR_ON_HUMIDITY = 4;
    
    /**
     * 船E行指令-开始用电
     */
    public static final int EHANG_SS_TYPE_START = 1;
    
    /**
     * 船E行指令-结束用电
     */
    public static final int EHANG_SS_TYPE_STOP = 2;


}
