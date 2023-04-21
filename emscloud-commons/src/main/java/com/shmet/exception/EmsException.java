package com.shmet.exception;

public class EmsException extends Exception {
    
    private static final long serialVersionUID = -8772263119076199648L;

    private String messageCode;

    private int messageCodeToClient;

    public EmsException() {
        super();
    }

    public EmsException(String message) {
        super(message);
    }

    public EmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmsException(Throwable cause) {
        super(cause);
    }

    protected EmsException(String message, Throwable cause, boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EmsException(String message, String messageCode, int messageCodeToClient) {
        super(message);
        this.messageCode = messageCode;
        this.messageCodeToClient = messageCodeToClient;
    }

    public EmsException(String message, String messageCode, int messageCodeToClient, Throwable cause) {
        super(message, cause);
        this.messageCode = messageCode;
        this.messageCodeToClient = messageCodeToClient;
    }

    /**
     * @return the messageCode
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * @param messageCode the messageCode to set
     */
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    /**
     * @return the messageCodeToClient
     */
    public int getMessageCodeToClient() {
        return messageCodeToClient;
    }

    /**
     * @param messageCodeToClient the messageCodeToClient to set
     */
    public void setMessageCodeToClient(int messageCodeToClient) {
        this.messageCodeToClient = messageCodeToClient;
    }


}
