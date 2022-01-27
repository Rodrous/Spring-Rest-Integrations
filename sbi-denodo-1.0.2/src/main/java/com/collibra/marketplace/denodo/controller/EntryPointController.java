/*
 * (c) 2021 Collibra Inc. This software is protected under international copyright law.
 * You may only install and use this software subject to the license agreement available at https://marketplace.collibra.com/binary-code-license-agreement/.
 * If such an agreement is not in place, you may not use the software.
 */
package com.collibra.marketplace.denodo.controller;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.collibra.marketplace.denodo.config.ApplicationConfig;
import com.collibra.marketplace.denodo.service.MainProcessor;
import com.collibra.marketplace.library.integration.CollibraImportApiHelper;
import com.collibra.marketplace.library.integration.utils.workflow.CollibraWorkflowCallback;
import com.collibra.marketplace.library.integration.utils.workflow.CollibraWorkflowUtils;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@EnableScheduling
@RestController
@Api(tags = "/sync")
public class EntryPointController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntryPointController.class);

	private final ApplicationConfig appConfig;
	private final CollibraImportApiHelper collibraImportApiHelper;
	private final CollibraWorkflowUtils collibraWorkflowUtils;
	private final MainProcessor mainProcessor;

	@Autowired
	public EntryPointController(ApplicationConfig appConfig, CollibraImportApiHelper collibraImportApiHelper,
			CollibraWorkflowUtils collibraWorkflowUtils, MainProcessor mainProcessor) {

		this.appConfig = appConfig;
		this.collibraImportApiHelper = collibraImportApiHelper;
		this.collibraWorkflowUtils = collibraWorkflowUtils;
		this.mainProcessor = mainProcessor;
	}

	@PostConstruct
	public void initialize() throws Exception {

		// Collibra Workflow
		this.collibraWorkflowUtils.intialize(this.appConfig.getCollibraDomainName(), new CollibraWorkflowCallback() {
			@Override
			public void onWorkflowTriggered() throws Exception {
				LOGGER.info("Synchronization triggered via Collibra Workflow User Task");
				mainProcessor.start(UUID.randomUUID(), appConfig.getCollibraWorkflowDenodoDatabase(), false);
			}
		});
	}

	// HTTP POST Endpoint
	@ApiOperation(value = "Synchronize the metadata")
	@PostMapping("/sync")
	@ResponseBody
	public JsonNode syncTriggeredByApiRequest(
			@RequestHeader(name = "X-Use-Mock-Data", required = false, defaultValue = "false") Boolean useMockData,
			@RequestParam(name = "denodoDatabase", required = true) String denodoDatabase) throws Exception {

		LOGGER.info("Synchronization triggered via API request with use mock data set to '{}'", useMockData);

		UUID importId = UUID.randomUUID();
		this.mainProcessor.start(importId, denodoDatabase, useMockData);

		return collibraImportApiHelper.getImportResponse(importId);
	}

	// CRON Scheduler
	@Scheduled(cron = "${trigger.scheduler.cron.expression}")
	public void syncTriggeredByCronScheduler() throws Exception {

		if (this.appConfig.getCronIsEnabled()) {

			LOGGER.info("Synchronization triggered via CRON Scheduler");
			this.mainProcessor.start(UUID.randomUUID(), this.appConfig.getCronDenodoDatabase(), false);
		}
	}

}
