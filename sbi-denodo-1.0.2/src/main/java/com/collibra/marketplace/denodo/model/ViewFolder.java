/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@IdClass(ViewFolderPk.class)
@Entity
@Table(name = "GET_VIEWS")
public class ViewFolder {

	@Id
	@Column(name = "name")
	private String viewName;

	@Id
	@Column(name = "folder")
	private String folder;

}
