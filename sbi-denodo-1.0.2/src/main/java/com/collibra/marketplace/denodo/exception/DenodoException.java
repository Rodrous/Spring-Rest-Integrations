/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.exception;

/**
 * Custom exception class.
 */
public class DenodoException extends RuntimeException {

	private static final long serialVersionUID = -655335117940616547L;

	public DenodoException() {

	}

	public DenodoException(String message) {
		super(message);
	}

	public DenodoException(Throwable cause) {
		super(cause);
	}

	public DenodoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DenodoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
