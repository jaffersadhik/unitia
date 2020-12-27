package simulatar.util;

import java.util.HashSet;
import java.util.Set;

public class MessageType {

	public static final String EM="EM";

	public static final String EF="EF";
	
	public static final String UM="UM";
	
	public static final String UF="UF";
	
	public static final String BM="BM";

	public static final String PEM="PEM";
	
	public static final String PUM="PUM";

	private static Set<String> UNICODE=new HashSet();
	static{
		
		UNICODE.add(UF);

		UNICODE.add(UM);

		UNICODE.add(PUM);
	}
	public static boolean isHexa(String msgtype){
		
		return UNICODE.contains(msgtype);
	}

}
