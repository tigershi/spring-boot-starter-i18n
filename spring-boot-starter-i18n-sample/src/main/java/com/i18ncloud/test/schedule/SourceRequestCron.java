package com.i18ncloud.test.schedule;

import com.i18ncloud.test.i18n.TestI18nComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.i18n.annotation.I18nObj;
import org.springframework.boot.i18n.annotation.I18nVal;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


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
    @I18nVal( key = "abc.ss.dd", component = "test")
	private String testVal = "this is i18n value test";
	@I18nObj
	private TestI18nComponent testI18n;

	@Scheduled(fixedDelay = 1000 * 10)
	public void syncToInternali18nManager() {
		logger.info(testVal);

		logger.info(testI18n.getTest_abc_msg());
		logger.info(testI18n.getTest_bcd_msg());
		logger.info(testI18n.getTest_cde_msg());
		logger.info(testI18n.getTest_def_msg());


	}


}
