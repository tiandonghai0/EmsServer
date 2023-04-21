package com.shmet.exception;

public class EmsBisException extends EmsException {

    private static final long serialVersionUID = -8772263119076199648L;

    public EmsBisException() {
        super();
    }

    public EmsBisException(String message) {
        super(message);
    }

    public EmsBisException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmsBisException(String message, String messageCode, int messageCodeToClient) {
        super(message, messageCode, messageCodeToClient);
    }

    public EmsBisException(String message, String messageCode, int messageCodeToClient, Throwable cause) {
        super(message, messageCode, messageCodeToClient, cause);
    }

    public EmsBisException(Throwable cause) {
        super(cause);
    }

    protected EmsBisException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
