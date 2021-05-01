package reroutekannelinsert;

public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	

    	start2("reroute_kannel");
    	

     }



	private static void start2(String poolname) {
		
		
		for(int i=0;i<5;i++){
			

			String redisid=System.getenv("redis");

			
				
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
				new RedisReceiver(poolname,redisid).start();

			
			
		
		}
	}    

}

