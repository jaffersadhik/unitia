package dnsql;

import java.io.IOException;
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

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.KannelStoreDBConnection;
import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.db.SplitupDAO;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNProcessor;
import com.winnovature.unitia.util.redis.QueueSender;

import unitiaroute.ReRouting;



public class DBReceiver extends Thread {

	String key=null;
	
	String kannelid=null;

	String smscid=null;
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String kannelid,String smscid){
		
		this.key=kannelid+"~"+smscid;
		this.kannelid=kannelid;
	
		this.smscid=smscid;
			
	}
	public void run(){
		
		while(true){
			
			try{
			Map<String,Object> logmap=new HashMap<String,Object>();

			logmap.put("username","sys");
			logmap.put("kannelid",kannelid);

			logmap.put("smscid",smscid);
			logmap.put("logname", "sqlboxdndbreceiver");

			
				
			long start=System.currentTimeMillis();
			

			List<Map<String,Object>> data=getData(logmap);
			
			if(data!=null&&data.size()>0){
				
				long end=System.currentTimeMillis();
			
				logmap.put("record count ",""+data.size());
				

				long start1=System.currentTimeMillis();

				List<Map<String,Object>> result=getPersistResult(data);
			
				List<Map<String,Object>> splitupresult=getSplitupResult(data);

				
				updateMap(result);
				
				updateTimeStamp(splitupresult);

				untilPersistSplitup(splitupresult);

				untilPersist(result);
				
				deleteUntilSuccess(data,logmap);
				
				doDNRetryForAll(result);
				
				logmap.put("status ","cycle completed");

				new FileWrite().write(logmap);
			}else{
				logmap.put("status ","no records available stop the poller");
			
				logmap.put("dbprop ",Kannel.getInstance().getKannelmap().get(kannelid).toString());
				

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
	
	

private void untilPersistSplitup(List<Map<String, Object>> splitupresult) {
		
	
	while(true){
		
		if(splitupresult==null || splitupresult.size()<1){
			
			return;
		}
		
		
		if(new SplitupDAO().insert("splitup_delivery",splitupresult)){
		
			return;
		}else{
			
			gotosleep();
		}
	}
		
	}
private void doDNRetryForAll(List<Map<String, Object>> result) throws IOException {
		
	for(int i=0,max=result.size();i<max;i++){
		
		doDNRetry(result.get(i), new HashMap<String,Object>());
	}
		
	}
private void updateMap(List<Map<String, Object>> datalist) throws IOException {
		
		
		for(int i=0,max=datalist.size();i<max;i++){
			
			Map<String, Object> data=datalist.get(i);
			
			new DNProcessor(data,new HashMap()).doProcess();
			
		
		}
		
		
	}

private void updateTimeStamp(List<Map<String, Object>> datalist) {
	
	
	for(int i=0,max=datalist.size();i<max;i++){
		
		Map<String, Object> data=datalist.get(i);
		
		new DNProcessor(data,new HashMap()).setTimeStamp();
		
	
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
			
			String statusid=(String)msgmap.get(MapKeys.STATUSID);
			
			if(statusid==null){
				
				statusid="";
			}
			
				if(!statusid.equals("139")&&!msgmap.get(MapKeys.SPLIT_SEQ).toString().equals("1")){
					
					continue;
				}

			
		}
		
		result.add(msgmap);
		}
		return result;
	}
	
	private List<Map<String, Object>> getSplitupResult(List<Map<String, Object>> data) {
		
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		
		for(int i=0,max=data.size();i<max;i++){
			Map<String,Object> msgmap=data.get(i);
		new DNProcessor(msgmap,new HashMap()).parseDliveryReceipt(msgmap);
		msgmap.put(MapKeys.INSERT_TYPE, "dn");

		if(!msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString().equals("1")){
			
			result.add(msgmap);
		}
		
		}
		return result;
	}
	/*
	private void doDNRetry(Map<String, Object> msgmap1,Map<String,Object> logmap) {
		
		if(msgmap1.get(MapKeys.ATTEMPT_TYPE).toString().equals("0") && PushAccount.instance().getPushAccount(msgmap1.get(MapKeys.USERNAME).toString()).get(MapKeys.DN_RETRY_YN).equals("1")){
			
			Map<String,Object> msgmap=new HashMap(msgmap1);
			
			
			String smscid=ReRouting.getInstance().getReRouteSmscid(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SMSCID_ORG).toString());

			msgmap.put(MapKeys.SMSCID_ORG, smscid);

			msgmap.put(MapKeys.SMSCID, smscid);

			if(isFailureErrorCode(msgmap)&&FeatureCode.isDNRetry(msgmap.get(MapKeys.FEATURECODE).toString())&&smscid!=null){
			
				new QueueSender().sendL("dnretrypool", msgmap, false,logmap);

			}
		}
		
	}
	
	*/
	
	
	private void doDNRetry(Map<String, Object> msgmap1,Map<String,Object> logmap) throws IOException {
		
		msgmap1.put(MapKeys.DN_RETRY_YN, PushAccount.instance().getPushAccount(msgmap1.get(MapKeys.USERNAME).toString()).get(MapKeys.DN_RETRY_YN));
		
		if(PushAccount.instance().getPushAccount(msgmap1.get(MapKeys.USERNAME).toString()).get(MapKeys.DN_RETRY_YN).equals("1")){
			
			Map<String,Object> msgmap=new HashMap(msgmap1);
			int attemptcountINt=1;
			try{
			String attemptcount=(String)msgmap1.get(MapKeys.ATTEMPT_COUNT);
			
			if(attemptcount==null){
				
				attemptcount="1";
				
			}
			msgmap1.put(MapKeys.ATTEMPT_COUNT, attemptcount);
			attemptcountINt=(Integer.parseInt(attemptcount)+1);
			msgmap.put(MapKeys.ATTEMPT_COUNT, ""+attemptcountINt);
			
			}catch(Exception e){
				
			}
			
			if(attemptcountINt>6){
				
				return;
			}
			
			String smscid=ReRouting.getInstance().getReRouteSmscid(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SMSCID).toString());

			msgmap1.put("founding reroute smscid", smscid);
			

			if(isFailureErrorCode(msgmap)&&smscid!=null){
			
				msgmap.put(MapKeys.SMSCID_ORG, msgmap.get(MapKeys.SMSCID).toString());

				msgmap.put(MapKeys.SMSCID, smscid);

				msgmap1.put("sending to dnretrypool", "yes");
				msgmap.put(MapKeys.INSERT_TYPE, "dnretry");
				new QueueSender().sendL("dnretrypool", msgmap, false,logmap);

			}
		}
		
	}

	private boolean isFailureErrorCode(Map<String, Object> msgmap) {

		return (msgmap.get(MapKeys.CARRIER_ERR)!=null && !msgmap.get(MapKeys.CARRIER_ERR).toString().equals("000"));
	
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
			
			return true;
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
