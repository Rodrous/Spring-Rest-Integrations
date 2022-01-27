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
@IdClass(ViewColumnPk.class)
@Entity
@Table(name = "CATALOG_VDP_METADATA_VIEWS")
public class ViewColumn {

	@Id
	@Column(name = "database_name")
	private String databaseName;

	@Id
	@Column(name = "view_name")
	private String viewName;

	@Id
	@Column(name = "column_name")
	private String columnName;

	@Column(name = "view_type")
	private Integer viewType;

	@Column(name = "column_type_name")
	private String columnTypeName;

	@Column(name = "column_type_precision")
	private String columnTypePrecision;

	@Column(name = "column_type_length")
	private String columnTypeLength;

	@Column(name = "column_type_scale")
	private String columnTypeScale;

	@Column(name = "column_description")
	private String columnDescription;

}
