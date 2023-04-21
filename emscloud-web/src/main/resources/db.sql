create table t_operate_log
(
    id          bigint(11) primary key auto_increment comment '自增主键Id(无业务含义)',
    username    varchar(40)  not null comment '用户名',
    ip          varchar(40) comment '客户端请求Ip',
    time        varchar(20)  not null comment '时间 格式 yyyy-MM-dd HH:mm:ss',
    log_type    int(3) not null comment '日志类型, 1:登录 2:新增 3:修改 4:删除 5:查询 6:下发指令',
    content     varchar(200) not null comment '操作内容',
    param       varchar(200) not null comment '接口待传入参数',
    op_platform varchar(60)  not null comment '操作平台'
);