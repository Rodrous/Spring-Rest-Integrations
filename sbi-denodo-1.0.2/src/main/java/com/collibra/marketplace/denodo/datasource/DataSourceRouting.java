/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.collibra.marketplace.denodo.util.DataSourceIdentifier;

public class DataSourceRouting extends AbstractRoutingDataSource {

	private static ThreadLocal<DataSourceIdentifier> threadLocal = new ThreadLocal<>();

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceRouting.getDataSource();
	}

	public static DataSourceIdentifier getDataSource() {
		return threadLocal.get();
	}

	public static void setDataSource(DataSourceIdentifier dataSourceName) {
		threadLocal.set(dataSourceName);
	}

	public static void removeDataSource() {
		threadLocal.remove();
	}

}
