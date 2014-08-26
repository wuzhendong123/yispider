package org.yi.spider.exception;

public class CmdParamException extends RuntimeException{

	/**
     * 序列化ID
     */
    private static final long serialVersionUID = -1316746661346991484L;

    /**
     * 构造函数 {@code CmdParamException}
     */
    public CmdParamException() {
        super();
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param message	错误信息
     */
    public CmdParamException(String message) {
        super(message);
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param cause  需要抛出的异常
     */
    public CmdParamException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param message  错误信息
     * @param cause    需要抛出的异常
     */
    public CmdParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
