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
import com.micaja.servipunto.database.dto.InventoryAdjustmentDTO;

public class InventoryAdjustmentDAO implements DAO {

	private static InventoryAdjustmentDAO inveAdjustDAO;

	private InventoryAdjustmentDAO() {

	}

	public static InventoryAdjustmentDAO getInstance() {
		if (inveAdjustDAO == null)
			inveAdjustDAO = new InventoryAdjustmentDAO();

		return inveAdjustDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_inventory_adjustment(adjustment_id,product_id,damage_type_id,quantity,uom,sync_status)VALUES (?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				InventoryAdjustmentDTO dto = (InventoryAdjustmentDTO) items;

				stmt.bindString(count++, dto.getAdjustmentId());
				stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getDamageTypeId() == null ? ""
						: dto.getDamageTypeId());
				stmt.bindString(count++,
						dto.getQuantity() == null ? "" : dto.getQuantity());
				stmt.bindString(count++,
						dto.getUom() == null ? "" : dto.getUom());
				stmt.bindLong(count++,
						dto.getSyncStatus() == null ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
				Log.v("============", "asdfg123"+dto.getUom());
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- insert", "insert"+e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			InventoryAdjustmentDTO dtoObj = (InventoryAdjustmentDTO) dto;

			String whereCls = "product_id = '" + dtoObj.getProductId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("damage_type_id", dtoObj.getDamageTypeId());
			cValues.put("quantity", dtoObj.getQuantity());
			cValues.put("uom", dtoObj.getUom());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_inventory_adjustment", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("InventoryAdjustmentDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		InventoryAdjustmentDTO invenDTO = (InventoryAdjustmentDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tbl_inventory_adjustment WHERE product_id = '"
							+ invenDTO.getProductId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_inventory_adjustment")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("Inventory Adjustment  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	// SQLiteStatement stmt =
	// dbObj.compileStatement("INSERT INTO tbl_inventory_adjustment(product_id,damage_type_id,quantity,uom,sync_status)VALUES (?,?,?,?,?)");

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_inventory_adjustment ", null);
			if (cursor.moveToFirst()) {
				do {
					InventoryAdjustmentDTO dto = new InventoryAdjustmentDTO();
					dto.setAdjustmentId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public InventoryAdjustmentDTO getRecordsByProductID(
			SQLiteDatabase dbObject, String prodId) {
		InventoryAdjustmentDTO dto = new InventoryAdjustmentDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_inventory_adjustment WHERE product_id = '"
							+ prodId + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setAdjustmentId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_inventory_adjustment WHERE "
							+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					InventoryAdjustmentDTO dto = new InventoryAdjustmentDTO();
					dto.setAdjustmentId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- getRecordsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

	public List<DTO> getSearchRecords(SQLiteDatabase dbObj, String query) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;

		try {
			if (query == null || (query != null && query.length() == 0))
				cursor = dbObj.rawQuery(
						"SELECT * FROM tbl_inventory_adjustment", null);
			else
				cursor = dbObj.rawQuery(
						"SELECT * FROM tbl_inventory_adjustment WHERE idProduct LIKE "
								+ "'" + query + "%'  COLLATE NOCASE", null);

			if (cursor.moveToFirst()) {
				do {
					InventoryAdjustmentDTO dto = new InventoryAdjustmentDTO();
					dto.setAdjustmentId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setDamageTypeId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("CountryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
}
