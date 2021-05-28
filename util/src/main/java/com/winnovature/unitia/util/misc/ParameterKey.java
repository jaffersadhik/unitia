package com.winnovature.unitia.util.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;

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

	public Object getObject(Map<String,Object> data) throws IOException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		Iterator itr=data.keySet().iterator();
		while(itr.hasNext()){
			
			String key=itr.next().toString();
			
			if(keys.contains(key)){
				
				result.put(key, data.get(key));
			}
		}
		
		CompressedObject object=new CompressedObject();
		object.setDatabytes(toCompressedBytes(result));
		return object;
	}
	
	
	public  byte[] toCompressedBytes(Object o) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(new DeflaterOutputStream(baos));
	    oos.writeObject(o);
	    oos.close();
	    return baos.toByteArray();
	}
	public static void main(String args[]){
		
		System.out.println(ParameterKey.getInstance().getKeys());
	}
}
