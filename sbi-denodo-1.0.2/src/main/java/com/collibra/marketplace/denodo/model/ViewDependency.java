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
@IdClass(ViewDependencyPk.class)
@Entity
@Table(name = "VIEW_DEPENDENCIES")
public class ViewDependency {

	@Id
	@Column(name = "view_name")
	private String viewName;

	@Id
	@Column(name = "dependency_name")
	private String dependencyName;

	@Column(name = "view_type")
	private String viewType;

	@Column(name = "private_view")
	private String privateView;

}
