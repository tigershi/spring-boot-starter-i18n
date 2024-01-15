/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.i18n.annotation.I18nObj;
import org.springframework.boot.i18n.annotation.I18nVal;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.vmware.l10agent.i18n.TestI18nComponent;

import java.io.IOException;


/**
 * 
 *
 * @author shihu
 *
 */
/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceRequestCron {
	private static Logger logger = LoggerFactory.getLogger(SourceRequestCron.class);
    @I18nVal("${abc.ss.dd:this is i18n value test}")
	private String testVal;
	@I18nObj
	private TestI18nComponent testI18n;

	@Scheduled(fixedDelay = 1000 * 10)
	public void syncToInternali18nManager() {
		logger.info(testVal);


		logger.info(testI18n.getTest_abc_msg());


	}


}
