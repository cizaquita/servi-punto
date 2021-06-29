package com.micaja.servipunto.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;


public final class MakeHeader {
	
	public static Header makeHeader(Context context,String password,String processid,String username,String jsondata) throws KeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, GeneralSecurityException, IOException {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		UserDetailsDTO udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		Header header = new Header();
		header.setLocaldate(Dates.current_date());
		header.setPassword(password);
		header.setProcessid(processid);
		header.setSerial(CommonMethods
				.getIpAddressOfDevice(context));
		header.setSign(CommonMethods.encryptrsadata(Dates.current_dateddmmyyy()+ username+ processid + jsondata));
		header.setToken(ServiApplication.SOAP_token);
		header.setTrace(ServiApplication.SOAP_trace);
		header.setUser(username);
		
		Log.v("=============", header.toString());
		return header;
	}
	public static PropertyInfo makepropertyInfo1(Header values) throws KeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, GeneralSecurityException, IOException {
		PropertyInfo pi = new PropertyInfo();
		pi.setName("header");
		pi.setValue(values);
		pi.setType(Header.class);
		return pi;
	}
	
	public static PropertyInfo makepropertyInfo2(String json,String type) throws KeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, GeneralSecurityException, IOException {
		PropertyInfo pi = new PropertyInfo();
		Data data = new Data();
		data.setData(json);
		data.setType(type);
		pi.setName("data");
		pi.setValue(data);
		pi.setType(Data.class);
		return pi;
	}
}
