package com.winnovature.unitia.util.threadpool;


import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;


public class ThreadPool 
{
	ThreadPoolExecutor executor = null;

	String name=null;
	int poolSize;
	int maxPoolSize;
	long keepAliveTime;
	int queueSize;
	ArrayBlockingQueue q;
	

	public ThreadPool(String name,Map<String,String> config) {
		
		
		
		this.name=name;
		this.poolSize=Integer.parseInt(config.get("poolsize"));
		
	
		this.maxPoolSize=Integer.parseInt(config.get("maxpoolsize"));
		this.keepAliveTime=Integer.parseInt(config.get("keepalivetime"));
		this.queueSize=Integer.parseInt(config.get("queuesize"));;
		
		if (queueSize < 1)
			queueSize = 1;

		q = new ArrayBlockingQueue(queueSize);

		executor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,	TimeUnit.SECONDS, q);

		/* Set RejectedExecutionHandler */
		executor.setRejectedExecutionHandler(new MyRejectedExecutionHandler());
	}

	public void runTask(Runnable task) {
		/* Hand over the task to thread pool */
		
		try
		{
			executor.execute(task);
					
			MyRejectedExecutionHandler rejHandler = (MyRejectedExecutionHandler) executor.getRejectedExecutionHandler();
					
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void resetConfig(Map<String,String> config){
		
		int poolsize=Integer.parseInt(config.get("poolsize"));
		
		setCorePoolSize(poolsize);
		setMaxPoolSize(Integer.parseInt(config.get("maxpoolsize")));
		
	}
	public String getCorePoolSize()
	{
		return "" + executor.getCorePoolSize();
	}

	public String getCurrentPoolSize()
	{
		return "" + executor.getPoolSize();
	}

	public String getActiveThreadCount()
	{
		return "" + executor.getActiveCount();
	}
	
	public String getCompletedTaskCount()
	{
		return "" + executor.getCompletedTaskCount();
	}

	public String getMaxPoolSize()
	{
		return "" + executor.getMaximumPoolSize();
	}

	public String getLargestPoolSize()
	{
		return "" + executor.getLargestPoolSize();
	}

	public boolean isAvailable(){
		
		return  executor.getQueue().size()<executor.getMaximumPoolSize();
	}
	public String getQSize()
	{		
		return "" + executor.getQueue().size();
	}

	public String getRemainingCapacity()
	{		
		return "" + executor.getQueue().remainingCapacity();
	}

	public boolean isTerminating()
	{
		return executor.isTerminating();
	}

	public boolean isTerminated()
	{
		return executor.isTerminated();
	}

	public boolean isShutdown()
	{
		return executor.isShutdown();
	}

	public boolean setCorePoolSize(int size)
	{
		try
		{
			executor.setCorePoolSize(size);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	public boolean setMaxPoolSize(int size)
	{
		try
		{
			executor.setMaximumPoolSize(size);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	public void shutdown()
	{
		executor.shutdown();
	}


}

class MyRejectedExecutionHandler implements RejectedExecutionHandler 
{

	public void rejectedExecution(Runnable worker, ThreadPoolExecutor executor)
	{
		
		try
		{
			
			if(worker instanceof SMSWorker){
				
				SMSWorker obj=(SMSWorker)worker;
				
				String poolname=obj.getPoolname();
				
				Map<String,String> message=obj.getPayload();
				
				new QueueSender().sendR(poolname, message, false);
				
			}else if(worker instanceof ScheduleWorker){
				
			}
			
			
		}
		catch (Exception e) 
		{
			
		}

		
	}
	

}
