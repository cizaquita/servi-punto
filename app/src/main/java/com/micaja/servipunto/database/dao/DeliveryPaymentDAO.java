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
import com.micaja.servipunto.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPaymentDAO implements DAO {

	private static DeliveryPaymentDAO clientPaymentDAO;

	private DeliveryPaymentDAO() {

	}

	public static DeliveryPaymentDAO getInstance() {
		if (clientPaymentDAO == null)
			clientPaymentDAO = new DeliveryPaymentDAO();

		return clientPaymentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_delivery_payments(payment_id,delivery_id,payment_type,amount_paid,date_time,sale_id,income_type,sync_status)VALUES (?,?,?,?,?,?,?,?)");	
			int count = 1;

			for (DTO items : list) {

				DeliveryPaymentsDTO dto = (DeliveryPaymentsDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,dto.getDeliveryId() == null ? "" : dto.getDeliveryId());

				stmt.bindString(count++,dto.getPaymentType() == null ? "" : dto.getPaymentType());

				stmt.bindString(count++,dto.getAmountPaid() == null ? "" : dto.getAmountPaid());
				stmt.bindString(count++,dto.getDateTime() == null ? "" : dto.getDateTime());
				stmt.bindString(count++,dto.getSaleId()== null ? "" : dto.getSaleId());
				stmt.bindString(count++,dto.getIncomeType() == null ? "" : dto.getIncomeType());
				
				stmt.bindLong(count++,dto.getSyncStatus() == 0 ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("clientPaymentsDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO pdtoObj) {
		try {

			DeliveryPaymentsDTO dtoObj = (DeliveryPaymentsDTO) pdtoObj;
			String whereCls = "delivery_id = '" + dtoObj.getDeliveryId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObj.update("tbl_delivery_payments", cValues, whereCls, null);
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
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObj) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tbl_delivery_payments", null);
			if (cursor.moveToFirst()) {
				do {

					DeliveryPaymentsDTO dto = new DeliveryPaymentsDTO();
					dto.setPaymentId(cursor.getString(count++));
					dto.setDelieryId(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSaleId(cursor.getString(count++));
					dto.setIncomeType(cursor.getString(count++));
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_delivery_payments WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					DeliveryPaymentsDTO dto = new DeliveryPaymentsDTO();
					dto.setPaymentId(cursor.getString(count++));
					dto.setDelieryId(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSaleId(cursor.getString(count++));
					dto.setIncomeType(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientPayments DAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_delivery_payments").execute();

			return true;
		} catch (Exception e) {
			Log.e("clientPayments DAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	
	public boolean deleteRecordsBySaleId(SQLiteDatabase dbObj,String saleId) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_delivery_payments WHERE sale_id = '"+saleId+"'").execute();

			return true;
		} catch (Exception e) {
			Log.e("clientPayments DAO  -- deleteRecordsBySaleId", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
}
