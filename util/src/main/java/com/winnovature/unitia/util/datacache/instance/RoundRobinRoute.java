package com.winnovature.unitia.util.datacache.instance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class RoundRobinRoute {
	
/*	private static RoundRobinRoute object= new RoundRobinRoute();
	
	private ArrayList sdmsgrouterList =new ArrayList();
	
	private ArrayList requestrouterList =new ArrayList();
	
	private ArrayList iciciqsrouterList =new ArrayList();
	
	private ArrayList icicixmlrouterList =new ArrayList();
	
	
	
	private static int icicixmlIncreamentor = 0;
	
	private static int iciciqsIncreamentor = 0;
	
	private static int requestrouterIncreamentor = 0;
	
	private static int sdmsgrouterIncreamentor = 0;
	*/
	/**
	 * class name for logging 
	 */
	private static String className = "[com.a2wi.ng.util.datacache.instance.RoundRobinRoute] ";
	
	 /**
	 * A2wi Logger instance
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 *	A2WIProperties instance.
	 */
/*	PropertiesConfiguration pc = null;
	
	private String instanceid = null;
	
	private Map allMap = new HashMap();
	
	private RoundRobinRoute(){
		
		try {			
			pc = PropertyFileLoaderTon.getInstance().getPropertiesConfiguration(Constants.A2WI_UTILS_NG_KEY);
			
		} catch (Exception e) {
			logger.error(className + " Not able to load properties file value",e);
		}
		
		load();
		
	}
	public static RoundRobinRoute instance(){
		if(object==null)
			object=new RoundRobinRoute();
		return object;
	}
	
	public synchronized void load() {
		
		allMap=loadInstanceRoute();
		
	}
	
	public synchronized void reload() {
		Map _tmpAllMap = loadInstanceRoute();
		allMap = _tmpAllMap;
		_tmpAllMap=null;
	}
	*/
	/**
	 * Schedule Delivery Msg Request Router Insatce Id
	 * @return
	 */
/*	public String getSdMsgRouterInstanceId(){
		//logger.debug(className + "getSdMsgRouterInstanceId()");
		sdmsgrouterList = (ArrayList) allMap.get("SDMSGROUTER");
		
		if(sdmsgrouterList==null)
			reload();

		if (sdmsgrouterList.size() > 0){
			if (sdmsgrouterIncreamentor >= sdmsgrouterList.size()) sdmsgrouterIncreamentor = 0;
		}else	return null;

		return sdmsgrouterList.get(sdmsgrouterIncreamentor++).toString();
	}*/
	/**
	 * 
	 * @return Request Router Insatce Id
	 */
	/*public String getRequestRouterInstanceId(){
		//logger.debug(className + "getRequestRouterInstanceId()");
		
		requestrouterList = (ArrayList) allMap.get("REQROUTER");
		
		if(requestrouterList==null )
			reload();

		if (requestrouterList.size() > 0){
			if (requestrouterIncreamentor >= requestrouterList.size()) requestrouterIncreamentor = 0;
		}else	return null;

		return requestrouterList.get(requestrouterIncreamentor++).toString();
	}*/
	/**
	 * 
	 * @return icici QS Request Router Insatce Id
	 */
/*	public String getICICIQSRouterInstanceId(){
		//logger.debug(className + "getICICIQSRouterInstanceId()");
		iciciqsrouterList = (ArrayList) allMap.get("ICICIQS");
		
		if(iciciqsrouterList==null)
			reload();

		if (iciciqsrouterList.size() > 0){
			if (iciciqsIncreamentor >= iciciqsrouterList.size()) iciciqsIncreamentor = 0;
		}else	return null;

		return iciciqsrouterList.get(iciciqsIncreamentor++).toString();
	}*/
	/**
	 * 
	 * @return icici XML Request Router Insatce Id
	 */
	/*public String getICICIXMLRouterInstanceId(){
		//logger.debug(className + "getICICIXMLRouterInstanceId()");
		
		icicixmlrouterList = (ArrayList) allMap.get("ICICIXML");
		
		if(icicixmlrouterList==null)
			reload();

		if (icicixmlrouterList.size() > 0){
			if (icicixmlIncreamentor >= icicixmlrouterList.size()) icicixmlIncreamentor = 0;
		}else	return null;

		return icicixmlrouterList.get(icicixmlIncreamentor++).toString();
	}
	*/
	
/*	public Map loadInstanceRoute()
	{
		logger.debug(className + "loadInstanceRoute()");
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String instanceId=null;
		Map _allMap = new HashMap();
		
		
		try {
			
			//instanceId=A2WIProperties.instance().getProperty(Constants.A2WI_UTILS_NG_KEY,PropertyFileKeys.PF_INSTANCE_ID);
			String sql = "SELECT CONNECTORID,COMPONENT_TYPE FROM INSTANCE_ROUTE where INSTANCEID='"+pc.getString(PropertyFileKeys.PF_INSTANCE_ID)+"' and COMPONENT_TYPE in ('"+ComponentType.REQUEST_ROUTER+"','"+ComponentType.ICICI_QS_REQ_ROUTER+"','"+ComponentType.ICICI_XML_REQ_ROUTER+"','"+ComponentType.SD_MSG_ROUTER+"')";

			connection  = ThinOracleConnectionFactory.instance().getConnection();
			statement = connection.prepareStatement(sql);			
			resultSet = statement.executeQuery();
			
			
			while(resultSet.next()) {
				String connectorId	=	resultSet.getString("CONNECTORID");
				String componentType=	resultSet.getString("COMPONENT_TYPE");				
				if(componentType.equalsIgnoreCase(ComponentType.ICICI_QS_REQ_ROUTER)){
					iciciqsrouterList.add(connectorId);
					
				}else if(componentType.equalsIgnoreCase(ComponentType.ICICI_XML_REQ_ROUTER)){
					icicixmlrouterList.add(connectorId);
					
				}else if(componentType.equalsIgnoreCase(ComponentType.SD_MSG_ROUTER)){
					sdmsgrouterList.add(connectorId);
				}else if(componentType.equalsIgnoreCase(ComponentType.REQUEST_ROUTER)){
					requestrouterList.add(connectorId);
				}
			}
			
			logger.debug(className + "loadInstanceRoute() iciciqsrouterList="+iciciqsrouterList);
			logger.debug(className + "loadInstanceRoute() icicixmlrouterList="+icicixmlrouterList);
			logger.debug(className + "loadInstanceRoute() sdmsgrouterList="+sdmsgrouterList);
			logger.debug(className + "loadInstanceRoute() requestrouterList="+requestrouterList);
			
			
			_allMap.put("ICICIQS", iciciqsrouterList);
			_allMap.put("ICICIXML", icicixmlrouterList);
			_allMap.put("SDMSGROUTER", sdmsgrouterList);
			_allMap.put("REQROUTER", requestrouterList);
			
			

		} catch(Exception e) {
			logger.error(className + "loadInstanceRoute(); Not able to load from table INSTANCE_ROUTE", e);
			new ERRLogger().insert(e, 0 , "", "DATACAHCE - INSTANCE ROUTE",pc.getString(PropertyFileKeys.PF_INSTANCE_ID));
		} finally {
			try {
				if(resultSet != null)	resultSet.close();
				if(statement != null)	statement.close();
				if(connection != null)	connection.close();
			} catch(Exception ignore) {
				}
			}
		
		return _allMap;
	}
	*/

}