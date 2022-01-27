/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.collibra.marketplace.denodo.datasource.DataSourceRouting;
import com.collibra.marketplace.denodo.util.DataSourceIdentifier;

/**
 * Configuration class for connecting to Denodo or a mock database using a JDBC
 * connection.
 */
@Configuration
public class DynamicDataSourceConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

	private final ApplicationConfig appConfig;

	@Autowired
	public DynamicDataSourceConfig(ApplicationConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Bean
	@Primary
	public DataSourceRouting createDataSource() {
		DataSourceRouting dynamicDataSource = new DataSourceRouting();
		dynamicDataSource.setDefaultTargetDataSource(this.getMockDataSource());

		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(DataSourceIdentifier.DENODO_DATABASE, this.getDataSource());
		dynamicDataSource.setTargetDataSources(targetDataSources);

		return dynamicDataSource;
	}

	private DataSource getDataSource() {

		LOGGER.info("Initializing the Denodo data source...");

		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(this.appConfig.getDenodoDriverClassName());
		dataSourceBuilder.url(this.appConfig.getDenodoJdbcUrl());
		dataSourceBuilder.username(this.appConfig.getDenodoUsername());
		dataSourceBuilder.password(this.appConfig.getDenodoPassword());
		DataSource dataSource = dataSourceBuilder.build();

		LOGGER.info("Initialized the Denodo data source.");
		return dataSource;
	}

	private DataSource getMockDataSource() {

		LOGGER.info("Initializing the Denodo mock data source...");

		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(this.appConfig.getMockDataDatabaseDriverClassName());
		dataSourceBuilder.url(this.appConfig.getMockDataDatabaseUrl());
		dataSourceBuilder.username(this.appConfig.getMockDataDatabaseUsername());
		dataSourceBuilder.password(this.appConfig.getMockDataDatabasePassword());
		DataSource dataSource = dataSourceBuilder.build();

		LOGGER.info("Initialized the Denodo mock data source.");
		return dataSource;
	}

}
