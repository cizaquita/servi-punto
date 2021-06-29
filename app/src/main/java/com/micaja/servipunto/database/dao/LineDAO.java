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
import com.micaja.servipunto.database.dto.LineDTO;

public class LineDAO implements DAO 
{
	
	private static LineDAO lineDAO;

	private LineDAO() {

	}

	public static LineDAO getInstance() 
	{
		if (lineDAO == null)
			lineDAO = new LineDAO();

		return lineDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tblm_line(line_id, name,group_id)VALUES (?,?,?)");			
			int count = 1;

			for (DTO items : list) 
			{
				LineDTO dto = (LineDTO) items;
				stmt.bindString(count++, dto.getLineId());
				stmt.bindString(count++, dto.getName());
				stmt.bindString(count++, dto.getGroupId());
				
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("LineDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try 
		{
			LineDTO dtoObj = (LineDTO) dto;
			
			String whereCls = "line_id = '" + dtoObj.getLineId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("group_id", dtoObj.getGroupId());
			dbObject.update("tblm_line", cValues, whereCls, null);

			
			return true;		} catch (SQLException e) {
			Log.e("LineDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		LineDTO lineDTO = (LineDTO) dto;
		try 
		{
			dbObj.compileStatement(
					"DELETE FROM tblm_line WHERE line_id = '" + lineDTO.getLineId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("LineDAO  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_line", null);
			if (cursor.moveToFirst())
			{
				do
				{
					LineDTO dto = new LineDTO();
					dto.setLineId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("LineDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
	public  LineDTO getRecordsByline_id(SQLiteDatabase dbObj,String line_id) {
		Cursor cursor = null;
		LineDTO dto = new LineDTO();
		int count	= 0;
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_line WHERE  line_id = '"+ line_id +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					dto.setLineId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}
	public  LineDTO getRecordsByline_name(SQLiteDatabase dbObj,String name) {
		Cursor cursor = null;
		LineDTO dto = new LineDTO();
		int count	= 0;
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_line WHERE  name = '"+ name +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					dto.setLineId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}
		
	public List<DTO> getRecordsByGroupId(SQLiteDatabase dbObject,String groupId)
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObject.rawQuery("SELECT * FROM tblm_line WHERE group_id = '"+groupId+"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					LineDTO dto = new LineDTO();
					dto.setLineId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("LineDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,String columnName, String columnValue) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_line WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					LineDTO dto = new LineDTO();
					dto.setLineId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("LineDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

}
