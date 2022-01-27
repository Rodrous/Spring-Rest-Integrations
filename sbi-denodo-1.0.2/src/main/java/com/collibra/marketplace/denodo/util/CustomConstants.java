/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.util;

import com.collibra.marketplace.library.integration.interfaces.CollibraAssetTypeInterface;
import com.collibra.marketplace.library.integration.interfaces.CollibraAttributeTypeInterface;
import com.collibra.marketplace.library.integration.interfaces.CollibraComplexRelationTypeInterface;
import com.collibra.marketplace.library.integration.interfaces.CollibraRelationTypeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum CustomConstants {

	;

	@AllArgsConstructor
	public enum AssetType implements CollibraAssetTypeInterface {

		;

		@Getter
		private final String name;

		@Getter
		private final String id;
	}

	@AllArgsConstructor
	public enum AttributeType implements CollibraAttributeTypeInterface {

		DENODOVIEW_COMPLEXRELATION_DEPENDENCYTYPE("Dependency Type", ""); // TODO: Set the Collibra Platform ID of the
																			// custom attribute type.

		@Getter
		private final String name;

		@Getter
		private final String id;
	}

	@AllArgsConstructor
	public enum RelationType implements CollibraRelationTypeInterface {

		DATABASE_CONTAINS_BIFOLDER(""), // TODO: Set the Collibra Platform ID of the custom relation type.
		DENODOVIEW_COMPLEXRELATION_SOURCE(""), // TODO: Set the Collibra Platform ID of the custom complex relation,
												// source relation.
		DENODOVIEW_COMPLEXRELATION_TARGET(""); // TODO: Set the Collibra Platform ID of the custom complex relation,
												// target relation.

		@Getter
		private final String id;
	}

	@AllArgsConstructor
	public enum ComplexRelationType implements CollibraComplexRelationTypeInterface {

		DENODO_VIEW_RELATION("Denodo View Relation", ""); // TODO: Set the Collibra Platform ID of the custom complex
															// relation type.

		@Getter
		private final String name;

		@Getter
		private final String id;
	}
}
