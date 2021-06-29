package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PuntoredCredintialsDTO;

public class PuntoredCredintialsDAO implements DAO {

	private static PuntoredCredintialsDAO PuntoredcredintialsDAO;

	private PuntoredCredintialsDAO() {

	}

	public static PuntoredCredintialsDAO getInstance() {
		if (PuntoredcredintialsDAO == null)
			PuntoredcredintialsDAO = new PuntoredCredintialsDAO();

		return PuntoredcredintialsDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_puntored_credintial(terminalId,puntoredId,comercioId,systemId,userName)VALUES (?,?,?,?,?)");
			int count = 1;
			for (DTO items : list) {
				PuntoredCredintialsDTO dto = (PuntoredCredintialsDTO) items;
				stmt.bindString(count++,
						dto.getTerminalId() == null ? "" : dto.getTerminalId());
				stmt.bindString(count++,
						dto.getPuntoredId() == null ? "" : dto.getPuntoredId());
				stmt.bindString(count++,
						dto.getComercioId() == null ? "" : dto.getComercioId());
				stmt.bindString(count++,
						dto.getSystemId() == null ? "" : dto.getSystemId());
				stmt.bindString(count++,
						dto.getUserName() == null ? "" : dto.getUserName());
				count = 1;
				stmt.executeInsert();
			}
			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("tblm_puntoredcredintial  -- insert", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_puntored_credintial",
					null);
			if (cursor.moveToFirst()) {
				do {
					PuntoredCredintialsDTO dto = new PuntoredCredintialsDTO();
					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("PuntoredCredintial  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_user WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					PuntoredCredintialsDTO dto = new PuntoredCredintialsDTO();
					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("PuntoredCredintialsDTO  -- getRecordsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

}
