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
import com.micaja.servipunto.database.dto.LendmoneyDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class LendMoneyDAO implements DAO {

	private static LendMoneyDAO clientPaymentDAO;

	private LendMoneyDAO() {

	}

	public static LendMoneyDAO getInstance() {
		if (clientPaymentDAO == null)
			clientPaymentDAO = new LendMoneyDAO();

		return clientPaymentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_lend_money(lend_id,client_id,amount,date_time,sync_status)VALUES (?,?,?,?,?)");			int count = 1;

			for (DTO items : list) {

				LendmoneyDTO dto = (LendmoneyDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,dto.getClientId() == null ? "" : dto.getClientId());

				stmt.bindString(count++,dto.getAmount() == null ? "" : dto.getAmount());

				stmt.bindString(count++,dto.getDateTime() == null ? "" : dto.getDateTime());
				
				stmt.bindLong(count++,dto.getSyncStatus() == 0 ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("Len Money DAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO pdtoObj) {
		try {
			LendmoneyDTO dtoObj = (LendmoneyDTO) pdtoObj;
			String whereCls = "client_id = '" + dtoObj.getClientId() + "'";
			ContentValues cValues = new ContentValues();
//			cValues.put("lend_id", dtoObj.getLendId());
//			cValues.put("client_id", dtoObj.getClientId());
//			cValues.put("amount", dtoObj.getAmount());//lend_id,client_id,amount,date_time,sync_status
//			cValues.put("date_time", dtoObj.getDateTime());
			cValues.put("sync_status", 1);
			dbObj.update("tbl_lend_money", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObj.close();
		}
		return false;
	
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dtoObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_lend_money WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					LendmoneyDTO dto = new LendmoneyDTO();
					dto.setLendId(cursor.getString(count++));
					dto.setClientId(cursor.getString(count++));
					dto.setAmount(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("LendmoneyDTO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_lend_money").execute();

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
