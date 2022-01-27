/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Class used to retrieve the application properties.
 */
@Data
@Configuration
public class ApplicationConfig {

	@Value("${asset.name.delimieter}")
	private String assetNameDelimiter;

	@Value("${collibra.domain}")
	private String collibraDomainName;

	@Value("${collibra.community}")
	private String collibraCommunityName;

	@Value("${trigger.scheduler.cron.enabled:false}")
	private Boolean cronIsEnabled;

	@Value("${trigger.scheduler.cron.database:}")
	private String cronDenodoDatabase;

	@Value("${trigger.collibra.workflow.database:}")
	private String collibraWorkflowDenodoDatabase;

	@Value("${denodo.driver-class-name}")
	private String denodoDriverClassName;

	@Value("${denodo.host}")
	private String denodoHost;

	@Value("${denodo.username}")
	private String denodoUsername;

	@Value("${denodo.password}")
	private String denodoPassword;

	@Value("${denodo.url}")
	private String denodoJdbcUrl;

	@Value("${denodo.restful.base-url}")
	private String denodoRestBaseUrl;

	@Value("${denodo.restful.views-path}")
	private String denodoRestViewsPath;

	@Value("${spring.datasource.driver-class-name}")
	private String mockDataDatabaseDriverClassName;

	@Value("${spring.datasource.url}")
	private String mockDataDatabaseUrl;

	@Value("${spring.datasource.database-name}")
	private String mockDataDatabaseName;

	@Value("${spring.datasource.username}")
	private String mockDataDatabaseUsername;

	@Value("${spring.datasource.password}")
	private String mockDataDatabasePassword;

}