/**
 * 
 */
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
import com.micaja.servipunto.database.dto.MenuInventoryAdjustmentDTO;

public class MenuInventoryAdjustmentDAO implements DAO {

	private static MenuInventoryAdjustmentDAO menuinventoryadjustmentDAO;

	private MenuInventoryAdjustmentDAO() {

	}

	public static MenuInventoryAdjustmentDAO getInstance() {
		if (menuinventoryadjustmentDAO == null)
			menuinventoryadjustmentDAO = new MenuInventoryAdjustmentDAO();

		return menuinventoryadjustmentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_menu_inventory_adjustment(menu_adjustment_id,dish_id,count,sync_status)VALUES (?,?,?,?)");

			
			int count = 1;

			for (DTO items : list) {

				MenuInventoryAdjustmentDTO dto = (MenuInventoryAdjustmentDTO) items;
				stmt.bindString(count++, dto.getMenuAdjustmentId());
				stmt.bindString(count++, dto.getDishId());
				stmt.bindString(count++, dto.getCount());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("MenuInventoryAdjustmentDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		MenuInventoryAdjustmentDTO dtoObj = (MenuInventoryAdjustmentDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_menu_inventory_adjustment WHERE menu_adjustment_id = '"
							+ dtoObj.getMenuAdjustmentId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("MenuInventoryAdjustmentDAO  -- delete", e.getMessage());
		}
		
		finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_menu_inventory_adjustment").execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_menu_inventory_adjustment  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			MenuInventoryAdjustmentDTO dtoObj = (MenuInventoryAdjustmentDTO) dto;
			
			String whereCls = "menu_adjustment_id = '"+ dtoObj.getMenuAdjustmentId() + "'";
			
			ContentValues cValues = new ContentValues();
			cValues.put("dish_id", dtoObj.getDishId());
			cValues.put("count", dtoObj.getCount());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			
			dbObject.update("tbl_menu_inventory_adjustment", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("MenuInventoryAdjustmentDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_inventory_adjustment",
					null);
			if (cursor.moveToFirst()) {
				do {
					MenuInventoryAdjustmentDTO dto = new MenuInventoryAdjustmentDTO();
					dto.setMenuAdjustmentId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryAdjustmentDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_menu_inventory_adjustment WHERE " + columnName
							+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					MenuInventoryAdjustmentDTO dto = new MenuInventoryAdjustmentDTO();
					dto.setMenuAdjustmentId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryAdjustmentDAO  -- getRecordsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public MenuInventoryAdjustmentDTO getRecordsByDishID(SQLiteDatabase dbObject,String dishId) {
		
		Cursor cursor = null;
		MenuInventoryAdjustmentDTO dto = new MenuInventoryAdjustmentDTO();
		int count	= 0;
		
		try 
		{
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_inventory_adjustment WHERE dish_id = '" +dishId + "'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					dto.setMenuAdjustmentId(cursor.getString(count++));;
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryAdjustmentDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return dto;
	}

}
