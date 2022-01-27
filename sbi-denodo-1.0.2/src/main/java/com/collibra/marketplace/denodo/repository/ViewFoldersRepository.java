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

import com.collibra.marketplace.denodo.model.ViewFolder;

@Repository
public interface ViewFoldersRepository extends JpaRepository<ViewFolder, Integer> {

	@Query(value = "SELECT * FROM GET_VIEWS (:denodoDatabase, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)", nativeQuery = true)
	List<ViewFolder> findMockViewFolders(@Param("denodoDatabase") String denodoDatabase);

	@Query(value = "SELECT name, folder FROM GET_VIEWS (:denodoDatabase, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)", nativeQuery = true)
	List<ViewFolder> findViewFolders(@Param("denodoDatabase") String denodoDatabase);

}
