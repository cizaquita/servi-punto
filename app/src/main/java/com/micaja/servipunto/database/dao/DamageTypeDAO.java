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
import com.micaja.servipunto.database.dto.DamageTypeDTO;
import com.micaja.servipunto.database.dto.GroupDTO;

public class DamageTypeDAO implements DAO {

	private static DamageTypeDAO groupDAO;

	private DamageTypeDAO() {

	}

	public static DamageTypeDAO getInstance() {
		if (groupDAO == null)
			groupDAO = new DamageTypeDAO();

		return groupDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tblm_damage_type(damage_type_id, name)VALUES (?,?)");

			int count = 1;

			for (DTO items : list) {
				DamageTypeDTO dto = (DamageTypeDTO) items;
				stmt.bindString(count++, dto.getDamageTypeId());
				stmt.bindString(count++, dto.getName());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("DamageTypeDTO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			DamageTypeDTO dtoObj = (DamageTypeDTO) dto;

			String whereCls = "damage_type_id = '" + dtoObj.getDamageTypeId()
					+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			dbObject.update("tblm_damage_type", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	
	

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		GroupDTO groupDTO = (GroupDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tblm_group WHERE idGroup = '"
							+ groupDTO.getGroupId() + "'").execute();

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
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_damage_type", null);
			if (cursor.moveToFirst()) {
				do {
					DamageTypeDTO dto = new DamageTypeDTO();
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DamageTypeDTO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public DamageTypeDTO getRecordsByDamage_id(SQLiteDatabase dbObj,
			String damage_type_id) {
		Cursor cursor = null;
		DamageTypeDTO dto = new DamageTypeDTO();
		int count = 0;
		try {
			cursor = dbObj.rawQuery(
					"SELECT * FROM tblm_damage_type WHERE  damage_type_id = '"
							+ damage_type_id + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
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

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_group WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					GroupDTO dto = new GroupDTO();
					dto.setGroupId(cursor.getString(count++));
					dto.setName(cursor.getString(count++));

					instList.add(dto);

					count = 0;

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
}
