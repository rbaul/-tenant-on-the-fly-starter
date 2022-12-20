package com.github.rbaul.tenantonthefly.exceptions;

public class TenantOnTheFlyException extends RuntimeException {
	public TenantOnTheFlyException() {
		super();
	}
	
	public TenantOnTheFlyException(String message) {
		super(message);
	}
	
	public TenantOnTheFlyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TenantOnTheFlyException(Throwable cause) {
		super(cause);
	}
	
	protected TenantOnTheFlyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
