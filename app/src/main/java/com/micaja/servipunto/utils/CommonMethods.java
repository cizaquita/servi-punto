/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/

package com.micaja.servipunto.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.service.PromotionClickService;
import com.micaja.servipunto.service.PromotionPurchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public final class CommonMethods {

	private static TextView provider, product, program, document_type, documentNo, otpNo, celularNo, emailId, accountNo,
			valueNo;
	private static ImageView close;
	private static EditText key;
	private static boolean isValid;
	public static ProgressDialog progressDialog;
	public Context context;

	/**
	 * Result of this method, Toast message that can be display on the screen.It
	 * will appear only few seconds. message argument is specifies the message
	 * of toast. context argument is specifies context of activity.
	 * <p>
	 * 
	 * @param message
	 *            the message of the toast
	 * @param context
	 *            context of application or activity
	 * @return none
	 * @see Toast message
	 */

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Result of this method, Navigate from one screen to another screen.
	 * current argument is specifies current screen. next argument is specifies
	 * next screen.
	 * <p>
	 * 
	 * @param current
	 *            Current activity
	 * @param next
	 *            next activity or next class
	 * @return none
	 * @see target screen
	 */

	public static void startIntent(Activity current, Class<?> next) {

		Intent intent = new Intent(current, next);
		current.startActivity(intent);

	}

	/**
	 * This method using for display progress dialog. message argument is
	 * specifies message of progress dialog. context argument is specifies
	 * application context or activity context.
	 * 
	 * <p>
	 * 
	 * @param message
	 *            message of dialog
	 * @param context
	 *            application context or activity context.
	 * @see progress dialog at required places.
	 */

	public static void showProgressDialog(String message, Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * This method using for dismiss progress dialog.
	 */

	public static void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}

	/**
	 * This method using for Handler message to update the UI thread. handler
	 * argument is handler object. message argument is message of handler. arg
	 * argument is specifies message argument.
	 * 
	 * <p>
	 * 
	 * @param handler
	 *            handler object.
	 * @param message
	 *            message of handler.
	 * @param arg
	 *            message argument.
	 */

	public static void sendMsgToHandler(Handler handler, String message, int arg) {
		Message msg = new Message();
		if (message != null && !"".equals(message))
			msg.obj = message;

		msg.arg1 = arg;

		handler.sendMessage(msg);
	}

	public static void openNewActivity(Context context, Class<?> next) {
		Intent intent = new Intent(context, next);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static AlertDialog displayAlert(boolean deleteIcon, String title, String message, String button1,
			String button2, Context context, OnClickListener listener, boolean cancelable) {
		System.out.println("Inside alert");
		AlertDialog alert = new AlertDialog.Builder(context).create();

		if (deleteIcon)
			alert.setIcon(R.drawable.alertimage_small);

		alert.setCancelable(cancelable);
		alert.setTitle(title);
		alert.setMessage(message);

		alert.setButton(DialogInterface.BUTTON_POSITIVE, button1, listener);
		if (!"".equals(button2))
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, button2, listener);

		alert.show();

		return alert;
	}

	public static void showcustomdialogbox(Context context, String title, String messages, OnClickListener listener) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alerdialog);
		final TextView txt_errmsg = (TextView) dialog.findViewById(R.id.txt_errmsg);
		txt_errmsg.setText(messages);
		txt_errmsg.setVisibility(View.VISIBLE);
		final Button btnOk;
		btnOk = (Button) dialog.findViewById(R.id.btn_OK);
		btnOk.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
		dialog.show();
		dialog.setCancelable(false);
	}

	public static void showCustomAlertWithTwobtns(Context context, String title, String messages,
			android.content.DialogInterface.OnClickListener listener) {
		final Dialog dialog2 = new Dialog(context);
		dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog2.setContentView(R.layout.custom_alertdialog_withtwobtns);
		final TextView txt_errmsg = (TextView) dialog2.findViewById(R.id.txt_errmsg);
		txt_errmsg.setText(messages);
		txt_errmsg.setVisibility(View.VISIBLE);
		final Button btnYes, btnNo;
		btnYes = (Button) dialog2.findViewById(R.id.btn_yes);
		btnNo = (Button) dialog2.findViewById(R.id.btn_no);
		btnYes.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog2.dismiss();
				return false;
			}
		});
		btnNo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog2.dismiss();
				return false;
			}
		});
		dialog2.show();
		dialog2.setCancelable(false);
	}

	
	public static void showCustomAlertWithOneButton(Context context, String title, String messages,
			android.content.DialogInterface.OnClickListener listener) {
		final Dialog dialog2 = new Dialog(context);
		dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog2.setContentView(R.layout.custom_alertdialog_withtwobtns);
		final TextView txt_errmsg = (TextView) dialog2.findViewById(R.id.txt_errmsg);
		final TextView txt_dialog_title=(TextView)dialog2.findViewById(R.id.txt_dialog_title);
		txt_dialog_title.setText(title);
		txt_errmsg.setText(messages);
		txt_errmsg.setVisibility(View.VISIBLE);
		final Button btnYes, btnNo;
		btnYes = (Button) dialog2.findViewById(R.id.btn_yes);
		btnNo = (Button) dialog2.findViewById(R.id.btn_no);
		btnNo.setVisibility(View.GONE);
		btnYes.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog2.dismiss();
				return false;
			}
		});
		btnNo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog2.dismiss();
				return false;
			}
		});
		dialog2.show();
		dialog2.setCancelable(false);
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String getKeyFromHash(Hashtable<String, String> maleRemarksTable, String seletedId) {
		for (Map.Entry<String, String> entry : maleRemarksTable.entrySet()) {

			if (entry.getValue().equals(seletedId))
				return entry.getKey();
		}
		return null;
	}

	public static String getRoundedVal(double value) {

		/*
		 * DecimalFormat format = new DecimalFormat("0.00"); return
		 * format.format(value);
		 */

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("0.00", dfs);

		Locale L = new Locale("en");
		Locale.setDefault(L);

		return format.format(value);
	}

	public static String getNumSeparator(double value) {

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(',');
		DecimalFormat format = new DecimalFormat("###,###,##0.00", dfs);

		Locale L = new Locale("en");
		Locale.setDefault(L);

		return format.format(value);
	}

	public static String getNumSeparator(String value) {

		Double valueTemp = getDoubleFormate (value);

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(',');
		DecimalFormat format = new DecimalFormat("###,###,##0.00", dfs);

		Locale L = new Locale("en");
		Locale.setDefault(L);

		return format.format(valueTemp);
	}

	/**
	 * Result of this method, Toast message that can be display on the screen.It
	 * will appear only few seconds. message argument is specifies the message
	 * of toast. context argument is specifies context of activity.
	 * <p>
	 * 
	 * @param message
	 *            the message of the toast
	 * @param context
	 *            context of application or activity
	 * @return none
	 * @see Toast message
	 */

	public static void showCustomToast(Context context, String message) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();

		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) ((Activity) context).findViewById(R.id.customToast));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(message);

		Toast toast = new Toast(context);
		toast.setDuration(10000000);
		toast.setView(layout);
		toast.show();
	}

	public static String UOMConversions(String qtyFromTable, String uomFromTable, String qtyFromList,
			String uomFromList, String selDishQty, String type) {
		double finalQty = 0;
		if (uomFromTable.equalsIgnoreCase("kg") || uomFromTable.equalsIgnoreCase("lt")) {
			if (uomFromList.equalsIgnoreCase("kg") || uomFromList.equalsIgnoreCase("lt")) {
				if (type.equals(SalesEditTypes.INVOICE_ADJUSTMENT.code()))
					finalQty = getDoubleFormate(qtyFromTable)
							+ (getDoubleFormate(qtyFromList) * getDoubleFormate(selDishQty));
				else
					finalQty = getDoubleFormate(qtyFromTable)
							- (getDoubleFormate(qtyFromList) * getDoubleFormate(selDishQty));
			} else if (uomFromList.equalsIgnoreCase("gm") || uomFromList.equalsIgnoreCase("ml")) {
				double kgToGm = getDoubleFormate(qtyFromTable) * 1000;
				if (type.equals(SalesEditTypes.INVOICE_ADJUSTMENT.code()))
					finalQty = kgToGm + (getDoubleFormate(qtyFromList) * (getDoubleFormate(selDishQty)));
				else
					finalQty = kgToGm - (getDoubleFormate(qtyFromList) * (getDoubleFormate(selDishQty)));
				finalQty = finalQty * 0.001;
			}
		} else if (uomFromTable.equalsIgnoreCase("gm") || uomFromTable.equalsIgnoreCase("ml")) {
			if (uomFromList.equalsIgnoreCase("kg") || uomFromList.equalsIgnoreCase("lt")) {
				double kgToGm = getDoubleFormate(qtyFromList) * 1000;
				if (type.equals(SalesEditTypes.INVOICE_ADJUSTMENT.code()))
					finalQty = getDoubleFormate(qtyFromTable) + (kgToGm * getDoubleFormate(selDishQty));
				else
					finalQty = getDoubleFormate(qtyFromTable) - (kgToGm * getDoubleFormate(selDishQty));
			} else if (uomFromList.equalsIgnoreCase("gm") || uomFromList.equalsIgnoreCase("ml")) {
				if (type.equals(SalesEditTypes.INVOICE_ADJUSTMENT.code()))
					finalQty = getDoubleFormate(qtyFromTable)
							+ (getDoubleFormate(qtyFromList) * getDoubleFormate(selDishQty));
				else
					finalQty = getDoubleFormate(qtyFromTable)
							- (getDoubleFormate(qtyFromList) * getDoubleFormate(selDishQty));
			}
		} else
			finalQty = CommonMethods.getDoubleFormate(qtyFromTable) - CommonMethods.getDoubleFormate(qtyFromList);
		return String.valueOf(CommonMethods.getRoundedVal(finalQty));
	}

	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();

			return availableBlocks * blockSize;
		}

		return 0;
	}

	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/*************************
	 * To re-install the Application
	 ****************************/
	public static void reInstallAPK(Context context) {
		String fileName = Environment.getExternalStorageDirectory() + "/Download/" + Constants.SERVIRESTAURENT_APK_NAME;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	public static double getDoubleFormate(String input) {
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		Number number = null;
		double d = 0;
		if (!isDouble(input)) {
			try {
				number = format.parse(input);
				d = number.doubleValue();
			} catch (ParseException e) {
				d = 0.0;
			}
		} else
			d = Double.valueOf(input);

		return d;
	}

	private static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static double getBalanceQuantity(String barcode, String Quantity){
		try {
			InventoryDTO inventoryDTO = InventoryDAO.getInstance().getInventoryValue(DBHandler.getDBObj(Constants.READABLE), barcode);
			double currentBalanceQuantity = inventoryDTO.getQuantityBalance();
			return currentBalanceQuantity + CommonMethods.getDoubleFormate(Quantity);
		}
		catch (Exception e){
			e.printStackTrace();
			return -1.0;
		}



	}

	/***********************
	 * Generating Html For Printing Receipt
	 ********************/

	public static String generateHtmlForReceipt(String invoiceNo, String merchantName, String address, String city,
			String State, String ZipCode, String phoneNo, String dateAndTime, String customerName, String customerId,
			String customerAddr, String customerPhone, List<DTO> list) {
		String receiptHtml = "<html><head></head><body style='text-align: center;font-size:22px;width:600px'><div style='text-align: center;'>"
				+ "<span><b> " + invoiceNo + "</b></span><br /><br /> <br />" + "<span><b> " + merchantName
				+ "</b></span><br /> " + "<span> " + address + " </span><br />" + "<span> " + city + "," + State + "  :"
				+ ZipCode + "</span><br />" + "<span> " + phoneNo + " </span><br />" + "<span>" + dateAndTime
				+ " </span><br /> " + "<br /> </div><br />" + "<table style='width:100%;font-size:22px'>";

		for (int i = 0; i < list.size(); i++) {
			SalesDetailDTO dto = (SalesDetailDTO) list.get(i);
			dto.getCount();

			ProductDTO productDTO = ProductDAO.getInstance()
					.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE), dto.getProductId());
			receiptHtml = receiptHtml + "<tr><td style='width:75%; text-align:left;'>" + productDTO.getName()
					+ "</td><td style='width:10%; text-align:center;'>" + dto.getCount()
					+ "</td><td style='width:15%; text-align:right;'>"
					+ (Double.parseDouble(dto.getCount())) * Double.parseDouble(dto.getPrice()) + "</td></tr>";

		}
		// +"<tr><td style='width:75%; text-align:left;'>Item2</td><td
		// style='width:10%; text-align:center;'>5</td><td style='width:15%;
		// text-align:right;'>$50.00</td></tr> "
		// +"<tr><td style='width:75%; text-align:left;'><b>TAX ( 10
		// %)</b></td><td style='width:10%; text-align:center;'></td><td
		// style='width:150px; text-align:right;'><b> $250.00 </b></td></tr>"
		receiptHtml = receiptHtml + "</table>"
				+ "<hr></hr><table style='width:100%;font-size:22px;font-weight:bold;'><tr><td style='width:400px; text-align:left;'>TOTAL</td><td style='width:50px; text-align:right;'> $270.00 </td></tr></table><hr></hr>"
				+ "  <br /><table style='width:70%;font-size:22px; text-align:center;'align=center>  "
				+ "<tr><td style='width:40%; text-align:left;'>Customer Name</td><td style='width:10%; text-align:center;'>:</td><td style='width:40%; text-align:right;'>"
				+ customerName + "</td></tr>"
				+ "<tr><td style='width:40%; text-align:left;'>Customer Id</td><td style='width:10%; text-align:center;'>:</td><td style='width:40%; text-align:right;'>"
				+ customerId + "</td></tr>"
				+ "<tr><td style='width:40%; text-align:left;'>Customer Addr</td><td style='width:10%; text-align:center;'>:</td><td style='width:40%; text-align:right;'>"
				+ customerAddr + "</td></tr>"
				+ "<tr><td style='width:40%; text-align:left;'>Customer Phone</td><td style='width:10%; text-align:center;'>:</td><td style='width:40%; text-align:right;'>"
				+ customerPhone + "</td></tr>	"
				+ "</table><br /> <br /> <br /><div style='text-align: center;'>	<span>********** Thank You ************</span></div></body> </html> ";

		return receiptHtml;
	}

	public static boolean checkDBExist(Context mContext) {
		File database = mContext.getApplicationContext().getDatabasePath(Constants.DB_NAME);

		if (database.exists()) {
			Log.i("Database", "Found");
			return true;
		}
		return false;
	}

	public static String getUOMPriceConverter(String priceFromTable, String uomFromTable, String uomFromList,
			String qty) {
		double finalPrice = 0;
		if (uomFromTable.equalsIgnoreCase("kg") || uomFromTable.equalsIgnoreCase("lt")) {
			if (uomFromList.equalsIgnoreCase("kg") || uomFromList.equalsIgnoreCase("lt")) {
				finalPrice = CommonMethods.getDoubleFormate(priceFromTable);
			} else if (uomFromList.equalsIgnoreCase("gm") || uomFromList.equalsIgnoreCase("ml")) {
				double forGm = CommonMethods.getDoubleFormate(priceFromTable) * 0.001;
				finalPrice = forGm;// forGm *
									// CommonMethods.getDoubleFormate(qty);
			}
		} else if (uomFromTable.equalsIgnoreCase("gm") || uomFromTable.equalsIgnoreCase("ml")) {
			if (uomFromList.equalsIgnoreCase("kg") || uomFromList.equalsIgnoreCase("lt")) {
				double forKg = CommonMethods.getDoubleFormate(priceFromTable) * 1000;
				finalPrice = forKg;// forKg *
									// CommonMethods.getDoubleFormate(qty);
			} else if (uomFromList.equalsIgnoreCase("gm") || uomFromList.equalsIgnoreCase("ml")) {
				finalPrice = CommonMethods.getDoubleFormate(priceFromTable);
			}
		}
		return String.valueOf(CommonMethods.getRoundedVal(finalPrice));
	}

	public static void getpurchaseprice(SalesDetailDTO dto) {
		try {
			if (dto.getDishId() != null) {
				ProductDTO pdto = ProductDAO.getInstance().getRecordsByBarcode(DBHandler.getDBObj(Constants.READABLE),
						dto.getProductId());
				ServiApplication.purchase_price = Double.parseDouble(pdto.getPurchasePrice());
			}
		} catch (Exception e) {
			if (dto.getDishId() != null) {
				DishesDTO ddto = DishesDAO.getInstance().getRecordsByDish_id(DBHandler.getDBObj(Constants.READABLE),
						dto.getDishId());
				Double itemsprice = Double.parseDouble(ddto.getDishPrice());
				Double no_items = Double.parseDouble(ddto.getNoOfItems());
				if (no_items == 0) {
					ServiApplication.purchase_price = 0.00;
				} else {
					ServiApplication.purchase_price = itemsprice / no_items;
				}
			} else {
				ServiApplication.purchase_price = 0.00;
			}
		}
	}

	public static void hidden_softkey(Context context) {
		((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public static void click_promo(Context context) {
		Intent intent = new Intent(context, PromotionClickService.class);
		context.startService(intent);
	}

	public static void purchase_promo(Context context) {
		Intent intent = new Intent(context, PromotionPurchase.class);
		context.startService(intent);
	}

	/* SHA-1 Encryption */
	public static String encryptrsadata(String request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(request.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return sha1;
	}

	public static String sha1(String password) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return sha1;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// private static String convertToHex(byte[] data) {
	// StringBuffer buf = new StringBuffer();
	// for (int i = 0; i < data.length; i++) {
	// int halfbyte = (data[i] >>> 4) & 0x0F;
	// int two_halfs = 0;
	// do {
	// if ((0 <= halfbyte) && (halfbyte <= 9)) {
	// buf.append((char) ('0' + halfbyte));
	// } else {
	// buf.append((char) ('a' + (halfbyte - 10)));
	// }
	// halfbyte = data[i] & 0x0F;
	// } while (two_halfs++ < 1);
	// }
	// return buf.toString();
	// }

	// private static String byteToHex(final byte[] hash) {
	// byte[] hash1 = Arrays.copyOf(hash, 16);
	// SecretKeySpec sha = new SecretKeySpec(hash1, "AES");
	//
	// // Formatter formatter = new Formatter();
	// // for (byte b : hash) {
	// // formatter.format("%02x", b);
	// // }
	// // String result = formatter.toString();
	// // formatter.close();
	// return "" + sha;
	// }

	public static String getIpAddressOfDevice(Context context) {
		String ip = null;
		try {
			TelephonyManager telmanager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			Log.v("BABU", "babu Device id" + telmanager.getDeviceId());
			return ip = telmanager.getDeviceId();
		} catch (Exception e) {
			return ip;
		}
	}

	public static String encrypt(String cleartext) throws Exception {
		byte[] rawKey = getRawKey(ServiApplication.AES_secret_key.getBytes());
		byte[] result = encrypt(rawKey, cleartext.getBytes());
		return result.toString();
	}

	// private static String toHex(byte[] buf) {
	// if (buf == null)
	// return "";
	// StringBuffer result = new StringBuffer(2 * buf.length);
	// for (int i = 0; i < buf.length; i++) {
	// appendHex(result, buf[i]);
	// }
	// return result.toString();
	// }
	//
	// private final static String HEX = ServiApplication.AES_secret_key;
	//
	// private static void appendHex(StringBuffer sb, byte b) {
	// sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	// }
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static SecretKeySpec createKeyFromString(String llva) {
		MessageDigest sha = null;
		byte[] key;
		SecretKeySpec secretKey = null;
		try {
			key = llva.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return secretKey;
	}

	// public static String AESCrypt(String password) {
	//
	// SecretKeySpec key=createKeyFromString(ServiApplication.AES_secret_key);
	// try {
	// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	// cipher.init(Cipher.ENCRYPT_MODE, key);
	// return (Base64.getEncoder().encodeToString(cipher.doFinal(password
	// .getBytes("UTF-8"))));
	// } catch (Exception e) {
	// System.out.println("Error while Crypting: " + e.toString());
	// }
	// return null;
	// }
	//
	// public static String AESDecrypt(String ciphertext, SecretKeySpec key) {
	// try {
	// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	// cipher.init(Cipher.DECRYPT_MODE, key);
	// System.out.println("llave que llega "
	// + Base64.getEncoder().encodeToString(key.getEncoded()));
	// return (new String(cipher.doFinal(Base64.getDecoder().decode(
	// ciphertext.getBytes("UTF-8")))));
	// } catch (Exception e) {
	// System.out.println("Error while decrypting: " + e.toString());
	// }
	// return null;
	//
	// }

	public static boolean keyvalidation(String vale) {
		if (vale.length() >= 6 && vale.length() <= 10) {
			return false;
		}
		return true;
	}

	public static boolean hoversparision(String keygendate, String user, String auto_genuser) {
		Log.v("varahlababu", "varahalababu" + keygendate);
		String dateStart = keygendate;
		String dateStop = Dates.c_date();
		Date d1 = null;
		Date d2 = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			Log.v("varahlababu", "varahalababu" + diffMinutes);
			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");
			if (diffHours <= 8) {
				return true;
			} else {
				Log.v("varahlababu", "varahalababu" + "else");
				return true;
			}

		} catch (Exception e) {
			Log.v("varahlababu", "varahalababu" + e.getMessage().toString());
			return false;
		}

	}

	public static int getInternetSpeed(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			return 0;
		} else {
			return -1;
		}
	}

	public static String utf8string(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8").replace("ￓ", "Ó").replace("￁", "Á").replace("ￍ", "Í");
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}

	public static String getLettersOfNumaric(String strOrig, Context context) {
		String temp = "";

		char[] stringArray = strOrig.toCharArray();
		for (int index = 0; index < stringArray.length; index++) {

			if (stringArray[index] == '0') {
				temp = temp + context.getString(R.string.l_zero) + " ";
			} else if (stringArray[index] == '1') {
				temp = temp + context.getString(R.string.l_one) + " ";
			} else if (stringArray[index] == '2') {
				temp = temp + context.getString(R.string.l_two) + " ";
			} else if (stringArray[index] == '3') {
				temp = temp + context.getString(R.string.l_three) + " ";
			} else if (stringArray[index] == '4') {
				temp = temp + context.getString(R.string.l_four) + " ";
			} else if (stringArray[index] == '5') {
				temp = temp + context.getString(R.string.l_five) + " ";
			} else if (stringArray[index] == '6') {
				temp = temp + context.getString(R.string.l_six) + " ";
			} else if (stringArray[index] == '7') {
				temp = temp + context.getString(R.string.l_seven) + " ";
			} else if (stringArray[index] == '8') {
				temp = temp + context.getString(R.string.l_eight) + " ";
			} else if (stringArray[index] == '9') {
				temp = temp + context.getString(R.string.l_nine) + " ";
			}
		}
		return temp;
	}

	public static void drawer_test(Context context) {
		
		ESCPOSPrinter printer = new ESCPOSPrinter();
		UsbDevice usbdevice = null;
		printer.setContext(context);
		int result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);

		if (ESCPOSConst.CMP_SUCCESS == result) {
			printer.printText("Hello world", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
			printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
			printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
			printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
			result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
			printer.openDrawer(ESCPOSConst.CMP_DRAWER_1, ESCPOSConst.CMP_DRAWER_2);
			printer.disconnect();
		} else {
			CommonMethods.showCustomToast(context, context.getString(R.string.printer_errmsg));
		}
	}
	
	public static String firstLetterasUppercase(String value){
		if (value.length()>2) {
			return value.substring(0, 1).toUpperCase()+ value.substring(1).toLowerCase();
		}else{
			return value;
		}
		
	}
	
	public static String removeAsrics(String value){
		if (value.length()>2) {
			return value.substring(0, 1).toUpperCase()+ value.substring(1).toLowerCase();
		}else{
			return value.replace("*", "");
		}
	}
}
