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
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.utils.Dates;

public class SincronizarTransaccionesDAO implements DAO {
	private static SincronizarTransaccionesDAO clientDAO;

	private SincronizarTransaccionesDAO() {

	}

	public static SincronizarTransaccionesDAO getInstance() {
		if (clientDAO == null)
			clientDAO = new SincronizarTransaccionesDAO();

		return clientDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {

		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_sincronizar_transaccion(rowid, module, tipo_transaction,authorization_number,id_pdb_servitienda ,transaction_datetime ,transaction_value ,status,creation_date ,created_by ,modified_date ,modified_by ,servitienda_synck_status , punthored_synck_status, module_tipo_id )VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				SincronizarTransaccionesDTO dto = (SincronizarTransaccionesDTO) items;
				stmt.bindString(
						count++,
						dto.getRowid() == null ? "" : String.valueOf(Dates
								.getSysDateinMilliSeconds()));

				stmt.bindString(count++,
						dto.getModule() == null ? "" : dto.getModule());
				stmt.bindString(count++, dto.getTipo_transaction() == null ? ""
						: dto.getTipo_transaction());
				stmt.bindString(
						count++,
						dto.getAuthorization_number() == null ? "" : dto
								.getAuthorization_number());
				stmt.bindString(
						count++,
						dto.getId_pdb_servitienda() == null ? "" : dto
								.getId_pdb_servitienda());
				stmt.bindString(
						count++,
						dto.getTransaction_datetime() == null ? "" : dto
								.getTransaction_datetime());

				stmt.bindString(
						count++,
						dto.getTransaction_value() == null ? "" : dto
								.getTransaction_value());
				stmt.bindString(count++,
						dto.getStatus() == null ? "" : dto.getStatus());

				stmt.bindString(count++, dto.getCreation_date() == null ? ""
						: dto.getCreation_date());
				stmt.bindString(count++,
						dto.getCreated_by() == null ? "" : dto.getCreated_by());
				stmt.bindString(count++, dto.getModified_date() == null ? ""
						: dto.getModified_date());
				stmt.bindString(count++, dto.getModified_by() == null ? ""
						: dto.getModified_by());
				stmt.bindString(
						count++,
						dto.getServitienda_synck_status() == null ? "" : dto
								.getServitienda_synck_status());
				stmt.bindString(
						count++,
						dto.getPunthored_synck_status() == null ? "" : dto
								.getPunthored_synck_status());
				stmt.bindString(
						count++,
						dto.getModule_tipo_id() == null ? "" : dto
								.getModule_tipo_id());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("clientDAO  -- tbl_sincronizar_transaccion", e.getMessage());
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
		SincronizarTransaccionesDTO dtoObjj = (SincronizarTransaccionesDTO) dtoObj;
		try {
			dbObj.compileStatement(
					"DELETE FROM tbl_sincronizar_transaccion WHERE rowid = '"
							+ dtoObjj.getRowid() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("clientDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tbl_sincronizar_transaccion", null);
			if (cursor.moveToFirst()) {
				do {
					SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
					
					dto.setRowid(cursor.getString(count++));
					dto.setModule(cursor.getString(count++));
					dto.setTipo_transaction(cursor.getString(count++));
					dto.setAuthorization_number(cursor.getString(count++));
					dto.setId_pdb_servitienda(cursor.getString(count++));
					dto.setTransaction_datetime(cursor.getString(count++));
					dto.setTransaction_value(cursor.getString(count++));
					dto.setStatus(cursor.getString(count++));
					dto.setCreation_date(cursor.getString(count++));
					dto.setCreated_by(cursor.getString(count++));
					dto.setModified_date(cursor.getString(count++));
					dto.setModified_by(cursor.getString(count++));
					dto.setServitienda_synck_status(cursor.getString(count++));
					dto.setPunthored_synck_status(cursor.getString(count++));
					dto.setModule_tipo_id(cursor.getString(count++));
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

		int count = 0;

		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_sincronizar_transaccion WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();

					
					
					
					dto.setRowid(cursor.getString(count++));
					dto.setModule(cursor.getString(count++));

					dto.setTipo_transaction(cursor.getString(count++));
					dto.setAuthorization_number(cursor.getString(count++));
					dto.setId_pdb_servitienda(cursor.getString(count++));
					dto.setTransaction_datetime(cursor.getString(count++));
					dto.setTransaction_value(cursor.getString(count++));
					dto.setStatus(cursor.getString(count++));
					dto.setCreation_date(cursor.getString(count++));
					dto.setCreated_by(cursor.getString(count++));

					dto.setModified_date(cursor.getString(count++));
					dto.setModified_by(cursor.getString(count++));
					dto.setServitienda_synck_status(cursor.getString(count++));
					dto.setPunthored_synck_status(cursor.getString(count++));
					dto.setModule_tipo_id(cursor.getString(count++));
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public boolean updateServiTiendaSynck(SQLiteDatabase dbObject, DTO dto) {
		try {
			SincronizarTransaccionesDTO dtoObj = (SincronizarTransaccionesDTO) dto;

			String whereCls = "rowid = '" + dtoObj.getRowid() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("servitienda_synck_status", 1);
			dbObject.update("tbl_sincronizar_transaccion", cValues, whereCls,null);
			return true;

		} catch (SQLException e) {
			Log.e("tbl_sincronizar_transaccion  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updatePuntoRedSynck(SQLiteDatabase dbObject, DTO dto) {
		try {
			SincronizarTransaccionesDTO dtoObj = (SincronizarTransaccionesDTO) dto;

			String whereCls = "rowid = '" + dtoObj.getRowid() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("punthored_synck_status", 1);
			dbObject.update("tbl_sincronizar_transaccion", cValues, whereCls,null);
			return true;

		} catch (SQLException e) {
			Log.e("tbl_sincronizar_transaccion  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
}
