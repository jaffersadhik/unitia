package smpp2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import java.util.SortedMap;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class BindDAO {

	


	private static String CREATE_BINDLOG_SQL="create table smpp_bind_log(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(50),bindtype varchar(5),ip varchar(200),port decimal(10,0),updatetime datetime,ctime datetime,window varchar(5),status varchar(100),ereqcount varchar(10),erespcount varchar(10),excount varchar(10),etime varchar(50),eptime varchar(50),ewtime varchar(50),sreqcount varchar(10),srespcount varchar(10),sxcount varchar(10),stime varchar(50),sptime varchar(50),swtime varchar(50),dreqcount varchar(10),drespcount varchar(10),dxcount varchar(10),dtime varchar(50),dptime varchar(50),dwtime varchar(50))" ;
	
	private static String CREATE_STATUS_SQL="create table smpp_status_log(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(50),bindtype varchar(5),port decimal(10,0),statustype varchar(50),statuscode varchar(10),count varchar(50))" ;

	private static String DELETE_BINDLOG_SQL="delete from smpp_bind_log where port=?";
	
	private static String DELETE_STATUS_SQL="delete from smpp_status_log  where port=?";

	private static String INSERT_BINDLOG_SQL="insert into smpp_bind_log(username,bindtype,ip,port,updatetime,ctime,window,status,ereqcount,erespcount,excount,etime,eptime,ewtime,sreqcount,srespcount,sxcount,stime,sptime,swtime,dreqcount,drespcount,dxcount,dtime,dptime,dwtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String INSERT_STATUS_SQL="insert into smpp_status_log(username ,bindtype ,port,statustype ,statuscode ,count ) values(? ,? ,? ,? ,?,? )";

	private static BindDAO obj=new BindDAO();
	
	private BindDAO(){
		
		init();
	}

	private void init() {
		
		


		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "smpp_bind_log")){
				
				if (table.create(connection, CREATE_BINDLOG_SQL , false)) {

				}

			}
			
			if(!table.isExsists(connection, "smpp_status_log")){
				
				if (table.create(connection, CREATE_STATUS_SQL , false)) {

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
			insertStatusLog(connection);
			connection.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}
	
	private void insertBindLog(Connection connection) {
		
		PreparedStatement statement=null;
		
		try{
			statement=connection.prepareStatement(INSERT_BINDLOG_SQL);
			
			List<Map<String,Object>> bindlist=SessionStore.getInstance().getBindData();
			
			for(int i=0;i<bindlist.size();i++){
				
				Map<String,Object> data=bindlist.get(i);
				

				statement.setString(1,(String)data.get("username") );
				statement.setString(2,(String)data.get("bindtype") );
				statement.setString(3,(String)data.get("ip") );
				statement.setString(4,System.getenv("port"));
				statement.setTimestamp(5,new Timestamp(getUpTime((String)data.get("uptime"))));
				statement.setTimestamp(6,new Timestamp(getUpTime((String)data.get("ctime"))));
				statement.setString(7,(String)data.get("window") );
				statement.setString(8,(String)data.get("status") );

				statement.setString(9,(String)data.get("ereqcount") );
				statement.setString(10,(String)data.get("erespcount") );
				statement.setString(11,(String)data.get("excount") );
				statement.setString(12,(String)data.get("etime") );
				statement.setString(13,(String)data.get("eptime") );
				statement.setString(14,(String)data.get("ewtime") );
				
				statement.setString(15,(String)data.get("sreqcount") );
				statement.setString(16,(String)data.get("srespcount") );
				statement.setString(17,(String)data.get("sxcount") );
				statement.setString(18,(String)data.get("stime") );
				statement.setString(19,(String)data.get("sptime") );
				statement.setString(20,(String)data.get("swtime") );
		
				statement.setString(21,(String)data.get("dreqcount") );
				statement.setString(22,(String)data.get("drespcount") );
				statement.setString(23,(String)data.get("dxcount") );
				statement.setString(24,(String)data.get("dtime") );
				statement.setString(25,(String)data.get("dptime") );
				statement.setString(26,(String)data.get("dwtime") );
				statement.addBatch();
				
			}
			
			statement.executeBatch();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
		
	}

	
	
	private void insertStatusLog(Connection connection) {
		
		PreparedStatement statement=null;
		
		try{
			statement=connection.prepareStatement(INSERT_STATUS_SQL);
			
			List<Map<String,Object>> bindlist=SessionStore.getInstance().getBindData();
			
			for(int i=0;i<bindlist.size();i++){
				
				Map<String,Object> data=bindlist.get(i);
				
				SortedMap<Integer, Integer> estatus=(SortedMap<Integer, Integer> )data.get("estatus");
				add(statement,(String)data.get("username"),(String)data.get("bindtype") ,"estatus",estatus);
				SortedMap<Integer, Integer> sstatus=(SortedMap<Integer, Integer> )data.get("sstatus");
				add(statement,(String)data.get("username"),(String)data.get("bindtype") ,"sstatus",sstatus);
				SortedMap<Integer, Integer> dstatus=(SortedMap<Integer, Integer> )data.get("dstatus");
				add(statement,(String)data.get("username"),(String)data.get("bindtype") ,"dstatus",dstatus);
				
			}
			
			statement.executeBatch();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
		
	}

	private void add(PreparedStatement statement,String username,String bindtype, String statustype, SortedMap<Integer, Integer> status) throws SQLException {
		
		Iterator<Integer> itr=status.keySet().iterator();
		
		while(itr.hasNext()){
			Integer statuscode=itr.next();
			Integer count=status.get(statuscode);
			statement.setString(1, username);
			statement.setString(2, bindtype);
			statement.setString(3, System.getenv("port"));
			statement.setString(4, statustype);
			statement.setString(5, statuscode.toString());
			statement.setString(6, count.toString());
			statement.addBatch();
		}
	}

	private long getUpTime(String uptime) {
		
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
