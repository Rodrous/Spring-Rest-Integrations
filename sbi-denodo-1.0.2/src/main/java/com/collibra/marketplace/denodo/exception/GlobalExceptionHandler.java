/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Exceptions thrown during the execution of the application will be caught in
	 * this method.
	 * 
	 * @param throwable, exception thrown.
	 * @return Json response.
	 */
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ObjectNode> handleException(Throwable throwable) {

		ObjectNode objectNode = new ObjectMapper().createObjectNode();
		objectNode.put("message", "Internal error during execution.");
		objectNode.put("exceptionMessage", throwable.getMessage());

		LOGGER.error("Internal error during execution.", throwable);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
				.body(objectNode);
	}

}
