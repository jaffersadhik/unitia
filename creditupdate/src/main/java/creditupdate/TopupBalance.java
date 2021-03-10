 package creditupdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.misc.CreditProcessor;
import com.winnovature.unitia.util.misc.FileWrite;

public class TopupBalance extends Thread
 {

	 private static String BALANCEKEY = "prepaid:bal" ;

   private CreditDAO dao = new CreditDAO();
 
   public void run()
   {

	   while (true)
     {
       try
       {
         doProcess();
 
       }
       catch (Exception e)
       {

    	   e.printStackTrace();
       }

       gotosleep();
     }
   }
 

   private void doProcess() throws Exception {
	
	
	   CreditProcessor creditprocessor=new CreditProcessor();
	   List<Map<String, String>> result=dao.getCreditTopupInfo();
	   
	   if(result.size()>0){
		   
		   for(int i=0,max=result.size();i<max;i++){
			   
			   Map<String, String> data=result.get(i);
				Map<String,Object> logmap=new HashMap<String,Object>();
				logmap.put("logname", "topupcredit");
				logmap.putAll(data);
			   creditprocessor.returnCredit(data.get("username"), Double.parseDouble(data.get("topupcredit")));
			  
			   if(dao.updateTopupHistory(data.get("id"))<1){
					logmap.put("rollback", "yes");

				   creditprocessor.returnCredit(data.get("username"), (Double.parseDouble(data.get("topupcredit"))*-1));

			   }
			   
				new FileWrite().write(logmap);

		   }
	   }
   }


private void gotosleep() {
	
	   try{
		   Thread.sleep(100L);
	   }catch(Exception e){
	   }
}


 
 }
