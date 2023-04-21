package com.shmet.exception;

/**
 * @author
 */
public class UserAlreadyExistException extends RuntimeException {

  public UserAlreadyExistException() {
  }

  public UserAlreadyExistException(String message) {
    super(message);
  }
}
