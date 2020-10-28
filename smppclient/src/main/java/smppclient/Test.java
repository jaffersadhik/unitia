package smppclient;

import java.io.InputStream;
import java.io.OutputStream;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;

public class Test {

	public static void main(String args[]){
		
		SmppSessionConfiguration config=new SmppSessionConfiguration(SmppBindType.TRANSCEIVER, "unitia", "unitia");

		config.setHost("103.212.205.89");
		config.setPort(2775);
		Session session=new Session(config);
		
		session.start();
		
		
		Process process=new Process() {
			
			@Override
			public int waitFor() throws InterruptedException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public OutputStream getOutputStream() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getInputStream() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getErrorStream() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int exitValue() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public void destroy() {
				// TODO Auto-generated method stub
				
			}
		};
	}
}
