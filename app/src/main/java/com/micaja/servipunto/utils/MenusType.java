package com.micaja.servipunto.utils;

public enum MenusType 
{
	DIALY(0), 
	WEEKLY(1), 
	OCCASIONALLY(3);
	
	private int menuType;
	
	private MenusType(int menuType)
	{
		this.menuType	= menuType;
	}
	
	public int code()
	{
		return menuType;
	}
}
