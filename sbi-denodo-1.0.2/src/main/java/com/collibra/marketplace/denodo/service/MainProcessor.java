/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.collibra.marketplace.denodo.component.CollibraAssetTransformer;
import com.collibra.marketplace.denodo.config.ApplicationConfig;
import com.collibra.marketplace.denodo.datasource.DataSourceRouting;
import com.collibra.marketplace.denodo.exception.DenodoException;
import com.collibra.marketplace.denodo.model.ViewColumn;
import com.collibra.marketplace.denodo.model.ViewDependency;
import com.collibra.marketplace.denodo.model.ViewFolder;
import com.collibra.marketplace.denodo.repository.ViewColumnsRepository;
import com.collibra.marketplace.denodo.repository.ViewDependenciesRepository;
import com.collibra.marketplace.denodo.repository.ViewFoldersRepository;
import com.collibra.marketplace.denodo.util.DataSourceIdentifier;
import com.collibra.marketplace.library.integration.CollibraAsset;
import com.collibra.marketplace.library.integration.CollibraComplexRelation;
import com.collibra.marketplace.library.integration.CollibraImportApiHelper;
import com.collibra.marketplace.library.integration.constants.CollibraImportComplexRelationsResponseType;
import com.collibra.marketplace.library.integration.constants.CollibraImportResponseType;
import com.collibra.marketplace.library.integration.model.CollibraDomain;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

@Service
public class MainProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainProcessor.class);

	private final ApplicationConfig appConfig;
	private final CollibraAssetTransformer collibraAssetTransformerService;
	private final CollibraImportApiHelper collibraImportApiHelper;
	private final ViewColumnsRepository viewColumnsRepository;
	private final ViewDependenciesRepository viewDependenciesRepository;
	private final ViewFoldersRepository viewFoldersRepository;

	@Autowired
	public MainProcessor(ApplicationConfig appConfig, CollibraAssetTransformer collibraAssetTransformerService,
			CollibraImportApiHelper collibraImportApiHelper, ViewColumnsRepository viewColumnsRepository,
			ViewDependenciesRepository viewDependenciesRepository, ViewFoldersRepository viewFoldersRepository) {

		this.appConfig = appConfig;
		this.collibraAssetTransformerService = collibraAssetTransformerService;
		this.collibraImportApiHelper = collibraImportApiHelper;
		this.viewColumnsRepository = viewColumnsRepository;
		this.viewDependenciesRepository = viewDependenciesRepository;
		this.viewFoldersRepository = viewFoldersRepository;
	}

	/**
	 * Main method used to synchronize the metadata.
	 * 
	 * @param importId       the ID that is used to identify which import response
	 *                       should be retrieved.
	 * @param denodoDatabase the Denodo database that should be synchronized.
	 * @param useMockData    whether mock data should be used.
	 * @throws Exception
	 */
	public void start(UUID importId, String denodoDatabase, Boolean useMockData) throws Exception {

		LOGGER.info("Started synchronizing the metadata");

		DataSourceRouting.removeDataSource();

		if (Strings.isNullOrEmpty(denodoDatabase)) {
			throw new DenodoException("The name of the Denodo database that should be used is required.");
		}

		List<ViewColumn> denodoViewColumns = null;
		List<ViewFolder> denodoViewFolders = null;
		if (useMockData) {

			denodoViewColumns = viewColumnsRepository.findMockViewColumns("h2MockDatabase");
			denodoViewFolders = viewFoldersRepository.findMockViewFolders(denodoDatabase);

		} else {

			DataSourceRouting.setDataSource(DataSourceIdentifier.DENODO_DATABASE);

			denodoViewColumns = viewColumnsRepository.findViewColumns(denodoDatabase);
			denodoViewFolders = viewFoldersRepository.findViewFolders(denodoDatabase);
		}

		List<String> uniqueViewNames = denodoViewColumns.stream().map(v -> v.getViewName()).distinct()
				.collect(Collectors.toList());

		Map<String, List<ViewDependency>> denodoViewsDependencies = new HashMap<>();
		for (String viewName : uniqueViewNames) {

			if (useMockData == true) {

				denodoViewsDependencies.put(viewName,
						viewDependenciesRepository.findMockViewDependencies(denodoDatabase, viewName));

			} else {

				denodoViewsDependencies.put(viewName,
						viewDependenciesRepository.findViewDependencies(denodoDatabase, viewName));
			}
		}

		Pair<List<CollibraAsset>, List<CollibraComplexRelation>> dataToUpsert = collibraAssetTransformerService
				.transform(denodoDatabase, denodoViewColumns, denodoViewFolders, denodoViewsDependencies);

		collibraImportApiHelper.importAssets(importId, dataToUpsert.getFirst(), CollibraImportResponseType.COUNTS);

		collibraImportApiHelper.importComplexRelations(dataToUpsert.getSecond(),
				CollibraImportComplexRelationsResponseType.COUNT);

		this.updateObsoleteAssetsStatus(importId);

		LOGGER.info("Finished synchronizing the metadata");
	}

	private void updateObsoleteAssetsStatus(UUID importId) {

		LOGGER.debug("Checking and updating the obsolete assets' status");

		List<CollibraDomain> domains = new ArrayList<CollibraDomain>();
		domains.add(new CollibraDomain.Builder().name(this.appConfig.getCollibraDomainName())
				.communityName(this.appConfig.getCollibraCommunityName()).build());

		JsonNode obsoleteAssetsResponse = collibraImportApiHelper.updateObsoleteAssetsStatus(importId, domains,
				CollibraImportResponseType.COUNTS);

		LOGGER.debug("Obsolete assets response: {}", obsoleteAssetsResponse);

	}

}
