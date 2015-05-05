package com.acl.paychamp.util;

public class DataValueException extends Exception {

	private static final long serialVersionUID = 1L;

	private String field;
	private String errorMessage;

	public DataValueException(String field, String message) {
		this.field = field;
		this.errorMessage = message;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
