package simulatar.server;


import simulatar.task.IdleSessionRemoverTask;

public class App 
{
 
    public static void doProcess() throws Exception
    {
    	
    	new T().start();

   
    	SmppServer.getInstance().start();
    	
		new IdleSessionRemoverTask();

		Runtime.getRuntime().addShutdownHook(new Thread(){
			
			public void run(){
				
				SmppServer.getInstance().stop();
		}
		});
     }

	    
}

