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
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;

public class SupplierDAO implements DAO
{

	private static SupplierDAO supplierDAO;

	private SupplierDAO() {

	}
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tblm_supplier").execute();

			return true;
		} catch (Exception e) {
			Log.e("tblm_supplier DAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	public static SupplierDAO getInstance() 
	{
		if (supplierDAO == null)
			supplierDAO = new SupplierDAO();

		return supplierDAO;
	}
	
	

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tblm_supplier(supplier_id,cedula,name,logo,address,telephone,contact_name,contact_telephone,visit_days,visit_frequency,active_status,balance_amount,last_payment_date,created_date,modified_date,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");			
			int count = 1;

			for (DTO items : list) 
			{
				SupplierDTO dto = (SupplierDTO) items;
				stmt.bindString(count++,  CommonMethods.getUUID());
				stmt.bindString(count++, dto.getCedula()== null ? "":dto.getCedula());
				stmt.bindString(count++, dto.getName() == null ? "":dto.getName());
				stmt.bindString(count++, dto.getLogo()== null ? "":dto.getLogo());
				stmt.bindString(count++, dto.getAddress()== null ? "":dto.getAddress());
				stmt.bindString(count++, dto.getTelephone() == null ? "":dto.getTelephone());
				stmt.bindString(count++, dto.getContactName() == null ? "":dto.getContactName());
				stmt.bindString(count++, dto.getContactTelephone() == null ? "":dto.getContactTelephone());
				stmt.bindString(count++, dto.getVisitDays() == null ? "":dto.getVisitDays());
				stmt.bindString(count++, dto.getVisitFrequency() == null ? "":dto.getVisitFrequency());
				stmt.bindLong(count++, dto.getActiveStatus() == null ? 0:dto.getActiveStatus());
				stmt.bindString(count++, dto.getBalanceAmount()== null ? "":dto.getBalanceAmount());
				stmt.bindString(count++, dto.getLastPaymentDate()== null ? "":dto.getLastPaymentDate());
				stmt.bindString(count++, dto.getCreatedDate()== null ? "":dto.getCreatedDate());
				stmt.bindString(count++, dto.getModifiedDate()== null ? "":dto.getModifiedDate());
				stmt.bindLong(count++, dto.getSyncStatus() == null ? 0:dto.getSyncStatus());
				count = 1;
				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("SupplierDAO  -- insert","dhana============="+ e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try 
		{
			SupplierDTO dtoObj = (SupplierDTO) dto;
			
			String whereCls = "supplier_id = '" + dtoObj.getSupplierId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("contact_name", dtoObj.getContactName());
			cValues.put("contact_telephone", dtoObj.getContactTelephone());
			cValues.put("visit_days", dtoObj.getVisitDays());
			cValues.put("visit_frequency", dtoObj.getVisitFrequency());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_supplier", cValues, whereCls, null);
			return true;		} catch (SQLException e) {
			Log.e("SupplierDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		SupplierDTO supplierDTO = (SupplierDTO) dto;
		try 
		{
			dbObj.compileStatement(
					"DELETE FROM tblm_supplier WHERE supplier_id = '" + supplierDTO.getSupplierId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("SupplierDAO  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier", null);
			if (cursor.moveToFirst())
			{
				do
				{

					SupplierDTO dto = new SupplierDTO();
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecords", e.getMessage());
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
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier WHERE "+ columnName
							+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					SupplierDTO dto = new SupplierDTO();
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}
	public SupplierDTO getRecordsByID(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		SupplierDTO dto = new SupplierDTO();

		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier WHERE supplier_id = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
				} while (cursor.moveToNext());
			}	
				
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
	public SupplierDTO getRecordscedula(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		SupplierDTO dto = new SupplierDTO();

		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier WHERE cedula = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
				} while (cursor.moveToNext());
			}	
				
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
	public SupplierDTO getRecordsBySupplierID(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		SupplierDTO dto = new SupplierDTO();

		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier WHERE cedula = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
				} while (cursor.moveToNext());
			}	
				
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	public String getRecordBySupplierID(SQLiteDatabase dbObject, String supplierId) {
		Cursor cursor = null;
	
        String suppName = ""; 

		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT name FROM tblm_supplier WHERE cedula = '" + supplierId + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					suppName = cursor.getString(count++);
					
				} while (cursor.moveToNext());
			}	
				
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return suppName;
	}
	public boolean updateDebtAmount(SQLiteDatabase dbObject, DTO dto) {
		try {
			SupplierDTO dtoObj = (SupplierDTO) dto;

			String whereCls = "supplier_id = '" + dtoObj.getSupplierId()+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("last_payment_date", Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));

			dbObject.update("tblm_supplier", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("supplierDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean changesynckstatus(SQLiteDatabase dbObject, DTO dto) {
		try {
			SupplierDTO dtoObj = (SupplierDTO) dto;
			String whereCls = "cedula = '" + dtoObj.getCedula()+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObject.update("tblm_supplier", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("supplierDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public List<DTO> getSearchRecords(SQLiteDatabase dbObj, String query)
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;

		try
		{
			if (query == null || (query != null && query.length() == 0))
			{
				cursor = dbObj.rawQuery("SELECT * FROM tblm_supplier", null);
			}
			else
			{
				cursor = dbObj.rawQuery("SELECT * FROM tblm_supplier WHERE cedula LIKE "+"'%"+ query + "%'  OR name LIKE "+"'%"+ query + "%' COLLATE NOCASE", null);
			}

			if (cursor.moveToFirst())
			{
				do
				{
					
					SupplierDTO dto = new SupplierDTO();
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			Log.e("SupplierDAO  -- getRecords", e.getMessage());
		}
		finally
		{
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
	
	public List<String> getSupplierIDs(SQLiteDatabase dbObject) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;

		try {
			cursor = dbObject.rawQuery("SELECT cedula FROM tblm_supplier", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(0));
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getSupplierIDs", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
	
	public boolean changesynckstatusasnzero(SQLiteDatabase dbObject, DTO dto) {
		try {
			SupplierDTO dtoObj = (SupplierDTO) dto;
			String whereCls = "cedula = '" + dtoObj.getCedula()+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tblm_supplier", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("supplierDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	
	public boolean updateSupplier(SQLiteDatabase dbObject, SupplierDTO dto) {
		try 
		{
			SupplierDTO dtoObj = dto;
			
			String whereCls = "supplier_id = '" + dtoObj.getSupplierId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("name", dtoObj.getName());
			cValues.put("address", dtoObj.getAddress());
			cValues.put("telephone", dtoObj.getTelephone());
			cValues.put("contact_name", dtoObj.getContactName());
			cValues.put("contact_telephone", dtoObj.getContactTelephone());
			cValues.put("visit_days", dtoObj.getVisitDays());
			cValues.put("visit_frequency", dtoObj.getVisitFrequency());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("balance_amount", dtoObj.getBalanceAmount());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_supplier", cValues, whereCls, null);
			return true;		} catch (SQLException e) {
			Log.e("SupplierDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
	public SupplierDTO getRecordsByName(SQLiteDatabase dbObject, String columnValue) {
		Cursor cursor = null;
		SupplierDTO dto = new SupplierDTO();

		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_supplier WHERE name = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					dto.setSupplierId(cursor.getString(count++));
					dto.setCedula(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setLogo(cursor.getString(count++));
					dto.setAddress(cursor.getString(count++));
					dto.setTelephone(cursor.getString(count++));
					dto.setContactName(cursor.getString(count++));
					dto.setContactTelephone(cursor.getString(count++));
					dto.setVisitDays(cursor.getString(count++));
					dto.setVisitFrequency(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setBalanceAmount(cursor.getString(count++));
					dto.setLastPaymentDate(cursor.getString(count++));
					dto.setCreatedDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					
				} while (cursor.moveToNext());
			}	
				
		} catch (Exception e) {
			Log.e("SupplierDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return dto;
	}
	
}
