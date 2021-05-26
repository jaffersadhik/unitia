package com.winnovature.unitia.util.dao;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.CampaignDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.misc.MapKeys;

public class Select {
	

	public List<Map<String, Object>> getData(String tablename,String username) {

		try {
			if(!Table.getInstance().isAvailableTable(tablename)){
				
				Table.getInstance().addTable(tablename);
			}
	
				return getRecords(tablename,username);
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return null;
	}

	
	public List<Map<String, Object>> getDataAsExpired(String tablename) {

		try {
			if(!Table.getInstance().isAvailableTable(tablename)){
				
				Table.getInstance().addTable(tablename);
			}
	
				return getRecordsAsExpired(tablename);
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return null;
	}
	private List<Map<String, Object>> getRecords(String tablename,String username) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			
			

			connection = QueueDBConnection.getInstance().getConnection();
			
			statement = connection.prepareStatement(
						" select data from " + tablename + " where username=? and  scheduletime < ? and pstatus=0 limit 100");
			
			statement.setString(1, username);

			statement.setLong(2, System.currentTimeMillis());

			resultset = statement.executeQuery();

			while (resultset.next()) {

				byte[] Bytes = resultset.getBytes("data");

				ByteArrayInputStream bis = new ByteArrayInputStream(Bytes);

				ObjectInputStream ois = new ObjectInputStream(bis);

				result.add((Map<String, Object>) ois.readObject());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		if (result.size() > 0) {

			update(tablename, result,false);
		}
		return result;
	}

	
	private List<Map<String, Object>> getRecordsAsExpired(String tablename) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			
			

			connection = QueueDBConnection.getInstance().getConnection();
			
			statement = connection.prepareStatement(
						" select data from " + tablename + " where itime < ? and pstatus=0 limit 100");
			

			statement.setLong(1, (System.currentTimeMillis()-(20*60*1000)));

			resultset = statement.executeQuery();

			while (resultset.next()) {

				byte[] Bytes = resultset.getBytes("data");

				ByteArrayInputStream bis = new ByteArrayInputStream(Bytes);

				ObjectInputStream ois = new ObjectInputStream(bis);

				result.add((Map<String, Object>) ois.readObject());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		
		return result;
	}

	
	public Map<String,List<Map<String, Object>>> getReportRecords(List<Map<String, Object>> queuelist) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		Map<String,List<Map<String, Object>>> result = new HashMap<String,List<Map<String, Object>>>();
		
		Map<String,Object> selectmap=new HashMap<String,Object>();
		try {
			
		    selectmap=getMapData(queuelist);

			connection = QueueDBConnection.getInstance().getConnection();

			statement = connection.prepareStatement(selectmap.get("sql").toString());

			for(int i=0,max=queuelist.size();i<max;i++){
			
				statement.setString((i+1),queuelist.get(i).get(MapKeys.MSGID).toString() );

			}
			resultset = statement.executeQuery();
			while (resultset.next()) {

				List<Map<String, Object>> datalist=result.get("insert");
				if(datalist==null){
					datalist=new ArrayList<Map<String, Object>>();
					result.put("insert", datalist);
				}
				byte[] Bytes = resultset.getBytes("data");

				ByteArrayInputStream bis = new ByteArrayInputStream(Bytes);

				ObjectInputStream ois = new ObjectInputStream(bis);

				Map<String, Object> data=(Map<String, Object>) ois.readObject();
				
				data.putAll((Map)selectmap.get(data.get(MapKeys.MSGID)));
				
				datalist.add(data);
			}

			if(result.size()<1){
				
				result.put("return", queuelist);
				
			}else{
				System.out.println("fetched records size : "+result.get("insert").size());
				result.put("return", getReturnList(queuelist,result.get("insert")));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		
		return result;
	}

	
	
	
	private List<Map<String, Object>> getReturnList(List<Map<String, Object>> queuelist,
			List<Map<String, Object>> insertlist) {
		List<Map<String, Object>> returnlist=new ArrayList<Map<String, Object>>();
		
		Set<String> insertmsgidset=getSet(insertlist);
		
		for(int i=0,max=queuelist.size();i<max;i++){
			
			if(!insertmsgidset.contains(queuelist.get(i).get(MapKeys.MSGID).toString())){
			
				returnlist.add(queuelist.get(i));
			}
		}
		
		return null;
	}

	private Set<String> getSet(List<Map<String, Object>> insertlist) {
		
		Set<String> msgidset=new HashSet<String>();
		for(int i=0,max=insertlist.size();i<max;i++){
			
			msgidset.add(insertlist.get(i).get(MapKeys.MSGID).toString());
		}
		
		return msgidset;
	}

	public Map<String, Object> getMapData(List<Map<String, Object>> queuelist) {
		
		Map<String, Object> result=new HashMap<String,Object>();
		StringBuffer sb1=new StringBuffer();

		int counter=0;
		for(int i=0,max=queuelist.size();i<max;i++){
			
			Map<String,Object> record=queuelist.get(i);
			result.put(record.get(MapKeys.MSGID).toString(), record);
			sb1.append("?").append(",");
			counter++;
		}
		
		String sql="select * from submissionpool where msgid in ("+ sb1.toString().subSequence(0,  sb1.toString().length()-1)+")";
		String deletesql="delete from submissionpool where msgid in ("+ sb1.toString().subSequence(0,  sb1.toString().length()-1)+")";

		result.put("sql", sql);
		result.put("deletesql", deletesql);

		result.put("counter", ""+queuelist.size());
		
		return result;
	}

	private void update(String tablename, List<Map<String, Object>> result,boolean iscampaign) {

		Connection connection = null;
		PreparedStatement statement = null;
		try {

			if(iscampaign){
				
				connection = CampaignDBConnection.getInstance().getConnection();
				statement = connection.prepareStatement(" update uiq_campaign." + tablename + " set pstatus=1 where id = ?");

			}else{
		
				connection = QueueDBConnection.getInstance().getConnection();
				statement = connection.prepareStatement(" update " + tablename + " set pstatus=1 where msgid = ?");

			
			}
			
			connection.setAutoCommit(false);

		
			for (int i = 0; i < result.size(); i++) {

				Map<String, Object> data = result.get(i);
				statement.setString(1, data.get(MapKeys.MSGID).toString());
				statement.addBatch();
			}

			statement.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(statement);
			Close.close(connection);
		}
	}

	public boolean delete(String tablename, List<Map<String, Object>> result,boolean isCampaign) {

		Connection connection = null;
		PreparedStatement statement = null;
		try {

			if(isCampaign){
				
				connection = CampaignDBConnection.getInstance().getConnection();
				statement = connection.prepareStatement(" delete from uiq_campaign." + tablename + "  where id = ?");
				
				
			}else{
			
				connection = QueueDBConnection.getInstance().getConnection();
				
				statement = connection.prepareStatement(" delete from " + tablename + "  where msgid = ?");
				
				
			}
			connection.setAutoCommit(false);
			for(int i=0,max=result.size();i<max;i++){
			statement.setString(1, result.get(i).get(MapKeys.MSGID).toString());
			statement.addBatch();
			}
			
			statement.executeBatch();
			connection.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		
			return false;
		} finally {

			Close.close(statement);
			Close.close(connection);
		}
		

	}

	public List<Map<String, Object>> getData(String tablename, String username, boolean iscampign, Map<String, Object> logmap) {


	try {
	
				return getRecords(tablename,username,iscampign,logmap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return null;
	
	}

	private List<Map<String, Object>> getRecords(String tablename, String username, boolean iscampign, Map<String, Object> logmap) {
		
		


		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {

			connection = CampaignDBConnection.getInstance().getConnection();
			String sql=" select * from uiq_campaign." + tablename + " where username=? and execution_date < ? and pstatus=0 limit 500";
			
			logmap.put("sql", sql);
			
			statement = connection.prepareStatement(sql);
			
			statement.setString(1, username);

			statement.setLong(2, System.currentTimeMillis());

			
			resultset = statement.executeQuery();

			while (resultset.next()) {

				Map<String, Object> data=new HashMap<String,Object>();
				data.put(MapKeys.USERNAME, resultset.getString("username").toLowerCase());
				data.put(MapKeys.ACKID, resultset.getString("id"));
				data.put(MapKeys.MSGID, resultset.getString("id"));
				long sysdate=System.currentTimeMillis();
				data.put(MapKeys.RTIME, ""+sysdate);
				data.put(MapKeys.FULLMSG, resultset.getString("content"));
				data.put(MapKeys.SENDERID_ORG, resultset.getString("sender_id"));
				data.put(MapKeys.SENDERID, resultset.getString("sender_id"));
				data.put(MapKeys.MSGTYPE,  resultset.getString("msg_type"));
				String mobile=new com.winnovature.unitia.util.misc.Utility().prefix91map(data,resultset.getString("username").toLowerCase(),resultset.getString("mobile") );
				data.put(MapKeys.MOBILE, mobile);
				data.put(MapKeys.SCHEDULE_TIME, resultset.getString("execution_date"));
				data.put(MapKeys.PARAM1, resultset.getString("campaign_activity_id"));
				data.put(MapKeys.PROTOCOL, "http");
				data.put(MapKeys.INTERFACE_TYPE, "ui");

				
				
				result.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		if (result.size() > 0) {

			update(tablename, result,iscampign);
		}
		return result;
	
	}

	public boolean delete(List<Map<String, Object>> queuelist) {
		Connection connection = null;
		PreparedStatement statement = null;
		Map<String,Object> selectmap=new HashMap<String,Object>();
		try {
			
		    selectmap=getMapData(queuelist);

			connection = QueueDBConnection.getInstance().getConnection();

			System.out.println(selectmap.get("deletesql").toString());
			statement = connection.prepareStatement(selectmap.get("deletesql").toString());

			for(int i=0,max=queuelist.size();i<max;i++){
			
				statement.setString((i+1),queuelist.get(i).get(MapKeys.MSGID).toString() );

			}
			
			return statement.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		
			return false;
		} finally {

			Close.close(statement);
			Close.close(connection);
		}
		
	}
}
