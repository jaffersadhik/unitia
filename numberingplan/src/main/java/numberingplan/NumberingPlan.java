package numberingplan;


import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Prop;



public class NumberingPlan
{
    private static NumberingPlan    obj        = null;
    
    private  Map<String,String> operator=new HashMap<String,String>();

    private  Map<String,String> circle=new HashMap<String,String>();

    private  Map<String,Map<String,String>> numberingplan=new HashMap<String,Map<String,String>>();
    
   private NumberingPlan()
    {
        
    
       addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "circle")) {
			addMaster(connection,table);
			}
			
			loadData(connection, table);
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}finally {
			
			Close.close(connection);
		}
		
	}

	private void addMaster(Connection connection,TableExsists table) {

				
			loadCircle(connection,table);
	    	loadOperator(connection,table);
	    	loadnumberingplan(connection,table);

			
	}







	private void loadData(Connection connection,TableExsists table) {

		reloadnumberingplan(connection,table);
    
	}
	
	private void reloadnumberingplan(Connection connection, TableExsists table) {



		Map<String, Map<String, String>> temp_nnp=table.getNP(connection);
		if(temp_nnp!=null) {
			numberingplan=temp_nnp;
		}
		
		Map<String, String> temp_operator=table.getOperator(connection);
		if(temp_operator!=null) {
			operator=temp_operator;
		}
		
		
		Map<String, String> temp_circle=table.getCircle(connection);
		if(temp_circle!=null) {
			circle=temp_circle;
		}
	
		
	}

	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}

	private void loadnumberingplan(Connection connection,TableExsists table ) {
		
		
	
		if(table.create(connection, SQLQuery.CREATE_NUMBERINGPLAN_TABLE, false)) {
		load9Series(connection,table);
    	load8Series(connection,table);
    	load7Series(connection,table);
    	load6Series(connection,table);		
		}

	}

	private void load6Series(Connection connection, TableExsists table) {

		List<String> data=Prop.getInstance().get6series();
		
		add(connection,table,data);

			
	
	}

	public static NumberingPlan getInstance() {
        
        if (obj == null)
        {
            
            obj = new NumberingPlan();
        }
        
        return obj;
    }
    
   

    
    
	


	private void load7Series(Connection connection, TableExsists table) {

		


		List<String> data=Prop.getInstance().get7series();
		
		add(connection, table, data);		
	}


	
	private void load9Series(Connection connection, TableExsists table) {

		


		List<String> data=Prop.getInstance().get9Series();
		
		add(connection,table,data);

	
			
	}


	private void load8Series(Connection connection, TableExsists table) {

		List<String> data=Prop.getInstance().get8series();
		
		add(connection,table,data);
		}
	private void add(Connection connection, TableExsists table,List<String> data) {

		for(int i=0;i<data.size();i++) {
			try {
			String str=data.get(i);
			
			StringTokenizer st=new StringTokenizer(str,"~");
			
			String series=st.nextToken().toString().trim();
			String operator="";
			try {
				operator=st.nextToken().toString().trim();
			}catch(Exception ignore) {
				
			}
			
			String circle="";
			try {
				circle=st.nextToken().toString().trim();
			}catch(Exception ignore) {
				
			}
					
			table.insertNumberingPlan(connection, series, operator, circle);
			}catch(Exception e) {
				
			}
		}
	}

	private  void loadOperator(Connection connection, TableExsists table) {
		

		if(table.create(connection, SQLQuery.CREATE_OPERATOR_TABLE, false)) {

		List<String> data=Prop.getInstance().getOperator();
		
		
		
		for(int i=0;i<data.size();i++) {
			try {
			String str=data.get(i);
			
			StringTokenizer st=new StringTokenizer(str,"~");
			
			String code=st.nextToken().toString().trim();
			String network=st.nextToken().toString().trim();
			
			table.insertOperator(connection, code, network);
			}catch(Exception e) {
				
			}
		}
	
		}
	}


	private  void loadCircle(Connection connection,TableExsists table) {

				
		if(table.create(connection, SQLQuery.CREATE_CIRCLE_TABLE, false)) {
		
			List<String> data=Prop.getInstance().getCircle();
			
			
			for(int i=0;i<data.size();i++) {
				try {
				String str=data.get(i);
				
				StringTokenizer st=new StringTokenizer(str,"~");
				String name=st.nextToken().toString().trim();
				String code=st.nextToken().toString().trim();
				String category=st.nextToken().toString().trim();
				String coverd_area=st.nextToken().toString().trim();
				table.insertCircle(connection, code, name, category, coverd_area);
			}catch(Exception e) {
				
			}
		}
			
	}
		
		

}

	public Map<String,Map<String, Map<String, String>>> getNumberingplantoString() {
		Map<String,Map<String, Map<String, String>>> result=new HashMap<String,Map<String, Map<String, String>>>();
		result.put("numberingplan", numberingplan);
		return result;
	}
	public Map<String, String> getNPInfo(String series) {
		
		return numberingplan.get(series);
	}

	public String getCircleName(String code){
		
		return circle.get(code);
	}
	
	public String getOperatorName(String code){
		
		return operator.get(code);
		
	}
}
