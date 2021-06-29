/**
 * 
 */
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
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.DishesEditDTO;

public class DishProductsDAO implements DAO {

	private static DishProductsDAO dishproductsDAO;

	private DishProductsDAO() {

	}

	public static DishProductsDAO getInstance() {
		if (dishproductsDAO == null)
			dishproductsDAO = new DishProductsDAO();

		return dishproductsDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_dish_products(dish_product_id,dish_id,product_id,uom,quantity,sync_status)VALUES (?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				DishProductsDTO dto = (DishProductsDTO) items;
				
				/*if (dto.getDishProductId()==null||dto.getDishProductId().equalsIgnoreCase("")) {
					stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				}else {*/
				System.out.println("Dish produ id :"+dto.getDishProductId());
					stmt.bindString(count++, dto.getDishProductId());
//				}
				
				//stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++, dto.getDishId());
				stmt.bindString(count++, dto.getProductId());
				stmt.bindString(count++, dto.getUom());
				stmt.bindString(count++, dto.getQuantity());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		DishProductsDTO dtoObj = (DishProductsDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_dish_products WHERE dish_product_id = '"
							+ dtoObj.getDishProductId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			DishProductsDTO dtoObj = (DishProductsDTO) dto;
			String whereCls = "dish_product_id = '" + dtoObj.getDishProductId()
					+ "'";

			ContentValues cValues = new ContentValues();
			
			cValues.put("dish_id", dtoObj.getDishId());
			cValues.put("product_id", dtoObj.getProductId());
			cValues.put("uom", dtoObj.getUom());
			cValues.put("quantity", dtoObj.getQuantity());
			cValues.put("sync_status", dtoObj.getSyncStatus());
			
			dbObject.update("tbl_dish_products", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("DishProductsDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_dish_products", null);
			if (cursor.moveToFirst()) {
				do {
					DishProductsDTO dto = new DishProductsDTO();
					dto.setDishProductId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}
	
	
	public  DishProductsDTO getRecordsByDish_id(SQLiteDatabase dbObj,String dish_id) {
		
		Cursor cursor = null;
		DishProductsDTO dto = new DishProductsDTO();
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT * FROM tbl_dish_products WHERE  dish_id = '"+ dish_id +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					dto.setDishProductId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
					count	= 0;

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

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_dish_products WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					DishProductsDTO dto = new DishProductsDTO();
					dto.setDishProductId(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setProductId(cursor.getString(count++));
					dto.setUom(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public List<DTO> getEditDishesRecords(SQLiteDatabase dbObject,String dish_id) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count	= 0;
		try {
			String qty	= "select TP.barcode,TP.name,TP.purchase_price,TP.selling_price,TDP.dish_product_id,TDP.uom,TDP.quantity,TD.dish_id,TD.dish_name,TD.dish_price,TD.no_of_items,TD.expiry_days,TD.vat,TD.selling_cost_per_item from tblm_product TP inner join tbl_dish_products TDP on TP.barcode = TDP.product_id inner join tbl_dishes TD on TD.dish_id=TDP.dish_id where TD.dish_id ='"+dish_id+"'";
			cursor = dbObject.rawQuery(qty, null);
			if (cursor.moveToFirst()) {
				do {
					DishesEditDTO dto = new DishesEditDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setProductName(cursor.getString(count++));
					dto.setPurchasePrice(cursor.getString(count++));
					dto.setSellingPrice(cursor.getString(count++));
					dto.setDishProductId(cursor.getString(count++));
					dto.setUnits(cursor.getString(count++));
					dto.setQuantity(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					
					dto.setVat(cursor.getString(count++));
					dto.setSellingCostPerItem(cursor.getString(count++));

					instList.add(dto);
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- getEditDishesRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
	public List<String> getDishProductRecords(SQLiteDatabase dbObject,String dish_id) {
		List<String> instList = new ArrayList<String>();
		Cursor cursor = null;
		int count	= 0;
		try {
			cursor = dbObject.rawQuery("SELECT dish_product_id FROM tbl_dish_products WHERE dish_id = '"+ dish_id + "'", null);
			if (cursor.moveToFirst()) {
				do {

					instList.add(cursor.getString(count++));
					count	= 0;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishProductsDAO  -- getDishProductRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

}
