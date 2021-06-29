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
import com.micaja.servipunto.database.dto.SalesDTO;

public class SalesDAO implements DAO {

	private static SalesDAO salesDAO;

	private SalesDAO() {

	}

	public static SalesDAO getInstance() {
		if (salesDAO == null)
			salesDAO = new SalesDAO();

		return salesDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_sales (sale_id,invoice_number,gross_amount,net_amount,"
							+ "discount,client_id,delivery_code,trans_delivery,amount_paid,payment_type,date_time,sync_status,fecha_inicial, fecha_final)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				SalesDTO dto = (SalesDTO) items;

				stmt.bindString(count++, dto.getSaleID());
				stmt.bindString(count++, dto.getInvoiceNumber());
				stmt.bindString(count++, dto.getGrossAmount());
				stmt.bindString(count++, dto.getNetAmount());
				stmt.bindString(count++, dto.getDiscount());

				stmt.bindString(count++, dto.getClientId());
				stmt.bindString(count++,dto.getDelivery_code());
				stmt.bindLong(count++,dto.getTrans_delivery());
				stmt.bindString(count++, dto.getAmountPaid());
				stmt.bindString(count++, dto.getPaymentType());
				stmt.bindString(count++, dto.getDateTime());
				stmt.bindLong(count++, dto.getSyncStatus());
				stmt.bindString(count++, dto.getFecha_inicial());
				stmt.bindString(count++, dto.getFecha_final());


				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("SalesDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			SalesDTO dtoObj = (SalesDTO) dto;

			String whereCls = "sale_id = '" + dtoObj.getSaleID() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sale_id", dtoObj.getSaleID());
			cValues.put("invoice_number", dtoObj.getInvoiceNumber());
			cValues.put("gross_amount", dtoObj.getGrossAmount());
			cValues.put("net_amount", dtoObj.getNetAmount());
			cValues.put("discount", dtoObj.getDiscount());
			cValues.put("client_id", dtoObj.getClientId());
			cValues.put("delivery_code", dtoObj.getDelivery_code() );
			cValues.put("trans_delivery", dtoObj.getTrans_delivery());
			cValues.put("amount_paid", dtoObj.getAmountPaid());

			cValues.put("payment_type", dtoObj.getPaymentType());
			cValues.put("date_time", dtoObj.getDateTime());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			cValues.put("fecha_inicial", dtoObj.getFecha_inicial());
			cValues.put("fecha_final", dtoObj.getFecha_final());

			dbObject.update("tbl_sales", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("SalesDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		SalesDTO productDTO = (SalesDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tbl_sales WHERE sale_id = '"
							+ productDTO.getSaleID() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("SalesDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_sales").execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_sales  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_sales", null);
			if (cursor.moveToFirst()) {
				do {
					SalesDTO dto = new SalesDTO();
					dto.setSaleID(cursor.getString(count++));
					dto.setInvoiceNumber(cursor.getString(count++));
					dto.setGrossAmount(cursor.getString(count++));
					dto.setNetAmount(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setClientId(cursor.getString(count++));
					dto.setDelivery_code(cursor.getString(count++));
					dto.setTrans_delivery(cursor.getInt(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SalesDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_sales WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					SalesDTO dto = new SalesDTO();
					dto.setSaleID(cursor.getString(count++));
					dto.setInvoiceNumber(cursor.getString(count++));
					dto.setGrossAmount(cursor.getString(count++));
					dto.setNetAmount(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setClientId(cursor.getString(count++));
					dto.setDelivery_code(cursor.getString(count++));
					dto.setTrans_delivery(cursor.getInt(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SalesDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

	public String getSaleID(SQLiteDatabase dbObject) {
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "SELECT MAX(sale_id) FROM tbl_sales";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					list = cursor.getString(0);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrdersDAO  -- getOrderID", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return list;
	}

	public String getMaxInvoiceNum(SQLiteDatabase dbObject) {
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "Select MAX(sale_id) from tbl_sales";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					list = cursor.getString(0);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrdersDAO  -- getMaxInvoiceNum", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return list;
	}

}
