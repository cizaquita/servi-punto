/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.database.dto.DTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Utils {

	public static Context context;
	public static String saleID;
	private static List<DTO> invoiceList = new ArrayList<DTO>();

	public static List<DTO> getInvoiceList() {
		return invoiceList;
	}

	public static void setInvoiceList(List<DTO> invoiceList) {
		Utils.invoiceList = invoiceList;
	}

	public static void clearEditText(ViewGroup viewGroup) {
		for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
			View view = viewGroup.getChildAt(i);
			if (view instanceof EditText) {
				((EditText) view).setText("");
			}

			if (view instanceof ViewGroup
					&& (((ViewGroup) view).getChildCount() > 0))
				clearEditText((ViewGroup) view);
		}
	}

	public static JSONArray sortJsonArray(JSONArray jsonArray, final String key){
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				jsonList.add(jsonArray.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(jsonList, new Comparator<JSONObject>() {
			public int compare(JSONObject a, JSONObject b) {
				try {
					return (a.getString(key).toLowerCase().compareTo(b.getString(key).toLowerCase()));
				} catch (JSONException e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
		JSONArray sortedJsonArray = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {
			sortedJsonArray.put(jsonList.get(i));
		}
		return sortedJsonArray;
	}

	public static String[] report_options = { "", "product_profit",
			"supplier_profit", "products_most_sold", "profit_days",
			"transaction_box", "accounts_receivable", "accounts_payable",
			"inventory_amount", "products_expired", "sales_by_date" };

}
