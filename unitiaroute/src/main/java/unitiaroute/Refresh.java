package unitiaroute;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.cdac.CDACSmscId;
import com.winnovature.unitia.util.duplicate.DuplicateCheck;
import com.winnovature.unitia.util.misc.Carrier;
import com.winnovature.unitia.util.misc.TeleMarketerId;

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
		
		InternalKannel.getInstance().reload();
		CDACSmscId.getInstance().reload();
		Carrier.getInstance().reload();
		TeleMarketerId.getInstance().reload();
		SMSPatternAllowedB.getInstance().reload();
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
		
		SMSPatternFiltering.getInstance().reload();
		WhiteListedSenderid.getInstance().reload();
		Entity.getInstance().reload();

	}
	
}
