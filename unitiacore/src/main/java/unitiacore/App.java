package unitiacore;

import unitiacore.threadpool.ThreadPoolTon;

public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	new T().start();

    	ThreadPoolTon.getInstance();
     }
    
}

