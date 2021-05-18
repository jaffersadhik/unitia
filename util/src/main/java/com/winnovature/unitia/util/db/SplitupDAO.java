package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.SplitupTable;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

public class SplitupDAO {

	private static String SQL="";
	
static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(");
		sb.append("ackid,msgid,username,senderid,");//4
		sb.append("senderid_org,mobile,smscid_org,smscid,rtime,ktime,");//6
		sb.append("itime,rtime_org,featurecode,udh,totalmsgcount,");//5
		sb.append("attempttype,pattern_id,carrier_stime,carrier_dtime,carrier_dtime_org,");//5
		sb.append("carrier_sdate,carrier_ddate,carrier_stat,carrier_err,carrier_msgid,");//5
		sb.append("carrier_systemid,carrier_dr,statusid,statusid_org,carrier_stime_org,");////5
		sb.append("templateid,entityid,dlttype,interfacetype)");//4

		sb.append("values(");
		sb.append("?,?,?,?,");//4
		sb.append("?,?,?,?,?,?,");//6
		sb.append("?,?,?,?,?,");//5
		sb.append("?,?,?,?,?,");//5
		sb.append("?,?,?,?,?,");//5
		sb.append("?,?,?,?,?,");//5
		sb.append("?,?,?,?)");//4
		

		SQL=sb.toString();

	}

public boolean insert(String tablename,List<Map<String, Object>> datalist) {
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		Map<String, Object> logmap=null;
		try{
		
			String sql=getSQL("billing."+tablename);
			
			if(!SplitupTable.getInstance().isVailableTable(tablename)){
			
				SplitupTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
			
			connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
				logmap=msgmap;

				statement.setString(1,(String) msgmap.get(MapKeys.ACKID));
			
				statement.setString(2, (String)msgmap.get(MapKeys.MSGID));
				statement.setString(3,(String) msgmap.get(MapKeys.USERNAME));
				String senderidorg=(String)msgmap.get(MapKeys.SENDERID_ORG);
				
				if(senderidorg!=null&&senderidorg.length()>15){
					
					senderidorg=senderidorg.substring(0,15);
				}
				if( msgmap.get(MapKeys.SENDERID)==null){
				statement.setString(4, senderidorg);
				}else{
					String senderid=(String)msgmap.get(MapKeys.SENDERID);
					if(senderid!=null&&senderid.length()>15){
						
						senderid=senderid.substring(0,15);
					}
					statement.setString(4,senderid);

				}
				statement.setString(5, senderidorg);

				
				
				
				statement.setString(6, (String)msgmap.get(MapKeys.MOBILE));
				
				
				statement.setString(7, (String)msgmap.get(MapKeys.SMSCID_ORG));
				if(msgmap.get(MapKeys.SMSCID)==null){
					
					statement.setString(8, (String)msgmap.get(MapKeys.SMSCID_ORG));
					
				}else{
				
					statement.setString(8, (String)msgmap.get(MapKeys.SMSCID));
					
				}
				statement.setTimestamp(9,new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME).toString())));
				if(msgmap.get(MapKeys.KTIME)!=null){
					statement.setTimestamp(10,new Timestamp(Long.parseLong(msgmap.get(MapKeys.KTIME).toString())));
				}else{
					statement.setTimestamp(10,null);
						
				}
	


				
				statement.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
				if(msgmap.get(MapKeys.RTIME_ORG)!=null){
				statement.setTimestamp(12, new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME_ORG).toString())));
				}else{
					
					statement.setTimestamp(12,null);

				}
				
				
				
				statement.setString(13, (String)msgmap.get(MapKeys.FEATURECODE));
				
				statement.setString(14,(String) msgmap.get(MapKeys.UDH));
				
				statement.setString(15,(String) msgmap.get(MapKeys.TOTAL_MSG_COUNT));

				
			
				
				statement.setString(16, (String)msgmap.get(MapKeys.ATTEMPT_TYPE));
				statement.setString(17, (String)msgmap.get(MapKeys.ALLOWED_PATTERN_ID));

				
				if(msgmap.get(MapKeys.CARRIER_SUBMITTIME)!=null){
					statement.setTimestamp(18, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME))));
					}else{
						statement.setTimestamp(18, null);
		
					}
					if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
					statement.setTimestamp(19,  new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME))));
					}else{
						statement.setTimestamp(19,null);

					}
					
					if(msgmap.get(MapKeys.CARRIER_DONETIME_ORG)!=null){
					statement.setTimestamp(20, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME_ORG))));
					}else{
						
						statement.setTimestamp(20, null);

					}
					
					
				
					statement.setString(21,(String) msgmap.get(MapKeys.CARRIER_SUBMITDATE));
					statement.setString(22,(String) msgmap.get(MapKeys.CARRIER_DONEDATE));
					statement.setString(23,(String) msgmap.get(MapKeys.CARRIER_STAT));
					statement.setString(24,(String) msgmap.get(MapKeys.CARRIER_ERR));
					statement.setString(25, (String)msgmap.get(MapKeys.CARRIER_MSGID));
		
					statement.setString(26, (String)msgmap.get(MapKeys.CARRIER_SYSTEMID));
//					statement.setString(41, (String)msgmap.get(MapKeys.CARRIER_DR));
					statement.setString(27, null);
					
					String statusid=(String) msgmap.get(MapKeys.STATUSID);

					if(statusid!=null&&statusid.trim().length()>0){
						
						if(tablename.equals("reportlog_delivery")){

							if(statusid.equals("200")){
								statement.setString(28, "000");

							}else{
								
								statement.setString(28, msgmap.get(MapKeys.STATUSID).toString());

							}
						}else{
					
							statement.setString(28, msgmap.get(MapKeys.STATUSID).toString());
						}
					}else{
						
						statusid=(String) msgmap.get(MapKeys.CARRIER_ERR);

						if(tablename.equals("reportlog_delivery")){
							
							if(statusid!=null&&statusid.equals("200")){

								statement.setString(28, "000");

							}else{
								statement.setString(28, (String) msgmap.get(MapKeys.CARRIER_ERR));

							}
						}else{
							statement.setString(28, (String)msgmap.get(MapKeys.STATUSID));

						}
					}
					String stausidorg=(String)msgmap.get(MapKeys.STATUSID_ORG);
					
					if(stausidorg==null||stausidorg.equals("ERROR")||stausidorg.trim().length()<1){
						
						statement.setString(29, statusid);

					}else{
						statement.setString(29, (String)msgmap.get(MapKeys.STATUSID_ORG));

					}

					
					if(msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG)!=null){
					statement.setTimestamp(30, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG))));
					}else{
						
						statement.setTimestamp(30, null);

					}
					

					statement.setString(31, (String)msgmap.get(MapKeys.TEMPLATEID));
					statement.setString(32, (String)msgmap.get(MapKeys.ENTITYID));
					statement.setString(33, (String)msgmap.get(MapKeys.DLT_TYPE));
					statement.setString(34, (String)msgmap.get(MapKeys.INTERFACE_TYPE));
					
					
					statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			return true;
		}catch(Exception e){
			
			
			
		     
					try{
						logmap.put("module", "inserterror");
						logmap.put("logname", "inserterror");
						logmap.put("error", tablename+"\n"+ErrorMessage.getMessage(e));

						new FileWrite().write(logmap);
					}catch(Exception e1){
						
					}
			
	        
			try{
				connection.rollback();
			}catch(Exception e1){
				
			}
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
		
		return false;
	}
	private String getSQL(String tablename) {

		String param[]={tablename};
		return MessageFormat.format(SQL, param);
	}
}
