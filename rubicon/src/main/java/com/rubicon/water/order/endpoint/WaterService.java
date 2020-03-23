package com.rubicon.water.order.endpoint;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rubicon.water.order.dao.WaterOrderState;
import com.rubicon.water.order.dto.Orders;
import com.rubicon.water.order.dto.WaterOrder;
import com.rubicon.water.order.dto.WaterStatus;
import com.rubicon.water.order.jobs.WaterOrderJob;

@RestController
@RequestMapping(path = "/waterservice")
public class WaterService {
	public static final Logger logger = LoggerFactory.getLogger(WaterService.class.getName());

	//public static final Logger logger = Logger.getLogger(WaterService.class);
	@Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
	
	@GetMapping(path="/test", produces = "text/plain")
    public String hello() {
		schedulerFactoryBean.getScheduler();
        return "Water service is up.";
    }	
	
	@PostMapping(path= "/create", produces = "application/xml")
    public WaterOrder createOrder(@RequestBody WaterOrder waterOrder) throws SchedulerException {
		
		logger.info("Creating an Order");		
		waterOrder.setStaus(WaterStatus.REQUESTED.toString());
		Boolean orderCreated = WaterOrderState.createWaterOrder(waterOrder);
		if (orderCreated) {
			String jobKeyName = getJobKeyName(waterOrder);
			JobDataMap map = new JobDataMap();
			map.put("job", waterOrder);			
			JobDetail detail = JobBuilder.newJob(WaterOrderJob.class).withIdentity(jobKeyName, "rubicon").usingJobData(map) .build();
			logger.info("group ::" + detail.getKey().getGroup());
			logger.info("name: " + detail.getKey().getName());
			Trigger trigger = TriggerBuilder
	                .newTrigger()
	                .startAt(Date.from(waterOrder.getDateTime().atZone(ZoneId.systemDefault()).toInstant()))
	                .endAt(waterOrder.getOrderFinishTime())
	                .build();
			schedulerFactoryBean.getScheduler().scheduleJob(detail, trigger);
			JobKey jobKey = new JobKey(jobKeyName, "rubicon");
			logger.debug("order successfully Created with staus : " + waterOrder.getStaus().toString());

		} else {
			throw new UnsupportedOperationException("Order not created as the orders overlap");
		}
		
		
		logger.debug("order successfully Created");
		logger.debug("order status is: " +  waterOrder.getStaus().toString());
		
    	return waterOrder;
    }

	@PostMapping(path= "/cancel")
    public String cancelOrder(@RequestBody WaterOrder waterOrder) throws SchedulerException {		
		logger.info("request to cancel an order recieved");
		List<WaterOrder> waterOrderList = WaterOrderState.cancelWaterOrder(waterOrder);
		String jobKeyName = getJobKeyName(waterOrder);
		JobKey jobKey = new JobKey(jobKeyName, "rubicon");
		if(waterOrderList.isEmpty() || !schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
			logger.debug("order can not be cancelled");
			throw new UnsupportedOperationException("Order can not be cancelled either it does not exist or in delievered status");
		} else {			
			schedulerFactoryBean.getScheduler().deleteJob(jobKey);
			logger.debug("order successfully cancelled and staus changed to cancelled");
		}
		return "Order Cancelled";		 
    }


    @PostMapping(path = "/find", produces = "application/xml")
    public Orders getExistingOrders(@RequestBody WaterOrder waterOrder) {    	
    	logger.info("request to fetch an order recieved");    	
    	List<WaterOrder> waterOrderList = WaterOrderState.fetchWaterOrder(waterOrder);    	
    	logger.info("order successfully fetched");
    	Orders orders = new Orders();
    	orders.setWaterOrders(waterOrderList);
    	return orders;
    }
    

	private String getJobKeyName(WaterOrder waterOrder) {
		String jobKeyName = waterOrder.getFarmId() + waterOrder.getDateTime().toString();
		return jobKeyName;
	}
}