package com.winnovature.unitia.util.cdac;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class CDACSmscId {

	private static CDACSmscId obj=null;
	
	private static String CREATE_SQL="create table cdac_connector(id INT PRIMARY KEY AUTO_INCREMENT,smscid varchar(50) not null,ip varchar(100) not null,port decimal(5,0),username varchar(50) not null,password varchar(50) not null,enc_key varchar(50) not null,dnsqldbid varchar(50) not null)";
	
	private static String INSERT_SQL="insert into cdac_connector(smscid,ip,port,username,password,enc_key,dnsqldbid) values('cdac','msdgweb.mgov.gov.in','443','Mobile_1-GOKOTP','Gokotp@1234','7c8fa3c0-a908-4aaf-b5ab-095a780dc981','dnsqldb_1')";

	private static String SELEC_SQL="select * from cdac_connector";

	private Map<String,Map<String,String>> datamap=new HashMap<String,Map<String,String>>();

	private CDACSmscId(){
		
		init();
		
		reload();
	}
	
	public static CDACSmscId getInstance(){
		
		if(obj==null){
			
			obj=new CDACSmscId();
		}
		
		return obj;
	}
	
	public void init(){
		
		

		Connection connection = null;
		try {
			connection = RouteDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "cdac_connector")){
				if (table.create(connection, CREATE_SQL, false)) {

					if (table.create(connection, INSERT_SQL, false)) {

					}
				}

			}
			
			
		
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}


	}
	
	
	public void reload(){
		
		Map<String,Map<String,String>> result=getData();
		
		if(result!=null){
			
			datamap=result;
			
		}
	}
	
	public Map<String,Map<String,String>> getData(){
		
		Connection connection=null;
		ResultSet resultset=null;
		PreparedStatement statement=null;
		
		Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();
		
		try{
			
			connection=RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(SELEC_SQL);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				Map<String,String> data=new HashMap<String,String>();
			
				data.put("ip", resultset.getString("ip"));

				data.put("port", resultset.getString("port"));

				data.put("username", resultset.getString("username"));

				data.put("password", MD5(resultset.getString("password")));

				data.put("enc_key", resultset.getString("enc_key"));

				data.put("dnsqldbid", resultset.getString("dnsqldbid"));

				result.put(resultset.getString("smscid"), data);
			
			}
			
		}catch(Exception e){
			
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return result;
	}
	
	public boolean isExsists(String smscid){
		
		
		return datamap.containsKey(smscid);
	}
	
	public Map<String,String> getData(String smscid){
		
		return datamap.get(smscid);
	}
	
	
	private static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
	MessageDigest md;
	md = MessageDigest.getInstance("SHA-1");
	byte[] md5 = new byte[64];
	md.update(text.getBytes("iso-8859-1"), 0, text.length());
	md5 = md.digest();
	return convertedToHex(md5);
	}
	private static String convertedToHex(byte[] data)
	{
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < data.length; i++)
	{
	int halfOfByte = (data[i] >>> 4) & 0x0F;
	int twoHalfBytes = 0;
	do
	{
	if ((0 <= halfOfByte) && (halfOfByte <= 9))
	{
	buf.append( (char) ('0' + halfOfByte) );
	}
	else
	{
	buf.append( (char) ('a' + (halfOfByte - 10)) );
	}
	halfOfByte = data[i] & 0x0F;
	} while(twoHalfBytes++ < 1);
	}
	return buf.toString();
	}

}
