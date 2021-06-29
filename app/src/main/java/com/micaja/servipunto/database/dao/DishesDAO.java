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
import com.micaja.servipunto.database.dto.DishEditDTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.ProductDTO;

public class DishesDAO implements DAO {

	private static DishesDAO dishesDAO;

	private DishesDAO() {

	}

	public static DishesDAO getInstance() {
		if (dishesDAO == null)
			dishesDAO = new DishesDAO();

		return dishesDAO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObject, List<DTO> list) {
		try {
			dbObject.beginTransaction();
			SQLiteStatement stmt = dbObject
					.compileStatement("INSERT INTO tbl_dishes(dish_id,dish_name,dish_price,no_of_items,expiry_days,vat,selling_cost_per_item,sync_status)VALUES (?,?,?,?,?,?,?,?)");

			int count = 1;

			for (DTO items : list) {

				DishesDTO dto = (DishesDTO) items;
				stmt.bindString(count++, dto.getDishId());
				/*if (dto.getDishId()==null||dto.getDishId().equalsIgnoreCase("")) {
					stmt.bindString(count++, String.valueOf(Dates.getSysDateinMilliSeconds()));
				}else {
					stmt.bindString(count++, dto.getDishId());
				}*/
//				stmt.bindString(count++,
//						String.valueOf(Dates.getSysDateinMilliSeconds()));
				stmt.bindString(count++, dto.getDishName());
				stmt.bindString(count++, dto.getDishPrice());
				stmt.bindString(count++, dto.getNoOfItems());
				stmt.bindString(count++, dto.getExpiryDays());
				stmt.bindString(count++, dto.getVat());
				stmt.bindString(count++, dto.getSellingCostperItem());
				stmt.bindLong(count++, dto.getSyncStatus());

				count = 1;

				stmt.executeInsert();
			}

			dbObject.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("DishesDAO  -- insert", e.getMessage());
		} finally {
			dbObject.endTransaction();
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean delete(SQLiteDatabase dbObject, DTO dto) {
		DishesDTO dtoObj = (DishesDTO) dto;
		try {
			dbObject.compileStatement(
					"DELETE FROM tbl_dishes WHERE dish_id = '"
							+ dtoObj.getDishId() + "'").execute();

			return true;
		} catch (Exception e) {
			Log.e("DishesDAO  -- delete", e.getMessage());
		}

		finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public boolean update(SQLiteDatabase dbObject, DTO dto) {
		try {
			DishesDTO dtoObj = (DishesDTO) dto;
			String whereCls = "dish_id = '" + dtoObj.getDishId() + "'";

			ContentValues cValues = new ContentValues();
			cValues.put("dish_name", dtoObj.getDishName());
			cValues.put("dish_price", dtoObj.getDishPrice());
			cValues.put("no_of_items", dtoObj.getNoOfItems());
			cValues.put("expiry_days", dtoObj.getExpiryDays());
			cValues.put("vat", dtoObj.getVat());
			cValues.put("selling_cost_per_item", dtoObj.getSellingCostperItem());
			cValues.put("sync_status", dtoObj.getSyncStatus());

			dbObject.update("tbl_dishes", cValues, whereCls, null);

			return true;
		} catch (SQLException e) {
			Log.e("DishesDAO  -- update", e.getMessage());
		} finally {
			dbObject.close();
		}
		return false;
	}

	@Override
	public List<DTO> getRecords(SQLiteDatabase dbObject) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_dishes", null);
			if (cursor.moveToFirst()) {
				do {
					DishesDTO dto = new DishesDTO();
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSellingCostperItem(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return instList;
	}

	public DishesDTO getRecordsByDish_id(SQLiteDatabase dbObj, String dish_id) {
		Cursor cursor = null;
		DishesDTO dto = new DishesDTO();
		int count = 0;
		try {
			cursor = dbObj.rawQuery(
					"SELECT * FROM tbl_dishes WHERE  dish_id = '" + dish_id
							+ "'", null);
			
			if (cursor.moveToFirst()) {
				do {
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSellingCostperItem(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));
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

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObject,
			String columnName, String columnValue) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject.rawQuery("SELECT * FROM tbl_dishes WHERE "
					+ columnName + " = '" + columnValue + "'", null);
			if (cursor.moveToFirst()) {
				do {
					DishesDTO dto = new DishesDTO();
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSellingCostperItem(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	/*public String getDishID(SQLiteDatabase dbObject) {
		String list = "";
		Cursor cursor = null;
		try {
			String qry = "select MAX(dish_id) from tbl_dishes";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					list = cursor.getString(0);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getDishID", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObject.close();
		}

		return list;
	}*/

	public List<DTO> getDishProductRecords(SQLiteDatabase dbObject,
			String dish_id) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			String qry = "select TP.product_id,TP.name,TP.barcode,TP.purchase_price,TP.vat,TDP.dish_product_id,TDP.uom,TDP.quantity,TD.dish_id,TD.dish_name,TD.dish_price,TD.no_of_items,TD.expiry_days,TD.vat Dishvat,TD.selling_cost_per_item from tblm_product TP inner join tbl_dish_products TDP on TP.product_id=TDP.product_id inner join tbl_dishes TD on TD.dish_id=TDP.dish_id where TD.dish_id='"
					+ dish_id + "'";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {

					DishEditDTO dto = new DishEditDTO();
					dto.setProductId(cursor.getString(count++));
					dto.setProductName(cursor.getString(count++));
					dto.setProductbarCode(cursor.getString(count++));
					dto.setProductPurchasePrice(cursor.getString(count++));
					dto.setProductVat(cursor.getString(count++));
					dto.setDishProductID(cursor.getString(count++));
					dto.setProductUOM(cursor.getString(count++));
					dto.setProductQty(cursor.getString(count++));
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));

					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setDishVat(cursor.getString(count++));
					dto.setSellingPricePerItem(cursor.getString(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getDishProductRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public List<DTO> getFilterDishes(SQLiteDatabase dbObject, String menuId) {
		List<DTO> instList = new ArrayList<DTO>();
		Cursor cursor = null;
		int count = 0;
		try {
			String qry = "select TD.dish_id,TD.dish_name,TD.dish_price,TD.no_of_items,TD.expiry_days,TD.vat,TD.selling_cost_per_item,TD.sync_status from tbl_dishes TD inner join tbl_menu_dishes TMD on TD.dish_id=TMD.dish_id inner join tbl_menu TM on TM.menu_id = TMD.menu_id where TMD.menu_id='"
					+ menuId + "'";
			cursor = dbObject.rawQuery(qry, null);
			if (cursor.moveToFirst()) {
				do {
					DishesDTO dto = new DishesDTO();
					dto.setDishId(cursor.getString(count++));
					dto.setDishName(cursor.getString(count++));
					dto.setDishPrice(cursor.getString(count++));
					dto.setNoOfItems(cursor.getString(count++));
					dto.setExpiryDays(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setSellingCostperItem(cursor.getString(count++));
					dto.setSyncStatus(cursor.getInt(count++));

					instList.add(dto);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getRecordsWithValues", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}

	public String isDishNameExist(SQLiteDatabase dbObject, String dishName) {
		String instList = "";
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = dbObject
					.rawQuery(
							"SELECT dish_name FROM tbl_dishes WHERE dish_name = ? COLLATE NOCASE;  ",
							new String[] { dishName });
			if (cursor.moveToFirst()) {
				do {

					instList = cursor.getString(count++);

					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- isDishNameExist", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			dbObject.close();
		}

		return instList;
	}
	
public ProductDTO getRecordsByDishID(SQLiteDatabase dbObj,String dishid) {
		
		ProductDTO dto = new ProductDTO();		Cursor cursor = null;
		
		int count	= 0;
		
		try 
		{
			cursor = dbObj.rawQuery("SELECT dish_name,vat FROM tbl_dishes WHERE  dish_id = '"+ dishid +"'", null);
			if (cursor.moveToFirst())
			{
				do
				{
					
					dto.setName(cursor.getString(count++));
					dto.setVat(cursor.getString(count++));
					dto.setQuantity("0");
					
					count	= 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DishesDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return dto;
	}

}
