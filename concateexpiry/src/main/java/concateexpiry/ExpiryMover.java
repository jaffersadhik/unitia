package concateexpiry;

import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;

public class ExpiryMover extends Thread {

	public void run(){
		
		
		while(true){
			
			
			doProcess();
			
			gotosleep();
		}
	}

	private void gotosleep() {
	try{
			Thread.sleep(5*60*1000);
		}catch(Exception e){
			
		}
		
	}

	private void doProcess() {
	
		try{
			
			getDataAndInsert();
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
	}

	

	private void getDataAndInsert() {
		Select select=new Select();

		while(true){
		List<Map<String, Object>> datalist=select.getDataAsExpired("concatedata");
		
		if(datalist!=null){
			
			for(int i=0;i<datalist.size();i++){
				
				Map<String, Object> data=datalist.get(i);
				
				data.put(MapKeys.STATUSID, ""+MessageStatus.CONCATE_EXPIRED);
			}
			
			if(datalist.size()>0){
				
				untilPersist(datalist);
				
				deleteUntilSuccess(datalist);
			}else{
				
				return;
			}
		}else{
			
			return;
		}

		}
	}
	
	
private void deleteUntilSuccess(List<Map<String, Object>> data) {

	Select select=new Select();

		
		while(true){
			
			if(select.delete( "concatedata",data,false)){
				
				return;
			}
			
			gotosleep();
		}
		
	}
	
	private void untilPersist(List<Map<String, Object>> datalist) {


		if(datalist==null || datalist.size()<1){
			
			return;
		}
		while(true){
			
			if(new ReportDAO().insert("reportlog_submit",datalist)){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
		
	}
}
