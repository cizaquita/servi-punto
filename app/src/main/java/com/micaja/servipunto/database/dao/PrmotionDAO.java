package com.micaja.servipunto.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;

public class PrmotionDAO implements DAO {

	private static PrmotionDAO promotionsDTO;

	private PrmotionDAO() {

	}

	public static PrmotionDAO getInstance() {
		if (promotionsDTO == null)
			promotionsDTO = new PrmotionDAO();

		return promotionsDTO;
	}

	@Override
	public boolean insert(SQLiteDatabase dbObj, List<DTO> list) {

		try {
			dbObj.beginTransaction();

			SQLiteStatement stmt = dbObj
					.compileStatement("INSERT INTO tbl_promotion(image_url, name, video_url , store_code , supplier_code , supplier_name , value , where_to_show ,  end_date , start_date , promotion_des , promoid ,orderval )VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = 1;

			for (DTO items : list) {

				PromotionsDTO dto = (PromotionsDTO) items;
				stmt.bindString(count++, dto.getImage_url());
				stmt.bindString(count++, dto.getName());
				stmt.bindString(count++, dto.getVideo_url());
				stmt.bindString(count++, dto.getStore_code());
				stmt.bindString(count++, dto.getSupplier_code());
				stmt.bindString(count++, dto.getSupplier_name());
				stmt.bindString(count++, dto.getValue());
				stmt.bindString(count++, dto.getWhere_to_show());
				stmt.bindString(count++, dto.getEnd_date());
				stmt.bindString(count++, dto.getStart_date());
				stmt.bindString(count++, dto.getPromotion_des());
				stmt.bindLong(count++, dto.getPromoid());
				stmt.bindLong(count++, dto.getOrder_promotion());
				count = 1;

				stmt.executeInsert();
			}

			dbObj.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e("clientDAO  -- insert", e.getMessage());
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

		int count = 0;

		try {
			cursor = dbObj.rawQuery("SELECT * FROM tbl_promotion", null);
			if (cursor.moveToFirst()) {
				do {
					PromotionsDTO dto = new PromotionsDTO();
					dto.setImage_url(cursor.getString(count++));
					dto.setName(cursor.getString(count++));
					dto.setVideo_url(cursor.getString(count++));
					dto.setStore_code(cursor.getString(count++));
					dto.setSupplier_code(cursor.getString(count++));
					dto.setSupplier_name(cursor.getString(count++));
					dto.setValue(cursor.getString(count++));
					dto.setWhere_to_show(cursor.getString(count++));
					dto.setEnd_date(cursor.getString(count++));
					dto.setStart_date(cursor.getString(count++));
					dto.setPromotion_des(cursor.getString(count++));
					dto.setPromoid(cursor.getLong(count++));
					dto.setOrder_promotion(cursor.getLong(count++));
					instList.add(dto);
					count = 0;

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("clientDAO  -- getRecords", e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();

			dbObj.close();
		}

		return instList;

	}

	@Override
	public List<DTO> getRecordsWithValues(SQLiteDatabase dbObj,
			String columnName, String columnValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteAllRecords(SQLiteDatabase dbObj) {
		try {
			dbObj.compileStatement("DELETE  FROM tbl_promotion").execute();
			return true;
		} catch (Exception e) {
			Log.e("tbl_orders  -- delete", e.getMessage());
		}

		finally {
			dbObj.close();
		}
		return false;
	}

}
