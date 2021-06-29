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

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class InventoryHistoryDAO implements DAO {

	private static InventoryHistoryDAO inventoryHistoryDAO;

	private InventoryHistoryDAO() {

	}

	public static InventoryHistoryDAO getInstance() {
		if (inventoryHistoryDAO == null)
			inventoryHistoryDAO = new InventoryHistoryDAO();

		return inventoryHistoryDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tbl_inventory_history(inventory_history_id,product_id,quantity,uom,price,date_time,sync_status,invoice_no)VALUES (?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				InventoryHistoryDTO dto = (InventoryHistoryDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				stmt.bindString(count++,dto.getProductId() == null ? "" : dto.getProductId());

				stmt.bindString(count++,dto.getQuantity() == null ? "" : dto.getQuantity());

				stmt.bindString(count++,dto.getUom() == null ? "" : dto.getUom());
				stmt.bindDouble(count++,dto.getPrice() == 0 ? 0 : dto.getPrice());

				stmt.bindString(count++,dto.getDateTime() == null ? "" : dto.getDateTime());
				stmt.bindLong(count++,dto.getSyncStatus()== 0 ? 0 : dto.getSyncStatus());
				stmt.bindString(count++,dto.getInvoiceNum() == null ? "" : dto.getInvoiceNum());

				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("InventoryHistoryDAO  -- insert", e.getMessage());
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
	public List<DTO> getRecords(SQLiteDatabase dbObj) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM  tbl_inventory_history", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					InventoryHistoryDTO dto = new InventoryHistoryDTO();
					dto.setInventorHistoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					
					dto.setUom(cursor.getString(count++));
					dto.setPrice(cursor.getDouble(count++));
					
					dto.setDateTime(cursor.getString(count++));
					
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setInvoiceNum(cursor.getString(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords", e.getMessage());
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
		
		int count	= 0;
		
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_inventory_history WHERE " + columnName
					+ " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					InventoryHistoryDTO dto = new InventoryHistoryDTO();
					dto.setInventorHistoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPrice(cursor.getDouble(count++));
					dto.setDateTime(cursor.getString(count++));
					
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setInvoiceNum(cursor.getString(count++));
					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryAdjustmentDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}
		return instList;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_inventory_history").execute();

			return true;
		} catch (Exception e) {
			Log.e("userDAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}
	
	public List<DTO> getInventoryHistoryDetails(SQLiteDatabase dbObj)
	{


		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
// tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,
//vat,supplier_id,line_id,active_status,create_date,modified_date,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");			int count = 1;
//inventory_history_id,product_id,quantity,uom,price,date_time,sync_status)VALUES (?,?,?,?,?,?,?)");

		try {
			String query = "SELECT i.product_id,p.name,i.quantity,i.price,p.selling_price  utility FROM tbl_inventory_history i,tblm_product p WHERE  i.product_id = p.barcode";	

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods.getDoubleFormate(dto.getSellingPrice()) - CommonMethods.getDoubleFormate(dto.getPurchasePrice())));
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
	
	public List<DTO> getProductDetailsExhausted(SQLiteDatabase dbObj)
	{


		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
	
		try {
			String query = "SELECT TBP.barcode,TBP.name,TBI.quantity,TBI.uom,TBP.selling_price , TBP.purchase_price , TBP.vat  FROM tbl_inventory_history TBI INNER JOIN tblm_product TBP ON TBP.barcode = TBI.product_id WHERE TBI.quantity < 0";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) 
			{
				do 
				{
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
			    	dto.setUtilityValue(String.valueOf(CommonMethods.getRoundedVal(CommonMethods.getDoubleFormate(dto.getSellingPrice()) - (CommonMethods.getDoubleFormate(dto.getPurchasePrice())))));
					instList.add(dto);
					
					count	= 0;
					
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsExhausted", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;
	
	}
}
