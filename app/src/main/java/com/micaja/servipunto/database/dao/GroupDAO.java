/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;

public class GroupDAO implements DAO
{
	
	private static GroupDAO groupDAO;

	private GroupDAO() {

	}

	public static GroupDAO getInstance() 
	{
		if (groupDAO == null)
			groupDAO = new GroupDAO();

		return groupDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tblm_group(group_id,name)VALUES (?,?)");
			int count = 1;

			for (DTO items : list) 
			{
				GroupDTO dto = (GroupDTO) items;
				stmt.bindString(count++, dto.getGroupId());
				stmt.bindString(count++, dto.getName());
				
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("GroupDTO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) 
	{
		try 
		{
			GroupDTO dtoObj = (GroupDTO) dto;
			
			String whereCls = "group_id = '" + dtoObj.getGroupId()+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			dbObject.update("tblm_group", cValues, whereCls, null);

			return true;		} catch (SQLException e) {
			Log.e("GroupDTO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		GroupDTO groupDTO = (GroupDTO) dto;
		try 
		{
			dbObj.compileStatement(
					"DELETE FROM tblm_group WHERE idGroup = '" + groupDTO.getGroupId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("GroupDTO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObject.rawQuery("SELECT * FROM tblm_group", null);
			if (cursor.moveToFirst())
			{
				do
				{
					GroupDTO dto = new GroupDTO();
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("GroupDTO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
	
	public  GroupDTO getRecordsByGroup_id(SQLiteDatabase dbObj,String group_id) {
		Cursor cursor = null;
		GroupDTO dto = new GroupDTO();
		int count	= 0;
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_group WHERE  group_id = '"+ group_id +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords with bar code", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}
	public  GroupDTO getRecordsByGroup_name(SQLiteDatabase dbObj,String name) {
		Cursor cursor = null;
		GroupDTO dto = new GroupDTO();
		int count	= 0;
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_group WHERE  name = '"+ name +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords with bar code", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}
	
	

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,String columnName, String columnValue) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_group WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					GroupDTO dto = new GroupDTO();
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("GroupDTO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}
	public boolean DBLMinsert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tblm_Dbyc(group_id,name)VALUES (?,?)");
			int count = 1;

			for (DTO items : list) {
				GroupDTO dto = (GroupDTO) items;
				stmt.bindString(count++, dto.getGroupId());
				stmt.bindString(count++, dto.getName());
				count = 1;
				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("GroupDTO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	public boolean DBLMupdate(SQLiteDatabase dbObject, DTO dto) {
		try {
			GroupDTO dtoObj = (GroupDTO) dto;
			String whereCls = "group_id = '" + dtoObj.getGroupId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			dbObject.update("tblm_Dbyc", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("GroupDTO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public GroupDTO DBLMByGroup_id(SQLiteDatabase dbObj, String group_id) {
		Cursor cursor = null;
		GroupDTO dto = new GroupDTO();
		int count = 0;
		try {
			cursor = dbObj.rawQuery(
					"SELECT * FROM tblm_Dbyc WHERE  group_id = '" + group_id
							+ "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords with bar code", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}
		return dto;
	}
}
