package queuecheck.testcase;

import java.util.HashSet;
import java.util.Set;

public class InterfaceAccount {

	private static InterfaceAccount obj=new InterfaceAccount();
	
	private Set<String> account=new HashSet<String>();
	
	private InterfaceAccount(){
		
		init();
	}
	
	private void init() {
		
		account.add("testcaseu001");
		account.add("testcaseu002");
		account.add("testcaseu003");
		account.add("testcaseu004");
		account.add("testcaseu005");
		account.add("testcaseu006");
		account.add("testcaseu007");
		account.add("testcaseu008");
		account.add("testcaseu009");
		account.add("testcaseu010");
		account.add("testcaseu011");
		account.add("testcaseu012");
		account.add("testcaseu013");
		account.add("testcaseu014");
		account.add("testcaseu015");
		account.add("testcaseu016");
		account.add("testcaseu017");

		
	}

	public static InterfaceAccount getInstance(){
		
		if(obj==null){
			
			obj=new InterfaceAccount();
		}
		
		return obj;
	}
	
	public Set<String> getAccount(){
		
		return account;
	}
}
