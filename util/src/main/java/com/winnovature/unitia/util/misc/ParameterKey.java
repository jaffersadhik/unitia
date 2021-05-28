package com.winnovature.unitia.util.misc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ParameterKey {

	private static ParameterKey obj=new ParameterKey();
	
	private Set<String> keys=new HashSet<String>();
	private ParameterKey(){
		
		init();
	}
	
	public static ParameterKey getInstance(){
		
		if(obj==null){
			
			obj=new ParameterKey();
		}
		
		return obj;
	}
	
	public void init(){
		
		Field [] fields=MapKeys.class.getFields();
		
		for(int i=0;i<fields.length;i++){
			
			Field field=fields[i];
			
			try {
				keys.add(field.get(null).toString());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public Set<String> getKeys() {
		return keys;
	}

	public Map<String,Object> getObject(Map<String,Object> data){
		
		Map<String,Object> result=new HashMap<String,Object>();
		Iterator itr=data.keySet().iterator();
		while(itr.hasNext()){
			
			String key=itr.next().toString();
			
			if(keys.contains(key)){
				
				result.put(key, data.get(key));
			}
		}
		
		return result;
	}
	public static void main(String args[]){
		
		System.out.println(ParameterKey.getInstance().getKeys());
	}
}
