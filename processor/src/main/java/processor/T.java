package processor;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.kannelQueue;

import blacklist.MobileBlackList;
import blacklistsenderid.SenderidBlackList;
import blacklistsms.SMSPatternBlackList;
import dlt.Entity;
import numberingplan.NumberingPlan;
import routegroup.InternationalRoute;
import routegroup.MobileRouting;
import routegroup.Route;
import routegroup.SenderidRouting;
import senderidcheck.InternationalSenderidSwapping;
import senderidcheck.SenderidSwapping;
import senderidcheck.WhiteListedSenderid;
import spamfilter.SMSPatternFiltering;
import templatecheck.SMSPatternAllowed;




public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				PushAccount.instance().reload();
				Countrycode.getInstance().reload();
				NumberingPlan.getInstance().reload();
				MobileBlackList.getInstance().reload();
				SMSPatternBlackList.getInstance().reload();
				SMSPatternFiltering.getInstance().reload();
				SMSPatternAllowed.getInstance().reload();
				InternationalSenderidSwapping.getInstance().reload();
				WhiteListedSenderid.getInstance().reload();
				SenderidSwapping.getInstance().reload();
				SenderidBlackList.getInstance().reload();
				Entity.getInstance().reload();
				SenderidRouting.getInstance().reload();
				MobileRouting.getInstance().reload();
				Route.getInstance().reload();
				InternationalRoute.getInstance().reload();
				kannelQueue.getInstance().reload();
		
				gotosleep();
				

			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}

	

	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}





