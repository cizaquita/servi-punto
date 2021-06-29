package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.DTO;

public class CellularProviderDAO implements DAO {

	private static CellularProviderDAO cellularProviderDAo;

	private CellularProviderDAO() {

	}

	public static CellularProviderDAO getInstance() {
		if (cellularProviderDAo == null)
			cellularProviderDAo = new CellularProviderDAO();

		return cellularProviderDAo;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_cellular_provider(cellularId,description,min_amount,max_amount,multiplier)VALUES (?,?,?,?,?)");
			int count = 1;
			for (DTO items : list) {
				CellularProviderDto dto = (CellularProviderDto) items;
				stmt.bindString(count++,
						dto.getCellularId() == null ? "" : dto.getCellularId());
				stmt.bindString(count++, dto.getDescription() == null ? ""
						: dto.getDescription());
				stmt.bindString(count++,
						dto.getMinAmount() == null ? "" : dto.getMinAmount());
				stmt.bindString(count++,
						dto.getMaxAmount() == null ? "" : dto.getMaxAmount());
				stmt.bindString(count++,
						dto.getMultiplier() == null ? "" : dto.getMultiplier());
				count = 1;
				stmt.executeInsert();
			}
			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("tblm_cellularprovider  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO dtoObj) {
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dtoObj) {
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_cellular_provider",
					null);
			if (cursor.moveToFirst()) {
				do {
					CellularProviderDto dto = new CellularProviderDto();
					dto.setCellularId(cursor.getString(count++));
					dto.setDescription(cursor.getString(count++));
					dto.setMinAmount(cursor.getString(count++));
					dto.setMaxAmount(cursor.getString(count++));
					dto.setMultiplier(cursor.getString(count++));
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public CellularProviderDto getRecordsBydescription(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		CellularProviderDto dto = new CellularProviderDto();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_cellular_provider WHERE description= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setCellularId(cursor.getString(count++));
					dto.setDescription(cursor.getString(count++));
					dto.setMinAmount(cursor.getString(count++));
					dto.setMaxAmount(cursor.getString(count++));
					dto.setMultiplier(cursor.getString(count++));
				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObj,
			String columnName, String columnValue) {
		return null;
	}

}
