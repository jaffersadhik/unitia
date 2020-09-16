package kannelconfig;
  
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.redis.RedisCreditPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
  
  public class SyncBal extends Thread
  {
      
      private static String BALANCEKEY = "prepaid:bal";

	  private CreditDAO dao = new CreditDAO();
  
    public void run()
    {
     
    	while (true)
      {
    	try
        {
    		doProcess();
  
    	
        }
        catch (Exception e)
        {
        	e.printStackTrace();
          
        }
    	
    	gotosleep();
      }
    }
  
    private void gotosleep() {
		
    	try{
    		Thread.sleep(100L);
    	}catch(Exception e){
    		
    	}
		
	}

	private void doProcess()
      throws Exception
    {

		dao.updateBalanceCount( getInCacheBalanceForCreditAccount() );
    }
  
  
    private Map<String, String> getInCacheBalanceForCreditAccount() throws Exception
    {
    	JedisPool pool = null;
    	Jedis jedis = null;
    	Map cacheBalMap = new HashMap();
      try
      {
  
  
       pool = RedisCreditPool.getInstance().getPool();
       jedis = pool.getResource();

       if(isKeyExsists(jedis)){
  
       cacheBalMap = jedis.hgetAll(BALANCEKEY);
       }
       
      }
      catch (Exception e)
      {
 
      throw e;
      }
      finally
      {
    	  if (jedis != null) {
          try
          {
           pool.returnResource(jedis);
          }
          catch (Exception e)
          {
          }
        }
      }
      return cacheBalMap;
    }

	private boolean isKeyExsists(Jedis jedis) {
		
		return jedis.exists(BALANCEKEY);
	}
  
  
  
  
  }