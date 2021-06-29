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
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class SupplierPaymentsDAO implements DAO {

	private static SupplierPaymentsDAO supplierPaymentDAO;

	private SupplierPaymentsDAO() {

	}

	public static SupplierPaymentsDAO getInstance() {
		if (supplierPaymentDAO == null)
			supplierPaymentDAO = new SupplierPaymentsDAO();

		return supplierPaymentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_supplier_payments(supplier_payment_id,supplier_id,amount_paid,payment_type,date_time,purchase_type,sync_status)VALUES (?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				SupplierPaymentsDTO dto = (SupplierPaymentsDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,
						dto.getSupplierId() == null ? "" : dto.getSupplierId());

				stmt.bindString(count++,
						dto.getAmountPaid() == null ? "" : dto.getAmountPaid());
				stmt.bindString(count++, dto.getPaymentType() == null ? ""
						: dto.getPaymentType());

				stmt.bindString(count++,
						dto.getDateTime() == null ? "" : dto.getDateTime());
				stmt.bindString(count++, dto.getPurchaseType() == null ? ""
						: dto.getPurchaseType());

				stmt.bindLong(count++,
						dto.getSyncStatus() == 0 ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("SuppliuerPaymentsDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO pdtoObj) {

		try {
			SupplierPaymentsDTO dtoObj = (SupplierPaymentsDTO) pdtoObj;
			String whereCls = "supplier_id = '" + dtoObj.getSupplierId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObj.update("tbl_supplier_payments", cValues, whereCls, null);
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
	public List<DTO> getRecords(SQLiteDatabase dbObj) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tbl_supplier_payments", null);
			if (cursor.moveToFirst()) {
				do {
					SupplierPaymentsDTO dto = new SupplierPaymentsDTO();
					dto.setSupplierPaymentId(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setPurchaseType(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierPaymentsDAO -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_supplier_payments WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					SupplierPaymentsDTO dto = new SupplierPaymentsDTO();
					dto.setSupplierPaymentId(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setPurchaseType(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierPaymentsDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_supplier_payments").execute();

			return true;
		} catch (Exception e) {
			Log.e("SupplierPaymentsDTO  -- deleteAllRecords", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
}
