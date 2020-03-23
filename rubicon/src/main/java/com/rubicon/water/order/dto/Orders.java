package com.rubicon.water.order.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "waterorders")
public class Orders {
 
	private WaterOrder waterOrder = null;

	@XmlElement
	public WaterOrder getWaterOrder() {
		return waterOrder;
	}

	public void setWaterOrder(WaterOrder waterOrder) {
		this.waterOrder = waterOrder;
	}
	
	
	
}
