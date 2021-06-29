package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserModuleIdDTO;

public class UserModuleIDDAO implements DAO
{

	private static UserModuleIDDAO userDAO;

	private UserModuleIDDAO() {

	}

	public static UserModuleIDDAO getInstance() 
	{
		if (userDAO == null)
			userDAO = new UserModuleIDDAO();

		return userDAO;
	}
	
	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO UserModules(moduleName,moduleCode)VALUES (?,?)");			
			int count = 1;
			for (DTO items : list) 
			{
				UserModuleIdDTO dto = (UserModuleIdDTO) items;
				
				stmt.bindString(count++, dto.getModuleName());
				stmt.bindString(count++, dto.getModuleCode());

				count = 1;
				
				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("UserModuleIDDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO dtoObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dtoObj) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM UserModules").execute();

			return true;
		} catch (Exception e) {
			Log.e("clientDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;


		int count = 0;
		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM UserModules ", null);
				
			
			if (cursor.moveToFirst()) {
				do {
					
					UserModuleIdDTO dto = new UserModuleIdDTO();
					dto.setUserModuleId(cursor.getString(count++));
					dto.setModuleName(cursor.getString(count++));
					dto.setModuleCode(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("userDetailsDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
	
	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObj,
			String columnName, String columnValue) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public List<String> getUserModuleCodes(SQLiteDatabase dbObject) 
	{
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;

		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT moduleCode FROM UserModules ", null);
				
			
			if (cursor.moveToFirst()) {
				do {
					
					instList.add(cursor.getString(count++));

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("userDetailsDAO  -- getUserModuleCodes", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

}