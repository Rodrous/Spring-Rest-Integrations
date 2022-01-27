/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ViewColumnPk implements Serializable {

	private static final long serialVersionUID = 754041426852894291L;

	private String databaseName;
	private String viewName;
	private String columnName;

}