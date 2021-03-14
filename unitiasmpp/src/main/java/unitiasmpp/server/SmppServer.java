package unitiasmpp.server;

import java.util.HashMap;
import java.util.Map;


import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerConfiguration;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

import unitiasmpp.event.handlers.SmppSessionBindUnbindHandler;

public class SmppServer {
	

	private String name = "SmppServer";
	private int port = 8080;
	// length of time to wait for a bind request
	private long bindTimeout = 30000;
	private int dnRequestTimeout=3000;
	private String systemId = "UNITIA GATEWAY";
	// if true, <= 3.3 for interface version normalizes to version 3.3
	// if true, >= 3.4 for interface version normalizes to version 3.4 and
	// optional sc_interface_version is set to 3.4
	private boolean autoNegotiateInterfaceVersion = true;
	// smpp version the server supports
	private double interfaceVersion = 3.4;
	// max number of connections/sessions this server will expect to handle
	// this number corrosponds to the number of worker threads handling reading
	// data from sockets and the thread things will be processed under
	private int maxConnectionSize = 500;

	private int defaultWindowSize = 10;
	private long defaultWindowWaitTimeout = SmppConstants.DEFAULT_WINDOW_WAIT_TIMEOUT;
	private long defaultRequestExpiryTimeout = SmppConstants.DEFAULT_REQUEST_EXPIRY_TIMEOUT;
	private long defaultWindowMonitorInterval = SmppConstants.DEFAULT_WINDOW_MONITOR_INTERVAL;
	private boolean defaultSessionCountersEnabled = true;	
	private long startUpTime=0;



	private SmppServer() {}
	// just to ensure the "Double Checked Locking" visit
	// "http://en.wikipedia.org/wiki/Singleton_pattern" for more details
	private static class SingletonHolder {
		public static final SmppServer INSTANCE = new SmppServer();
	}

	/**
	 * This method is used to get the reference of the engine
	 * @return SessionManager
	 */
	public static SmppServer getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private DefaultSmppServer defaultSmppServer = null;

	private SmppServerHandler smppServerHandler = null;

	
	public void setName(String name) {
		this.name = name;
	}

	protected String getName() {
		return name;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setBindTimeout(long bindTimeout) {
		this.bindTimeout = bindTimeout;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setAutoNegotiateInterfaceVersion(boolean autoNegotiateInterfaceVersion) {
		this.autoNegotiateInterfaceVersion = autoNegotiateInterfaceVersion;
	}

	public void setInterfaceVersion(double interfaceVersion) throws Exception {
		if (interfaceVersion != 3.4 && interfaceVersion != 3.3) {
			throw new Exception("Only SMPP version 3.4 or 3.3 is supported");
		}
		this.interfaceVersion = interfaceVersion;
	}

	public void setMaxConnectionSize(int maxConnectionSize) {
		this.maxConnectionSize = maxConnectionSize;
	}

	public void setDefaultWindowSize(int defaultWindowSize) {
		this.defaultWindowSize = defaultWindowSize;
	}

	public void setDefaultWindowWaitTimeout(long defaultWindowWaitTimeout) {
		this.defaultWindowWaitTimeout = defaultWindowWaitTimeout;
	}

	public void setDefaultRequestExpiryTimeout(long defaultRequestExpiryTimeout) {
		this.defaultRequestExpiryTimeout = defaultRequestExpiryTimeout;
	}

	public void setDefaultWindowMonitorInterval(long defaultWindowMonitorInterval) {
		this.defaultWindowMonitorInterval = defaultWindowMonitorInterval;
	}

	public void setDefaultSessionCountersEnabled(boolean defaultSessionCountersEnabled) {
		this.defaultSessionCountersEnabled = defaultSessionCountersEnabled;
	}

	public long getStartUpTime() {
		return startUpTime;
	}

	public void setStartUpTime(long startUpTime) {
		this.startUpTime = startUpTime;
	}
	
	public void start() throws SmppChannelException {

		setStartUpTime(System.currentTimeMillis());
		
		// create a server configuration
		SmppServerConfiguration configuration = new SmppServerConfiguration();
		configuration.setName(this.name);
		configuration.setPort(this.port);
		configuration.setBindTimeout(this.bindTimeout);
		configuration.setSystemId(this.systemId);
		configuration.setAutoNegotiateInterfaceVersion(this.autoNegotiateInterfaceVersion);
		
		if (this.interfaceVersion == 3.4) {
			configuration.setInterfaceVersion(SmppConstants.VERSION_3_4);
		} else if (this.interfaceVersion == 3.3) {
			configuration.setInterfaceVersion(SmppConstants.VERSION_3_3);
		}
		
		configuration.setMaxConnectionSize(maxConnectionSize);
		configuration.setNonBlockingSocketsEnabled(true);

		// SMPP Request sent would wait for 30000 milli seconds before throwing
		// exception
		configuration.setDefaultRequestExpiryTimeout(defaultRequestExpiryTimeout);
		configuration.setDefaultWindowMonitorInterval(dnRequestTimeout);
		
		// The "window" is the amount of unacknowledged requests that are
		// permitted to be outstanding/unacknowledged at any given time.
		configuration.setDefaultWindowSize(defaultWindowSize);

		// Set the amount of time to wait until a slot opens up in the
		// sendWindow.
		//configuration.setDefaultWindowWaitTimeout(this.defaultWindowWaitTimeout);
		configuration.setDefaultWindowWaitTimeout(defaultRequestExpiryTimeout*2);
		configuration.setDefaultSessionCountersEnabled(this.defaultSessionCountersEnabled);
		
		configuration.setJmxEnabled(false);
		DefaultSmppServerHandler serverHandler=new DefaultSmppServerHandler();
		serverHandler.setSmppSessionHandlerInterface(new SmppSessionBindUnbindHandler());
		this.smppServerHandler = serverHandler;
		this.defaultSmppServer = new DefaultSmppServer(configuration, serverHandler);
		this.defaultSmppServer.start();
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put(MapKeys.USERNAME, "system");
		logmap.put(MapKeys.PORT, ""+this.port);
		logmap.put(MapKeys.USERNAME, "system");
		logmap.put(MapKeys.STATUS_DESC, "Server Started");

		System.out.println(logmap);

		new FileWrite().write(logmap);
		System.out.println("Server Started");
	}

	public DefaultSmppServer getDefaultSmppServer() {
		return defaultSmppServer;
	}

	public void setDefaultSmppServer(DefaultSmppServer defaultSmppServer) {
		this.defaultSmppServer = defaultSmppServer;
	}

	public void stop() {
		this.defaultSmppServer.stop();
	}

	public void destroy() {

	}




	public DefaultSmppServerHandler getDefaultSmppServerHandler() {
		return (DefaultSmppServerHandler) smppServerHandler;
	}

}
