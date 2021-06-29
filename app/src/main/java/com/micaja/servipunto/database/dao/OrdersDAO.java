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
import com.micaja.servipunto.database.dto.OrdersDTO;

public class OrdersDAO implements DAO {

	private static OrdersDAO ordersDAO;

	private OrdersDAO() {

	}

	public static OrdersDAO getInstance() {
		if (ordersDAO == null)
			ordersDAO = new OrdersDAO();

		return ordersDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_orders(order_id,supplier_id,invoice_no,gross_amount,net_amount,discount,payment_type,is_order_confirmed,date_time,sync_status,fecha_inicial, fecha_final)VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				OrdersDTO dto = (OrdersDTO) items;
				stmt.bindString(count++, dto.getOrderId());
				stmt.bindString(count++, dto.getSupplierId());
				stmt.bindString(count++, dto.getInvoiceNo());
				stmt.bindString(count++, dto.getGrossAmount());
				stmt.bindString(count++, dto.getNetAmount());
				stmt.bindString(count++, dto.getDiscount());
				stmt.bindString(count++, dto.getPaymentType());
				stmt.bindLong(count++, dto.getIsOrderConfirmed());
				stmt.bindString(count++, dto.getDateTime());
				stmt.bindLong(count++, dto.getSyncStatus());
				stmt.bindString(count++, dto.getFecha_inicial());
				stmt.bindString(count++, dto.getFecha_final());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("OrdersDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		OrdersDTO dtoObj = (OrdersDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_orders WHERE order_id = '"
							+ dtoObj.getOrderId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("OrdersDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_orders").execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_orders  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			OrdersDTO dtoObj = (OrdersDTO) dto;
			String whereCls = "order_id = '" + dtoObj.getOrderId() + "'";

			ContentValues cValues = new ContentValues();
			dbObject.update("tbl_orders", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("OrdersDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_orders", null);
			if (cursor.moveToFirst()) {
				do {
					OrdersDTO dto = new OrdersDTO();
					dto.setOrderId(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count));
					dto.setInvoiceNo(cursor.getString(count));
					dto.setGrossAmount(cursor.getString(count));
					dto.setNetAmount(cursor.getString(count));
					dto.setDiscount(cursor.getString(count));
					dto.setPaymentType(cursor.getString(count));
					dto.setIsOrderConfirmed(cursor.getInt(count));
					dto.setDateTime(cursor.getString(count));
					dto.setSyncStatus(cursor.getInt(count));
					dto.setFecha_inicial(cursor.getString(count));
					dto.setFecha_final(cursor.getString(count));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrdersDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_orders WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					OrdersDTO dto = new OrdersDTO();
					dto.setOrderId(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setInvoiceNo(cursor.getString(count++));
					dto.setGrossAmount(cursor.getString(count++));
					dto.setNetAmount(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setIsOrderConfirmed(cursor.getInt(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setFecha_inicial(cursor.getString(count));
					dto.setFecha_final(cursor.getString(count));

					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrdersDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public String getMaxInvoiceNum(SQLiteDatabase dbObject) 
	{
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "Select MAX(invoice_no) from tbl_orders";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) 
			{
				do {
					list	= cursor.getString(0);

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
	
	public String getOrderID(SQLiteDatabase dbObject) 
	{
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "SELECT order_id FROM tbl_orders where invoice_no = (select MAX(invoice_no) from tbl_orders)";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) 
			{
				do {
					list	= cursor.getString(0);

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

}
