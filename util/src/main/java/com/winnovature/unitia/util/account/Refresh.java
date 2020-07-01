package com.winnovature.unitia.util.account;

import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.optin.OptinAccount;
import com.winnovature.unitia.util.optout.OptoutAccount;

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
		
		PushAccount.instance().reload();
		BillingTableRouting.getInstance().reload();
		OptinAccount.getInstance().reload();
		OptoutAccount.getInstance().reload();
		new DNDProcessoer().isDND("919487660738");
	}
	
}
