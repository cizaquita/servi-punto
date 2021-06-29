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
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.HistoryDetailsDTO;
import com.micaja.servipunto.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO implements DAO {

	private static DeliveryDAO delveryDAO;

	private DeliveryDAO() {

	}

	public static DeliveryDAO getInstance() {
		if (delveryDAO == null)
			delveryDAO = new DeliveryDAO();

		return delveryDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_delivery(delivery_id,cedula,name,telephone,address,email,gender,active_status,initial_debt,balance_amount,last_payment_date,created_date,modified_date,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				DeliveryDTO dto = (DeliveryDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,dto.getCedula() == null ? "" : dto.getCedula());
				stmt.bindString(count++,dto.getName() == null ? "" : dto.getName());
				stmt.bindString(count++,dto.getTelephone() == null ? "" : dto.getTelephone());
				stmt.bindString(count++,dto.getAddress() == null ? "" : dto.getAddress());
				stmt.bindString(count++,dto.getEmail() == null ? "" : dto.getEmail());
				stmt.bindString(count++,dto.getGender() == null ? "" : dto.getGender());

				stmt.bindLong(
						count++,
						dto.getActiveStatus() == null ? 0 : dto
								.getActiveStatus());
				stmt.bindString(count++, dto.getInitialDebt() == null ? ""
						: dto.getInitialDebt());

				stmt.bindString(count++, dto.getBalanceAmount() == null ? ""
						: dto.getBalanceAmount());
				stmt.bindString(count++, dto.getLastPaymentDate() == null ? ""
						: dto.getLastPaymentDate());
				stmt.bindString(count++, dto.getCreatedDate() == null ? ""
						: dto.getCreatedDate());
				stmt.bindString(count++, dto.getModifiedDate() == null ? ""
						: dto.getModifiedDate());
				stmt.bindLong(count++,
						dto.getSyncStatus() == 0 ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("delveryDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "delivery_id = '" + dtoObj.getDeliveryID() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("email", dtoObj.getEmail());
			cValues.put("gender", dtoObj.getGender());
			cValues.put("modified_date", dtoObj.getModifiedDate());

			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public boolean updateDeliveryData(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "delivery_id = '" + dtoObj.getDeliveryID() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("email", dtoObj.getEmail());
			cValues.put("gender", dtoObj.getGender());
			cValues.put("active_status", dtoObj.getActiveStatus());
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("initial_debt", dtoObj.getInitialDebt());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateSynckstatus(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateSynckstatussuccess(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	public boolean updateSynckstatussuccess1(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateDebtAmount(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "delivery_id = '" + dtoObj.getDeliveryID() + "'";
			ContentValues cValues = new ContentValues();
			
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("last_payment_date", dtoObj.getLastPaymentDate());

			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateDeliveryDebtAmount(SQLiteDatabase dbObject, DTO dto) {
		try {
			DeliveryDTO dtoObj = (DeliveryDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			
			ContentValues cValues = new ContentValues();
			
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("created_date", dtoObj.getCreatedDate());

			dbObject.update("tbl_delivery", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("delveryDAO  -- update Client  Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		DeliveryDTO customerDTO = (DeliveryDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tbl_delivery WHERE delivery_id = '"
							+ customerDTO.getDeliveryID() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("Deliverydao  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_delivery").execute();

			return true;
		} catch (Exception e) {
			Log.e("delveryDAO  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_delivery", null);
			if (cursor.moveToFirst()) {
				do {
					DeliveryDTO dto = new DeliveryDTO();
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));

					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_delivery WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					DeliveryDTO dto = new DeliveryDTO();
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));

					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public DeliveryDTO getRecordsByDeliveryID(SQLiteDatabase dbObject,
			String columnValue) {
		Cursor cursor = null;
		DeliveryDTO dto = new DeliveryDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_delivery WHERE delivery_id= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
	
	public DeliveryDTO getRecordsByDeliveryCedula(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		DeliveryDTO dto = new DeliveryDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tbl_delivery WHERE cedula= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));

					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
	
	

	public List<DTO> getSearchRecords(SQLiteDatabase dbObj, String query) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;

		try {
			if (query == null || (query != null && query.length() == 0)) {
				cursor = dbObj.rawQuery("SELECT * FROM tbl_delivery", null);
			} else {
				cursor = dbObj.rawQuery(
						"SELECT * FROM tbl_delivery WHERE cedula LIKE " + "'%"
								+ query + "%'  OR name LIKE " + "'%" + query
								+ "%' COLLATE NOCASE", null);
			}

			if (cursor.moveToFirst()) {
				do {

					DeliveryDTO dto = new DeliveryDTO();
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));

					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}

	public List<DTO> getClientPaymentHistory(SQLiteDatabase dbObj,String clientID)
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try {
		
			String query = "SELECT c.initial_debt,c.balance_amount,cp.amount_paid,cp.date_time FROM tbl_delivery c,tbl_client_payments cp WHERE  c.cedula =cp.delivery_id and c.cedula ='"+clientID +"' ";	

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					HistoryDetailsDTO dto = new HistoryDetailsDTO();
					
					dto.setInitialDebt(cursor.getString(count++));
					dto.setBalance(cursor.getString(count++));
					dto.setPay(cursor.getString(count++));
					dto.setDate(cursor.getString(count++));
					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;
	}
	public List<DTO> getFilterRecords(SQLiteDatabase dbObj, String filterBy) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;

		try {
			if (filterBy.equals("DATE")) {

				cursor = dbObj
						.rawQuery(
								"SELECT * FROM tbl_delivery ORDER BY last_payment_date  DESC",
								null);

			} else {
				cursor = dbObj
						.rawQuery(
								"SELECT * FROM tbl_delivery ORDER BY  CAST(balance_amount as real) DESC",
								null);
			}

			if (cursor.moveToFirst()) {
				do {

					DeliveryDTO dto = new DeliveryDTO();
					dto.setDeliveryID(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setGender(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setInitialDebt(cursor.getString(count++));

					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("delveryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
	
	public List<String> getDeliveryIDs(SQLiteDatabase dbObject) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;

		try {
			cursor = dbObject.rawQuery("SELECT cedula FROM tbl_delivery", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(0));
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("delveryDAO  -- getClientIDs", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
}
