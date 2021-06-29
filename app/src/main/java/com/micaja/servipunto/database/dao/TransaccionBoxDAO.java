package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class TransaccionBoxDAO implements DAO {

	private static TransaccionBoxDAO transaccionBoxDAO;

	private TransaccionBoxDAO() {

	}

	public static TransaccionBoxDAO getInstance() {
		if (transaccionBoxDAO == null)
			transaccionBoxDAO = new TransaccionBoxDAO();

		return transaccionBoxDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();

			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_transaccion_box(transaccion_box_id,store_code,user_name,tipo_trans,module_name,transaccion_type,amount,date_time,sync_status)VALUES (?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				TransaccionBoxDTO dto = (TransaccionBoxDTO) items;
				stmt.bindString(count++, dto.getStore_code() == null ? ""
						: CommonMethods.getUUID());
				stmt.bindString(count++,
						dto.getStore_code() == null ? "" : dto.getStore_code());
				stmt.bindString(count++,
						dto.getUsername() == null ? "" : dto.getUsername());
				stmt.bindString(count++, dto.getTipo_transction() == null ? ""
						: dto.getTipo_transction());
				stmt.bindString(count++, dto.getModule_name() == null ? ""
						: dto.getModule_name());
				stmt.bindString(count++, dto.getTransaction_type() == null ? ""
						: dto.getTransaction_type());
				stmt.bindString(count++,
						dto.getAmount() == null ? "" : dto.getAmount());
				stmt.bindString(count++,
						dto.getDatetime() == null ? "" : dto.getDatetime());
				stmt.bindLong(count++,
						dto.getSyncstatus() == 0 ? 0 : dto.getSyncstatus());
				count = 1;
				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("tbl_transaccion_box  -- insert", e.getMessage());
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
	public List<DTO> getRecords(SQLiteDatabase dbObj) {
		return null;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_transaccion_box WHERE " + columnName
							+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					TransaccionBoxDTO dto = new TransaccionBoxDTO();
					dto.setTransaccion_box_id(cursor.getString(count++));
					dto.setStore_code(cursor.getString(count++));
					dto.setUsername(cursor.getString(count++));
					dto.setTipo_transction(cursor.getString(count++));
					dto.setModule_name(cursor.getString(count++));
					dto.setTransaction_type(cursor.getString(count++));
					dto.setAmount(cursor.getString(count++));
					dto.setDatetime(cursor.getString(count++));
					dto.setSyncstatus(cursor.getInt(count++));
					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("tbl_transaccion_box  -- getRecordsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

	// public boolean changesynckstatusasnzero(SQLiteDatabase dbObject, String
	// string) {
	// try {
	// String whereCls = "store_code = '"
	// + string + "'";
	// ContentValues cValues = new ContentValues();
	// cValues.put("sync_status", 1);
	// dbObject.update("tbl_transaccion_box", cValues, whereCls, null);
	//
	// return true;
	// } catch (SQLException e) {
	// Log.e("tbl_transaccion_box  -- update Debt", e.getMessage());
	// } finally {
	// dbObject.close();
	// }
	// return false;
	// }

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_transaccion_box")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("tbl_transaccion_box  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

}
