package com.winnovature.unitia.util.template;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class Template {

	private static Template obj=new Template();
	
	Map<String,List<Map<String,String>>> result =new HashMap<String,List<Map<String,String>>> ();
	
	private Template(){
		
		reload();
	}
	
	public static Template getInstance(){
		
		if(obj==null){
			
			obj=new Template();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,List<Map<String,String>>> data=getData();
		
		if(data!=null){
			result=data;
		}
		
	}
	
	public boolean isAvailableSenderid(String senderid){
		
		return result.containsKey(senderid.toLowerCase());
	}
	
	
	public List<Map<String,String>> getTemplateList(String senderid){
		
		return result.get(senderid);
	}
	
	public  Map<String,List<Map<String,String>>> getData(){
		
		Map<String,List<Map<String,String>>> result=new HashMap<String,List<Map<String,String>>>();
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection=CoreDBConnection.getInstance().getConnection();
			
			statement=connection.prepareStatement("select username,senderid,entity_id,template_id,template_msg,template_sample_msg from user_reports.ui_dltrequests");
			resultset=statement.executeQuery();
			
			
			while(resultset.next()){
				
				String username=resultset.getString("username");
				String senderid=resultset.getString("senderid");
				String entity_id=resultset.getString("entity_id");
				String template_id=resultset.getString("template_id");
				String template_msg=resultset.getString("template_msg");

				if(template_msg==null||template_id==null||entity_id==null||senderid==null||username==null){
					continue;
				}
				
				username=username.trim().toLowerCase();
				senderid=senderid.trim().toLowerCase();
				template_msg=template_msg.trim().toLowerCase();
				username=username.trim().toLowerCase();
				
				
				
				List<Map<String,String>> templatelist=result.get(senderid);
				
				if(templatelist==null){
					templatelist=new ArrayList<Map<String,String>>();
					result.put(senderid, templatelist);
				}
				
				Map<String,String> data=new HashMap<String,String>();
				

				data.put("template_msg", template_msg);
				data.put("entity_id", entity_id);
				data.put("template_id", template_id);
			
				templatelist.add(data);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return result;
	}	
	
	
}

