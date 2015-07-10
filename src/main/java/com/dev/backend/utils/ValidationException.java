package com.dev.backend.utils;

public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {
		
	}
	
	public ValidationException(String msg, Throwable t) {
		 super(msg, t);
	}
	
	public ValidationException(Throwable t) {
		 super(t);
	}
	
	public ValidationException(String msg) {
		 super(msg);
	}
	
}
