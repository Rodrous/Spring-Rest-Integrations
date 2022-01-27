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

import com.collibra.marketplace.denodo.model.ViewDependency;

@Repository
public interface ViewDependenciesRepository extends JpaRepository<ViewDependency, Integer> {

	@Query(value = "SELECT * FROM VIEW_DEPENDENCIES (:denodoDatabase, :viewName);", nativeQuery = true)
	List<ViewDependency> findMockViewDependencies(@Param("denodoDatabase") String denodoDatabase,
			@Param("viewName") String viewName);

	@Query(value = "SELECT distinct view_name, private_view, dependency_name, view_type "
			+ "FROM VIEW_DEPENDENCIES (:denodoDatabase, :viewName)"
			+ "WHERE depth = 1 AND dependency_name IS NOT NULL AND dependency_database_name = :denodoDatabase "
			+ "AND dependency_type NOT LIKE '%Storedprocedure%' AND dependency_type NOT LIKE '%Datasource%'", nativeQuery = true)
	List<ViewDependency> findViewDependencies(@Param("denodoDatabase") String denodoDatabase,
			@Param("viewName") String viewName);

}
