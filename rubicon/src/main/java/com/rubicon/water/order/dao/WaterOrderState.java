package com.rubicon.water.order.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.rubicon.water.order.dto.WaterOrder;
import com.rubicon.water.order.dto.WaterStatus;

public class WaterOrderState {
	
	public static Map<String,List<WaterOrder>> waterOrderMap;
	
	static {
		waterOrderMap = new HashMap<>();
	}

	public static Map<String, List<WaterOrder>> getWaterOrderMap() {
		return waterOrderMap;
	}

	public static void createWaterOrder(WaterOrder waterOrder) {
		
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))){
			List<WaterOrder> waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));
			boolean orderOverlap = checkIfOrdersOverlap(waterOrderList, waterOrder);
			if(!orderOverlap) {
				waterOrderList.add(waterOrder);
			}
		} else {
			List<WaterOrder> waterOrderList = new ArrayList<>();
			waterOrderList.add(waterOrder);
			waterOrderMap.put(String.valueOf(waterOrder.getFarmId()), waterOrderList);
		}
	}
		
	private static boolean checkIfOrdersOverlap(List<WaterOrder> waterOrderList, WaterOrder newWaterOrder) {
		
		LocalDateTime orderDateTime = newWaterOrder.getDateTime();
		boolean orderOverlap = false;
		for(WaterOrder wo : waterOrderList) {
			LocalDateTime finishTime = wo.getOrderFinishTime(wo.getDateTime(), wo.getDuration());
			if ((orderDateTime.isAfter(wo.getDateTime()) && orderDateTime.isBefore(finishTime)) || orderDateTime.equals(wo.getDateTime())) {
				System.out.println("Orders overlap for the same farm and will not be created.");
				return true;
			} else {
				 System.out.println("Order do not overlap for the same farm."); 
				 orderOverlap = false;
			}
		}
		return orderOverlap;
	}

	public static List<WaterOrder> fetchWaterOrder(WaterOrder waterOrder) {
		
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))){
			List<WaterOrder> waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));
			return waterOrderList;
		} else {
			System.out.println("Order with the farm id does not exist");
			 return null;
		}
		
	}
	
	public static List<WaterOrder> cancelWaterOrder(WaterOrder waterOrder) {
		
		int cancelledOrdersCount = 0;
		List<WaterOrder> cancelledWaterOrderList = new ArrayList<>();
		
		if (waterOrderMap.containsKey(String.valueOf(waterOrder.getFarmId()))){
			List<WaterOrder> waterOrderList = waterOrderMap.get(String.valueOf(waterOrder.getFarmId()));
			for (WaterOrder wo: waterOrderList) {
				 if(!wo.getStaus().equals(WaterStatus.DELIEVERED)) {
					 wo.setStaus(WaterStatus.CANCELLED.toString());
					 cancelledWaterOrderList.add(wo);
					 waterOrderList.remove(wo);
					 cancelledOrdersCount++;
					 if(waterOrderList.isEmpty()) {
						 waterOrderMap.remove(String.valueOf(waterOrder.getFarmId()));
					 }
				 } else {
					 System.out.println("Order with the farm id can not be cancelled as it is delieverd");
				 }
			}
			
		} else {
			System.out.println("No order found with the farmId");
		}
		
		System.out.println("Number of cancelled orders" + cancelledOrdersCount);
		
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
		
		
	//	System.out.println("Changed status of task to progress");
		
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
