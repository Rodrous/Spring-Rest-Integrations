/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.mock;

import java.sql.ResultSet;
import java.sql.Types;

import org.h2.tools.SimpleResultSet;

public class DenodoMockData {

	public static ResultSet getCatalogVdpMetadataViews(String input_database_name, String input_view_name)
			throws Exception {

		SimpleResultSet simpleResultSet = new SimpleResultSet();

		simpleResultSet.addColumn("database_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("view_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("view_type", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_type_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_type_precision", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_type_length", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_type_scale", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("column_description", Types.VARCHAR, 0, 0);

		// @formatter:off
		simpleResultSet.addRow(input_database_name, "Phone_Incident", "1", "Id", "BIGINT", "19", "20", "0", "");
		simpleResultSet.addRow(input_database_name, "Phone_Incident", "1", "Summary", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Phone_Incident", "1", "Reported_Date", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Phone_Incident", "1", "Severity", "VARCHAR", "65536", "65536", "0", "");

		simpleResultSet.addRow(input_database_name, "Internet_Incident", "1", "Id", "BIGINT", "19", "20", "0", "");
		simpleResultSet.addRow(input_database_name, "Internet_Incident", "1", "Summary", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Internet_Incident", "1", "Reported_Date", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Internet_Incident", "1", "Severity", "VARCHAR", "65536", "65536", "0", "");
		
		simpleResultSet.addRow(input_database_name, "Incidents", "1", "Id", "BIGINT", "19", "20", "0", "");
		simpleResultSet.addRow(input_database_name, "Incidents", "1", "Summary", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Incidents", "1", "Reported_Date", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Incidents", "1", "Severity", "VARCHAR", "65536", "65536", "0", "");
		simpleResultSet.addRow(input_database_name, "Incidents", "1", "Type", "VARCHAR", "65536", "65536", "0", "");
		// @formatter:on

		return simpleResultSet;
	}

	public static ResultSet getViewDependencies(String input_database_name, String input_view_name) throws Exception {

		SimpleResultSet simpleResultSet = new SimpleResultSet();

		simpleResultSet.addColumn("view_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("private_view", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("dependency_name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("view_type", Types.VARCHAR, 0, 0);

		if (input_view_name != null && input_view_name.equals("Incidents")) {
			simpleResultSet.addRow("Incidents", "false", "Phone_Incident", "Union");
			simpleResultSet.addRow("Incidents", "false", "Internet_Incident", "Union");

		} else if (input_view_name != null && input_view_name.equals("Phone_Incident")) {

			simpleResultSet.addRow("Phone_Incident", "false", "_Phone_Incident_Private_View", "Union");
		}

		return simpleResultSet;
	}

	public static ResultSet getViews(String input_database_name, String input_name, String input_user_creator,
			String input_last_user_modifier, String input_init_create_date, String input_end_create_date,
			String input_init_last_modification_date, String input_end_last_modification_date, String input_view_type,
			String input_swap_active, String input_cache_status, String input_description) throws Exception {

		SimpleResultSet simpleResultSet = new SimpleResultSet();

		simpleResultSet.addColumn("name", Types.VARCHAR, 0, 0);
		simpleResultSet.addColumn("folder", Types.VARCHAR, 0, 0);

		simpleResultSet.addRow("Phone_Incident", "/derived-views");
		simpleResultSet.addRow("Internet_Incident", "/derived-views/sub-folder");
		simpleResultSet.addRow("Incidents", "/business-views");

		return simpleResultSet;
	}

}
