package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class LatencySlap {

    public static final String  T_CNT            = "TOTAL_COUNT";

	private static String SQL="create table latencyslap(username varchar(16) ,startinsec numeric(3,0),endinsec numeric(3,0),percentage numeric(2,0),unique(username,endinsec))";

	private static LatencySlap obj=null;
	
	private Map<String,Map<String,Object>> users= new HashMap<String,Map<String,Object>>();
	
	private Map<String,Map<String,String>> history=new HashMap<String,Map<String,String>> ();

	
	private LatencySlap(){
		
		init();
	}
	
	public static LatencySlap getInstance(){
		
		if(obj==null){
			
			obj=new LatencySlap();
		}
		
		return obj;
	}
	
	
	private void init() {

		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "latencyslap")){
		
				if (table.create(connection, SQL, false)) {

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}
	
	public boolean isExsistingUser(String username){
		
		return users.containsKey(username);
		
	}
	
	public void reload() {
		
		Map<String,Map<String,Object>> map=loadUser();
		
		if(map!=null){
			
			
			users=ordertheMap(map);
		}
		
	}


	private static Map<String,Map<String,Object>> ordertheMap(Map<String, Map<String, Object>> map) {
		
		Map<String,Map<String,Object>> result=new HashMap<String,Map<String,Object>>();
		
		
		Iterator itr=map.keySet().iterator();
		
		while(itr.hasNext()){
			
			String username=itr.next().toString();
		
			Map<String,Object>	data=map.get(username);
			
			int lastslap=Integer.parseInt(data.get("LASTSLAP").toString());

			Map<String,Object>	resultdata=result.get(username);
			
			if(resultdata==null){
				
				resultdata=new HashMap<String,Object>();
				
				result.put(username, resultdata);
				
				resultdata.put("LASTSLAP", data.get("LASTSLAP").toString());
				
			}
			
			
			for(int i=1;i<=lastslap;i++){
				
				Map<String,String> first=(Map<String,String>)data.get(""+i);
				
				int firstslap =Integer.parseInt(first.get("endinsec"));
				
				Map<String,String> resultfirst=(Map<String,String>)resultdata.get(""+(i-1));
				
				if(resultfirst==null){
					
					resultdata.put(""+(i), first);
					
				}else{
				
					int previousslap =Integer.parseInt(resultfirst.get("endinsec"));
					
					if(previousslap<firstslap){
						
						resultdata.put(""+(i), first);

					}else{
						
						resultdata.put(""+(i), resultfirst );
						resultdata.put(""+(i-1), first);
					}

				}

			}
			
			
		}
		return result;
		
	}

	private Map<String,Map<String,Object>> loadUser() 
	{
		Map<String,Map<String,Object>> usermap = new HashMap<String,Map<String,Object>>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
				
			String sql = "select username,startinsec,endinsec,percentage from latencyslap ";
		
			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{
				
				Map<String,Object> slap= usermap.get(resultSet.getString("username").toLowerCase());	
				
				if(slap==null){
					
					slap=new HashMap<String,Object>();
					slap.put("LASTSLAP","0");
				
					usermap.put(resultSet.getString("username").toLowerCase(), slap);
				}
				
				String lastslap=(String)slap.get("LASTSLAP");
				
				int lastslapInt=Integer.parseInt(lastslap);
				lastslapInt++;
				Map<String,String> data=new HashMap<String,String>();
				slap.put(""+lastslapInt, data);
				slap.put("LASTSLAP",""+lastslapInt);

				data.put("startinsec", resultSet.getString("startinsec"));
				data.put("endinsec", resultSet.getString("endinsec"));
				data.put("percentage", resultSet.getString("percentage"));
				
				
			}
			
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		} 
		finally 
		{
			Close.close(resultSet);
			Close.close(statement);
			Close.close(connection);
		}//end of finally
		
		return usermap;
		
	}
	
	
	public String getSlap(String username,long timedifference){
		
		
		Map<String,Object> slap=users.get(username);
		
		if(slap!=null){
			
			
			int maxslap=Integer.parseInt(slap.get("LASTSLAP").toString());
			
			for(int i=1;i<=maxslap;i++)
			{
				Map<String,String> data=(Map<String,String>)slap.get(""+i);
			
				int startinsec=Integer.parseInt(data.get("startinsec"));
				
				int endinsec=Integer.parseInt(data.get("endinsec"));
				
				endinsec=endinsec*1000;
	
				if(timedifference<=endinsec){
						
					return ""+i;
				}
				
			}
		}
		
		return "0";
	}
	
	
	
	public synchronized void incrementTotalCountHistory(String username){
		
		try{
			
			Map result=(Map)history.get(username);

			if(result==null){
				
				result=new HashMap();
				result.put(T_CNT, "0");
				history.put(username, result);
			}
		
		long tcnt=Long.parseLong(result.get(T_CNT).toString());
		tcnt++;
		result.put(T_CNT, ""+tcnt);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public synchronized void incrementCountHistory(String username,String slap){
		
		try{
			
			Map result=(Map)history.get(username);

			if(result==null){
				
				result=new HashMap();
				history.put(username, result);
			}
		
		String slapcount=(String)result.get(slap);
		
		if(slapcount==null){
			slapcount="0";
			result.put(slap, slapcount);
		}
		long tcnt=Long.parseLong(slapcount);
		tcnt++;
		result.put(slap, ""+tcnt);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		
		
		Map<String,Map<String,Object>> users= new HashMap<String,Map<String,Object>>();


		 Map<String,Object> slap1=new HashMap<String,Object>();
		 slap1.put("LASTSLAP","5");
		 Map<String,String> data=new HashMap<String,String>();		 
		 data.put("endinsec", "6");
		 slap1.put("1", data);
		 Map<String,String> data1=new HashMap<String,String>();		 
		 data1.put("endinsec", "5");
		 slap1.put("2", data1);
		 Map<String,String> data2=new HashMap<String,String>();		 
		 data2.put("endinsec", "7");
		 slap1.put("3", data2);

		 Map<String,String> data3=new HashMap<String,String>();		 
		 data3.put("endinsec", "10");
		 slap1.put("4", data3);
		 Map<String,String> data4=new HashMap<String,String>();		 
		 data4.put("endinsec", "8");
		 slap1.put("5", data4);

		 users.put("u1", slap1);
		 users.put("u2", slap1);
		 
		 System.out.println(users);

		 System.out.println(ordertheMap(users));
	}

	public int getParcentage(String username,String slabstring) {
	
		int result=0;
		try{
			
		
		Map<String,Object> slap=users.get(username);
		
		Map<String,String> data=(Map<String,String>)slap.get(slabstring);
		
		result=Integer.parseInt(data.get("percentage"));
		
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return result;
		
	}

	public Map<String,String> getHistory(String username) {
		
		return history.get(username);
	}

	public double getTotalCount(String username) {
	
		double result=0;
		
		try{
			result=Double.parseDouble(history.get(username).get(T_CNT));
			
		}catch(Exception e){
			
		}
		
		return result;
	}

	public double getCount(String username, String slab) {
		
		double result=0;
		
		try{
			result=Double.parseDouble(history.get(username).get(slab));
			
		}catch(Exception e){
			
		}
		
		return result;
	}

	public int getMaxSlap(String username) {
		
		int result=0;
		
		try{
			result=Integer.parseInt(((Map)users.get(username)).get("LASTSLAP").toString());
		}catch(Exception e){
			
		}
		return 0;
	}

	public int getMaixmumRandomSeed(String username,String slab) {
		

		int result=0;
		try{
			
		
		Map<String,Object> slap=users.get(username);
		
		Map<String,String> data=(Map<String,String>)slap.get(slab);
		
		int startinsec=Integer.parseInt(data.get("startinsec"));
		
		int endinsec=Integer.parseInt(data.get("endinsec"));
		
		result=getRandomSecond(startinsec,endinsec);

		}catch(Exception e){
			
			e.printStackTrace();
		}
		return result;
	}
	
	

	private int getRandomSecond(int start,int end){		
		  Random r = new Random();
		  return  start + r.nextInt( end - start );		
	}

	public void resetHistory() {
		
		history=new HashMap<String,Map<String,String>> ();
		
	}

}
