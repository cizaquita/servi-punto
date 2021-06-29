/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.database.dto.LendDeliveryDTO;
import com.micaja.servipunto.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class LendDeliveryDAO implements DAO {

	private static LendDeliveryDAO clientPaymentDAO;

	private LendDeliveryDAO() {

	}

	public static LendDeliveryDAO getInstance() {
		if (clientPaymentDAO == null)
			clientPaymentDAO = new LendDeliveryDAO();

		return clientPaymentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_lend_delivery(lend_id,delivery_id,amount,date_time,sync_status)VALUES (?,?,?,?,?)");			int count = 1;

			for (DTO items : list) {
				LendDeliveryDTO dto = (LendDeliveryDTO) items;

				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,dto.getDeliveryId() == null ? "" : dto.getDeliveryId());

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

			LendDeliveryDTO dtoObj = (LendDeliveryDTO) pdtoObj;
			String whereCls = "delivery_id = '" + dtoObj.getDeliveryId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObj.update("tbl_lend_delivery", cValues, whereCls, null);
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
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tbl_lend_delivery", null);
			if (cursor.moveToFirst()) {
				do {
					LendDeliveryDTO dto = new LendDeliveryDTO();

					dto.setLendId(cursor.getString(count++));
					dto.setDeliveryId(cursor.getString(count++));
					dto.setAmount(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientPayments DAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_lend_delivery WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					LendDeliveryDTO dto = new LendDeliveryDTO();
					dto.setLendId(cursor.getString(count++));
					dto.setDeliveryId(cursor.getString(count++));
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
			dbObj.compileStatement("DELETE  FROM tbl_lend_delivery").execute();

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
