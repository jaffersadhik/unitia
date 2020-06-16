package com.winnovature.unitia.util.datacache.account;

import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.mobileblacklist.MobileBlackList;
import com.winnovature.unitia.util.multiplesenderid.WhiteListedSenderid;
import com.winnovature.unitia.util.optin.OptinAccount;
import com.winnovature.unitia.util.optout.OptoutAccount;
import com.winnovature.unitia.util.senderidblacklist.SenderidBlackList;
import com.winnovature.unitia.util.smspatternallowed.SMSPatternAllowed;
import com.winnovature.unitia.util.smspatternblacklist.SMSPatternBlackList;
import com.winnovature.unitia.util.smspatternfiltering.SMSPatternFiltering;

public class Refresh {

	private static Refresh obj=null;
	
	private Refresh(){
		
		new T().start();
	}
	
	public static Refresh getInsatnce(){
		
		if(obj==null){
			
			obj=new Refresh();
		}
		
		return obj;
	}
	
	public void reload(){
		
		PushAccount.instance().reload();
		BillingTableRouting.getInstance().reload();
		MobileBlackList.getInstance().reload();
		SenderidBlackList.getInstance().reload();
		SMSPatternBlackList.getInstance().reload();
		SMSPatternAllowed.getInstance().reload();
		SMSPatternFiltering.getInstance().reload();
		OptinAccount.getInstance().reload();
		OptoutAccount.getInstance().reload();
		new DNDProcessoer().isDND("919487660738");
		WhiteListedSenderid.getInstance().reload();
	}
	
	class T extends Thread{
		
		public void run(){
			
			while(true){
				
				Refresh.getInsatnce().reload();
			
				gotosleep();
			}
		}

		private void gotosleep() {
			
			try{
				
				Thread.sleep(20L);
			}catch(Exception e){
				
			}
		}
	}
}
