package unitiaroute;

import java.util.Map;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.misc.MapKeys;

public class SenderidMasking {

		
	public  void doSenderIDMask(Map<String,Object> msgmap){

		String senderid=msgmap.get(MapKeys.SENDERID).toString();
		
		String smscid=(String)msgmap.get(MapKeys.SMSCID);
		
		if(smscid==null){
			
			return;
		}

		String senderidmask=null;
		
		if(msgmap.get(MapKeys.COUNTRYCODE).toString().equals("91")){
			
			for(int i=1;i<9;i++){
				
				String key=getKey(msgmap.get(MapKeys.SMSCID).toString(),msgmap.get(MapKeys.OPERATOR).toString(),msgmap.get(MapKeys.CIRCLE).toString(),i);
				
				senderidmask=SenderidSwapping.getInstance().getSwapingSenderid(key, senderid);
			
				msgmap.put("masked senderid key", key);

				msgmap.put("masked senderid", senderidmask);
				
				if(senderidmask!=null){
					
					break;
				}
				
				}
			
		}else{
		
			senderidmask=InternationalSenderidSwapping.getInstance().getSwapingSenderid(msgmap.get(MapKeys.COUNTRYCODE).toString());
			
		}
		
		
		if(senderidmask==null||senderidmask.trim().length()<1){
			
			senderidmask=senderid;
		}

	}

	
	
	private String getKey(String smscid,String operator, String circle, int logic) {
		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+smscid+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+smscid+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+smscid+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+smscid+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 5:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 6:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 7:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 8:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
			
		}
		
		return "";
	}

}
