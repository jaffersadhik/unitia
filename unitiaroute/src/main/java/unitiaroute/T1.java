package unitiaroute;

import org.apache.commons.lang.StringUtils;

public class T1 {

public  boolean isMatch(String template,String fullmsg){
		
		String temp[]=StringUtils.split(template);
		String msg[]=StringUtils.split(fullmsg);

		int msgpointer=0;

		for(int i=0;i<temp.length;i++){
			
			if(msgpointer>=msg.length){
				
				return  false;
			}
			String m=msg[msgpointer];
			String t=temp[i];
			if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
				int lastTempPointer=getLastTempPointer(temp,i);
				int maxVarCharCount=getMaxVarcharCount(lastTempPointer,temp);
				int upcomingmsgpointer=getUpcomingMsgPointer(lastTempPointer,temp,msg,msgpointer);
				String varchar=getVarcharCharacter(msg,msgpointer,upcomingmsgpointer);
				int varcharcount=varchar.length();
/*
				System.out.println(" i "+ i);
				System.out.println(" t "+ t);

				System.out.println(" lastTempPointer "+ lastTempPointer);
				System.out.println(" maxVarCharCount "+ maxVarCharCount);
				System.out.println(" upcomingmsgpointer "+ upcomingmsgpointer);
				System.out.println(" varcharcount "+ varcharcount);
				System.out.println(" varchar "+ varchar);
				System.out.println(" msgpointer "+ msgpointer);
				System.out.println(" msg.length "+ msg.length);
				System.out.println(" temp.length "+ temp.length);
*/
				if(lastTempPointer==i&& t.indexOf("{#var#}")>-1){
					String	prefixingcharacter=getPrefixingCharacter(t);
		//			System.out.println(" prefixingcharacter "+ prefixingcharacter);

					int prefixingcount=prefixingcharacter.length();
					maxVarCharCount=maxVarCharCount+prefixingcount;
					
					if(!varchar.startsWith(prefixingcharacter)){
						
						return false;
					}
					
					String	suffixingcharacter=getSufffixingCharacter(t);
					
			//		System.out.println(" suffixingcharacter "+ suffixingcharacter);

					int suffixingcount=suffixingcharacter.length();
					
					maxVarCharCount=maxVarCharCount+suffixingcount;

					
					if(!varchar.endsWith(suffixingcharacter)){
						
						return false;
					}
				}
		
		
				if(maxVarCharCount>varcharcount){
					msgpointer=upcomingmsgpointer;
				}else{
					return false;
				}
			}else if(! m.equalsIgnoreCase(t)){
				return false;
			}else{
				msgpointer++;
			}

			
		
		} 
/*
*/
		if(msg.length==temp.length){
			
			return true;
			
		}else if(msg.length>temp.length){
			
			
			String t=temp[temp.length-1];
			if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			
				if(msgpointer==msg.length-1){
					
					return true;

				
				
				}else{
					
					return false;
				}
			}else{
		
			if(msgpointer==msg.length){
		
				return true;

			
			
			}else{
				
				return false;
			}
			
			}
		}else{
			
			return true;
		}
		
}

private String getSufffixingCharacter(String t) {
	int i=t.lastIndexOf("{#var#}");
	if(i<t.length()){
	
		i=i+7;
		if(i<t.length()){
		return t.substring(i,t.length());
	
		}
	}
	return "";
}

private String getPrefixingCharacter(String t) {
	
	int i=t.indexOf("{#var#}");
	if(i>0){
	
		return t.substring(0,i);
	}
	return "";
}

private String getVarcharCharacter(String[] msg, int msgpointer, int upcomingmsgpointer) {
	
	StringBuffer sb=new StringBuffer();
	if(msgpointer==upcomingmsgpointer){
		sb.append(msg[msgpointer]);
	}else{
	for(int i=msgpointer;i<upcomingmsgpointer;i++){
		
		sb.append(msg[i]).append(" ");
		
	}
	}
	return sb.toString().trim();
}

private int getUpcomingMsgPointer(int lastTempPointer, String[] temp, String[] msg, int msgpointer) {
	
	int temppointer=lastTempPointer;
	
	
	temppointer++;
	
	if(temppointer==temp.length){
		
		return msg.length-1;
	}
	
	if(temppointer<temp.length){
		
		String t=temp[temppointer];
		
		for(int i=msgpointer;i<msg.length;i++){
			
			String m=msg[i];
			
			if(m.equalsIgnoreCase(t)){
				
				return i;
			}
		}
	}
		
		return msg.length-1;
	
	
}

private int getMaxVarcharCount(int lastTempPointer, String[] temp) {

	int pointer=lastTempPointer;
	int count=getCount(temp[pointer]);
	while(pointer>-1){
		
		pointer--;
		
		if(pointer==-1){
			
			return (count*30)+1;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count=count+getCount(t);
		}else{
			return (count*30)+1;
		}
	}

	return (count*30)+1;

	
}

private int getCount(String keyword) {
	String var="{#var#}";
	int count=0;
	while(keyword!=null&&keyword.length()>0){
		
		int index=keyword.indexOf(var);
		if(index > -1){
		keyword=keyword.substring(index+7);
		count++;
		}else{
			
			break;
		}
	}
	
	return count;
}

private int getLastTempPointer(String[] temp, int i) {

	int pointer=i;
	int count=0;
	while(pointer<temp.length){
		
		pointer++;
		
		if(pointer==temp.length){
			
			return i+count;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count++;
		}else{
			return count+i;
		}
	}

	return count+i;
}


}
