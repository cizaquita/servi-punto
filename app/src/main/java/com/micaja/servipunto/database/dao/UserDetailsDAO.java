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

import com.micaja.servipunto.database.dto.CashDetailsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class UserDetailsDAO implements DAO {

	private static UserDetailsDAO userDAO;

	private UserDetailsDAO() {

	}

	public static UserDetailsDAO getInstance() {
		if (userDAO == null)
			userDAO = new UserDetailsDAO();

		return userDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tblm_user(user_id,nit_shop_id,user_name,password,name,company_name,imei,registration_date,last_login,cedula_document,opening_balance,opening_date_time,is_closed,close_date_time,sync_status,actual_balance,terminal_Id,puntored_Id,comercio_Id,system_Id,is_admin,is_authorized,email,authtoken)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				UserDetailsDTO dto = (UserDetailsDTO) items;
				stmt.bindString(count++, dto.getUserId());
				stmt.bindString(count++, dto.getNitShopId());
				stmt.bindString(count++, dto.getUserName());
				stmt.bindString(count++, dto.getPassword());
				stmt.bindString(count++, dto.getName());
				stmt.bindString(count++, dto.getCompanyName());
				stmt.bindString(count++, dto.getImei());
				stmt.bindString(count++, dto.getRegistrationDate());
				stmt.bindString(count++, dto.getLastLogin());
				stmt.bindString(count++, dto.getCedulaDocument());
				stmt.bindString(count++, dto.getOpeningBalance());
				stmt.bindString(count++, dto.getOpeningDateTime());
				stmt.bindLong(count++, dto.getIsClosed());
				stmt.bindString(count++, dto.getCloseDateTime());
				stmt.bindLong(count++, dto.getSyncStatus());
				stmt.bindString(count++, dto.getActualBalance());
				stmt.bindString(count++, dto.getTerminalId());
				stmt.bindString(count++, dto.getPuntoredId());
				stmt.bindString(count++, dto.getComercioId());
				stmt.bindString(count++, dto.getSystemId());
				stmt.bindString(count++, dto.getIs_admin());
				stmt.bindString(count++, dto.getIs_authorized());
				stmt.bindString(count++, dto.getEmail());
				stmt.bindString(count++, dto.getAuthtoken());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("UserDetailsDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	// user_id,nit_shop_id,user_name,password,name,company_name,imei,registration_date,last_login,cedula_document,opening_balance,opening_date_time,is_closed,close_date_time,sync_status

	public boolean updateShopOpenInfoDB(SQLiteDatabase dbObj,
			UserDetailsDTO dtoObj) {
		try {
			String whereCls = "user_id = '" + dtoObj.getUserId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("opening_balance", dtoObj.getOpeningBalance());
			cValues.put("is_closed", dtoObj.getIsClosed());
			cValues.put("opening_date_time", dtoObj.getOpeningDateTime());
			cValues.put("close_date_time", dtoObj.getCloseDateTime());
			cValues.put("actual_balance", dtoObj.getActualBalance());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObj.update("tblm_user", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("userDAO  -- update", e.getMessage());
		} finally {
			dbObj.close();
		}
		return false;
	}

	public List<DTO> getShopOpenCloseDetails(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		String name = ConstantsEnum.SUPPLIER_PAYMENT.code();
		String lendType = ConstantsEnum.CLIENT_LEND.code();
		String shopOpenType = ConstantsEnum.CASH_SHOP_OPEN.code();
		String delivery_lend = ConstantsEnum.DELIVERY_LEND.code();
		//String delivery_payment = ConstantsEnum.DELIVERY_PAYMENT.code();

		int count = 0;
		try {
			/*String sql = "SELECT  amount, date_time,'"
					+ lendType
					+ "' as type,'cash' as type1,'quantity' qty  FROM  tbl_lend_money "
					+ "UNION "
					+ "SELECT  amount, date_time,'"
					+ delivery_lend
					+ "' as type,'cash' as type1,'quantity' qty  FROM  tbl_lend_delivery "
					+ "UNION "
					+ "SELECT opening_balance,opening_date_time,'"
					+ shopOpenType
					+ "' as type, 'shop cash' as type,'quantity' qty  FROM tblm_user "
					+ "UNION "
					+ "SELECT amount,date_time,amount_type, 'cashfolow' as type,'quantity' qty  FROM tbl_cash_flow  GROUP BY amount_type "
					+ "UNION "
					+ "SELECT amount_paid,date_time,income_type,payment_type,'quantity' qty  FROM  tbl_client_payments  "
					+ "UNION "
					+ "SELECT amount_paid,date_time,income_type,payment_type,'quantity' qty  FROM  tbl_delivery_payments  "
					+ "UNION "
					+ "SELECT amount_paid,date_time,purchase_type,payment_type,'quantity' qty  FROM  tbl_supplier_payments ";
			System.out.println(sql);*/
			cursor = dbObject
					.rawQuery(
							"SELECT  amount, date_time,'"
									+ lendType
									+ "' as type,'cash' as type1,'quantity' qty  FROM  tbl_lend_money "
									+ "UNION "
									+ "SELECT opening_balance,opening_date_time,'"
									+ shopOpenType
									+ "' as type, 'shop cash' as type,'quantity' qty  FROM tblm_user "
									+ "UNION "
									+ "SELECT amount,date_time,amount_type, 'cashfolow' as type,'quantity' qty  FROM tbl_cash_flow  GROUP BY amount_type "
									+ "UNION "
									+ "SELECT amount_paid,date_time,income_type,payment_type,'quantity' qty  FROM  tbl_client_payments  "
									+ "UNION "
									+ "SELECT amount_paid,date_time,purchase_type,payment_type,'quantity' qty  FROM  tbl_supplier_payments ",
							null);

			/*
			 * "UNION " + "SELECT price,date_time,'"+ name +"' as type,'cash' as
			 * purchases,quantity FROM tbl_inventory_history
			 */
			if (cursor.moveToFirst()) {
				do {

					CashDetailsDTO dto = new CashDetailsDTO();
					dto.setAmount(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setAmountType(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("cashDetailsDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public List<DTO> getTotalIncome(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		String name = ConstantsEnum.CASH_TYPE_DEPOSIT.code();

		int count = 0;
		try {
			cursor = dbObject
					.rawQuery(

							"SELECT (opening_balance + sum(amount) + sum(amount_paid))  as total FROM tblm_user,tbl_cash_flow,tbl_client_payments WHERE amount_type  = '"
									+ name + "'", null);

			if (cursor.moveToFirst()) {
				do {

					CashDetailsDTO dto = new CashDetailsDTO();
					dto.setAmount(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("cashDetailsDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO dtoObj) {

		try {
			UserDetailsDTO userObj = (UserDetailsDTO) dtoObj;

			String whereCls = "user_name = '" + userObj.getUserName() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("user_id", userObj.getUserId());
			cValues.put("nit_shop_id", userObj.getNitShopId());
			cValues.put("user_name", userObj.getUserName());
			cValues.put("password", userObj.getPassword());
			cValues.put("name", userObj.getName());
			cValues.put("company_name", userObj.getCompanyName());
			cValues.put("imei", userObj.getImei());
			cValues.put("registration_date", userObj.getRegistrationDate());
			cValues.put("last_login", userObj.getLastLogin());
			cValues.put("cedula_document", userObj.getCedulaDocument());
			cValues.put("opening_balance", userObj.getOpeningBalance());
			cValues.put("opening_date_time", userObj.getOpeningDateTime());
			cValues.put("is_closed", userObj.getIsClosed());
			cValues.put("close_date_time", userObj.getCloseDateTime());
			cValues.put("actual_balance", userObj.getActualBalance());
			cValues.put("sync_status", userObj.getSyncStatus());

			cValues.put("terminal_Id", userObj.getTerminalId());
			cValues.put("puntored_Id", userObj.getPuntoredId());
			cValues.put("comercio_Id", userObj.getComercioId());
			cValues.put("system_Id", userObj.getSystemId());

			dbObj.update("tblm_user", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("tblm_user  -- update---lgs", e.getMessage());
		} finally {
			dbObj.close();
		}
		return false;

	}

	public UserDetailsDTO getRecordsuser_name(SQLiteDatabase dbObject,
			String user_name) {
		Cursor cursor = null;
		UserDetailsDTO dto = new UserDetailsDTO();

		int count = 0;

		try {
			cursor = dbObject.rawQuery(
					"SELECT * FROM tblm_user WHERE user_name= '" + user_name
							+ "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setUserId(cursor.getString(count++));
					dto.setNitShopId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));
					dto.setPassword(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setCompanyName(cursor.getString(count++));

					dto.setImei(cursor.getString(count++));
					dto.setRegistrationDate(cursor.getString(count++));

					dto.setLastLogin(cursor.getString(count++));
					dto.setCedulaDocument(cursor.getString(count++));

					dto.setOpeningBalance(cursor.getString(count++));
					dto.setOpeningDateTime(cursor.getString(count++));

					dto.setIsClosed(cursor.getInt(count++));
					dto.setCloseDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setActualBalance(cursor.getString(count++));

					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));

					dto.setIs_admin(cursor.getString(count++));
					dto.setIs_authorized(cursor.getString(count++));
					dto.setEmail(cursor.getString(count++));
					dto.setAuthtoken(cursor.getString(count++));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("menu_inventory_id  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_user ", null);

			if (cursor.moveToFirst()) {
				do {

					UserDetailsDTO dto = new UserDetailsDTO();
					dto.setUserId(cursor.getString(count++));
					dto.setNitShopId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));
					dto.setPassword(cursor.getString(count++));

					dto.setName(cursor.getString(count++));
					dto.setCompanyName(cursor.getString(count++));

					dto.setImei(cursor.getString(count++));
					dto.setRegistrationDate(cursor.getString(count++));

					dto.setLastLogin(cursor.getString(count++));
					dto.setCedulaDocument(cursor.getString(count++));

					dto.setOpeningBalance(cursor.getString(count++));
					dto.setOpeningDateTime(cursor.getString(count++));

					dto.setIsClosed(cursor.getInt(count++));
					dto.setCloseDateTime(cursor.getString(count++));

					dto.setSyncStatus(cursor.getInt(count++));
					dto.setActualBalance(cursor.getString(count++));

					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));
					dto.setAuthtoken(cursor.getString(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("userDetailsDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public UserDetailsDTO getUserRecords(SQLiteDatabase dbObject) {
		UserDetailsDTO dto = new UserDetailsDTO();

		Cursor cursor = null;

		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_user ", null);

			if (cursor.moveToFirst()) {
				do {
					dto.setUserId(cursor.getString(count++));
					dto.setNitShopId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));
					dto.setPassword(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setCompanyName(cursor.getString(count++));
					dto.setImei(cursor.getString(count++));
					dto.setRegistrationDate(cursor.getString(count++));
					dto.setLastLogin(cursor.getString(count++));
					dto.setCedulaDocument(cursor.getString(count++));
					dto.setOpeningBalance(cursor.getString(count++));
					dto.setOpeningDateTime(cursor.getString(count++));
					dto.setIsClosed(cursor.getInt(count++));
					dto.setCloseDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setActualBalance(cursor.getString(count++));

					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));
					dto.setAuthtoken(cursor.getString(count++));

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("userDetailsDAO  -- getRecords", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_user WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					UserDetailsDTO dto = new UserDetailsDTO();
					dto.setUserId(cursor.getString(count++));
					dto.setNitShopId(cursor.getString(count++));
					dto.setUserName(cursor.getString(count++));
					dto.setPassword(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setCompanyName(cursor.getString(count++));
					dto.setImei(cursor.getString(count++));
					dto.setRegistrationDate(cursor.getString(count++));
					dto.setLastLogin(cursor.getString(count++));
					dto.setCedulaDocument(cursor.getString(count++));
					dto.setOpeningBalance(cursor.getString(count++));
					dto.setOpeningDateTime(cursor.getString(count++));
					dto.setIsClosed(cursor.getInt(count++));
					dto.setCloseDateTime(cursor.getString(count++));

					dto.setSyncStatus(cursor.getInt(count++));
					dto.setActualBalance(cursor.getString(count++));

					dto.setTerminalId(cursor.getString(count++));
					dto.setPuntoredId(cursor.getString(count++));
					dto.setComercioId(cursor.getString(count++));
					dto.setSystemId(cursor.getString(count++));
					dto.setAuthtoken(cursor.getString(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("GroupDTO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

	public boolean deleteRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement(
					"DELETE  FROM tbl_cash_flow,tbl_lend_money,tbl_client_payments,tbl_inventory_history")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("userDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public boolean UpdateUserMycashBoxInfo(SQLiteDatabase dbObj,
			UserDetailsDTO dtoObj) {
		try {
			String whereCls = "user_name = '" + dtoObj.getUserName() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("is_admin", dtoObj.getIs_admin());
			cValues.put("is_authorized", dtoObj.getIs_authorized());
			cValues.put("terminal_Id", dtoObj.getTerminalId());
			cValues.put("puntored_Id", dtoObj.getPuntoredId());
			cValues.put("comercio_Id", dtoObj.getComercioId());
			cValues.put("system_Id", dtoObj.getSystemId());
			cValues.put("authtoken", dtoObj.getAuthtoken());
			dbObj.update("tblm_user", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("userDAO  -- update", e.getMessage());
		} finally {
			dbObj.close();
		}
		return false;
	}
}
