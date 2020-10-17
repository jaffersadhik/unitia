package delivery;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.KannelStoreDBConnection;
import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNProcessor;



public class DBReceiver extends Thread {

	String key=null;
	
	String kannelid=null;

	String smscid=null;
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String kannelid,String smscid){
		
		this.key=kannelid+"~"+smscid;
		this.kannelid=kannelid;
	
		this.smscid=smscid;
			
		System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " started");
	}
	public void run(){
		
		while(true){
			
			try{
			Map<String,Object> logmap=new HashMap<String,Object>();

			logmap.put("username","sys");
			logmap.put("kannelid",kannelid);

			logmap.put("smscid",smscid);
			logmap.put("logname", "sqlboxdnpoller");

			
				
			long start=System.currentTimeMillis();
			
			System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " fetching record");

			List<Map<String,Object>> data=getData(logmap);
			
			if(data!=null&&data.size()>0){
				
				long end=System.currentTimeMillis();
			
				logmap.put("record count ",""+data.size());
				
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " fetching record count "+data.size());

				long start1=System.currentTimeMillis();

				List<Map<String,Object>> result=getPersistResult(data);
			
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " insert record count "+result.size());

				
				updateMap(result);
				
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " after update record count "+result.size());

				untilPersist(result);
				
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " after insert record count "+result.size());

					
						
				deleteUntilSuccess(data,logmap);
				
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " after delete record count "+data.size());

				
				logmap.put("status ","cycle completed");

				new FileWrite().write(logmap);
			}else{
				logmap.put("status ","no records available stop the poller");
			
				logmap.put("dbprop ",Kannel.getInstance().getKannelmap().get(kannelid).toString());
				
				System.out.println("kannelid : "+kannelid+" smscid : "+smscid+ " no record avilable");

				new FileWrite().write(logmap);
				
				PollerStartup.getInstance(kannelid,smscid).runninguser.remove(key);
				
				return;
			}
			
			}catch(Exception e){
				
				e.printStackTrace();
				
				gotosleep();
			}
		}
	}
	
	

private void updateMap(List<Map<String, Object>> datalist) {
		
		
		for(int i=0,max=datalist.size();i<max;i++){
			
			Map<String, Object> data=datalist.get(i);
			
			new DNProcessor(data,new HashMap()).doProcess();
			
		
		}
		
		
	}
	private void untilPersist(List<Map<String, Object>> datalist) {


		while(true){
			
			if(datalist==null || datalist.size()<1){
				
				return;
			}
			
			
			if(new ReportDAO().insert("reportlog_delivery",datalist)){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
	}
	private List<Map<String, Object>> getPersistResult(List<Map<String, Object>> data) {
		
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		for(int i=0,max=data.size();i<max;i++){
			Map<String,Object> msgmap=data.get(i);
		new DNProcessor(msgmap,new HashMap()).parseDliveryReceipt(msgmap);
		msgmap.put(MapKeys.INSERT_TYPE, "dn");

		if(!msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString().equals("1")){
			
				if(!msgmap.get(MapKeys.SPLIT_SEQ).toString().equals("1")){
					
					continue;
				}

			
		}
		
		result.add(msgmap);
		}
		return result;
	}
	private List<Map<String, Object>> getData(Map<String, Object> logmap) {

		 	Connection connection = null;
			PreparedStatement statement = null;
			ResultSet resultset = null;
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			try {

				connection = KannelStoreDBConnection.getInstance(kannelid, Kannel.getInstance().getKannelmap().get(kannelid)).getConnection();
				String sql=" select a.smsc smsc,a.ts ts,a.url r,b.url dlr,b.mask status from dlr_unitia a,dlr_unitia_resp b where a.smsc=b.smsc and a.ts=b.ts and a.smsc=? limit 500";
				
				logmap.put("sql", sql);
				
				statement = connection.prepareStatement(sql);
				
				statement.setString(1, smscid);

				resultset = statement.executeQuery();

				while (resultset.next()) {

						
					String urls= resultset.getString("r");
					

			        URL url=null;
					try {
						url = new URL(urls);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					 Map<String,Object> reqmap=null;
					if(url!=null){
						
						reqmap= getRequestParam(url.getQuery());

					}else{
						reqmap=new HashMap<String,Object>();
						
					}
					
					   reqmap.put("smsc", resultset.getString("smsc"));
					   reqmap.put("ts", resultset.getString("ts"));

					   reqmap.put(MapKeys.SMSCID, resultset.getString("smsc"));
					   
					   reqmap.put(MapKeys.CARRIER_DR, resultset.getString("dlr"));

					   reqmap.put(MapKeys.DN_STATUSCD, resultset.getString("status"));

					   reqmap.put(MapKeys.CARRIER_SYSTEMID, "not implement");

					result.add(reqmap);
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
	private void deleteUntilSuccess(List<Map<String, Object>> data,Map<String, Object> logmap) {

		
		while(true){
			
			if(delete(data,logmap)){
				
				return;
			}
			
			gotosleep();
		}
		
	}
	
	
	private boolean delete(List<Map<String, Object>> data,Map<String, Object> logmap) {

	 	Connection connection = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;

		try {

			connection = KannelStoreDBConnection.getInstance(kannelid, Kannel.getInstance().getKannelmap().get(kannelid)).getConnection();
			connection.setAutoCommit(false);
			String sql1=" delete from dlr_unitia where smsc=? and ts=?";
			String sql2=" delete from dlr_unitia_resp where smsc=? and ts=?";
				
			logmap.put("sql1", sql1);
			logmap.put("sql2", sql2);

			statement1 = connection.prepareStatement(sql1);
			statement2 = connection.prepareStatement(sql2);



			for (int i=0,max=data.size();i<max;i++) {

				Map<String,Object> req=data.get(i);
				statement1.setString(1, req.get("smsc").toString());
				statement1.setString(2, req.get("ts").toString());
				statement2.setString(1, req.get("smsc").toString());
				statement2.setString(2, req.get("ts").toString());

				statement1.addBatch();
				statement2.addBatch();
			}
			
			statement1.executeBatch();
			statement2.executeBatch();
			
			connection.commit();
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(statement1);
			Close.close(statement2);
			Close.close(connection);
		}

		
		return false;
	
	}
	public Map getRequestParam(final String queryString)
	    {
	        final StringTokenizer st = new StringTokenizer(queryString, "&");
	        final HashMap reqParam = new HashMap();
	        while (st.hasMoreTokens())
	        {
	            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
	            String key = "";
	            String value = "";
	            if (st2.hasMoreTokens())
	            {
	                key = st2.nextToken();
	                if (st2.hasMoreTokens())
	                {
	                    value = st2.nextToken();
	                }
	            }
	            reqParam.put(key, value);
	        }
	        return reqParam;
	    }
	private void gotosleep() {
		
		try{
			
			Thread.sleep(100L);
		}catch(Exception e){
			
		}
	}
	
}
