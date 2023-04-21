package com.shmet.aop;

import java.lang.annotation.*;

/**
 * @author
 * 自定义操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogAnnotation {
  //1:登录 2:新增 3:修改 4:删除 5:查询 6:下发指令
  int logType() default 5;

  //操作类型 对应 上述的 logType
  String opType() default "";

  //操作内容
  String opContent() default "";
}
