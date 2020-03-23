package com.rubicon.water.order.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "waterorders")
public class Orders {
 
	private List<WaterOrder> waterOrders = null;

	@XmlElement
	public List<WaterOrder> getWaterOrders() {
		return waterOrders;
	}

	public void setWaterOrders(List<WaterOrder> waterOrders) {
		this.waterOrders = waterOrders;
	}
}
