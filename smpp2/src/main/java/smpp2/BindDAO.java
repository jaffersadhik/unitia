package smpp2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class BindDAO {

	


	private static String CREATE_BINDLOG_SQL="create table smpp_bind_log(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(50),bindtype varchar(5),ip varchar(200),port decimal(10,0),updatetime datetime,ctime datetime,window varchar(5),status varchar(100),ereqcount varchar(10),erespcount varchar(10),excount varchar(10),etime varchar(50),eptime varchar(50),ewtime varchar(50),sreqcount varchar(10),srespcount varchar(10),sxcount varchar(10),stime varchar(50),sptime varchar(50),swtime varchar(50),dreqcount varchar(10),drespcount varchar(10),dxcount varchar(10),dtime varchar(50),dptime varchar(50),dwtime varchar(50))" ;
	
	private static String CREATE_STATUS_SQL="create table smpp_status_log(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(50),bindtype varchar(5),statustype varchar(50),statuscode varchar(10),count varchar(50))" ;

	private static String DELETE_BINDLOG_SQL="delete from smpp_bind_log where port=?";
	
	private static String DELETE_STATUS_SQL="delete from smpp_status_log  where port=?";

	private static String INSERT_BINDLOG_SQL="insert into smpp_bind_log(username,bindtype,ip,port,updatetime,ctime,window,status,ereqcount,erespcount,excount,etime,eptime,ewtime,sreqcount,srespcount,sxcount,stime,sptime,swtime,dreqcount,drespcount,dxcount,dtime,dptime,dwtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String INSERT_STATUS_SQL="insert into smpp_status_log(username ,bindtype ,statustype ,statuscode ,count ) values(? ,? ,? ,? ,? )";

	private static BindDAO obj=new BindDAO();
	
	private BindDAO(){
		
		init();
	}

	private void init() {
		
		


		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "smpp_bindlog")){
				
				if (table.create(connection, SQL , false)) {

				}

			}
			
			delete(connection,DELETE_BINDLOG_SQL);
			delete(connection,DELETE_STATUS_SQL);


		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}
	
	public static BindDAO getInstance(){
		
		if(obj==null){
			
			obj=new BindDAO();
		}
		
		return obj;
	}
	
	public void insert(){
		
		Connection connection=null;
		
		try{
			connection =CoreDBConnection.getInstance().getConnection();
			connection.setAutoCommit(false);
			delete(connection,DELETE_BINDLOG_SQL);
			delete(connection,DELETE_STATUS_SQL);
			insertBindLog(connection);
			connection.commit();
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
	}
	
	private void insertBindLog(Connection connection) {
		
		PreparedStatement statement=null;
		
		try{
			statement=connection.prepareStatement(INSERT_BINDLOG_SQL);
			
			List<Map<String,String>> bindlist=SessionStore.getInstance().getBindData();
			
			for(int i=0;i<bindlist.size();i++){
				
				Map<String,String> data=bindlist.get(i);
				
	
				statement.setString(1,data.get("username") );
				statement.setString(2,data.get("bindtype") );
				statement.setString(3,data.get("ip") );
				statement.setString(4,System.getenv("port"));
				statement.setTimestamp(5,new Timestamp(getUpTime(data)));
				statement.addBatch();
				
			}
			
			statement.executeBatch();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
		
	}

	private long getUpTime(Map<String, String> data) {
		String uptime=data.get("uptime");
		
		long uptimeL=0;
		
		if(uptime!=null){
			try{
				uptimeL=Long.parseLong(uptime);
			}catch(Exception e){
				
			}
		}

	
		return uptimeL;
	}

	public void delete(Connection connection, String sql){
	
		PreparedStatement statement=null;
		
		try{
			
			statement=connection.prepareStatement(sql);
			
			statement.setString(1,System.getenv("port"));
			
			statement.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
		
		
	}
	
}
