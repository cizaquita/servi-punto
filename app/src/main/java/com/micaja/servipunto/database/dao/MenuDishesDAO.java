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
import com.micaja.servipunto.database.dto.MenuDishesDTO;
import com.micaja.servipunto.database.dto.MenusEditDTO;
import com.micaja.servipunto.utils.Dates;

public class MenuDishesDAO implements DAO {

	private static MenuDishesDAO menudishesDAO;

	private MenuDishesDAO() {

	}

	public static MenuDishesDAO getInstance() {
		if (menudishesDAO == null)
			menudishesDAO = new MenuDishesDAO();

		return menudishesDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_menu_dishes(menu_dishes_id,menu_id,dish_id,sync_status)VALUES (?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				MenuDishesDTO dto = (MenuDishesDTO) items;
				if (dto.getMenuDishesId()==null||dto.getMenuDishesId().equalsIgnoreCase("")) {
					stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				}else {
					stmt.bindString(count++, dto.getMenuDishesId());
				}
				
				//stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++, dto.getMenuId());
				stmt.bindString(count++, dto.getDishId());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		MenuDishesDTO dtoObj = (MenuDishesDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_menu_dishes WHERE menu_dishes_id = '"
							+ dtoObj.getMenuDishesId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			MenuDishesDTO dtoObj = (MenuDishesDTO) dto;
			String whereCls = "menu_dishes_id = '" + dtoObj.getMenuDishesId()
					+ "'";

			ContentValues cValues = new ContentValues();
			
			cValues.put("menu_id", dtoObj.getMenuId());
			cValues.put("dish_id", dtoObj.getDishId());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			
			dbObject.update("tbl_menu_dishes", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("MenuDishesDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	
	public MenuDishesDTO getRecordsMenu_dishes_id(SQLiteDatabase dbObject, String menu_dishes_id) {
		Cursor cursor = null;
		MenuDishesDTO dto = new MenuDishesDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_menu_dishes WHERE menu_dishes_id= '"
							+ menu_dishes_id + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setMenuDishesId(cursor.getString(count++));
					dto.setMenuId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_dishes", null);
			if (cursor.moveToFirst()) {
				do {
					MenuDishesDTO dto = new MenuDishesDTO();
					dto.setMenuDishesId(cursor.getString(count++));
					dto.setMenuId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_dishes WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					MenuDishesDTO dto = new MenuDishesDTO();
					dto.setMenuDishesId(cursor.getString(count++));
					dto.setMenuId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	
	public List<DTO> getEditMenusRecords(SQLiteDatabase dbObject,String menu_id) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			String qty	= "Select TD.dish_id,TD.dish_name,TD.dish_price,TD.expiry_days,TD.no_of_items,TD.vat,TD.selling_cost_per_item,TM.menu_id,TM.name,TMD.menu_dishes_id from tbl_dishes TD inner join tbl_menu_dishes TMD on TD.dish_id=TMD.dish_id inner join tbl_menu TM on TM.menu_id=TMD.menu_id where TMD.menu_id='"+menu_id+"'";
			cursor = dbObject.rawQuery(qty, null);
			if (cursor.moveToFirst()) {
				do {
					MenusEditDTO dto = new MenusEditDTO();
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSellingPricePerItem(cursor.getString(count++));
					dto.setMenuId(cursor.getString(count++));
					dto.setMenuName(cursor.getString(count++));
					dto.setMenuDishId(cursor.getString(count++));
					

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- getEditMenusRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public List<String> getMenuDishRecords(SQLiteDatabase dbObject,String menu_id) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT dish_id FROM tbl_menu_dishes WHERE menu_id = '"+ menu_id + "'", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(count++));
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDishesDAO  -- getMenuDishRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
}
