package com.micaja.servipunto.print;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.BaseActivity;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;

import PRTAndroidSDK.PRTAndroidPrint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PRTSDKApp extends BaseActivity {

	private Spinner spnPrinterList = null;
	private Button btnBT = null;
	private Button btnDisplayRemainingPower = null;
	private Button btnWiFi = null;
	private Button btnUSB = null;
	private EditText edtIP = null;
	private Button btnPrint = null;
	private TextView txtTips = null;
	private TextView txtRemainingPower = null;
	private EditText edtPrintText = null;
	private Spinner spinnerLanguage = null;
	private String ConnectType = "";
	private Context thisCon = null;
	private ArrayAdapter arrPrinterList;
	private static PRTAndroidPrint PRT = null;
	private BluetoothAdapter mBluetoothAdapter;
	private String strBTAddress = "", type;
	private Intent intent;
	private Bundle bundle;

	private Handler uiHandler;
	
	ESCPOSPrinter printer;
	UsbDevice usbdevice = null;
	private int result;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prtsdkapp);
		intent = getIntent();
		bundle = intent.getExtras();
		type = bundle.getString("babu");
		printer = new ESCPOSPrinter();
		printer.setContext(PRTSDKApp.this);
		result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		
		spnPrinterList = (Spinner) findViewById(R.id.spn_printer_list);
		btnBT = (Button) findViewById(R.id.btnBT);
		btnDisplayRemainingPower = (Button) findViewById(R.id.btnDisplayRemainingPower);
		txtRemainingPower = (TextView) findViewById(R.id.txtRemainingPower);
		btnWiFi = (Button) findViewById(R.id.btnWiFi);
		btnUSB = (Button) findViewById(R.id.btnUSB);
		edtIP = (EditText) findViewById(R.id.txtIPAddress);
		edtIP.setText("192.168.0.33");
		btnPrint = (Button) findViewById(R.id.btnPrint);
		txtTips = (TextView) findViewById(R.id.txtTips);
		edtPrintText = (EditText) findViewById(R.id.edtPrintText);
		intent = new Intent(PRTSDKApp.this, PRTSDKApp.class);
		ArrayAdapter<String> languageItem = getLangguageInfo();
		spinnerLanguage = (Spinner) findViewById(R.id.spn_language);
		languageItem.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinnerLanguage.setAdapter(languageItem);

		// Enable Bluetooth
		EnableBluetooth();

		thisCon = this.getApplicationContext();

		// Add Printer List
		InitCombox();

		btnBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (PRT != null) {
					PRT.CloseProt();
				}
				Intent serverIntent = new Intent(PRTSDKApp.this, DeviceListActivity.class);
				startActivityForResult(serverIntent, 10);
				ConnectType = "Bluetooth";
				return;
			}
		});

		btnDisplayRemainingPower.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (PRT == null) {
					Toast.makeText(thisCon, R.string.no_printer_object, Toast.LENGTH_LONG).show();
					return;
				}

				if (!PRT.IsOpen()) {
					Toast.makeText(thisCon, R.string.warmingconnect, Toast.LENGTH_LONG).show();
					return;
				}
				txtRemainingPower.setText(PRT.PRTGetRemainingPower());
				return;
			}
		});

		btnWiFi.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (PRT != null) {
					PRT.CloseProt();
				}
				String strIP = "";
				strIP = edtIP.getText().toString().trim();
				ConnectType = "WiFi";
				if (strIP.length() == 0) {
					Toast.makeText(thisCon, R.string.err_noIP, Toast.LENGTH_SHORT).show();
					return;
				}

				// PRT=new
				// PRTAndroidPrint(thisCon,"WiFi",arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());
				PRT = new PRTAndroidPrint(thisCon, "WiFi");
				// PRT.OpenPort("192.168.0.33,9100")
				if (!PRT.OpenPort(strIP + ",9100")) {
					txtTips.setText(thisCon.getString(R.string.connecterr));
					Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
					return;
				} else {
					PRT.InitPort();
					txtTips.setText("Printer:" + spnPrinterList.getSelectedItem().toString().trim());
					Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});

		btnUSB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (PRT != null) {
					PRT.CloseProt();
				}
				ConnectType = "USB";
				PRT = new PRTAndroidPrint(thisCon, "USB",
						arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());
				ServiApplication
						.setPrinter_id(arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());
				// USB not need call "iniPort"
				if (!PRT.OpenPort("")) {
					txtTips.setText(thisCon.getString(R.string.connecterr));
					Toast.makeText(thisCon, R.string.connecterr + PRT.GetPrinterName(), Toast.LENGTH_SHORT).show();
					return;
				} else {
					txtTips.setText("Printer:" + spnPrinterList.getSelectedItem().toString().trim());
					Toast.makeText(thisCon,
							R.string.connected + "  "
									+ arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});

		btnPrint.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (PRT == null) {
					Toast.makeText(thisCon, R.string.no_printer_object, Toast.LENGTH_LONG).show();
					return;
				}
				if (!PRT.IsOpen()) {
					Toast.makeText(thisCon, R.string.warmingconnect, Toast.LENGTH_LONG).show();
					return;
				}
				try {
					//PRT.PRTReset();
					System.out.println(PRT.PRTGetCurrentStatus());
					System.out.println(PRT.CloseProt());
					PRT.InitPort();

					if (PRT.PRTCapPrintCurveText()) {
						int nPos = 0;
						for (int i = 0; i < 12; i++) {
							if (!PRT.PRTPrintCurveText(i, nPos, "Hello word")) {
								break;
							}
							nPos = nPos + 30;
						}
					}
					if (!ConnectType.equals("USB") && !ConnectType.equals("WiFi")) {
						if (PRT.PRTGetCurrentStatus() == 3) {
							Toast.makeText(thisCon, R.string.PrintIsNotReady, Toast.LENGTH_SHORT).show();
							return;
						}
					}

					// some printer haven't this function
					if (PRT.PRTCapBKPrintMode()) {
						PRT.PRTEnterBKPrintMode(0x0000, 0x017B);
						PRT.PRTExitBKPrintMode();
					}

					if (PRT.PRTCapPrintCurveText()) {
						int nPos = 0;
						for (int i = 0; i < 12; i++) {
							if (!PRT.PRTPrintCurveText(i, nPos, "Hello word")) {
								break;
							}
							nPos = nPos + 30;
						}
					}

					if (PRT.PRTCapPrintHLine()) {
						int nIndex;
						int nLineData[] = new int[2];
						for (int i = 0; i < 1200; i++) {
							nIndex = (int) (Math.sin(((double) i) / 180.0 * 3.1416) * (380 - 30) / 2 + 180);
							nLineData[0] = nIndex; // begin position
							nLineData[1] = nIndex; // end position.if both of
													// them,print a point
							if (!PRT.PRTPrintHLines(1, nLineData)) {
								break;
							}
						}
					}
					// print string
					if (!edtPrintText.getText().toString().equals("")) {
						String codeLanguage = spinnerLanguage.getSelectedItem().toString().trim();
						HashMap<String, Integer> map = getMapLanguage();
						HashMap<String, String> codeMap = getCodeLanguage();

						int intLanguageNum = 23;
						String codeL = "gb2312"; // chinese charset
						if (codeMap.containsKey(codeLanguage)) {
							codeL = codeMap.get(codeLanguage);
						}
						if (map.containsKey(codeLanguage)) {
							intLanguageNum = map.get(codeLanguage);
						}
						PRT.Language = "utf-8";
						// PRT.PRTSendString(spinnerLanguage.getSelectedItem().toString().trim()+":\n");
						PRT.PRTSendString(URLEncoder.encode(spinnerLanguage.getSelectedItem().toString().trim() + ":\n",
								"utf-8"));
						// String s1=URLEncoder.encode(s, "utf-8");
						byte data[] = null;
						byte sendText[] = new byte[3];
						sendText[0] = 0x1B;
						sendText[1] = 0x74;
						sendText[2] = (byte) intLanguageNum;
						PRT.WriteData(sendText, sendText.length);

						String strPrintText = edtPrintText.getText().toString();
						// data =
						// (URLEncoder.encode(thisCon.getString(R.string.originalsize)
						// + strPrintText,"utf-8")).getBytes(codeL);
						data = (strPrintText).getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						// widthï¼Œheightï¼Œboldï¼Œunderlineï¼Œminifont
						PRT.PRTFormatString(true, false, false, false, false);
						PRT.WriteData(sendText, sendText.length);
						data = (URLEncoder.encode(thisCon.getString(R.string.heightsize) + strPrintText, "utf-8"))
								.getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						PRT.PRTFormatString(false, true, false, false, false);
						PRT.WriteData(sendText, sendText.length);
						data = (URLEncoder.encode(thisCon.getString(R.string.widthsize) + strPrintText, "utf-8"))
								.getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						PRT.PRTFormatString(true, true, false, false, false);
						PRT.WriteData(sendText, sendText.length);
						data = (URLEncoder.encode(thisCon.getString(R.string.heightwidthsize) + strPrintText, "utf-8"))
								.getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						PRT.PRTFormatString(false, false, true, false, false);
						PRT.WriteData(sendText, sendText.length);
						data = (URLEncoder.encode(thisCon.getString(R.string.bold) + strPrintText, "utf-8"))
								.getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						PRT.PRTFormatString(false, false, false, true, false);
						PRT.WriteData(sendText, sendText.length);
						data = (URLEncoder.encode(thisCon.getString(R.string.underline) + strPrintText, "utf-8"))
								.getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();

						if (PRT.PRTCapPrintMiniFont()) {
							PRT.PRTFormatString(false, false, false, false, true);
							PRT.WriteData(sendText, sendText.length);
							data = (thisCon.getString(R.string.minifront) + strPrintText).getBytes(codeL);
							PRT.WriteData(data, data.length);
						}
					}

					PRT.PRTFeedLines(20);
					PRT.PRTReset();

					// print barcode
					if (PRT.PRTCapPrintBarcode()) {
						Barcode_BC_UPCA();
						Barcode_BC_UPCE();
						Barcode_BC_EAN8();
						Barcode_BC_EAN13();
						Barcode_BC_CODE93();
						Barcode_BC_CODE39();
						Barcode_BC_CODEBAR();
						Barcode_BC_ITF();
						Barcode_BC_CODE128();
					}

					PRT.PRTSendString("\n");

					if (PRT.PRTCapPrintBarcode2()) {
						// print QR code
						PRT.PRTSendString("BarCode2:0123456789abcdef0123456789abcdef\n");
						// print QR Code:
						// Align:0:Left,1:Center,2:Right
						// LeftMargin:0mm~4112mm
						// Model:49,50
						// Size:1~16
						// ErrLevel:48,49,50,51
						// pData:Code String
						PRT.PRTPrintBarcode2(0, 5, 49, 8, 48, "0123456789abcdef0123456789abcdef");
						PRT.PRTFeedLines(240);
						PRT.PRTFeedLines(240);
					}
					if (PRT.PRTCapPaperCut()) {
						PRT.PRTPaperCut(true); // true:half cut
					}
				} catch (UnsupportedEncodingException e) {
					Toast.makeText(thisCon, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});

		/*uiHandler = ServiApplication.getUiHandler();
		if (type.equals("non_print_long")) {
			type = "non_print";
			PRT = null;
			citizenPrinter1();
		} else {
			citizenPrinter();
		}*/

// callUsbPrinterConncetivity();
	
//		try {
//			if (PRT == null) {
//				if (type.equals("non_print")) {
//
//				} else {
//					setResult(Activity.RESULT_CANCELED);
//					Intent intent = new Intent();
//					setResult(10, intent);
//					finish();
//					CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
//				}
//				return;
//			} else {
//				checkPrint();
//			}
//		} catch (UnsupportedEncodingException e) {
//			sendMessage(false);
//			e.printStackTrace();
//		}
		
		
	}
	
	void citizenPrinter1(){

		if (ESCPOSConst.CMP_SUCCESS == result) {
			Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.print_logo);
			printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
			printer.printText("\n\n" + CommonMethods.utf8string(ServiApplication.print_flage), ESCPOSConst.CMP_ALIGNMENT_CENTER,
					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
			printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
			printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
			result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
			printer.disconnect();
			
			ServiApplication.allprinters_flage = false;
			// callService();
			Intent intent = new Intent();
			setResult(10, intent);
			finish();
			sendMessage(true);
			
		} else {
			sendMessage(false);
			setResult(Activity.RESULT_CANCELED);
			Intent intent = new Intent();
			setResult(10, intent);
			finish();
			CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
		}
	
	}

	public void citizenPrinter() {
		
		
		if (ESCPOSConst.CMP_SUCCESS == result) {
			Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.print_logo);
			printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
			printer.printText("\n\n" + CommonMethods.utf8string(ServiApplication.print_flage), ESCPOSConst.CMP_ALIGNMENT_CENTER,
					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
			printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
			printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
			printer.openDrawer(ESCPOSConst.CMP_DRAWER_1, 1);
			result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
			printer.disconnect();
			
			ServiApplication.allprinters_flage = false;
			// callService();
			Intent intent = new Intent();
			setResult(10, intent);
			finish();
			sendMessage(true);
			
		} else {
			sendMessage(false);
			setResult(Activity.RESULT_CANCELED);
			Intent intent = new Intent();
			setResult(10, intent);
			finish();
			CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
		}
	}


	private void checkPrint() throws UnsupportedEncodingException {
		setResult(Activity.RESULT_CANCELED);
		String codeLanguage = spinnerLanguage.getSelectedItem().toString().trim();
		HashMap<String, Integer> map = getMapLanguage();
		HashMap<String, String> codeMap = getCodeLanguage();
		int intLanguageNum = 23;
		String codeL = "gb2312"; // chinese charset
		if (codeMap.containsKey(codeLanguage)) {
			codeL = codeMap.get(codeLanguage);
		}
		if (PRT.PRTCapPrintMiniFont()) {
			if (ServiApplication.allprinters_flage) {
				PRT.PRTAlignType(0);
			} else {
				PRT.PRTAlignType(1);
			}
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.print_logo);
			PRT.PRTPrintBitmap(icon, 0);
			PRT.PRTReset();
			String strPrintText = ServiApplication.print_flage + "\n\n";
			byte[] data = (URLEncoder.encode(thisCon.getString(R.string.originalsize) + strPrintText, "utf-8"))
					.getBytes(codeL);
			data = (strPrintText).getBytes(codeL);
			if (ServiApplication.allprinters_flage) {
				PRT.PRTAlignType(0);
			} else {
				PRT.PRTAlignType(1);
			}
			PRT.WriteData(data, data.length);
			PRT.PRTFeedLines(20);
			PRT.PRTReset();
			PRT.PRTPaperCut(true);
			ServiApplication.allprinters_flage = false;
			// callService();
			Intent intent = new Intent();
			setResult(10, intent);
			finish();
			sendMessage(true);
		} else {
			sendMessage(false);
			CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
		}

	}

	private void callService() {

		try {
			ESCPOSPrinter printer = new ESCPOSPrinter();
			UsbDevice usbdevice = null;
			printer.setContext(PRTSDKApp.this);
			int result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
			if (ESCPOSConst.CMP_SUCCESS == result) {
				printer.openDrawer(ESCPOSConst.CMP_DRAWER_1, 1);
				printer.disconnect();
			} else {
			}
		} catch (Exception e) {
		}
	}

	public boolean Allprints() {
		boolean falge;
		String codeLanguage = spinnerLanguage.getSelectedItem().toString().trim();
		HashMap<String, Integer> map = getMapLanguage();
		HashMap<String, String> codeMap = getCodeLanguage();
		int intLanguageNum = 23;
		String codeL = "gb2312"; // chinese charset
		if (codeMap.containsKey(codeLanguage)) {
			codeL = codeMap.get(codeLanguage);
		}
		if (PRT.PRTCapPrintMiniFont()) {
			String strPrintText;
			try {
				try {
					if (ServiApplication.getPrinter_id().equals("DASCOM DT-210")) {
						strPrintText = ServiApplication.print_flage_eps + "\n\n";
					} else {
						strPrintText = ServiApplication.print_flage + "\n\n";
					}
				} catch (Exception e) {
					strPrintText = ServiApplication.print_flage + "\n\n";
				}
				byte[] data = (URLEncoder.encode(thisCon.getString(R.string.originalsize) + strPrintText, "utf-8"))
						.getBytes(codeL);
				data = (strPrintText).getBytes(codeL);
				PRT.WriteData(data, data.length);
				PRT.PRTFeedLines(20);
				PRT.PRTReset();
				PRT.PRTPaperCut(true);
				falge = true;
			} catch (Exception e) {
				CommonMethods.showCustomToast(PRTSDKApp.this, e.getMessage().toUpperCase());
				falge = false;
			}
		} else {
			falge = false;
			CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
		}
		CommonMethods.showCustomToast(PRTSDKApp.this, "Allprints");
		return falge;
	}

	private boolean Barcode_BC_UPCA() {
		PRT.PRTSendString("BC_UPCA:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_UPCA, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "075678164125");
	}

	private boolean Barcode_BC_UPCE() {
		PRT.PRTSendString("BC_UPCE:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_UPCE, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "01227000009");// 04252614
	}

	private boolean Barcode_BC_EAN8() {
		PRT.PRTSendString("BC_EAN8:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_EAN8, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "04210009");
	}

	private boolean Barcode_BC_EAN13() {
		PRT.PRTSendString("BC_EAN13:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_EAN13, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "6901028075831");
	}

	private boolean Barcode_BC_CODE93() {
		PRT.PRTSendString("BC_CODE93:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE93, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "TEST93");
	}

	private boolean Barcode_BC_CODE39() {
		PRT.PRTSendString("BC_CODE39:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE39, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "123456789");
	}

	private boolean Barcode_BC_CODEBAR() {
		PRT.PRTSendString("BC_CODEBAR:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODEBAR, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "A40156B");
	}

	private boolean Barcode_BC_ITF() {
		PRT.PRTSendString("BC_ITF:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_ITF, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "123456789012");
	}

	private boolean Barcode_BC_CODE128() {
		PRT.PRTSendString("BC_CODE128:\n");
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE128, PRTAndroidPrint.BC_DEFAULT, PRTAndroidPrint.BC_DEFAULT,
				PRTAndroidPrint.BC_HRIBELOW, "{BS/N:{C\014\042\070\116{A3"); // decimal
																				// 1234
																				// =
																				// octonary
																				// 1442
	}

	// create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prtsdkapp, menu);
		return true;
	}

	// back key
	// public boolean onKeyDown(int keyCode, KeyEvent event)
	// {
	// if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
	// {
	// new AlertDialog.Builder(this)
	// .setIcon(R.drawable.ic_launcher)
	// .setMessage(R.string.sureExit).setTitle(R.string.warmTips)
	// .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
	// {
	// public void onClick(DialogInterface dialog, int which)
	// {
	// finish();
	// }
	// }
	// ).setNegativeButton(R.string.cancel, new
	// DialogInterface.OnClickListener()
	// {
	// public void onClick(DialogInterface dialog, int which)
	// {
	//
	// }
	// }
	// ) .show();
	// }
	// return false;
	// }

	// menu click
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Quit:
			if (PRT != null) {
				PRT.CloseProt();
			}
			finish();
		}
		return false;
	}

	// call back by scan bluetooth printer
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (resultCode) {
			case 10:
				String strIsConnected = data.getExtras().getString("is_connected");
				if (strIsConnected.equals("NO")) {
					txtTips.setText(thisCon.getString(R.string.scan_error));
					Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
					return;
				} else {
					strBTAddress = data.getExtras().getString("BTAddress");
					if (strBTAddress == null) {
						return;
					} else if (!strBTAddress.contains(":")) {
						return;
					} else if (strBTAddress.length() != 17) {
						return;
					}

					PRT = new PRTAndroidPrint(thisCon, "Bluetooth", spnPrinterList.getSelectedItem().toString().trim());
					PRT.InitPort();

					if (!PRT.OpenPort(strBTAddress)) {
						Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
						txtTips.setText(thisCon.getString(R.string.scan_error));
						return;
					} else {
						Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
						txtTips.setText(thisCon.getString(R.string.scan_success));
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// add printer list
	// this version support these printer only.
	// if input other printer,SDK can't connect printer.
	private void InitCombox() {
		arrPrinterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		arrPrinterList = ArrayAdapter.createFromResource(this, R.array.printer_list,
				android.R.layout.simple_spinner_item);
		arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// set adapter to spinner
		spnPrinterList.setAdapter(arrPrinterList);
	}

	// EnableBluetooth
	private boolean EnableBluetooth() {
		boolean bRet = false;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isEnabled())
				return true;
			mBluetoothAdapter.enable();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!mBluetoothAdapter.isEnabled()) {
				bRet = true;
				Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
			}
		} else {
			Log.d("PRTLIB", "BTO_EnableBluetooth --> mBluetoothAdapter is null");
		}
		return bRet;
	}

	private ArrayAdapter<String> getLangguageInfo() {
		ArrayAdapter<String> languageItem = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		languageItem.add("Chinese");// China [ä¸­æ–‡]
		languageItem.add("CP437 [USA]");// CP437 [ç¾Žå›½ï¼Œæ¬§æ´²æ ‡å‡†]
		languageItem.add("KataKana [Japan]");// KataKana [ç‰‡å�‡å��] JIS_X_0201
		languageItem.add("Korea [Korea]");// éŸ©è¯­
		languageItem.add("CP720 [Arabic]");// CP720[é˜¿æ‹‰ä¼¯è¯­] iso8859-6
		languageItem.add("CP737 [Greek]");// CP737 [å¸Œè…Š] iso8859-7
		languageItem.add("CP755 [Eastern Europe, Latvia 2]");// MIK[æ–¯æ‹‰å¤«/ä¿�åŠ åˆ©äºš]
																// iso8859-5
		languageItem.add("CP775 [Baltic]");// æ³¢ç½—çš„æµ· iso8859-1
		languageItem.add("CP850 [Multilingual]");// CP850 [å¤šè¯­è¨€] iso8859-3
		languageItem.add("CP852 [Latin2]");// CP852 [æ‹‰ä¸�è¯­ 2] iso8859-2
		languageItem.add("CP855 [IBM Cyrillic]");// è¥¿é‡Œå°”è¯­ iso8859-5
		languageItem.add("CP856 [IBM Hebrew]");// [å¸Œä¼¯æ�¥è¯­] iso8859-8
		languageItem.add("CP857 [Turkish]");// CP857[åœŸè€³å…¶è¯­] iso8859-3
		languageItem.add("CP858 [Multilingual Latin â… +Euro]");// CP858
																// [å¤šç§�è¯­è¨€æ‹‰ä¸�è¯­
																// 1+æ¬§å…ƒç¬¦]
																// iso8859-15
		languageItem.add("CP860 [Portugal]");// CP860 [è‘¡è�„ç‰™] iso8859-1
		languageItem.add("CP862 [Hebrew]");// CP862 [å¸Œä¼¯æ�¥] iso8859-8
		languageItem.add("CP863 [Canada - French]");// CP863 [åŠ æ‹¿å¤§-æ³•è¯­]
													// iso8859-1
		languageItem.add("CP864 [Arabic]");// CP864 [é˜¿æ‹‰ä¼¯è¯­] iso8859-6
		languageItem.add("CP865 [Nordic]");// CP865 [åŒ—æ¬§] nls iso8859-1
		languageItem.add("CP866 [Slavic2]");// CP866 æ–¯æ‹‰å¤«2 iso8859-5
		languageItem.add("Cp874 [IBM Hebrew]");// [å¸Œä¼¯æ�¥è¯­] iso8859-8
		languageItem.add("WCP1250 [Central Europe]");// WCP1250[ä¸­æ¬§]
														// iso8859-2
		languageItem.add("WCP1251 [Slavic]");// WCP1251 [æ–¯æ‹‰å¤«è¯­] iso8859-5
		languageItem.add("WCP1252 [Latin 1]");// ï¼·CP1252 [æ‹‰ä¸�è¯­ 1]
												// iso8859-1
		languageItem.add("WCP1253 [Greek]");// WCP1253 [å¸Œè…Š] iso8859-7
		languageItem.add("WCP1254 [Turkish]");// WCP1254åœŸè€³å…¶è¯­] iso8859-3
		languageItem.add("WCP1255 [Hebrew]");// WCP1255[å¸Œä¼¯æ�¥è¯­] iso8859-8
		languageItem.add("WCP1256 [Arabic]");// WCP1256[é˜¿æ‹‰ä¼¯è¯­] iso8859-6
		languageItem.add("WCP1257 [Baltic]");// WCP1257 [æ³¢ç½—çš„æµ·] iso8859-1
												// å¥—ç”¨è¥¿æ¬§ä½“ç³»
		languageItem.add("WCP1258 [Vietnamese]");// WCP1258[è¶Šå�—è¯­] bg2312
		languageItem.add("ISO-8859-1 [West Europe]");// ISO-8859-1 [è¥¿æ¬§]
														// iso8859-1
		languageItem.add("ISO-8859-2 [Latin 2]");// ISO-8859-2[æ‹‰ä¸�è¯­2]
		languageItem.add("ISO-8859-3 [Latin 3]");// ISO-8859-3[æ‹‰ä¸�è¯­3]
		languageItem.add("ISO-8859-4 [Baltic]");// ISO-8859-4[æ³¢ç½—çš„è¯­]
		languageItem.add("ISO-8859-5 [Slavic]");// ISO-8859-5[æ–¯æ‹‰å¤«è¯­]
		languageItem.add("ISO-8859-6 [Arabic]");// ISO-8859-6[é˜¿æ‹‰ä¼¯è¯­]
		languageItem.add("ISO-8859-7 [Greek]");// ISO-8859-7[å¸Œè…Šè¯­]
		languageItem.add("ISO-8859-8 [Hebrew]");// ISO-8859-8[å¸Œä¼¯æ�¥è¯­]
		languageItem.add("ISO-8859-9 [Turkish]");// ISO-8859-9[åœŸè€³å…¶è¯­]
		languageItem.add("ISO-8859-15 [Latin 9]");// ISO-8859-15[æ‹‰ä¸�è¯­9]
		languageItem.add("Iran [Iran, Persia]");// [ä¼Šæœ—ï¼Œæ³¢æ–¯] iso8859-6
		languageItem.add("Iran â…¡ [Persian]");// ä¼Šæœ—â…¡[æ³¢æ–¯è¯­] iso8859-6
												// å¥—ç”¨é˜¿æ‹‰ä¼¯è¯­
		languageItem.add("Latvia [Latvian]");// æ‹‰è„±ç»´äºš iso8859-4
		languageItem.add("[Thai]");// æ³°æ–‡ï¼ˆä¸ŽTM-88 Thai character code
									// 14å�Œï¼‰
									// gb2312
		languageItem.add("[Thai2]");// [æ³°æ–‡2] gb2312
		return languageItem;
	}

	private HashMap<String, String> getCodeLanguage() {
		HashMap<String, String> codeMap = new HashMap<String, String>();
		codeMap.put("Chinese", "gb2312");// gb2312
		codeMap.put("CP437 [USA]", "iso8859-1");// iso8859-1
		codeMap.put("KataKana [Japan]", "Shift_JIS");
		codeMap.put("Korea [Korea]", "EUC-KR");
		codeMap.put("CP720 [Arabic]", "iso8859-6");
		codeMap.put("CP737 [Greek]", "iso8859-7");
		codeMap.put("CP755 [Eastern Europe, Latvia 2]", "iso8859-5");
		codeMap.put("CP775 [Baltic]", "iso8859-1");
		codeMap.put("CP850 [Multilingual]", "iso8859-3");
		codeMap.put("CP852 [Latin2]", "iso8859-2");
		codeMap.put("CP855 [IBM Cyrillic]", "iso8859-5");
		codeMap.put("CP856 [IBM Hebrew]", "iso8859-6");
		codeMap.put("CP857 [Turkish]", "iso8859-3");
		codeMap.put("CP858 [Multilingual Latin â… +Euro]", "iso8859-15");
		codeMap.put("CP860 [Portugal]", "iso8859-6");
		codeMap.put("CP862 [Hebrew]", "iso8859-8");
		codeMap.put("CP863 [Canada - French]", "iso8859-1");
		codeMap.put("CP864 [Arabic]", "iso8859-6");
		codeMap.put("CP865 [Nordic]", "iso8859-1");
		codeMap.put("CP866 [Slavic2]", "iso8859-5");
		codeMap.put("Cp874 [IBM Hebrew]", "iso8859-8");
		codeMap.put("WCP1250 [Central Europe]", "iso8859-2");
		codeMap.put("WCP1251 [Slavic]", "iso8859-5");
		codeMap.put("WCP1252 [Latin 1]", "iso8859-1");
		codeMap.put("WCP1253 [Greek]", "iso8859-7");
		codeMap.put("WCP1254 [Turkish]", "iso8859-3");
		codeMap.put("WCP1255 [Hebrew]", "iso8859-8");
		codeMap.put("WCP1256 [Arabic]", "iso8859-6");
		codeMap.put("WCP1257 [Baltic]", "iso8859-1");
		codeMap.put("WCP1258 [Vietnamese]", "VISCII");
		codeMap.put("ISO-8859-1 [West Europe]", "iso8859-1");
		codeMap.put("ISO-8859-2 [Latin 2]", "iso8859-2");
		codeMap.put("ISO-8859-3 [Latin 3]", "iso8859-3");
		codeMap.put("ISO-8859-4 [Baltic]", "iso8859-4");
		codeMap.put("ISO-8859-5 [Slavic]", "iso8859-5");
		codeMap.put("ISO-8859-6 [Arabic]", "iso8859-6");
		codeMap.put("ISO-8859-7 [Greek]", "iso8859-7");
		codeMap.put("ISO-8859-8 [Hebrew]", "iso8859-8");
		codeMap.put("ISO-8859-9 [Turkish]", "iso8859-9");
		codeMap.put("ISO-8859-15 [Latin 9]", "iso8859-15");
		codeMap.put("Iran [Iran, Persia]", "iso8859-6");
		codeMap.put("Iran â…¡ [Persian]", "iso8859-6");
		codeMap.put("Latvia [Latvian]", "iso8859-4");
		codeMap.put("[Thai]", "ISO-8859-11");
		codeMap.put("[Thai2]", "ISO-8859-11");
		return codeMap;
	}

	private HashMap<String, Integer> getMapLanguage() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Chinese", 0);
		map.put("CP437 [USA]", 0);
		map.put("KataKana [Japan]", 1);
		map.put("Korea [Korea]", 1);
		map.put("CP720 [Arabic]", 27);
		map.put("CP737 [Greek]", 24);
		map.put("CP755 [Eastern Europe, Latvia 2]", 8);
		map.put("CP775 [Baltic]", 31);
		map.put("CP850 [Multilingual]", 2);
		map.put("CP852 [Latin2]", 18);
		map.put("CP855 [IBM Cyrillic]", 28);
		map.put("CP856 [IBM Hebrew]", 46);
		map.put("CP857 [Turkish]", 29);
		map.put("CP858 [Multilingual Latin â… +Euro]", 19);
		map.put("CP860 [Portugal]", 3);
		map.put("CP862 [Hebrew]", 15);
		map.put("CP863 [Canada - French]", 4);
		map.put("CP864 [Arabic]", 22);
		map.put("CP865 [Nordic]", 5);
		map.put("CP866 [Slavic2]", 7);
		map.put("Cp874 [IBM Hebrew]", 47);
		map.put("WCP1250 [Central Europe]", 30);
		map.put("WCP1251 [Slavic]", 6);
		map.put("WCP1252 [Latin 1]", 16);
		map.put("WCP1253 [Greek]", 17);
		map.put("WCP1254 [Turkish]", 32);
		map.put("WCP1255 [Hebrew]", 33);
		map.put("WCP1256 [Arabic]", 34);
		map.put("WCP1257 [Baltic]", 25);
		map.put("WCP1258 [Vietnamese]", 35);
		map.put("ISO-8859-1 [West Europe]", 23);
		map.put("ISO-8859-2 [Latin 2]", 36);
		map.put("ISO-8859-3 [Latin 3]", 37);
		map.put("ISO-8859-4 [Baltic]", 38);
		map.put("ISO-8859-5 [Slavic]", 39);
		map.put("ISO-8859-6 [Arabic]", 40);
		map.put("ISO-8859-7 [Greek]", 41);
		map.put("ISO-8859-8 [Hebrew]", 42);
		map.put("ISO-8859-9 [Turkish]", 43);
		map.put("ISO-8859-15 [Latin 9]", 44);
		map.put("Iran [Iran, Persia]", 9);
		map.put("Iran â…¡ [Persian]", 20);
		map.put("Latvia [Latvian]", 21);
		map.put("[Thai]", 26);
		map.put("[Thai2]", 45);
		return map;
	}

	/* Print for Dialog */
	public boolean pRintForDialog(Handler uiHandler, boolean flage) {
		boolean falge;
		this.uiHandler = uiHandler;
		Message msg = new Message();
		try {
			String codeLanguage = spinnerLanguage.getSelectedItem().toString().trim();
			HashMap<String, Integer> map = getMapLanguage();
			HashMap<String, String> codeMap = getCodeLanguage();
			int intLanguageNum = 23;
			String codeL = "gb2312"; // chinese charset
			if (codeMap.containsKey(codeLanguage)) {
				codeL = codeMap.get(codeLanguage);
			}
			if (PRT.PRTCapPrintMiniFont()) {
				String strPrintText;
				try {
					// try {
					// if (ServiApplication.getPrinter_id().equals("DASCOM
					// DT-210")) {
					// strPrintText = ServiApplication.print_flage_eps + "\n\n";
					// } else {
					// strPrintText = ServiApplication.print_flage + "\n\n";
					// }
					// } catch (Exception e) {
					// strPrintText = ServiApplication.print_flage + "\n\n";
					// }
					strPrintText = ServiApplication.print_flage_eps + "\n\n";
					byte[] data = (URLEncoder.encode(thisCon.getString(R.string.originalsize) + strPrintText, "utf-8"))
							.getBytes(codeL);
					data = (strPrintText).getBytes(codeL);
					PRT.WriteData(data, data.length);
					PRT.PRTFeedLines(20);
					PRT.PRTReset();
					PRT.PRTPaperCut(true);
					falge = true;
					msg.arg1 = SalesCodes.EDIT.code();
					uiHandler.sendMessage(msg);

				} catch (Exception e) {
					Toast.makeText(PRTSDKApp.this, "local exception", 100).show();
					msg.arg1 = SalesCodes.ERROR.code();
					uiHandler.sendMessage(msg);
					CommonMethods.showCustomToast(PRTSDKApp.this, e.getMessage().toUpperCase());
					falge = false;
				}
			} else {
				Toast.makeText(PRTSDKApp.this, "inner exception", 100).show();
				msg.arg1 = SalesCodes.ERROR.code();
				uiHandler.sendMessage(msg);
				falge = false;
				CommonMethods.showCustomToast(PRTSDKApp.this, getResources().getString(R.string.printer_errmsg));
			}
			return falge;

		} catch (Exception e) {
			Toast.makeText(PRTSDKApp.this, "main exception", 100).show();
			msg.arg1 = SalesCodes.ERROR.code();
			uiHandler.sendMessage(msg);
			falge = false;
			return falge;

		}
	}

	private void sendMessage(boolean flage) {

		Message msg = new Message();

		if (flage) {
			msg.arg1 = SalesCodes.SUCESS_PRINT.code();
		} else {
			msg.arg1 = SalesCodes.CLIENT_SALE_END.code();
		}
		Log.v("===============", "varahlababu123" + SalesCodes.SUCESS_PRINT.code());
		uiHandler.sendMessage(msg);

	}
}
