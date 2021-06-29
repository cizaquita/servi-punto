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
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.database.dto.MenusFilterDTO;

public class MenuDAO implements DAO {

	private static MenuDAO menuDAO;

	private MenuDAO() {

	}

	public static MenuDAO getInstance() {
		if (menuDAO == null)
			menuDAO = new MenuDAO();

		return menuDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_menu(menu_id,name,menu_type_id,start_date,end_date,week_days,sync_status)VALUES (?,?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				MenuDTO dto = (MenuDTO) items;
				
				stmt.bindString(count++, dto.getMenuId());
				stmt.bindString(count++, dto.getName());
				stmt.bindString(count++, dto.getMenuTypeId());
				stmt.bindString(count++, dto.getStartDate());
				stmt.bindString(count++, dto.getEndDate());
				stmt.bindString(count++, dto.getWeekDays());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("MenuDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		MenuDTO dtoObj = (MenuDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_menu WHERE menu_id = '"
							+ dtoObj.getMenuId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("MenuDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			MenuDTO dtoObj = (MenuDTO) dto;
			String whereCls = "menu_id = '" + dtoObj.getMenuId() + "'";

			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("menu_type_id", dtoObj.getMenuTypeId());
			cValues.put("start_date", dtoObj.getStartDate());
			cValues.put("end_date", dtoObj.getEndDate());
			cValues.put("week_days", dtoObj.getWeekDays());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_menu", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("MenuDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu", null);
			if (cursor.moveToFirst()) {
				do {
					MenuDTO dto = new MenuDTO();
					dto.setMenuId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setStartDate(cursor.getString(count++));
					dto.setEndDate(cursor.getString(count++));
					dto.setWeekDays(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public MenuDTO getRecordsMenu_id(SQLiteDatabase dbObject, String menu_id) {
		Cursor cursor = null;
		MenuDTO dto = new MenuDTO();
		int count = 0;
		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_menu WHERE menu_id= '" + menu_id + "'",
					null);
			if (cursor.moveToFirst()) {
				do {
					dto.setMenuId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setStartDate(cursor.getString(count++));
					dto.setEndDate(cursor.getString(count++));
					dto.setWeekDays(cursor.getString(count++));
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
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					MenuDTO dto = new MenuDTO();
					dto.setMenuId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setStartDate(cursor.getString(count++));
					dto.setEndDate(cursor.getString(count++));
					dto.setWeekDays(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public String getMenuID(SQLiteDatabase dbObject) {
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "select MAX(menu_id) from tbl_menu";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					list = cursor.getString(0);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDAO  -- getMenuID", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return list;
	}

	public List<DTO> getMenusFilterRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			String qry = "select menu_id,name, menu_type_id, week_days,null [weekday] from tbl_menu where menu_type_id = 0 union all select menu_id,name, menu_type_id, week_days,null from tbl_menu where menu_type_id=2 and date() between start_date and end_date union all select menu_id,name, menu_type_id, week_days, case when cast(strftime('%w',date()) as integer) = 1 then 0 when cast(strftime('%w',date()) as integer) = 2 then 1 when cast(strftime('%w',date()) as integer) = 3 then 2 when cast(strftime('%w',date()) as integer) = 4 then 3 when cast(strftime('%w',date()) as integer) = 5 then 4 when cast(strftime('%w',date()) as integer) = 6 then 5 when cast(strftime('%w',date()) as integer) = 0 then 6 end from tbl_menu where menu_type_id = 1";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					MenusFilterDTO dto = new MenusFilterDTO();
					dto.setMenuId(cursor.getString(count++));
					dto.setMenuName(cursor.getString(count++));
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setDays(cursor.getString(count++));
					dto.setWeekDays(cursor.getString(count++));

					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuDAO  -- getMenusFilterRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public String isMenuNameExist(SQLiteDatabase dbObject, String menusName) {
		String instList = "";
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject
					.rawQuery(
							"SELECT name FROM tbl_menu WHERE name = ? COLLATE NOCASE; ",
							new String[] { menusName });
			if (cursor.moveToFirst()) {
				do {

					instList = cursor.getString(count++);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- isDishNameExist", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
}
