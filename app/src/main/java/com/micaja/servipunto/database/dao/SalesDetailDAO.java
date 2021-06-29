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
import com.micaja.servipunto.database.dto.SalesDetailDTO;

public class SalesDetailDAO implements DAO
{

	private static SalesDetailDAO salesDAO;

	private SalesDetailDAO() {

	}

	public static SalesDetailDAO getInstance() 
	{
		if (salesDAO == null)
			salesDAO = new SalesDetailDAO();

		return salesDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_sales_details (sale_id,product_id,count,uom,price,dish_id,sync_status)VALUES (?,?,?,?,?,?,?)");
			int count = 1;
			
			for (DTO items : list) 
			{
				SalesDetailDTO dto = (SalesDetailDTO) items;
				stmt.bindString(count++, dto.getSaleId());
				stmt.bindString(count++, dto.getProductId() == null ?"":dto.getProductId());
				stmt.bindString(count++, dto.getCount() == null ?"":dto.getCount());
				stmt.bindString(count++, dto.getUom() == null ?"":dto.getUom());
				stmt.bindString(count++, dto.getPrice() == null ?"":dto.getPrice());
				stmt.bindString(count++, dto.getDishId() == null ?"":dto.getDishId());
				stmt.bindLong(count++, dto.getSyncStatus() == 0 ?0:dto.getSyncStatus());
				count = 1;
				stmt.executeInsert();
			}
			
			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("SalesDetailsDAO  -- insert", e.getMessage());
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
			SalesDetailDTO dtoObj = (SalesDetailDTO) dto;
			
			String whereCls = "sale_id = '" + dtoObj.getSaleId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("idProduct", dtoObj.getPrice());
			cValues.put("count", dtoObj.getCount());
			cValues.put("price", dtoObj.getPrice());
			cValues.put("discount", dtoObj.getDishId());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_sales_details", cValues, whereCls, null);
			
			return true;		} catch (SQLException e) {
			Log.e("SalesDetailsDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		SalesDetailDTO salesDetailDTO = (SalesDetailDTO) dto;
		try 
		{
			dbObj.compileStatement("DELETE FROM tbl_sales_details WHERE sale_id = '" + salesDetailDTO.getSaleId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("SalesDetailsDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_sales_details").execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_orders  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_sales_details", null);
			if (cursor.moveToFirst())
			{
				do
				{
					SalesDetailDTO dto = new SalesDetailDTO();
					dto.setSaleId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SalesDetailsDAO  -- getRecords", e.getMessage());
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
		
		int count	= 1;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_sales_details WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					SalesDetailDTO dto = new SalesDetailDTO();
					//dto.setSalesDetailID(cursor.getString(count++));
					dto.setSaleId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					instList.add(dto);
					count	= 1;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SalesDetailsDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}
	
	public boolean updatesynck(SQLiteDatabase dbObject, String sale_id) 
	{
		try 
		{
			String whereCls = "sale_id = '" + sale_id + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObject.update("tbl_sales_details", cValues, whereCls, null);
			
			return true;		} catch (SQLException e) {
			Log.e("SalesDetailsDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
}
