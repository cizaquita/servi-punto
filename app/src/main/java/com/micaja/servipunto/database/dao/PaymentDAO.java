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
import com.micaja.servipunto.database.dto.PaymentDTO;

public class PaymentDAO  implements DAO
{

	private static PaymentDAO paymentDAO;

	private PaymentDAO() {

	}

	public static PaymentDAO getInstance() 
	{
		if (paymentDAO == null)
			paymentDAO = new PaymentDAO();

		return paymentDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO PAYMENT(idPayment,idCustomer,amountPaid,dateTime,paymentType,syncStatus)VALUES (?,?,?,?,?,?)");
			
			int count = 1;

			for (DTO items : list) 
			{
				PaymentDTO dto = (PaymentDTO) items;
				stmt.bindString(count++, dto.getIdPayment());
				stmt.bindString(count++, dto.getIdCustomer());
				stmt.bindString(count++, dto.getAmountPaid());
				stmt.bindString(count++, dto.getDateTime());
				stmt.bindString(count++, dto.getPaymentType());
				stmt.bindLong(count++, dto.getSyncStatus());
				
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("PaymentDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) 
	{
		try 
		{
			PaymentDTO dtoObj = (PaymentDTO) dto;
			
			String whereCls = "idPayment = '" + dtoObj.getIdPayment() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("idCustomer", dtoObj.getIdCustomer());
			cValues.put("amountPaid", dtoObj.getAmountPaid());
			cValues.put("dateTime", dtoObj.getDateTime());
			cValues.put("paymentType", dtoObj.getPaymentType());
			cValues.put("syncStatus", dtoObj.getSyncStatus());
			dbObject.update("PAYMENT", cValues, whereCls, null);

			
			return true;		} catch (SQLException e) {
			Log.e("PaymentDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		PaymentDTO paymentDTO = (PaymentDTO) dto;
		try 
		{
			dbObj.compileStatement(
					"DELETE FROM PAYMENT WHERE idPayment = '" + paymentDTO.getIdPayment() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("PaymentDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObject.rawQuery("SELECT * FROM PAYMENT", null);
			if (cursor.moveToFirst())
			{
				do
				{
					PaymentDTO dto = new PaymentDTO();
					dto.setIdPayment(cursor.getString(count++));
					dto.setIdCustomer(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("PaymentDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,String columnName, String columnValue) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM PAYMENT WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					PaymentDTO dto = new PaymentDTO();
					dto.setIdPayment(cursor.getString(count++));
					dto.setIdCustomer(cursor.getString(count++));
					dto.setAmountPaid(cursor.getString(count++));
					dto.setDateTime(cursor.getString(count++));
					dto.setPaymentType(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("PaymentDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}
}
