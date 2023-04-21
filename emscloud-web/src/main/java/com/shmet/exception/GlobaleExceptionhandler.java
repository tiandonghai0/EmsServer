package com.shmet.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author
 */
@RestControllerAdvice
public class GlobaleExceptionhandler {

  public static final Logger log = LoggerFactory.getLogger(GlobaleExceptionhandler.class);

  @ExceptionHandler(NotTokenException.class)
  public Result handlerNotTokenEx(Exception ex) {
    log.error(ex.getMessage());
    return new Result(ResultCode.NOT_TOKEN, false, "", "没有Token,请登录");
  }

  @ExceptionHandler(InvalidTokenException.class)
  public Result handlerInvalidTokenEx(InvalidTokenException ex) {
    log.error(ex.getMessage());
    return new Result(ResultCode.NOT_TOKEN, false, "", "非法Token");
  }

  @ExceptionHandler(UserNotExistException.class)
  public Result handlerUserNotExistEx(Exception ex) {
    return new Result(ResultCode.ERROR, false, ex.getMessage(), null);
  }

  @ExceptionHandler(UserAlreadyExistException.class)
  public Result handlerUserAlreadyExistEx(UserAlreadyExistException ex) {
    log.error(ex.getMessage());
    return new Result(ResultCode.USER_ALREADY_EXIST, false, ex.getMessage(), null);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public Result handlerTokenExpiredEx(TokenExpiredException ex) {
    log.error(ex.getMessage());
    return new Result(ResultCode.NOT_TOKEN, false, "", "TOKEN过期");
  }


  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Result handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    if (ex.getCause() instanceof JsonMappingException) {
      JsonMappingException jex = (JsonMappingException) ex.getCause();
      return Result.getErrorResultInfo(jex.getMessage());
    } else {
      return Result.getErrorResultInfo("必要的请求参数没有传值");
    }
  }


  @ExceptionHandler(IllegalArgumentException.class)
  public Result handlerIllegalArgumentEx(IllegalArgumentException ex) {
    ex.printStackTrace();
    String message = ex.getMessage();
    if (message.contains("token")) {
      log.error(" Token ERROR " + message);
    } else {
      log.error(message);
    }
    return Result.getErrorResultInfo("非法参数异常");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result handlerMethodArgumentNotValidEx(MethodArgumentNotValidException ex) {
    log.error(ex.getMessage());
    return Result.getErrorResultInfo(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public Result handlerRuntimeEx(RuntimeException ex) {
    log.error(ex.getMessage());
    return Result.getErrorResultInfo(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public Result handlerGlobalEx(HttpServletRequest req, Exception ex) {
    log.error(ex.getMessage());
    return Result.getErrorResultInfo(getStatus(req).value(), ex.getMessage());
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.valueOf(statusCode);
  }

}
