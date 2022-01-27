/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.collibra.marketplace.denodo.model.ViewColumn;

@Repository
public interface ViewColumnsRepository extends JpaRepository<ViewColumn, Integer> {

	@Query(value = "SELECT * FROM CATALOG_VDP_METADATA_VIEWS (:denodoDatabase, NULL);", nativeQuery = true)
	List<ViewColumn> findMockViewColumns(@Param("denodoDatabase") String denodoDatabase);

	@Query(value = "SELECT database_name, view_name, view_type, column_name, column_type_name, column_type_precision, "
			+ "column_type_length, column_type_scale, column_description FROM CATALOG_VDP_METADATA_VIEWS (:denodoDatabase, NULL);", nativeQuery = true)
	List<ViewColumn> findViewColumns(@Param("denodoDatabase") String denodoDatabase);

}
