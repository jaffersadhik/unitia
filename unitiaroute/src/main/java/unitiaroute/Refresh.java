package unitiaroute;

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
		
		Kannel.getInstance().reload();
		ReRouting.getInstance().reload();
		SenderidSwapScheduling.getInstance().reload();
		RouteGroup.getInstance().reload();

	}
	
}
