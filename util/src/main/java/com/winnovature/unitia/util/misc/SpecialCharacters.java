package com.winnovature.unitia.util.misc;


import java.util.ArrayList;
import java.util.List;

public class SpecialCharacters 
{
	/*
	 * The ClassName variable
	 */
	private static String className = "[SpecialCharacters]";
	
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
	private List loadSplCharacters() 
	{
		List _splCharList = new ArrayList();
		_splCharList.add("^");_splCharList.add("{");_splCharList.add("}");	_splCharList.add("\\");
		_splCharList.add("[");_splCharList.add("~");_splCharList.add("]");	_splCharList.add("|");	
		
	
		
		return _splCharList;
	}
	public static SpecialCharacters instance()
	{
		if(specialCharacters == null)
			specialCharacters = new SpecialCharacters();
		
		return specialCharacters;
		
	}

} // end of class CountryCode