package reroutekannelselect;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.queue.kannelQueue;

public class T  extends Thread{


	public void run(){
		
		while(true){
			
			try{
				kannelQueue.getInstance().reload();
				Refresh.getInsatnce().reload();
				DBReceiver.GRACESTOP=ConfigParams.getInstance().getProperty(ConfigKey.GRACE_STOP).equals("1");
				PollerStartup.updateUsers();
				gotosleep();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(50L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}





