package com.rubicon.water.order.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rubicon.water.order.dto.WaterOrder;
import com.rubicon.water.order.dto.WaterStatus;

/* @
 * This class is maintaining the state of orders in Map. When an order arrives, it is saved in map.
 * When service needs to fetch an order, it is fetched from map.
 * When services wants to cancel an order, it fetches that order from map and changes the status of the order to cancel. 
 */
public class WaterOrderState {
	
	public static final Logger logger = LoggerFactory.getLogger(WaterOrderState.class.getName());

	public static Map<String, List<WaterOrder>> waterOrderMap;

	static {
		waterOrderMap = new ConcurrentHashMap<>();
	}

	public static Map<String, List<WaterOrder>> getWaterOrderMap() {
		return waterOrderMap;
	}

	public static boolean createWaterOrder(final WaterOrder waterOrder) {

		boolean orderCreated = false;
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))) {
			List<WaterOrder> waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));
			boolean orderOverlap = checkIfOrdersOverlap(waterOrderList, waterOrder);
			if (!orderOverlap) {
				waterOrder.setStaus(WaterStatus.REQUESTED.toString());
				waterOrderList.add(waterOrder);
				orderCreated = true;
				logger.info("Order created and saved in map.");
			}
		} else {
			waterOrder.setStaus(WaterStatus.REQUESTED.toString());
			List<WaterOrder> waterOrderList = new ArrayList<>();
			waterOrderList.add(waterOrder);
			waterOrderMap.put(String.valueOf(waterOrder.getFarmId()), waterOrderList);
			orderCreated = true;
			logger.info("Order created and saved in map.");
		}
		
		return orderCreated;
	}

	/* Checks if the received order does not overlap with existing order 
	i.e. order does not start when another order is in progress at the same time.
	It only checks for those orders which are not cancelled.*/
	private static boolean checkIfOrdersOverlap(final List<WaterOrder> waterOrderList, final WaterOrder newWaterOrder) {
		
		LocalDateTime orderDateTime = newWaterOrder.getDateTime();
		boolean orderOverlap = false;
		for (WaterOrder wo : waterOrderList) {
			logger.info("datetime======."+ wo.getDateTime());
			if(!wo.getStaus().equals(WaterStatus.CANCELLED.toString())) {
				LocalDateTime finishTime = wo.getOrderFinishTime(wo.getDateTime(), wo.getDuration());
				if ((orderDateTime.isAfter(wo.getDateTime()) && orderDateTime.isBefore(finishTime))
						|| orderDateTime.equals(wo.getDateTime())) {
					logger.info("Orders overlap for the same farm and will not be created.");
					return true;
				} 
			}
		}
		logger.info("Order do not overlap and will be created.");
		return orderOverlap;
	}

	public static List<WaterOrder> fetchWaterOrder(final WaterOrder waterOrder) {
		List<WaterOrder> waterOrderList = new ArrayList<>();
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))) {
			waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));
			logger.info("Orders successfully fetched from map.");
			return waterOrderList;
		} else {
			logger.info("Order with the farm id "+  waterOrder.getFarmId() + "does not exist. ");
			return waterOrderList;
		}

	}

	/*Cancel those orders which are not in delievered status. 
	 */
	public static List<WaterOrder> cancelWaterOrder(final WaterOrder waterOrder) {

		List<WaterOrder> cancelledWaterOrderList = new ArrayList<>();
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))) {

			List<WaterOrder> waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));

			cancelledWaterOrderList = waterOrderList.stream()
					.filter(wo -> !(wo.getStaus().equals(WaterStatus.DELIEVERED.toString())))
					.collect(Collectors.toList());

			cancelledWaterOrderList.forEach(wo -> wo.setStaus(WaterStatus.CANCELLED.toString()));

			logger.info("Number of cancelled orders" + cancelledWaterOrderList.size());
		} else {
			logger.info("Order with the farm id "+  waterOrder.getFarmId() + "does not exist. ");
		}

		return cancelledWaterOrderList;
	}

	public static void getOrdersToStartProcessing() {

		System.out.println("Started Processing");

		/*
		 * List<List<WaterOrder>> values =
		 * getWaterOrderMap().values().stream().collect(Collectors.toList());
		 * 
		 * List<WaterOrder> waterOrderList = values.stream().flatMap(List::stream)
		 * .filter(wo -> wo.getStaus().equals(WaterStatus.REQUESTED.toString()))
		 * .filter(wo -> wo.getDateTime().equals(LocalDateTime.now())) //.filter(wo ->
		 * wo.getDateTime().equals(LocalDateTime.of(2020, 3, 19, 16, 33, 42)))
		 * .collect(Collectors.toList());
		 * 
		 * if(!waterOrderList.isEmpty()) { System.out.println("Orders Picked: " +
		 * waterOrderList.size()); }
		 * 
		 * 
		 * waterOrderList.stream() .forEach(wo ->
		 * wo.setStaus(WaterStatus.IN_PROGRESS.toString()));
		 */

		// System.out.println("Changed status of task to progress");

		for (Entry<String, List<WaterOrder>> entry : getWaterOrderMap().entrySet()) {

			List<WaterOrder> waterOrderLs = entry.getValue();
			for (WaterOrder wo : waterOrderLs) {
				System.out.println("System date time====" + LocalDateTime.now());
				System.out.println("wo date time====" + wo.getDateTime());
				if (wo.getStaus().equals(WaterStatus.REQUESTED.toString())
						&& wo.getDateTime().isEqual(LocalDateTime.now())) {
					wo.setStaus(WaterStatus.IN_PROGRESS.toString());
				} else {
					System.out.println("Cannot start either delieverd or not the time");
				}

			}
		}

	}
}
