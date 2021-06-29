/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.database.dto.ProductDetailsLessInventoryDTO;
import com.micaja.servipunto.database.dto.ProductDetailsShortDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SuggestedOrderDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;

public class InventoryDAO implements DAO {
	private static InventoryDAO inventoryDAO;

	private InventoryDAO() {

	}
	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_inventory").execute();
			return true;
		} catch (Exception e) {
			Log.e("clientPayments DAO  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

	public static InventoryDAO getInstance() {
		if (inventoryDAO == null)
			inventoryDAO = new InventoryDAO();

		return inventoryDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_inventory(inventory_id,product_id,quantity,uom,sync_status,quantity_balance)VALUES (?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {
				InventoryDTO dto = (InventoryDTO) items;

				System.out.println("Insert ========================= :" + dto.getInventoryId());

				if (dto.getInventoryId() == null
						|| dto.getInventoryId().equalsIgnoreCase("")) {
					stmt.bindString(count++,
							String.valueOf(Dates.getSysDateinMilliSeconds()));
				} else {
					stmt.bindString(count++, dto.getInventoryId());
				}

				// stmt.bindString(count++,
				// String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++,
						dto.getProductId() == null ? "" : dto.getProductId());
				stmt.bindString(count++,
						dto.getQuantity() == null ? "" : dto.getQuantity());
				stmt.bindString(count++,
						dto.getUom() == null ? "" : dto.getUom());
				stmt.bindLong(count++, dto.getSyncStatus());
				stmt.bindDouble(count++, dto.getQuantityBalance() == null ? 0.0:dto.getQuantityBalance());


				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("InventoryDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			InventoryDTO dtoObj = (InventoryDTO) dto;
			// product_id,quantity,uom,sync_status)VALUES (?,?,?,?)");

			String whereCls = "product_id = '" + dtoObj.getProductId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("quantity", dtoObj.getQuantity());
			cValues.put("quantity_balance",dtoObj.getQuantityBalance());
			// cValues.put("uom", dtoObj.getUom());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_inventory", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("InventoryDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObj, DTO dto) {
		InventoryDTO invenDTO = (InventoryDTO) dto;
		try {
			dbObj.compileStatement(
					"DELETE FROM tbl_inventory WHERE idProduct = '"
							+ invenDTO.getProductId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("InventoryDAO  -- delete", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_inventory", null);
			if (cursor.moveToFirst()) {
				do {
					InventoryDTO dto = new InventoryDTO();
					dto.setInventoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();

		}

		return instList;
	}
	public InventoryDTO getInventoryValue(SQLiteDatabase dbObject,String product_id){
		InventoryDTO dto = new InventoryDTO();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_inventory WHERE product_id = '" + product_id + "'", null);
			if (cursor.moveToFirst()) {
				do {
					dto.setInventoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setQuantityBalance(cursor.getDouble(count++));
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecordsWithValues", e.getMessage());
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
			cursor = dbObject.rawQuery("SELECT * FROM tbl_inventory WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					InventoryDTO dto = new InventoryDTO();
					dto.setInventoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					dto.setQuantityBalance(cursor.getDouble(count++));

					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public List<DTO> getProductDetailsWithBarcode(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT p.barcode,p.name,p.supplier_id,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode order by UPPER(p.name) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setSupplierName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellingPrice())
									- (CommonMethods.getDoubleFormate(dto
									.getPurchasePrice())))));
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;

	}
	
	
	public List<DTO> getProductDetailsWithnextexp(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT p.barcode,p.name,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat,p.expiry_date  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode order by datetime(p.expiry_date) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellingPrice())
									- (CommonMethods.getDoubleFormate(dto
											.getPurchasePrice())))));
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;

	}
	

	public List<DTO> getProductDetailsWithLessInventory(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT p.barcode,p.name,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode order by CAST(i.quantity as DECIMAL(6,2)) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellingPrice())
									- (CommonMethods.getDoubleFormate(dto
											.getPurchasePrice())))));
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;

	}

	@SuppressWarnings("static-access")
	public List<ProductDetailsShortDTO> getProductDetailsWithnexttoexpair(
			SQLiteDatabase dbObj) {
		List<ProductDetailsShortDTO> instList = new ArrayList<ProductDetailsShortDTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT p.barcode,p.name,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat,p.expiry_date  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode order by UPPER(p.name) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				String productCode, name, supplierName, quantity, sellingPrice, purchasePrice, utilityValue, vat, uom;
				Date exp_date;
				do {

					productCode = cursor.getString(count++);
					name = cursor.getString(count++);
					supplierName = "";
					quantity = cursor.getString(count++);
					uom = cursor.getString(count++);
					sellingPrice = cursor.getString(count++);
					purchasePrice = cursor.getString(count++);
					vat = cursor.getString(count++);
					exp_date = new Dates().getdate(cursor.getString(count++));
					utilityValue = String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods
									.getDoubleFormate(sellingPrice)
									- (CommonMethods
											.getDoubleFormate(purchasePrice))));
					// ProductDetailsShortDTO dto = new
					// ProductDetailsShortDTO();
					ProductDetailsShortDTO dto = new ProductDetailsShortDTO(
							productCode, name, supplierName, quantity,
							sellingPrice, purchasePrice, utilityValue, vat,
							uom, exp_date);
					dto.setProductCode(productCode);
					dto.setName(name);
					dto.setQuantity(quantity);
					dto.setUom(uom);
					dto.setSellingPrice(sellingPrice);
					dto.setPurchasePrice(purchasePrice);
					dto.setVat(vat);
					dto.setExp_date(exp_date);
					dto.setUtilityValue(utilityValue);
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}

		Collections.sort(instList);
		Collections.reverse(instList);
		return instList;

	}
	
	@SuppressWarnings("static-access")
	public List<ProductDetailsLessInventoryDTO> getProductDetailsWithlessInventory(
			SQLiteDatabase dbObj) {
		List<ProductDetailsLessInventoryDTO> instList = new ArrayList<ProductDetailsLessInventoryDTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT p.barcode,p.name,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat,p.expiry_date  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode order by UPPER(i.quantity) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {

				String productCode, name, supplierName, quantity, sellingPrice, purchasePrice, utilityValue, vat, uom;
				Date exp_date;
				do {

					productCode = cursor.getString(count++);
					name = cursor.getString(count++);
					supplierName = "";
					quantity = cursor.getString(count++);
					uom = cursor.getString(count++);
					sellingPrice = cursor.getString(count++);
					purchasePrice = cursor.getString(count++);
					vat = cursor.getString(count++);
					exp_date = new Dates().getdate(cursor.getString(count++));
					utilityValue = String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods
									.getDoubleFormate(sellingPrice)
									- (CommonMethods
											.getDoubleFormate(purchasePrice))));
					// ProductDetailsShortDTO dto = new
					// ProductDetailsShortDTO();
					ProductDetailsLessInventoryDTO dto = new ProductDetailsLessInventoryDTO(
							productCode, name, supplierName, Double.parseDouble(quantity),
							sellingPrice, purchasePrice, utilityValue, vat,
							uom, exp_date);
					dto.setProductCode(productCode);
					dto.setName(name);
					dto.setQuantity(Double.parseDouble(quantity));
					dto.setUom(uom);
					dto.setSellingPrice(sellingPrice);
					dto.setPurchasePrice(purchasePrice);
					dto.setVat(vat);
					dto.setExp_date(exp_date);
					dto.setUtilityValue(utilityValue);
					instList.add(dto);
					count = 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductDetailsWithValues",
					e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}

		Collections.sort(instList);
		Collections.reverse(instList);
		return instList;

	}

	public List<DTO> getSuggestedOrders(SQLiteDatabase dbObj, String supplierId) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT i.quantity,P.min_count_inventory,i.uom,p.name,p.purchase_price  FROM tbl_inventory I INNER JOIN tblm_product P ON P.barcode= I.product_id WHERE  CAST(I.quantity AS int)   <= CAST(P.min_count_inventory AS int)  and supplier_id = '"
					+ supplierId + "';";

			// String query =
			// "SELECT i.quantity,i.uom,p.name,p.purchase_price  FROM tbl_inventory I INNER JOIN tblm_product P ON P.barcode= I.product_id WHERE  CAST(I.quantity AS int)   < 5 ;";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					SuggestedOrderDTO dto = new SuggestedOrderDTO();
					dto.setQuantity(cursor.getString(count++));
					dto.setMin_count_inventory(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getSuggestedOrders", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;

	}

	public List<DTO> getSalesProductsDetails(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		String dish = ConstantsEnum.PRODUCT_DISH.code();
		String NondishType = ConstantsEnum.PRODUCT_NON_DISH.code();
		String uom = "";

		try {
			/*
			 * String query =
			 * "SELECT p.barcode,p.barcode,p.name,i.quantity,i.uom,p.selling_price, p.purchase_price,p.vat , '"
			 * +NondishType +
			 * "' as product  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode"
			 * +" UNION "
			 * +"SELECT m.dish_id,m.dish_id,d.dish_name,m.count,'"+uom
			 * +"' as uom,d.selling_cost_per_item, d.dish_price,d.vat ,  '"+dish
			 * +
			 * "' as dish  FROM tbl_menu_inventory m,tbl_dishes d WHERE  m.dish_id = d.dish_id"
			 * ;
			 */

			String query = "Select id,id,name,qty,uom,sellingPrice,purchasePrice,vat,productType from (SELECT p.barcode as id,p.barcode  as id,p.name as name,i.quantity as qty,i.uom as uom,p.selling_price as sellingPrice, p.purchase_price as purchasePrice,p.vat as vat , '"
					+ NondishType
					+ "' as productType  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode"
					+ " UNION "
					+ "SELECT m.dish_id  as id,m.dish_id  as id,d.dish_name as name,m.count as qty,'"
					+ uom
					+ "' as uom,d.selling_cost_per_item as sellingPrice, d.dish_price as purchasePrice,d.vat as vat ,  '"
					+ dish
					+ "' as productType  FROM tbl_menu_inventory m,tbl_dishes d WHERE  m.dish_id = d.dish_id) order by UPPER(name) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					SelectedProddutsDTO dto = new SelectedProddutsDTO();
					dto.setIdProduct(cursor.getString(count++));
					dto.setBarcode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUnits(cursor.getString(count++));
					dto.setSellPrice(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setProductType(cursor.getString(count++));

					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellPrice())
									- (CommonMethods.getDoubleFormate(dto
									.getPrice())))));
					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getSalesProductsDetails", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return instList;

	}

	public SelectedProddutsDTO getProductBySearchID(SQLiteDatabase dbObj,
			String barCode) {

		SelectedProddutsDTO dto = new SelectedProddutsDTO();

		Cursor cursor = null;

		int count = 0;

		String dish = ConstantsEnum.PRODUCT_DISH.code();
		String NondishType = ConstantsEnum.PRODUCT_NON_DISH.code();

		try {
			// String query =
			// "SELECT p.barcode,p.name,i.quantity,p.selling_price, p.purchase_price,p.vat , '"+NondishType
			// +"' as product  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = '"
			// +barCode +"'";
			// +" UNION "
			// +"SELECT m.dish_id,d.dish_name,m.count,d.selling_cost_per_item, d.dish_price,d.vat ,  '"+dish
			// +"' as dish  FROM tbl_menu_inventory m,tbl_dishes d WHERE  m.dish_id = d.dish_id";

			String query = "SELECT i.product_id,p.name,i.quantity,p.selling_price,p.uom, p.purchase_price,p.vat , '"
					+ NondishType
					+ "' as product  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = p.barcode AND i.product_id ='"
					+ barCode + "'";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					dto.setIdProduct(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setSellPrice(cursor.getString(count++));
					dto.setUnits(cursor.getString(count++));
					dto.setPrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setProductType(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getPrice())
									- (CommonMethods.getDoubleFormate(dto
									.getSellPrice())))));

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getProductBySearchID", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return dto;

	}

	public int isProductExists(SQLiteDatabase dbObj, String productCode) {

		Cursor cursor = null;

		int count = 0;

		String dish = ConstantsEnum.PRODUCT_DISH.code();
		String NondishType = ConstantsEnum.PRODUCT_NON_DISH.code();

		try {
			String query = "SELECT p.barcode,p.name,i.quantity,p.selling_price, p.purchase_price,p.vat , '"
					+ NondishType
					+ "' as product  FROM tbl_inventory i,tblm_product p WHERE  i.product_id = '"
					+ productCode + "'";

			cursor = dbObj.rawQuery(query, null);
			count = cursor.getCount();
			return count;

		} catch (Exception e) {
			Log.e("InventoryDAO  -- getSalesProductsDetails", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObj.close();
		}
		return count;
	}

	public InventoryDTO getRecordByProductID(SQLiteDatabase dbObj,
			String productCode) {

		InventoryDTO dto = new InventoryDTO();
		Cursor cursor = null;

		int count = 0;

		try {
			cursor = dbObj.rawQuery(
					"SELECT * FROM tbl_inventory WHERE  product_id = '"
							+ productCode + "'", null);
			if (cursor.moveToFirst()) {
				do {

					dto.setInventoryId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventoryDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}

	public List<DTO> getProductDetailsExhausted(SQLiteDatabase dbObj) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT TBP.barcode,TBP.name,TBI.quantity,TBI.uom,TBP.selling_price , TBP.purchase_price , TBP.vat  FROM tbl_inventory TBI , tblm_product TBP where  TBP.barcode = TBI.product_id and cast(TBI.quantity as Integer) <=  0 order by UPPER(TBP.name) ASC";

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellingPrice())
									- (CommonMethods.getDoubleFormate(dto
									.getPurchasePrice())))));
					instList.add(dto);

					count = 0;

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

	public List<DTO> getProductDetailsGroup(SQLiteDatabase dbObj, String groupId) {

		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;

		int count = 0;

		try {
			String query = "SELECT TBP.barcode,TBP.name,TBI.quantity,TBI.uom,TBP.selling_price , TBP.purchase_price , TBP.vat  FROM tbl_inventory TBI INNER JOIN tblm_product TBP ON TBP.barcode = TBI.product_id WHERE TBP.group_id = "
					+ groupId;

			cursor = dbObj.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetailsDTO dto = new ProductDetailsDTO();
					dto.setProductCode(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setUtilityValue(String.valueOf(CommonMethods
							.getRoundedVal(CommonMethods.getDoubleFormate(dto
									.getSellingPrice())
									- (CommonMethods.getDoubleFormate(dto
											.getPurchasePrice())))));
					instList.add(dto);

					count = 0;

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
	
	public boolean executeUpdateDelete(SQLiteDatabase dbObj, List<DTO> list) {
		try {
			dbObj.beginTransaction();
			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT or REPLACE INTO tbl_inventory(inventory_id,product_id,quantity,uom,sync_status)VALUES (?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {
				InventoryDTO dto = (InventoryDTO) items;

				System.out.println("Insert ========================= :" + dto.getInventoryId());

				if (dto.getInventoryId() == null
						|| dto.getInventoryId().equalsIgnoreCase("")) {
					stmt.bindString(count++,
							String.valueOf(Dates.getSysDateinMilliSeconds()));
				} else {
					stmt.bindString(count++, dto.getInventoryId());
				}

				// stmt.bindString(count++,
				// String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++,
						dto.getProductId() == null ? "" : dto.getProductId());
				stmt.bindString(count++,
						dto.getQuantity() == null ? "" : dto.getQuantity());
				stmt.bindString(count++,
						dto.getUom() == null ? "" : dto.getUom());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;
//				InventoryDTO Dedto=InventoryDAO.getInstance().getRecordByProductID(dbObj,dto.getProductId());
//				if (null!=Dedto.getProductId()&&Dedto.getProductId().length()>0) {
//					InventoryDAO.getInstance().delete(DBHandler.getDBObj(Constants.WRITABLE),dto);
//				}
				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("InventoryDAO  -- insert", e.getMessage());
		} finally {
			dbObj.endTransaction();
			dbObj.close();
		}
		return false;
	}

	public List<String> getInventoryIDs(SQLiteDatabase dbObject) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;

		try {
			cursor = dbObject.rawQuery("SELECT product_id FROM tbl_inventory", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(0));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("InventeryDAO  -- getProductsIDs", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}


}
