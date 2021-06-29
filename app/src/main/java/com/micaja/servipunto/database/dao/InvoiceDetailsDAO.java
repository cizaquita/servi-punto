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
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.ConstantsEnum;

public class InvoiceDetailsDAO implements DAO
{

	private static InvoiceDetailsDAO invoiceDAO;

	private InvoiceDetailsDAO() {

	}

	public static InvoiceDetailsDAO getInstance() 
	{
		if (invoiceDAO == null)
			invoiceDAO = new InvoiceDetailsDAO();

		return invoiceDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_invoice_details (sale_id,barcode,dish_id,count,uom,price,product_type)VALUES (?,?,?,?,?,?,?)");
			
			int count = 1;
			
			for (DTO items : list) 
			{
				SelectedProddutsDTO dto = (SelectedProddutsDTO) items;
				stmt.bindString(count++, dto.getSaleID());
				stmt.bindString(count++, dto.getIdProduct());
				stmt.bindString(count++, dto.getIdDishProduct());
				stmt.bindString(count++, dto.getQuantity());
				stmt.bindString(count++, dto.getUnits());
				stmt.bindString(count++, dto.getPrice());
				stmt.bindString(count++, dto.getProductType());
				
				count = 1;
				stmt.executeInsert();
			}
			
			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("InvoiceDetailsDAO  -- insert", e.getMessage());
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
			SalesDetailDTO dtoObj = (SalesDetailDTO) dto;
			
			String whereCls = "sale_id = '" + dtoObj.getSaleId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("idProduct", dtoObj.getPrice());
			cValues.put("count", dtoObj.getCount());
			cValues.put("price", dtoObj.getPrice());
			cValues.put("discount", dtoObj.getDishId());
			dbObject.update("tbl_sales_details", cValues, whereCls, null);
			
			return true;		} catch (SQLException e) {
			Log.e("InvoiceDetailsDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		SalesDetailDTO salesDetailDTO = (SalesDetailDTO) dto;
		try 
		{
			dbObj.compileStatement("DELETE FROM tbl_invoice_details WHERE sale_id = '" + salesDetailDTO.getSaleId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("InvoiceDetailsDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_invoice_details").execute();

			return true;
		} catch (Exception e) {
			Log.e("InvoiceDetailsDAO  -- delete", e.getMessage());
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
		
		int count	= 1;
		
		try 
		{
			cursor = dbObject.rawQuery("SELECT * FROM tbl_invoice_details", null);
			if (cursor.moveToFirst())
			{
				do
				{
					SelectedProddutsDTO dto = new SelectedProddutsDTO();
					
					dto.setSaleID(cursor.getString(count++));
					dto.setIdProduct(cursor.getString(count++));
					dto.setIdDishProduct(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUnits(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setProductType(cursor.getString(count++));
					dto.setInventoryType(ConstantsEnum.INVOICE.code());

					instList.add(dto);
					
					count	= 1;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InvoiceDetailsDAO  -- getRecords", e.getMessage());
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
		
		int count	= 1;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_invoice_details WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					SelectedProddutsDTO dto = new SelectedProddutsDTO();
					
					dto.setSaleID(cursor.getString(count++));
					dto.setIdProduct(cursor.getString(count++));
					dto.setIdDishProduct(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUnits(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setProductType(cursor.getString(count++));
					dto.setInventoryType(ConstantsEnum.INVOICE.code());
					instList.add(dto);
					count	= 1;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InvoiceDetailsDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}
}
