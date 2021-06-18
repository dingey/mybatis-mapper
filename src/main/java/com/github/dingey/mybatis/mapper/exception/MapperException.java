package com.github.dingey.mybatis.mapper.exception;

/**
 * @author d
 */
public class MapperException extends RuntimeException {
	private static final long serialVersionUID = -5722497802821376449L;

	public MapperException(String message) {
		super(message);
	}

	public MapperException(String message, Throwable cause) {
		super(message, cause);
	}
}
