package unitiasmpp.task;

import java.util.Timer;
import java.util.TimerTask;

import unitiasmpp.manager.SessionManager;

public class IdleSessionRemoverTask extends TimerTask {
	
	Timer timer=new Timer("IdleSessionRemoverTask-Thread");
	int inactiveInterval = 60000;
	
	public IdleSessionRemoverTask() {
		timer.scheduleAtFixedRate(this, inactiveInterval+1000, inactiveInterval+1000);
	}
	
	@Override
	public void run() {
		try {

			int removedCount=SessionManager.getInstance().removeExpiredSessions(inactiveInterval);
			

			
		} catch(Exception unexpected) {
			unexpected.printStackTrace();
		}
	}
	
}
