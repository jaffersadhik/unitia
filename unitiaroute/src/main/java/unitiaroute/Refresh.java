package unitiaroute;

import com.winnovature.unitia.util.db.DuplicateCheck;

public class Refresh {

	private static Refresh obj=null;
	
	private Refresh(){
		
	
	}
	
	public static Refresh getInsatnce(){
		
		if(obj==null){
			
			obj=new Refresh();
		}
		
		return obj;
	}
	
	public void reload(){
		
		new DuplicateCheck().flushDuplicate();
		Countrycode.getInstance().reload();
		Kannel.getInstance().reload();
		MobileBlackList.getInstance().reload();
		MobileRouting.getInstance().reload();
		NumberingPlan.getInstance().reload();
		ReRouting.getInstance().reload();
		RouteGroup.getInstance().reload();
		Route.getInstance().reload();
		InternationalRoute.getInstance().reload();
		InternationalSenderidSwapping.getInstance().reload();
		WhiteListedSenderid.getInstance().reload();
		SenderidSwapScheduling.getInstance().reload();
		SenderidRouting.getInstance().reload();
		SenderidSwapping.getInstance().reload();
		SenderidBlackList.getInstance().reload();
		SMSPatternBlackList.getInstance().reload();
		SMSPatternAllowed.getInstance().reload();
		SMSPatternFiltering.getInstance().reload();
		WhiteListedSenderid.getInstance().reload();
		Entity.getInstance().reload();

	}
	
}
