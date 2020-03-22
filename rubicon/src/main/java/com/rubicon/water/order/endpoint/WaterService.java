package com.rubicon.water.order.endpoint;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rubicon.water.order.dao.WaterOrderState;
import com.rubicon.water.order.dto.WaterOrder;
import com.rubicon.water.order.dto.WaterStatus;
import com.rubicon.water.order.jobs.WaterOrderJob;

@RestController
@RequestMapping(path = "/waterservice")
public class WaterService {
	
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
		
		//logger.info("Creating an Order");
		System.out.println ("Entered create service " + waterOrder.getDateTime());
		
		String jobKey = waterOrder.getFarmId() + waterOrder.getDateTime().toString();
		JobDataMap map = new JobDataMap();
		map.put("job", waterOrder);
		
		JobDetail detail = JobBuilder.newJob(WaterOrderJob.class).withIdentity(jobKey).usingJobData(map) .build();

		
		waterOrder.setStaus(WaterStatus.REQUESTED.toString());
		WaterOrderState.createWaterOrder(waterOrder);
		Trigger trigger = trigger = TriggerBuilder
                .newTrigger()
                .startAt(Date.from(waterOrder.getDateTime().atZone(ZoneId.systemDefault()).toInstant()))
                .endAt(waterOrder.getOrderFinishTime())
                .build();
		
		schedulerFactoryBean.getScheduler().scheduleJob(detail, trigger);
		//logger.debug("order successfully Created");
		//logger.debug("order status is: " +  waterOrder.getStaus().toString());
		
    	return waterOrder;
    }
    
	 

	@PostMapping(path= "/cancel", produces = "application/xml")
    public List<WaterOrder> cancelOrder(@RequestBody WaterOrder waterOrder) {
		
		 //logger.info("request to cancel an order recieved");
		List<WaterOrder> waterOrderList = WaterOrderState.cancelWaterOrder(waterOrder);
		
		return waterOrderList;
		 
    }


    @PostMapping(path = "/find", produces = "application/xml")
    public List<WaterOrder> getExistingOrders(WaterOrder waterOrder) {
    	
    	//logger.info("request to fetch an order recieved");
    	
    	List<WaterOrder> waterOrderList = WaterOrderState.fetchWaterOrder(waterOrder);
    	
    	//logger.info("order successfully fetched");
    	return waterOrderList;
    }
}