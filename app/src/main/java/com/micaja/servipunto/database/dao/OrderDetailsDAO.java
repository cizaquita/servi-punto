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
import com.micaja.servipunto.database.dto.OrderDetailsDTO;

public class OrderDetailsDAO implements DAO {

	private static OrderDetailsDAO orderdetailsDAO;

	private OrderDetailsDAO() {

	}

	public static OrderDetailsDAO getInstance() {
		if (orderdetailsDAO == null)
			orderdetailsDAO = new OrderDetailsDAO();

		return orderdetailsDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_order_details(order_id,product_id,count,uom,price,sync_status)VALUES (?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				OrderDetailsDTO dto = (OrderDetailsDTO) items;
				
				stmt.bindString(count++, dto.getOrderId());
				stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getCount());
				stmt.bindString(count++, dto.getUom());
				stmt.bindString(count++, dto.getPrice());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("OrderDetailsDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		OrderDetailsDTO dtoObj = (OrderDetailsDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_order_details WHERE order_details_id = '"
							+ dtoObj.getOrderDetailsId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("OrderDetailsDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_order_details").execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_menu_inventory_adjustment  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			OrderDetailsDTO dtoObj = (OrderDetailsDTO) dto;
			String whereCls = "order_details_id = '" + dtoObj.getOrderDetailsId()
					+ "'";

			ContentValues cValues = new ContentValues();
			dbObject.update("tbl_order_details", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("OrderDetailsDAO  -- update", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_order_details", null);
			if (cursor.moveToFirst()) {
				do {
					OrderDetailsDTO dto = new OrderDetailsDTO();
					dto.setOrderDetailsId(cursor.getString(count++));
					dto.setOrderId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrderDetailsDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_order_details WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					OrderDetailsDTO dto = new OrderDetailsDTO();
					dto.setOrderDetailsId(cursor.getString(count++));
					dto.setOrderId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setCount(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("OrderDetailsDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

}
