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
import com.micaja.servipunto.database.dto.Master_ProductDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class Master_ProductDAO implements DAO
{

	private static Master_ProductDAO productDAO;

	private Master_ProductDAO() {

	}

	public static Master_ProductDAO getInstance() 
	{
		if (productDAO == null)
			productDAO = new Master_ProductDAO();

		return productDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) 
	{
		try 
		{
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement("INSERT INTO tblm_master_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,productFlag,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");			
			int count = 1;

			for (DTO items : list) 
			{
				Master_ProductDTO dto = (Master_ProductDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				//stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getBarcode()== null?"":dto.getBarcode());
				stmt.bindString(count++, dto.getName()== null?"":dto.getName());
				stmt.bindString(count++, dto.getQuantity()== null?"":dto.getQuantity());
				stmt.bindString(count++, dto.getUom()== null?"":dto.getUom());
				stmt.bindString(count++, dto.getPurchasePrice()== null?"":dto.getPurchasePrice());
				stmt.bindString(count++, dto.getSellingPrice()== null?"":dto.getSellingPrice());
				stmt.bindString(count++, dto.getGroupId()== null?"":dto.getGroupId());
				stmt.bindString(count++, dto.getVat()== null?"":dto.getVat());
				stmt.bindString(count++, dto.getSupplierId()== null?"":dto.getSupplierId());
				stmt.bindString(count++, dto.getLineId()== null?"":dto.getLineId());
				stmt.bindLong(count++, dto.getActiveStatus()== null? 0:dto.getActiveStatus());
				stmt.bindString(count++, dto.getCreateDate()== null?"":dto.getCreateDate());
				stmt.bindString(count++, dto.getModifiedDate()== null?"":dto.getModifiedDate());
				stmt.bindString(count++, dto.getProductFlag()== null?"":dto.getProductFlag());
				stmt.bindLong(count++, dto.getSyncStatus()== null?0:dto.getSyncStatus());
				
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- insert", e.getMessage());
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
			Master_ProductDTO dtoObj = (Master_ProductDTO) dto;
			
			String whereCls = "product_id = '" + dtoObj.getProductId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("barcode", dtoObj.getBarcode());
			cValues.put("name", dtoObj.getName());
			cValues.put("quantity", dtoObj.getQuantity());
			cValues.put("UOM", dtoObj.getUom());
			cValues.put("purchase_price", dtoObj.getPurchasePrice());
			cValues.put("selling_price", dtoObj.getSellingPrice());
			cValues.put("group_id", dtoObj.getGroupId());
			cValues.put("vat", dtoObj.getVat());
			cValues.put("supplier_id", dtoObj.getSupplierId());
			cValues.put("line_id", dtoObj.getLineId());
			cValues.put("active_status", dtoObj.getActiveStatus());
			cValues.put("create_date", dtoObj.getCreateDate());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("productFlag", dtoObj.getProductFlag());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_product", cValues, whereCls, null);
			
			return true;		} catch (SQLException e) {
			Log.e("Master_ProductDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) 
	{
		Master_ProductDTO productDTO = (Master_ProductDTO) dto;
		try 
		{
			dbObj.compileStatement(
					"DELETE FROM PRODUCT WHERE tblm_master_product = '" + productDTO.getProductId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- delete", e.getMessage());
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
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_master_product", null);
			
			if (cursor.moveToFirst())
			{
				do
				{
					Master_ProductDTO dto = new Master_ProductDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
	//INSERT INTO tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,sync_status)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");			int count = 1;
	
	public Master_ProductDTO getRecordsByProductID(SQLiteDatabase dbObj,String productCode) {
		
		Master_ProductDTO dto = new Master_ProductDTO();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_master_product WHERE  barcode = '"+ productCode +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}

    public int isProductExists(SQLiteDatabase dbObj,String productCode) {
		
    	Master_ProductDTO dto = new Master_ProductDTO();
    	Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_master_product WHERE  barcode = '"+ productCode +"'", null);
			
			count = cursor.getCount();
			return count;
		      
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return count;
	}
	
	
	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_master_product WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					Master_ProductDTO dto = new Master_ProductDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity((cursor.getString(count++)));
					dto.setUom((cursor.getString(count++)));
					dto.setPurchasePrice((cursor.getString(count++)));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId((cursor.getString(count++)));
					dto.setLineId((cursor.getString(count++)));
					dto.setActiveStatus((cursor.getInt(count++)));
					dto.setCreateDate((cursor.getString(count++)));
					dto.setModifiedDate((cursor.getString(count++)));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus((cursor.getInt(count++)));
					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("SupplierPaymentsDTO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	
		
		
	}
  public List<DTO> getRecordsBySupplierID(SQLiteDatabase dbObj,String supplierID) {
		
	  List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_master_product WHERE  supplier_id = '"+ supplierID +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					Master_ProductDTO dto = new Master_ProductDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
  
  
  
  public  Master_ProductDTO getRecordsByBarcode(SQLiteDatabase dbObj,String barcode) {
		
	  Master_ProductDTO dto = new Master_ProductDTO();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tblm_master_product WHERE  barcode = '"+ barcode +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords with bar code", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}
  
  
  public boolean updateProductSynk(SQLiteDatabase dbObject, DTO dto) {
		try {
			Master_ProductDTO dtoObj = (Master_ProductDTO) dto;

			String whereCls = "barcode = '" + dtoObj.getBarcode() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("barcode", dtoObj.getBarcode());
			cValues.put("name", dtoObj.getName());
			cValues.put("quantity", dtoObj.getQuantity());
			cValues.put("UOM", dtoObj.getUom());
			cValues.put("purchase_price", dtoObj.getPurchasePrice());
			cValues.put("selling_price", dtoObj.getSellingPrice());
			cValues.put("group_id", dtoObj.getGroupId());
			cValues.put("vat", dtoObj.getVat());
			cValues.put("supplier_id", dtoObj.getSupplierId());
			cValues.put("line_id", dtoObj.getLineId());
			cValues.put("active_status", dtoObj.getActiveStatus());
			cValues.put("create_date", dtoObj.getCreateDate());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			cValues.put("productFlag", dtoObj.getProductFlag());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_master_product", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}
  

	public List<DTO> getSearchRecords(SQLiteDatabase dbObj, String query) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;

		try {
			if (query == null || (query != null && query.length() == 0)) {
				cursor = dbObj.rawQuery("SELECT * FROM tblm_product", null);
			} else {
				cursor = dbObj.rawQuery(
						"SELECT * FROM tblm_master_product WHERE barcode LIKE " + "'%"
								+ query + "%'  OR name LIKE " + "'%" + query
								+ "%' COLLATE NOCASE", null);
			}

			if (cursor.moveToFirst()) {
				do {

					Master_ProductDTO dto = new Master_ProductDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Master_ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}
	public List<DTO> getRecordsWithInvenQty(SQLiteDatabase dbObj) 
	{
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			String qry = "SELECT  TBP.product_id,TBP.barcode,TBP.name,TBI.quantity,TBP.uom,TBP.purchase_price,TBP.selling_price,TBP.group_id,TBP.vat,TBP.supplier_id,TBP.line_id,TBP.active_status,TBP.create_date,TBP.modified_date,TBP.productFlag,TBP.sync_status FROM tblm_product TBP left join tbl_inventory TBI on TBP.barcode=TBI.product_id order by TBI.quantity DESC";
			cursor = dbObj.rawQuery(qry, null);
			if (cursor.moveToFirst())
			{
				do
				{
					Master_ProductDTO dto = new Master_ProductDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setGroupId(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSupplierId(cursor.getString(count++));
					dto.setLineId(cursor.getString(count++));
					dto.setActiveStatus(cursor.getInt(count++));
					dto.setCreateDate(cursor.getString(count++));
					dto.setModifiedDate(cursor.getString(count++));
					dto.setProductFlag(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
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
}
