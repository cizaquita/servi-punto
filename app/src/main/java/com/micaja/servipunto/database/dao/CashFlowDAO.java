/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.CashDetailsDTO;
import com.micaja.servipunto.database.dto.CashFlowDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class CashFlowDAO implements DAO {

	private static CashFlowDAO cashFlowDAO;

	private CashFlowDAO() {

	}

	public static CashFlowDAO getInstance() {
		if (cashFlowDAO == null)
			cashFlowDAO = new CashFlowDAO();

		return cashFlowDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_cash_flow(cash_flow_id,amount,amount_type,reason,date_time,sync_status)VALUES (?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				CashFlowDTO dto = (CashFlowDTO) items;

				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,
						dto.getAmount() == null ? "" : dto.getAmount());

				stmt.bindString(count++,
						dto.getAmountType() == null ? "" : dto.getAmountType());

				stmt.bindString(count++,
						dto.getReason() == null ? "" : dto.getReason());
				stmt.bindString(count++,
						dto.getDateTime() == null ? "" : dto.getDateTime());

				stmt.bindLong(count++,
						dto.getSyncStatus() == 0 ? 0 : dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("cashFlowDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObj, DTO dtoObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dtoObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_cash_flow", null);
			if (cursor.moveToFirst()) {
				do {
					CashFlowDTO dto = new CashFlowDTO();
					dto.setCashFlowId(cursor.getString(count++));

					dto.setAmount(cursor.getString(count++));
					dto.setAmountType(cursor.getString(count++));
					dto.setReason(cursor.getString(count++));

					dto.setDateTime(cursor.getString(count++));
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_cash_flow WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					CashFlowDTO dto = new CashFlowDTO();
					dto.setCashFlowId(cursor.getString(count++));
					dto.setAmount(cursor.getString(count++));

					dto.setAmountType(cursor.getString(count++));
					dto.setReason(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Cash FlowDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public List<DTO> getCashRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		String type = ConstantsEnum.SUPPLIER_PAYMENT.code();
		String name = ConstantsEnum.SUPPLIER_PAYMENT.code();
		String lendType = ConstantsEnum.CLIENT_LEND.code();
		String shopOpenType = ConstantsEnum.CASH_SHOP_OPEN.code();
		String lendDeliveryType = ConstantsEnum.DELIVERY_LEND.code();
		String paymentDelivery = ConstantsEnum.DELIVERY_PAYMENT.code();

		int count = 0;
		try {

			
			cursor = dbObject.rawQuery(
					"SELECT lm.amount,c.name,lm.date_time as datetime, '"+ lendType +"'as type,'cash'as ptype,'quantity' qty  FROM  tbl_lend_money lm ,tblm_client c WHERE lm.client_id = c.cedula " +
					" UNION " +
					"SELECT opening_balance,'shop open balance' as type,opening_date_time as datetime, '"+ shopOpenType +"'as type,'cash'as ptype,'quantity' qty  FROM tblm_user " +
					" UNION " +
					"SELECT amount,reason,date_time,amount_type,'cash'as ptype,'quantity' qty   FROM tbl_cash_flow " +
					" UNION " +
					"SELECT p.amount_paid,c.name,p.date_time as datetime,income_type,payment_type,'quantity' qty  FROM  tbl_client_payments p LEFT JOIN tblm_client c ON  p.client_id = c.cedula " 
					+" UNION " +
					"SELECT sp.amount_paid,sup.name,sp.date_time as datetime,sp.purchase_type,sp.payment_type,'quantity' qty  FROM  tbl_supplier_payments sp,tblm_supplier sup WHERE sp.supplier_id = sup.cedula  " +
							" UNION " +
							" SELECT  dlm.amount,d.name,dlm.date_time as datetime,  '"+lendDeliveryType+"'  as type,'cash' as ptype,'quantity' qty  FROM  tbl_lend_delivery dlm ,tbl_delivery d WHERE dlm.delivery_id = d.cedula " +
							" UNION " +
							" SELECT p.amount_paid,d.name,p.date_time as datetime,'"+paymentDelivery+"',payment_type,'quantity' qty  FROM  tbl_delivery_payments p LEFT JOIN tbl_delivery d ON  p.delivery_id = d.cedula " +
							"" +
							" ORDER BY date_time DESC ", null);


			
			if (cursor.moveToFirst()) {
				do {

					CashDetailsDTO dto = new CashDetailsDTO();
					dto.setAmount(cursor.getString(count++));
					dto.setReason(cursor.getString(count++));
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

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_cash_flow").execute();

			return true;
		} catch (Exception e) {
			Log.e("cashFlowtDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
}
