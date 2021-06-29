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
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.MenusInveDishesDTO;
import com.micaja.servipunto.utils.Dates;

public class MenuInventoryDAO implements DAO {

	private static MenuInventoryDAO menuinventoryDAO;

	private MenuInventoryDAO() {

	}

	public static MenuInventoryDAO getInstance() {
		if (menuinventoryDAO == null)
			menuinventoryDAO = new MenuInventoryDAO();

		return menuinventoryDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_menu_inventory(menu_inventory_id,dish_id,count,sync_status)VALUES (?,?,?,?)");

			int count = 1;
			
			for (DTO items : list) {

				MenuInventoryDTO dto = (MenuInventoryDTO) items;
				stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++, dto.getDishId());
				stmt.bindString(count++, dto.getCount());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		MenuInventoryDTO dtoObj = (MenuInventoryDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_menu_inventory WHERE menu_inventory_id = '"
							+ dtoObj.getMenuInventoryId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			MenuInventoryDTO dtoObj = (MenuInventoryDTO) dto;
			String whereCls = "menu_inventory_id = '"
					+ dtoObj.getMenuInventoryId() + "'";

			ContentValues cValues = new ContentValues();
			
			cValues.put("dish_id", dtoObj.getDishId());
			cValues.put("count", dtoObj.getCount());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			
			dbObject.update("tbl_menu_inventory", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("MenuInventoryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public MenuInventoryDTO getRecordsMenu_inventory_id(SQLiteDatabase dbObject, String menu_inventory_id) {
		Cursor cursor = null;
		MenuInventoryDTO dto = new MenuInventoryDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_menu_inventory WHERE menu_inventory_id= '"
							+ menu_inventory_id + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setMenuInventoryId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("menu_inventory_id  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_inventory", null);
			if (cursor.moveToFirst()) {
				do {
					MenuInventoryDTO dto = new MenuInventoryDTO();
					dto.setMenuInventoryId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_inventory WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					MenuInventoryDTO dto = new MenuInventoryDTO();
					dto.setMenuInventoryId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public List<DTO> getMenusInventory(SQLiteDatabase dbObject)
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			String qry	= "select TD.dish_name,TD.dish_price,TD.expiry_days,TD.selling_cost_per_item,TMI.dish_id,TMI.menu_inventory_id,TMI.count from tbl_dishes TD inner join tbl_menu_inventory TMI on TMI.dish_id = TD.dish_id where TMI.count > 0";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					MenusInveDishesDTO dto = new MenusInveDishesDTO();
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setSellingCostPerItem(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setMenuInventoryId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public List<String> getMenuInvenRecords(SQLiteDatabase dbObject) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT dish_id FROM tbl_menu_inventory", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(count++));
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuInventoryDAO  -- getMenuInvenRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

//"INSERT INTO tbl_menu_inventory(dish_id,count,sync_status)VALUES (?,?,?)");
	//"INSERT INTO tbl_dishes(dish_name,dish_price,no_of_items,expiry_days,vat,selling_cost_per_item,sync_status)VALUES (?,?,?,?,?,?,?)");	
	
	public MenuInventoryDTO getRecordByDishID(SQLiteDatabase dbObj,String productCode) {
		 
		MenuInventoryDTO dto = new MenuInventoryDTO();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tbl_menu_inventory WHERE  dish_id = '"+ productCode +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
				
					dto.setMenuInventoryId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
 }
	//"INSERT INTO tbl_menu_inventory(dish_id,count,sync_status)VALUES (?,?,?)");

	public boolean updateMenuInventoryData(SQLiteDatabase dbObject, DTO dto) 
	{
		try 
		{
			MenuInventoryDTO dtoObj = (MenuInventoryDTO) dto;
			
			String whereCls = "dish_id = '" + dtoObj.getDishId()+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("count", dtoObj.getCount());
			dbObject.update("tbl_menu_inventory", cValues, whereCls, null);

			
			return true;		} catch (SQLException e) {
			Log.e("InventoryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_menu_inventory").execute();

			return true;
		} catch (Exception e) {
			Log.e("userDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
}

