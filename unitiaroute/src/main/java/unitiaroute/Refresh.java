package unitiaroute;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.duplicate.DuplicateCheck;

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
		Kannel.getInstance().reload();
		MobileRouting.getInstance().reload();
		ReRouting.getInstance().reload();
		RouteGroup.getInstance().reload();
		Route.getInstance().reload();
		InternationalRoute.getInstance().reload();
		SenderidSwapScheduling.getInstance().reload();
		SenderidRouting.getInstance().reload();
		Entity.getInstance().reload();

	}
	
}
