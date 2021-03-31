package unitiaroute;

import org.apache.commons.lang.StringUtils;

public class T1 {

	public static void main(String args[]){
		
		System.out.println("======true=========");
		T1 obj=new T1();

	}
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
				int varcharcount=getVarcharCount(msg,msgpointer,upcomingmsgpointer);
			/*
				System.out.println(" lastTempPointer "+ lastTempPointer);
				System.out.println(" maxVarCharCount "+ maxVarCharCount);
				System.out.println(" upcomingmsgpointer "+ upcomingmsgpointer);
				System.out.println(" varcharcount "+ varcharcount);
*/
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
		System.out.println(" msgpointer "+ msgpointer);
		System.out.println(" msg.length "+ msg.length);
		System.out.println(" temp.length "+ temp.length);
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

private int getVarcharCount(String[] msg, int msgpointer, int upcomingmsgpointer) {
	
	StringBuffer sb=new StringBuffer();
	for(int i=msgpointer;i<=upcomingmsgpointer;i++){
		
		sb.append(msg[i]).append(" ");
		
	}
	
	return sb.toString().trim().length();
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
