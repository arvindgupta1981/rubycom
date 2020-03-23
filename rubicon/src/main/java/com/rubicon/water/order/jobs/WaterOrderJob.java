package com.rubicon.water.order.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rubicon.water.order.dto.WaterOrder;
import com.rubicon.water.order.dto.WaterStatus;
import com.rubicon.water.order.endpoint.WaterService;

public class WaterOrderJob implements Job {
	public static final Logger logger = LoggerFactory.getLogger(WaterOrderJob.class.getName());
	private static long hourToMilli = 3600000l;
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {	
		WaterOrder order = (WaterOrder) ctx.getJobDetail().getJobDataMap().get("job");	
		order.setStaus(WaterStatus.IN_PROGRESS.toString());
		logger.info("Job Farm Id:: " + order.getFarmId() + " Status is :: " + order.getStaus());
		try {
			Thread.sleep(hourToMilli * order.getDuration());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		order.setStaus(WaterStatus.DELIEVERED.toString());
		
		logger.info("Job Farm Id:: " + order.getFarmId() + " Status is :: " + order.getStaus());
	}

}
