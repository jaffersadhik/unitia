package queuecheck.testcase;

import java.util.HashMap;
import java.util.Map;

public class TestcaseDAO {

	private static TestcaseDAO obj=new TestcaseDAO();
	
	private Map<String,Map<String,String>> data=new HashMap<String,Map<String,String>>();
	
	private TestcaseDAO(){
		
	}
	
	public static TestcaseDAO getInstance(){
		
		if(obj==null){
			
			obj=new TestcaseDAO();
		}
		
		return obj;
	}
	
	public void reload(){
		
	}
	/*
	public Map<String,Map<String,String>> getData(){
		
		
	}
*/
}

