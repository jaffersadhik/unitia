package com.winnovature.unitia.util.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.processor.DNProcessor;

public class DeliveryUtility {

public void updateMap(List<Map<String, Object>> datalist) {
		
		
		for(int i=0,max=datalist.size();i<max;i++){
			
			Map<String, Object> data=datalist.get(i);
			
			new DNProcessor(data,new HashMap()).doProcess();
			
		
		}
		
		
	}
}
