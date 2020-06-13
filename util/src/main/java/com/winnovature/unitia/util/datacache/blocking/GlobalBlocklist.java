package com.winnovature.unitia.util.datacache.blocking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



import com.winnovature.unitia.util.db.CoreDBConnection;

public class GlobalBlocklist {


	private static GlobalBlocklist instance = new GlobalBlocklist();

	private List<String> senderidlist =new ArrayList();
	
	private List<String> mobilelist =new ArrayList();
	
	private List<String> msgtemplateregex =new ArrayList();


	private GlobalBlocklist() {
		
		load();
	}

	
	public void load(){
		
		
		try{
		Map data=getData();
		
		senderidlist=(List)data.get("SENDERID");
		mobilelist=(List)data.get("MOBILE");
		msgtemplateregex=(List)data.get("MSGTEMPLATE");
		}catch(Exception e){
			
		}
	}
	public static GlobalBlocklist getInstance() {
		return instance;
	}

	public boolean isMobileBlockList(String mobile){
		
		return mobilelist.contains(mobile);
		
	}
	
	
	public boolean isSenderidBlockList(String senderid){
		
		return senderidlist.contains(senderid.toUpperCase());
		
	}
	
	public boolean isMsgTemplateBlockList(String fullmsg){
		
		for(int i=0;i<msgtemplateregex.size();i++)
		{			
			if(Pattern.compile(msgtemplateregex.get(i).toString(), Pattern.CASE_INSENSITIVE).matcher(fullmsg).matches())
			{
				return true;
			}
		}
		return false;

	}




	private synchronized Map getData() throws Exception  {


		String sql="select senderid,mobile,msgtemplate from global_blocklist";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		String identifier =  "load() ";
		Map tmpTemplates = new HashMap();
		
		tmpTemplates.put("SENDERID", new ArrayList<String>());
		tmpTemplates.put("MOBILE", new ArrayList<String>());
		tmpTemplates.put("MSGTEMPLATE", new ArrayList<String>());


		try {
			connection = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql.toString());
			rs = statement.executeQuery();
			while (rs.next()) {
				String senderid = rs.getString("senderid");
				String mobile = rs.getString("mobile");
				String msgtemplate = rs.getString("msgtemplate");
				
				if(senderid!=null&&senderid.trim().length()>0){
					
					
					List list=(List)tmpTemplates.get("SENDERID");
					
					list.add(senderid.trim().toUpperCase());
				}
				
				
				
				if(mobile!=null&&mobile.trim().length()>0){
					
					
					List list=(List)tmpTemplates.get("MOBILE");
					
					list.add(mobile.trim());
				}
				
				
				
				if(msgtemplate!=null&&msgtemplate.trim().length()>0){
					
					List _msgPattern=(List)tmpTemplates.get("MSGTEMPLATE");

					_msgPattern.add(msgtemplate.trim());
				
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (Exception ignore) {

			}

		}// end of finally
		return tmpTemplates;
 	}

	public void reload() {
		load();
	}
	
	public List getSenderidList(){
		
		return senderidlist;
	}
	
	public List getMobileList(){
		
		return mobilelist;
	}
	
	public List<String> getMsgTemplateRegex(){
		
		return msgtemplateregex;
	}
}
