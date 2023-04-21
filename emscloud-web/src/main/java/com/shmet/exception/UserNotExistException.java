package com.shmet.exception;

/**
 * @author
 */
public class UserNotExistException extends RuntimeException {
  public UserNotExistException(String message) {
    super(message);
  }
}
