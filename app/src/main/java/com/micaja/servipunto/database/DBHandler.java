/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database;

import android.database.sqlite.SQLiteDatabase;

import com.micaja.servipunto.utils.Utils;



public class DBHandler {
	
	private static Database db;
	
	
	public static SQLiteDatabase getDBObj(int value)
	{
		db	= new Database(Utils.context);
		
		if(value == 0)
		{
			return db.getReadableDatabase();
		}
		else
		{
			return db.getWritableDatabase();
		}
	}
}
