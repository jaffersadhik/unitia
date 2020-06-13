package com.winnovature.unitia.util.datacache.routing;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author  venkatesh (venkatesh@air2web.co.in)
 * @version STDCode.java v1.0
 * 			Created: Jul 15, 2008 3:42:35 PM
 *			Last Modified Jul 15, 2008 3:42:35 PM by venkatesh
 *
 */
public class SpecialCharacters 
{
	/*
	 * The ClassName variable
	 */
	private static String className = "[SpecialCharacters]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static SpecialCharacters specialCharacters = new SpecialCharacters();
	
	/*
	 * The Memory holders
	 */
	private List splCharList = new ArrayList();
	
	/*
	 * The private Constructor
	 */
	private SpecialCharacters()
	{
		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the std codes when the object instantiate
	 */
	private synchronized void load() 
	{
		splCharList = loadSplCharacters();
	}
	
	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized  void reload()
	{
		List _tmpSplChar = loadSplCharacters();
		
		if(_tmpSplChar!=null&&_tmpSplChar.size()>0){
		splCharList = _tmpSplChar;
		}
		
		_tmpSplChar=null;
	}
	
	public List getSplCharacters() 
	{
		
		return splCharList;
	}

	/**
	 * 
	 * Method : loadCarrier
	 *       usage : load all the stdcode from the table to the memory.
	 */
	private List loadSplCharacters() 
	{
		List _splCharList = new ArrayList();
		_splCharList.add("^");_splCharList.add("{");_splCharList.add("}");	_splCharList.add("\\");
		_splCharList.add("[");_splCharList.add("~");_splCharList.add("]");	_splCharList.add("|");	
		
		if(logger.isDebugEnabled())
		logger.debug(className+"loadSplCharacters() _splCharList - " + _splCharList);
		
		return _splCharList;
	}

	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static SpecialCharacters instance()
	{
		if(specialCharacters == null)
			specialCharacters = new SpecialCharacters();
		
		return specialCharacters;
		
	}

} // end of class CountryCode