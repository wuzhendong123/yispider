package org.yi.spider.exception;

public class BaseException extends RuntimeException{

	/**
     * 序列化ID
     */
    private static final long serialVersionUID = -1316746661346991484L;

    /**
     * 构造函数 {@code CmdParamException}
     */
    public BaseException() {
        super();
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param message	错误信息
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param cause  需要抛出的异常
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数 {@code CmdParamException}
     *
     * @param message  错误信息
     * @param cause    需要抛出的异常
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
