package smpp2;

import java.util.ArrayList;
import java.util.List;

public class TableList {

	private static TableList obj=new TableList();
	
	List<String> tlist=new ArrayList<String>();

	private TableList(){
		
		init();
	}
	
	public static TableList getInstance(){
		
		if(obj==null){
			
			obj=new TableList();
		}
		
		return obj;
	}
	
	private void init(){
		
		tlist.add("t1");
		tlist.add("t2");
		tlist.add("t3");
		tlist.add("t4");
	}
	
	public List<String> getList(){
		
		return tlist;
	}
}
