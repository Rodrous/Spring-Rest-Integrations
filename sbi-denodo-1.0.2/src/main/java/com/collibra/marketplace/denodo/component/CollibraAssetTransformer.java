/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.collibra.marketplace.denodo.config.ApplicationConfig;
import com.collibra.marketplace.denodo.exception.DenodoException;
import com.collibra.marketplace.denodo.model.ViewColumn;
import com.collibra.marketplace.denodo.model.ViewDependency;
import com.collibra.marketplace.denodo.model.ViewFolder;
import com.collibra.marketplace.denodo.util.CustomConstants;
import com.collibra.marketplace.library.integration.CollibraAsset;
import com.collibra.marketplace.library.integration.CollibraComplexRelation;
import com.collibra.marketplace.library.integration.CollibraRelation;
import com.collibra.marketplace.library.integration.CollibraRelation.Direction;
import com.collibra.marketplace.library.integration.constants.CollibraConstants;

/**
 * A class that contains methods that are used to transform the assets and
 * complex relations that are used by this integration.
 */
@Component
public class CollibraAssetTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollibraAssetTransformer.class);

	private final ApplicationConfig appConfig;

	@Autowired
	public CollibraAssetTransformer(ApplicationConfig appConfig) {
		this.appConfig = appConfig;
	}

	/**
	 * Method used to transform all the data that is retrieved from Denodo.
	 * 
	 * @param denodoDatabase          the Denodo database
	 * @param denodoViewColumns       the Denodo view columns result
	 * @param denodoViewFolders       the Denodo view folders result
	 * @param denodoViewsDependencies the Denodo views dependencies
	 * @return the transformed assets and complex relations that should be upserted
	 */
	public Pair<List<CollibraAsset>, List<CollibraComplexRelation>> transform(String denodoDatabase,
			List<ViewColumn> denodoViewColumns, List<ViewFolder> denodoViewFolders,
			Map<String, List<ViewDependency>> denodoViewsDependencies) {

		LOGGER.info("Transforming the assets...");

		List<CollibraAsset> assetsToUpsert = new ArrayList<CollibraAsset>();
		List<CollibraComplexRelation> complexRelationsToUpsert = new ArrayList<CollibraComplexRelation>();

		CollibraAsset databaseAsset = this.transformDatabaseAsset(denodoDatabase);
		LOGGER.debug("Transformed the database asset.");

		List<String> viewsInAFolder = new ArrayList<String>();
		Map<String, CollibraAsset> folderAssetsToUpsert = new HashMap<String, CollibraAsset>();

		for (ViewFolder viewFolder : denodoViewFolders) {

			String viewName = this.transformFolderAsset(folderAssetsToUpsert, denodoDatabase, viewFolder);

			if (viewName != null) {
				viewsInAFolder.add(viewName);
			}
		}
		LOGGER.debug("Transformed the folder assets.");

		Map<String, CollibraAsset> viewAssetsToUpsert = new HashMap<String, CollibraAsset>();
		Map<String, CollibraAsset> columnAssetsToUpsert = new HashMap<String, CollibraAsset>();

		for (ViewColumn viewColumn : denodoViewColumns) {
			this.transformViewAsset(viewAssetsToUpsert, denodoDatabase, viewColumn, viewsInAFolder);
			this.transformColumnAsset(columnAssetsToUpsert, denodoDatabase, viewColumn);
		}
		LOGGER.debug("Transformed the view and column assets.");

		Map<String, CollibraAsset> viewDependencyAssetsToUpsert = new HashMap<String, CollibraAsset>();

		for (String viewName : denodoViewsDependencies.keySet()) {
			this.transformViewDependenciesAssets(viewDependencyAssetsToUpsert, complexRelationsToUpsert, denodoDatabase,
					viewName, denodoViewsDependencies.get(viewName));
		}
		LOGGER.debug("Transformed the view dependency assets.");

		LOGGER.info("Transformed all assets.");

		// Compile list of assets to upsert
		assetsToUpsert.add(databaseAsset);
		assetsToUpsert.addAll(viewAssetsToUpsert.values());
		assetsToUpsert.addAll(columnAssetsToUpsert.values());
		assetsToUpsert.addAll(folderAssetsToUpsert.values());
		assetsToUpsert.addAll(viewDependencyAssetsToUpsert.values());

		return new Pair<List<CollibraAsset>, List<CollibraComplexRelation>>(assetsToUpsert, complexRelationsToUpsert);
	}

	public CollibraAsset transformDatabaseAsset(String denodoDatabase) {

		LOGGER.debug("Transforming the database asset...");

		// @formatter:off
		CollibraAsset databaseAsset = new CollibraAsset.Builder()
				.name(denodoDatabase)
				.displayName(denodoDatabase)
				.type(CollibraConstants.AssetType.DATABASE)
				.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
				.build();
		// @formatter:on

		LOGGER.debug("Transformed the database asset.");

		return databaseAsset;
	}

	public void transformViewAsset(Map<String, CollibraAsset> assetsToUpsert, String denodoDatabase,
			ViewColumn viewColumn, List<String> viewsInAFolder) {

		String viewName = viewColumn.getViewName();
		String viewType = this.getViewType(viewColumn.getViewType());

		LOGGER.debug("Transforming view asset having name '{}'...", viewName);

		String denodoViewsRestPath = String.format("%s/%s/%s/%s", this.appConfig.getDenodoRestBaseUrl(), denodoDatabase,
				this.appConfig.getDenodoRestViewsPath(), viewName);

		// @formatter:off
		CollibraAsset.Builder viewAssetBuilder = new CollibraAsset.Builder()
				.name(denodoDatabase + this.getNameDelimiter() + viewName)
				.displayName(viewName)
				.type(CollibraConstants.AssetType.TABLE)
				.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
				.addAttributeValue(CollibraConstants.AttributeType.TABLE_TYPE, viewType)
				.addAttributeValueDefaultToEmptyString(CollibraConstants.AttributeType.URL, denodoViewsRestPath);
		// @formatter:on

		if (!viewsInAFolder.contains(viewName)) {
			// @formatter:off
			viewAssetBuilder.addRelation(CollibraConstants.RelationType.TABLE_ISPARTOF_DATABASE, 
						Direction.TARGET,
						new CollibraRelation.Builder()
							.relatedAssetName(denodoDatabase)
							.relatedAssetByDomainNameAndCommunityName(this.appConfig.getCollibraDomainName(), 
									this.appConfig.getCollibraCommunityName())
							.build());
			// @formatter:on
		}

		CollibraAsset viewAsset = viewAssetBuilder.build();

		if (!assetsToUpsert.containsKey(viewAsset.getName())) {
			assetsToUpsert.put(viewAsset.getName(), viewAsset);
		}

		LOGGER.debug("Transformed view asset.");
	}

	public void transformViewDependenciesAssets(Map<String, CollibraAsset> assetsToUpsert,
			List<CollibraComplexRelation> complexRelationsToUpsert, String denodoDatabase, String viewName,
			List<ViewDependency> currentViewDependencies) {

		LOGGER.debug("Transforming dependency assets and complex relations for view having name '{}'...", viewName);

		if (currentViewDependencies.size() == 0) {
			return;
		}

		this.transformPrivateViewDependencyAssets(assetsToUpsert, currentViewDependencies, denodoDatabase);

		this.transformViewDependencyComplexRelation(complexRelationsToUpsert, currentViewDependencies, denodoDatabase);

		LOGGER.debug("Transformed dependency assets and complex relations.");
	}

	private void transformPrivateViewDependencyAssets(Map<String, CollibraAsset> assetsToUpsert,
			List<ViewDependency> currentViewDependencies, String denodoDatabase) {

		LOGGER.debug("Transforming private view dependency assets...");

		List<ViewDependency> privateViews = currentViewDependencies.stream()
				.filter(v -> v.getDependencyName().startsWith("_")).collect(Collectors.toList());

		for (ViewDependency privateView : privateViews) {

			String privateViewName = privateView.getDependencyName();

			// @formatter:off
			CollibraAsset privateViewAsset = new CollibraAsset.Builder()
					.name(denodoDatabase + this.getNameDelimiter() + privateViewName)
					.displayName(privateViewName)
					.type(CollibraConstants.AssetType.DATABASE_VIEW)
					.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
					.addAttributeValue(CollibraConstants.AttributeType.TABLE_TYPE, "Private View")
					.build();
			// @formatter:on

			if (!assetsToUpsert.containsKey(privateViewAsset.getName())) {
				assetsToUpsert.put(privateViewAsset.getName(), privateViewAsset);
			}
		}

		LOGGER.debug("Transformed private view dependency assets.");
	}

	private void transformViewDependencyComplexRelation(List<CollibraComplexRelation> complexRelationsToUpsert,
			List<ViewDependency> currentViewDependencies, String denodoDatabase) {

		LOGGER.debug("Transforming view dependencies' complex relation...");

		Map<Object, List<ViewDependency>> viewsGroupedByViewName = currentViewDependencies.stream()
				.collect(Collectors.groupingBy(v -> v.getViewName()));

		for (Object viewName : viewsGroupedByViewName.keySet()) {

			List<ViewDependency> currentDependencies = viewsGroupedByViewName.get(viewName);

			String dependencyType = currentDependencies.stream().map(v -> v.getViewType()).distinct().findFirst()
					.orElseThrow(() -> new DenodoException("The view dependencies, view type is empty."));

			// @formatter:off
			CollibraComplexRelation.Builder complexRelationBuilder = new CollibraComplexRelation.Builder()
					.type(CustomConstants.ComplexRelationType.DENODO_VIEW_RELATION)
					.addRelationByDomainNameAndCommunityName(
							CustomConstants.RelationType.DENODOVIEW_COMPLEXRELATION_TARGET.getId(),
							denodoDatabase + this.getNameDelimiter() + viewName, 
							this.appConfig.getCollibraDomainName(), 
							this.appConfig.getCollibraCommunityName())
					.addAttributeValue(CustomConstants.AttributeType.DENODOVIEW_COMPLEXRELATION_DEPENDENCYTYPE, dependencyType);
			// @formatter:on

			List<String> viewCRSources = currentDependencies.stream().map(v -> v.getDependencyName())
					.collect(Collectors.toList());

			for (String viewCRSource : viewCRSources) {

				// @formatter:off
				complexRelationBuilder.addRelationByDomainNameAndCommunityName(
						CustomConstants.RelationType.DENODOVIEW_COMPLEXRELATION_SOURCE.getId(),
						denodoDatabase + this.getNameDelimiter() + viewCRSource, 
						this.appConfig.getCollibraDomainName(), 
						this.appConfig.getCollibraCommunityName());
				// @formatter:on
			}

			complexRelationsToUpsert.add(complexRelationBuilder.build());
		}

		LOGGER.debug("Transformed view dependencies' complex relation.");
	}

	public void transformColumnAsset(Map<String, CollibraAsset> assetsToUpsert, String denodoDatabase,
			ViewColumn viewColumn) {

		String viewName = viewColumn.getViewName();
		String columnName = viewColumn.getColumnName();

		LOGGER.debug("Transforming column asset having name '{}'...", columnName);

		// @formatter:off
		CollibraAsset columnAsset = new CollibraAsset.Builder()
				.name(denodoDatabase + this.getNameDelimiter() + viewName + this.getNameDelimiter() + columnName)
				.displayName(columnName)
				.type(CollibraConstants.AssetType.COLUMN)
				.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
				.addAttributeValue(CollibraConstants.AttributeType.TECHNICAL_DATA_TYPE, viewColumn.getColumnTypeName())
				.addAttributeValue(CollibraConstants.AttributeType.DATA_TYPE_PRECISION, viewColumn.getColumnTypePrecision())
				.addAttributeValue(CollibraConstants.AttributeType.SIZE, viewColumn.getColumnTypeLength())
				.addAttributeValue(CollibraConstants.AttributeType.DESCRIPTION_FROM_SOURCE_SYSTEM, viewColumn.getColumnDescription())
				.addRelation(CollibraConstants.RelationType.COLUMN_ISPARTOF_TABLE, 
								Direction.TARGET,
								new CollibraRelation.Builder()
									.relatedAssetName(denodoDatabase + this.getNameDelimiter() + viewName)
									.relatedAssetByDomainNameAndCommunityName(this.appConfig.getCollibraDomainName(), 
											this.appConfig.getCollibraCommunityName())
									.build())
				.build();
		// @formatter:on

		if (!assetsToUpsert.containsKey(columnAsset.getName())) {
			assetsToUpsert.put(columnAsset.getName(), columnAsset);
		}

		LOGGER.debug("Transformed column asset.");
	}

	public String transformFolderAsset(Map<String, CollibraAsset> assetsToUpsert, String denodoDatabase,
			ViewFolder viewFolder) {

		String viewName = viewFolder.getViewName();
		String folderPath = viewFolder.getFolder();

		LOGGER.debug("Transforming folder assets having name '{}'...", folderPath);

		// If the view is not in any folder, the value is /
		if (folderPath.equals("/")) {
			return null;
		}

		String[] splitFolderName = StringUtils.strip(folderPath, "/").split("/");

		String parentFoldersAssetName = denodoDatabase;
		for (String currentFolderName : splitFolderName) {

			CollibraAsset.Builder folderAssetBuilder = null;
			if (parentFoldersAssetName.equals(denodoDatabase)) {
				folderAssetBuilder = this.addRootFolderAsset(denodoDatabase, currentFolderName);
			} else {
				folderAssetBuilder = this.addSubFolderAsset(denodoDatabase, parentFoldersAssetName, currentFolderName);
			}

			CollibraAsset folderAsset = null;
			if (folderPath.endsWith(currentFolderName)) {
				folderAsset = this.addFolderViewRelation(folderAssetBuilder, denodoDatabase, viewName);
			} else {
				folderAsset = folderAssetBuilder.build();
			}

			if (!assetsToUpsert.containsKey(folderAsset.getName())) {
				assetsToUpsert.put(folderAsset.getName(), folderAsset);

			} else if (folderPath.endsWith(currentFolderName)) {
				CollibraAsset.Builder existingFolderAssetBuilder = assetsToUpsert.get(folderAsset.getName())
						.getBuilder();
				CollibraAsset existingFolderAsset = this.addFolderViewRelation(existingFolderAssetBuilder,
						denodoDatabase, viewName);
				assetsToUpsert.put(existingFolderAsset.getName(), existingFolderAsset);
			}

			parentFoldersAssetName += this.getNameDelimiter() + currentFolderName;
		}

		LOGGER.debug("Transformed folder assets.");

		return viewName;
	}

	private CollibraAsset.Builder addRootFolderAsset(String denodoDatabase, String folderName) {

		LOGGER.debug("Transforming root folder asset having name '{}'...", folderName);

		// @formatter:off
		CollibraAsset.Builder folderAssetBuilder = new CollibraAsset.Builder()
				.name(denodoDatabase + this.getNameDelimiter() + folderName)
				.displayName(folderName)
				.type(CollibraConstants.AssetType.BI_FOLDER)
				.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
				.addRelation(CustomConstants.RelationType.DATABASE_CONTAINS_BIFOLDER, 
						Direction.SOURCE,
						new CollibraRelation.Builder()
							.relatedAssetName(denodoDatabase)
							.relatedAssetByDomainNameAndCommunityName(this.appConfig.getCollibraDomainName(), 
									this.appConfig.getCollibraCommunityName())
							.build());
		// @formatter:on

		LOGGER.debug("Transformed root folder asset.");

		return folderAssetBuilder;
	}

	private CollibraAsset.Builder addSubFolderAsset(String denodoDatabase, String parentFoldersAssetName,
			String folderName) {

		LOGGER.debug("Transforming sub-folder asset having name '{}'...", folderName);

		// @formatter:off
		CollibraAsset.Builder folderAssetBuilder = new CollibraAsset.Builder()
				.name(parentFoldersAssetName + this.getNameDelimiter() + folderName)
				.displayName(folderName)
				.type(CollibraConstants.AssetType.BI_FOLDER)
				.domainNameAndCommunityName(this.appConfig.getCollibraDomainName(), this.appConfig.getCollibraCommunityName())
				.addRelation(CollibraConstants.RelationType.BIFOLDER_ASSEMBLES_BIFOLDER, 
						Direction.TARGET,
						new CollibraRelation.Builder()
							.relatedAssetName(parentFoldersAssetName)
							.relatedAssetByDomainNameAndCommunityName(this.appConfig.getCollibraDomainName(), 
									this.appConfig.getCollibraCommunityName())
							.build());
		// @formatter:on

		LOGGER.debug("Transformed sub-folder asset.");

		return folderAssetBuilder;
	}

	private CollibraAsset addFolderViewRelation(CollibraAsset.Builder folderAssetBuilder, String denodoDatabase,
			String viewName) {

		LOGGER.debug("Adding folder-view relation for view having name '{}'...", viewName);

		// @formatter:off
		CollibraAsset folderAsset = folderAssetBuilder
				.addRelation(CollibraConstants.RelationType.BIFOLDER_CONTAINS_DATAASSET, 
						Direction.TARGET,
						new CollibraRelation.Builder()
							.relatedAssetName(denodoDatabase + this.getNameDelimiter() + viewName)
							.relatedAssetByDomainNameAndCommunityName(this.appConfig.getCollibraDomainName(), 
									this.appConfig.getCollibraCommunityName())
							.build())
				.build();
		// @formatter:on

		LOGGER.debug("Added folder-view relation.");

		return folderAsset;
	}

	private String getViewType(Integer viewTypeId) {
		switch (viewTypeId) {
		case 0:
			return "Base View";
		case 1:
			return "Derived View";
		case 2:
			return "Interface View";
		case 3:
			return "Materialized View";
		default:
			return "";
		}
	}

	private String getNameDelimiter() {
		return " " + this.appConfig.getAssetNameDelimiter() + " ";
	}

}
