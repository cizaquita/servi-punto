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

import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class ProductDAO implements DAO {

	private static ProductDAO productDAO;

	private ProductDAO() {

	}

	public static ProductDAO getInstance() {
		if (productDAO == null)
			productDAO = new ProductDAO();

		return productDAO;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tblm_product").execute();

			return true;
		} catch (Exception e) {
			Log.e("tblm_product  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement(
					"INSERT INTO tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,productFlag,sync_status,sub_group,min_count_inventory,expiry_date,discount,fecha_inicial,fecha_final)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				ProductDTO dto = (ProductDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				// stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getBarcode() == null ? "" : dto.getBarcode());
				stmt.bindString(count++, dto.getName() == null ? "" : dto.getName());
				stmt.bindString(count++, dto.getQuantity() == null ? "" : dto.getQuantity());
				stmt.bindString(count++, dto.getUom() == null ? "" : dto.getUom());
				stmt.bindString(count++, dto.getPurchasePrice() == null ? "" : dto.getPurchasePrice());
				stmt.bindString(count++, dto.getSellingPrice() == null ? "" : dto.getSellingPrice());
				stmt.bindString(count++, dto.getGroupId() == null ? "" : dto.getGroupId());
				stmt.bindString(count++, dto.getVat() == null ? "" : dto.getVat());
				stmt.bindString(count++, dto.getSupplierId() == null ? "" : dto.getSupplierId());
				stmt.bindString(count++, dto.getLineId() == null ? "" : dto.getLineId());
				stmt.bindLong(count++, dto.getActiveStatus() == null ? 0 : dto.getActiveStatus());
				stmt.bindString(count++, dto.getCreateDate() == null ? "" : dto.getCreateDate());
				stmt.bindString(count++, dto.getModifiedDate() == null ? "" : dto.getModifiedDate());
				stmt.bindString(count++, dto.getProductFlag() == null ? "" : dto.getProductFlag());
				stmt.bindLong(count++, dto.getSyncStatus() == null ? 0 : dto.getSyncStatus());
				stmt.bindString(count++, dto.getSubgroup() == null ? "" : dto.getSubgroup());
				stmt.bindString(count++, dto.getMin_count_inventory() == null ? "" : dto.getMin_count_inventory());
				stmt.bindString(count++, dto.getExpiry_date() == null ? "" : dto.getExpiry_date());
				stmt.bindString(count++, dto.getDiscount() == null ? "" : dto.getDiscount());
				stmt.bindString(count++, dto.getFecha_inicial() == null ? "" : dto.getFecha_inicial());
				stmt.bindString(count++, dto.getFecha_final() == null ? "" : dto.getFecha_final());
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("ProductDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			ProductDTO dtoObj = (ProductDTO) dto;

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
			cValues.put("sub_group", dtoObj.getSubgroup());
			cValues.put("min_count_inventory", dtoObj.getMin_count_inventory());
			cValues.put("expiry_date", dtoObj.getExpiry_date());
			cValues.put("discount", dtoObj.getDiscount());
			cValues.put("fecha_inicial", dtoObj.getFecha_inicial());
			cValues.put("fecha_final", dtoObj.getFecha_final());

			dbObject.update("tblm_product", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("ProductDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public boolean updateSynckStatusasone(SQLiteDatabase dbObject, String barcode) {
		try {
			// ProductDTO dtoObj = (ProductDTO) dto;
			String whereCls = "barcode = '" + barcode + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tblm_product", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("ProductDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public boolean updateSynckDate(SQLiteDatabase dbObject, DTO dto) {
		try {
			ProductDTO dtoObj = (ProductDTO) dto;
			String whereCls = "barcode = '" + dtoObj.getBarcode() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 0);
			dbObject.update("tblm_product", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("ProductDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		ProductDTO productDTO = (ProductDTO) dto;
		try {
			dbObj.compileStatement("DELETE FROM PRODUCT WHERE tblm_product = '" + productDTO.getProductId() + "'")
					.execute();

			return true;
		} catch (Exception e) {
			Log.e("ProductDAO  -- delete", e.getMessage());
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
			cursor = dbObj.rawQuery("SELECT * FROM tblm_product", null);

			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);

					count = 0;

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

	// INSERT INTO
	// tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,sync_status)VALUES
	// (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"); int count = 1;

	public ProductDTO getRecordsByProductID(SQLiteDatabase dbObj, String productCode) {

		ProductDTO dto = new ProductDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tblm_product WHERE  barcode = '" + productCode + "'", null);
			if (cursor.moveToFirst()) {
				do {

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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}

	public int isProductExists(SQLiteDatabase dbObj, String productCode) {

		ProductDTO dto = new ProductDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tblm_product WHERE  barcode = '" + productCode + "'", null);

			count = cursor.getCount();
			return count;

		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return count;
	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject, String columnName, String columnValue) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tblm_product WHERE " + columnName + " = '" + columnValue + "'",
					null);
			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup((cursor.getString(count++)));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);
					count = 0;
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

	public List<DTO> getRecordsBySupplierID(SQLiteDatabase dbObj, String supplierID) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObj.rawQuery(
					"SELECT * FROM tblm_product WHERE  supplier_id = '" + supplierID + "' order by UPPER(name) ASC",
					null);
			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);

					count = 0;

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

	public ProductDTO getRecordsByBarcode(SQLiteDatabase dbObj, String barcode) {

		ProductDTO dto = new ProductDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tblm_product WHERE  barcode = '" + barcode + "'", null);
			if (cursor.moveToFirst()) {
				do {

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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getRecords with bar code", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}

	public boolean updateProductSynk(SQLiteDatabase dbObject, DTO dto) {
		try {
			ProductDTO dtoObj = (ProductDTO) dto;

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
			cValues.put("sub_group", dtoObj.getSubgroup());
			cValues.put("min_count_inventory", dtoObj.getMin_count_inventory());
			cValues.put("expiry_date", dtoObj.getExpiry_date());
			cValues.put("discount", dtoObj.getDiscount());
			cValues.put("fecha_inicial", dtoObj.getFecha_inicial());
			cValues.put("fecha_final", dtoObj.getFecha_final());
			dbObject.update("tblm_product", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("clientDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public boolean updateProducts(SQLiteDatabase dbObject, DTO dto) {
		try {
			SelectedProddutsDTO dtoObj = (SelectedProddutsDTO) dto;

			String whereCls = "barcode = '" + dtoObj.getBarcode() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("selling_price", dtoObj.getSellPrice());
			cValues.put("purchase_price", dtoObj.getPrice());
			cValues.put("expiry_date", dtoObj.getExpiry_date());
			cValues.put("modified_date", dtoObj.getModifiedDate());
			dbObject.update("tblm_product ", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("ProductDAO  -- update", e.getMessage());
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
			SupplierDTO supplierDto = SupplierDAO.getInstance().getRecordsByName(DBHandler.getDBObj(Constants.READABLE),
					query);
			if (supplierDto.getCedula().length() > 0) {
				query = supplierDto.getCedula();
			}
		} catch (Exception e) {
			query = query;
		}

		try {
			if (query == null || (query != null && query.length() == 0)) {
				cursor = dbObj.rawQuery("SELECT * FROM tblm_product", null);
			} else {
				cursor = dbObj.rawQuery("SELECT * FROM tblm_product WHERE barcode LIKE " + "'%" + query
						+ "%'  OR name LIKE " + "'%" + query + "%'  OR supplier_id LIKE " + "'%" + query
						+ "%' COLLATE NOCASE order by name ASC", null);
			}

			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("productDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;
	}

	public List<DTO> getRecordsWithInvenQty(SQLiteDatabase dbObj) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {

			String qry = "SELECT  TBP.product_id,TBP.barcode,TBP.name,TBI.quantity,TBP.uom,TBP.purchase_price,TBP.selling_price,TBP.group_id,TBP.vat,TBP.supplier_id,TBP.line_id,TBP.active_status,TBP.create_date,TBP.modified_date,TBP.productFlag,TBP.sync_status,TBP.sub_group,TBP.min_count_inventory,TBP.expiry_date,TBP.fecha_inicial,TBP.fecha_final FROM tblm_product TBP left join tbl_inventory TBI on TBP.barcode=TBI.product_id order by UPPER(TBP.name) ASC";
			cursor = dbObj.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					instList.add(dto);

					count = 0;

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

	public ProductDTO getPriceUOMByBarcode(SQLiteDatabase dbObj, String barcode) {

		ProductDTO dto = new ProductDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery(
					"SELECT selling_price,purchase_price,uom FROM tblm_product WHERE  barcode = '" + barcode + "'",
					null);
			if (cursor.moveToFirst()) {
				do {
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ProductDAO  -- getPriceUOMByBarcode", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}
		return dto;
	}

	public boolean update_orderproduct(SQLiteDatabase dbObject, ProductDTO dtoObj) {
		try {
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
			cValues.put("sub_group", dtoObj.getSubgroup());
			cValues.put("min_count_inventory", dtoObj.getMin_count_inventory());
			cValues.put("expiry_date", dtoObj.getExpiry_date());
			cValues.put("discount", dtoObj.getDiscount());
			cValues.put("fecha_inicial", dtoObj.getFecha_inicial());
			cValues.put("fecha_final", dtoObj.getFecha_final());
			dbObject.update("tblm_product", cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("ProductDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	public List<DTO> getSearchRecordsbyinventorysupplier(SQLiteDatabase dbObj, String query, String Supplierid) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			SupplierDTO supplierDto = SupplierDAO.getInstance().getRecordsByName(DBHandler.getDBObj(Constants.READABLE),
					query);
			if (supplierDto.getCedula().length() > 0) {
				query = supplierDto.getCedula();
			}
		} catch (Exception e) {
			query = query;
		}

		try {
			if (query == null || (query != null && query.length() == 0)) {
				cursor = dbObj.rawQuery("SELECT * FROM tblm_product", null);
			} else {
				cursor = dbObj.rawQuery("SELECT * FROM tblm_product WHERE barcode LIKE " + "'%" + query
						+ "%'  OR name LIKE " + "'%" + query + "%'  OR supplier_id LIKE " + "'%" + query
						+ "%' COLLATE NOCASE order by name ASC", null);
			}

			if (cursor.moveToFirst()) {
				do {
					ProductDTO dto = new ProductDTO();
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
					dto.setSubgroup(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setExpiry_date(cursor.getString(count++));
					dto.setDiscount(cursor.getString(count++));
					dto.setFecha_inicial(cursor.getString(count++));
					dto.setFecha_final(cursor.getString(count++));
					if (Supplierid.equalsIgnoreCase(dto.getSupplierId())) {
						instList.add(dto);
					}
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("productDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}
		return instList;
	}
	
	public boolean executeUpdateDelete(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj.compileStatement(
					"INSERT INTO tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,productFlag,sync_status,sub_group,min_count_inventory,expiry_date,discount,fecha_inicial,fecha_final)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {
				ProductDTO dto = (ProductDTO) items;
				stmt.bindString(count++, CommonMethods.getUUID());
				// stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getBarcode() == null ? "" : dto.getBarcode());
				stmt.bindString(count++, dto.getName() == null ? "" : dto.getName());
				stmt.bindString(count++, dto.getQuantity() == null ? "" : dto.getQuantity());
				stmt.bindString(count++, dto.getUom() == null ? "" : dto.getUom());
				stmt.bindString(count++, dto.getPurchasePrice() == null ? "" : dto.getPurchasePrice());
				stmt.bindString(count++, dto.getSellingPrice() == null ? "" : dto.getSellingPrice());
				stmt.bindString(count++, dto.getGroupId() == null ? "" : dto.getGroupId());
				stmt.bindString(count++, dto.getVat() == null ? "" : dto.getVat());
				stmt.bindString(count++, dto.getSupplierId() == null ? "" : dto.getSupplierId());
				stmt.bindString(count++, dto.getLineId() == null ? "" : dto.getLineId());
				stmt.bindLong(count++, dto.getActiveStatus() == null ? 0 : dto.getActiveStatus());
				stmt.bindString(count++, dto.getCreateDate() == null ? "" : dto.getCreateDate());
				stmt.bindString(count++, dto.getModifiedDate() == null ? "" : dto.getModifiedDate());
				stmt.bindString(count++, dto.getProductFlag() == null ? "" : dto.getProductFlag());
				stmt.bindLong(count++, dto.getSyncStatus() == null ? 0 : dto.getSyncStatus());
				stmt.bindString(count++, dto.getSubgroup() == null ? "" : dto.getSubgroup());
				stmt.bindString(count++, dto.getMin_count_inventory() == null ? "" : dto.getMin_count_inventory());
				stmt.bindString(count++, dto.getExpiry_date() == null ? "" : dto.getExpiry_date());
				stmt.bindString(count++, dto.getDiscount() == null ? "" : dto.getDiscount());
				stmt.bindString(count++, dto.getFecha_inicial() == null ? "" : dto.getFecha_inicial());
				stmt.bindString(count++, dto.getFecha_final() == null ? "" : dto.getFecha_final());
				count = 1;
				
				ProductDTO Dedto=ProductDAO.getInstance().getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE),dto.getBarcode());
				if (null!=Dedto.getBarcode()&&Dedto.getBarcode().length()>0) {
					ProductDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE),dto);
				}else{
					stmt.executeInsert();
				}
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("ProductDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}


}
