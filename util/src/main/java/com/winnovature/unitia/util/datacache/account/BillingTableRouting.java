package com.winnovature.unitia.util.datacache.account;

import java.sql.Connection;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class BillingTableRouting {

	private static final String BILLINGTABLE_ROUTING = "create table billingtable_routing(id INT PRIMARY KEY AUTO_INCREMENT,superadmin varchar(16),admin varchar(16),username varchar(16),submission_tablename varchar(50) ,delivery_tablename varchar(50),dnpost_tablename varchar(50),itime timestamp default CURRENT_TIMESTAMP)";

	private static BillingTableRouting obj=null;

	private BillingTableRouting(){
	
		init();
	}
	
	private void init() {
		
		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			loadbillingroutingtable(connection, table);


		} catch (Exception e) {

			obj=null;
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}

	public static BillingTableRouting getInstance(){
		
		if(obj==null){
			
			obj=new BillingTableRouting();
		}
		
		return obj;
	}
	
	private void loadbillingroutingtable(Connection connection, TableExsists table) {
		
		if (!table.isExsists(connection, table.getTableName(BILLINGTABLE_ROUTING))) {
			if (table.create(connection, BILLINGTABLE_ROUTING, false)) {

			
			}
		}
	}

	public void reload(){
		
	}
	
	
	
}
