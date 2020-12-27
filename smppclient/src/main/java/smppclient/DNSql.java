package smppclient;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.KannelStoreDBConnection;

public class DNSql {

	static String SQL ="insert into dlr_unitia_resp(smsc,ts,url) values(?,?,?)";
	
	public void insertDN(String kannelid,String smscid,String dn){
		
		Connection connection=null;
		PreparedStatement statement=null;
		try{
		String msgid=getMessageId(dn);
		
		if(msgid!=null){
			
			connection = KannelStoreDBConnection.getInstance(kannelid, Kannel.getInstance().getKannelmap().get(kannelid)).getConnection();
			statement=connection.prepareStatement(SQL);
			
			statement.setString(1, smscid);
			statement.setString(2, msgid);
			statement.setString(3, dn);
			statement.execute();
			
		}
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}

	private String getMessageId(String dr) {
	
		String id=null;
  
        if(dr.indexOf("ID:")!=-1)
                        id=dr.substring(dr.indexOf("ID:")+3, dr.indexOf(" ",dr.indexOf("ID:")+3));
      
        return id;	
	}
}
