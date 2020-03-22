package com.rubicon.water.order.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.rubicon.water.order.adapter.LocalDateTimeAdapter;

@XmlRootElement(name = "waterorder")
public class WaterOrder {
	
	private long farmId;
	private int duration;
	//private String dateTime;
	private LocalDateTime dateTime;
	//private WaterStatus staus;
	private String staus;
	
	public WaterOrder() {
	}
	
	@XmlElement
	public long getFarmId() {
		return farmId;
	}
	
	
	public void setFarmId(long farmId) { 
		this.farmId = farmId;
	}
	 
	
	@XmlElement
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	@XmlElement
	public String getStaus() {
		return staus;
	}

	public void setStaus(String staus) {
		this.staus = staus;
	}
	
	public LocalDateTime getOrderFinishTime(LocalDateTime dateTime, int duration) {		
		System.out.println("Start time is: " + dateTime); 
		LocalDateTime finishTime = dateTime.plusHours(duration);
		System.out.println("Finish time is: " + finishTime); 
		return finishTime;
		 
	}
	
	public Date getOrderFinishTime() {		
		System.out.println("Start time is: " + dateTime); 
		LocalDateTime finishTime = dateTime.plusHours(duration);
		Date finishDate = Date.from(finishTime.atZone(ZoneId.systemDefault()).toInstant());
		System.out.println("Finish time is: " + finishDate); 
		return finishDate;
		 
	}
	
}
