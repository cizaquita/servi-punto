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

import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.HistoryDetailsDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class ClientDAO implements DAO {

	private static ClientDAO clientDAO;

	private ClientDAO() {

	}

	public static ClientDAO getInstance() {
		if (clientDAO == null)
			clientDAO = new ClientDAO();

		return clientDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tblm_client(client_id,cedula,name,telephone,address,email,gender,active_status,initial_debt,balance_amount,last_payment_date,created_date,modified_date,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				ClientDTO dto = (ClientDTO) items;
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
			Log.e("clientDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "client_id = '" + dtoObj.getClientID() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("email", dtoObj.getEmail());
			cValues.put("gender", dtoObj.getGender());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public boolean updateClientData(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "client_id = '" + dtoObj.getClientID() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("email", dtoObj.getEmail());
			cValues.put("gender", dtoObj.getGender());
			cValues.put("active_status", dtoObj.getActiveStatus());
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateSynckstatus(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateSynckstatussuccess(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	public boolean updateSynckstatussuccess1(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateDebtAmount(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "client_id = '" + dtoObj.getClientID() + "'";
			ContentValues cValues = new ContentValues();
			
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("last_payment_date", dtoObj.getLastPaymentDate());

			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateClientDebtAmount(SQLiteDatabase dbObject, DTO dto) {
		try {
			ClientDTO dtoObj = (ClientDTO) dto;

			String whereCls = "cedula = '" + dtoObj.getCedula() + "'";
			
			ContentValues cValues = new ContentValues();
			
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("created_date", dtoObj.getCreatedDate());

			dbObject.update("tblm_client", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Client  Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		ClientDTO customerDTO = (ClientDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tblm_client WHERE client_id = '"
							+ customerDTO.getClientID() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("clientDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tblm_client").execute();

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
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_client ORDER BY name ASC", null);
			if (cursor.moveToFirst()) {
				do {
					ClientDTO dto = new ClientDTO();
					dto.setClientID(cursor.getString(count++));
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
			Log.e("clientDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_client WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					ClientDTO dto = new ClientDTO();
					dto.setClientID(cursor.getString(count++));
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
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public ClientDTO getRecordsByClientID(SQLiteDatabase dbObject,
			String columnValue) {
		Cursor cursor = null;
		ClientDTO dto = new ClientDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tblm_client WHERE client_id= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setClientID(cursor.getString(count++));
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
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
	
	public ClientDTO getRecordsByClientCedula(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		ClientDTO dto = new ClientDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tblm_client WHERE cedula= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setClientID(cursor.getString(count++));
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
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}


	public ClientDTO getRecordsByClientPhone(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		ClientDTO dto = new ClientDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tblm_client WHERE telephone= '"
							+ columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setClientID(cursor.getString(count++));
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
			Log.e("clientDAO  -- getRecordsWithValues", e.getMessage());
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
				cursor = dbObj.rawQuery("SELECT * FROM tblm_client", null);
			} else {
				cursor = dbObj.rawQuery(
						"SELECT * FROM tblm_client WHERE cedula LIKE " + "'%"
								+ query + "%'  OR name LIKE " + "'%" + query
								+ "%' COLLATE NOCASE", null);
			}

			if (cursor.moveToFirst()) {
				do {

					ClientDTO dto = new ClientDTO();
					dto.setClientID(cursor.getString(count++));
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
			Log.e("ClientDAO  -- getRecords", e.getMessage());
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
		//	String querystr = "SELECT i.idProduct,p.name,p.provider,i.count FROM INVENTORY i,PRODUCT p WHERE  i.idProduct =p.idProduct and i.idProduct ='"+query +"' ";
			String query = "SELECT c.initial_debt,c.balance_amount,cp.amount_paid,cp.date_time FROM tblm_client c,tbl_client_payments cp WHERE  c.cedula =cp.client_id and c.cedula ='"+clientID +"' ";	

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
								"SELECT * FROM tblm_client ORDER BY last_payment_date  DESC",
								null);

			} else {
				cursor = dbObj
						.rawQuery(
								"SELECT * FROM tblm_client ORDER BY  CAST(balance_amount as real) DESC",
								null);
			}

			if (cursor.moveToFirst()) {
				do {

					ClientDTO dto = new ClientDTO();
					dto.setClientID(cursor.getString(count++));
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
			Log.e("ClientDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
	
	public List<String> getClientIDs(SQLiteDatabase dbObject) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;

		try {
			cursor = dbObject.rawQuery("SELECT cedula FROM tblm_client", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(0));
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientDAO  -- getClientIDs", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
}
