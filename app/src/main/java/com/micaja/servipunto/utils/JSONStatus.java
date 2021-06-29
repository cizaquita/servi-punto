package com.micaja.servipunto.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.micaja.servipunto.ServiApplication;

import java.util.HashMap;
import java.util.Iterator;

public class JSONStatus {

	public int status(String string) {
		try {
			JSONObject jsonobj = new JSONObject(string);
			return jsonobj.getInt("status");
		} catch (Exception e) {
			return 1;
		}
	}

	public HashMap<String,Double> data (String data){
		try{
			JSONObject jsonobj = new JSONObject(data);
			JSONArray jsonArray = jsonobj.getJSONArray("data");
			HashMap<String, Double> pairs = new HashMap<String, Double>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject a= (JSONObject) jsonArray.get(i);
				String barcode = a.get("barcode").toString();
				double quantity = (Double) a.get("quantity");
				pairs.put(barcode, quantity);
			}
			return pairs;

		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String message(String string) {
		try {
			return new JSONObject(string).getString("message");
		} catch (Exception e) {
			return null;
		}
	}

	public boolean getStoreCode(String string) {
		try {
			ServiApplication.store_id = new JSONObject(string).getJSONObject(
					"user").getString("store_code");
			try {
				if (ServiApplication.store_id.length()>1) {
					return true;
				}else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public String notactivemsg(String string) {
		try {
			 return new JSONObject(string).getString("message");
		} catch (Exception e) {
			return null;
		}
	}

	public boolean user_id_comparision(String string) {
		String user_id1;
		try {

			if (ServiApplication.block_user_flage) {
				ServiApplication.block_user_flage = false;
				ServiApplication.res_user_id = ""
						+ new JSONObject(string).getLong("userid");
				return true;
			} else {
				user_id1 = ""+new JSONObject(string).getLong("userid");
				if (user_id1.equals(ServiApplication.res_user_id)) {
					ServiApplication.res_user_id = "" + user_id1;
					return true;
				} else {
					ServiApplication.login_session_count=0;
					return false;
				}
			}

		} catch (Exception e) {
			return false;
		}
	}
}
