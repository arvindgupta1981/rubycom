package com.rubicon.water.order.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rubicon.water.order.dto.WaterOrder;

public class WaterOrderJob implements Job {

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		System.out.println("JOBBBBBBBBBBBBSSS");
		System.out.println(ctx.getJobDetail().getJobDataMap().get("job"));
		WaterOrder order = (WaterOrder) ctx.getJobDetail().getJobDataMap().get("job");
		System.out.println("Job Farm Id:: " + order.getFarmId());
	}

}
