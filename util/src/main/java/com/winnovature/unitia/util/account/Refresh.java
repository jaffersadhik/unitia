package com.winnovature.unitia.util.account;

import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.dngen.ErrorCodeType;
import com.winnovature.unitia.util.misc.Carrier;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.optin.OptinAccount;
import com.winnovature.unitia.util.optin.OptoutAccount;
import com.winnovature.unitia.util.processor.DNSuccessMaskingProcessor;

public class Refresh {

	private static Refresh obj=null;
	
	private long lastupdate=System.currentTimeMillis();
	private Refresh(){
		
	}
	
	public static Refresh getInsatnce(){
		
		if(obj==null){
			
			obj=new Refresh();
		}
		
		return obj;
	}
	
	public void reload(){
		
		PushAccount.instance().reload();
		Carrier.getInstance().reload();
		OptinAccount.getInstance().reload();
		OptoutAccount.getInstance().reload();
		new DNDProcessoer().isDND("919487660738");
		DNSuccessMasking.getInstance().reload();
		LatencySlap.getInstance().reload();
		
		if((System.currentTimeMillis()-lastupdate)>1*60*60*1000){
		
			lastupdate=System.currentTimeMillis();
			
			LatencySlap.getInstance().resetHistory();
			
			DNSuccessMaskingProcessor.getInstance().resetHistory();
		}
		ConfigParams.getInstance().reload();
		WhiteListedIP.getInstance().reload();
		ErrorCodeType.getInstance().reload();
		MessageStatus.getInstance().reload();
	}
	
}
