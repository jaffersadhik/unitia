package concateexpiry;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	
    	new TConcateUpdate().start();

    	new ExpiryMover().start();

     }


}

