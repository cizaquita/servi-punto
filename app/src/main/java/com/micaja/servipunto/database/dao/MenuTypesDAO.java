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
import com.micaja.servipunto.database.dto.MenuTypesDTO;

public class MenuTypesDAO implements DAO {

	private static MenuTypesDAO menutypesDAO;

	private MenuTypesDAO() {

	}

	public static MenuTypesDAO getInstance() {
		if (menutypesDAO == null)
			menutypesDAO = new MenuTypesDAO();

		return menutypesDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_menu_types(name,sync_status)VALUES (?,?)");

			int count = 1;

			for (DTO items : list) {

				MenuTypesDTO dto = (MenuTypesDTO) items;
				stmt.bindString(count++, dto.getName());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("MenuTypesDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		MenuTypesDTO dtoObj = (MenuTypesDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_menu_types WHERE menu_type_id = '"
							+ dtoObj.getMenuTypeId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("MenuTypesDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			MenuTypesDTO dtoObj = (MenuTypesDTO) dto;
			String whereCls = "menu_type_id = '" + dtoObj.getMenuTypeId() + "'";

			ContentValues cValues = new ContentValues();
			dbObject.update("tbl_menu_types", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("MenuTypesDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public MenuTypesDTO getRecordsMenu_type_id(SQLiteDatabase dbObject,
			String menu_type_id) {
		Cursor cursor = null;
		MenuTypesDTO dto = new MenuTypesDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_menu_types WHERE menu_type_id= '"
							+ menu_type_id + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
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
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_types", null);
			if (cursor.moveToFirst()) {
				do {
					MenuTypesDTO dto = new MenuTypesDTO();
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuTypesDAO  -- getRecords", e.getMessage());
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
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_menu_types WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					MenuTypesDTO dto = new MenuTypesDTO();
					dto.setMenuTypeId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("MenuTypesDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

}
