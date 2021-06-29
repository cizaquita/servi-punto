package com.micaja.servipunto.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.micaja.servipunto.R;

import PRTAndroidSDK.PRTAndroidPrint;
import android.content.Context;
import android.widget.Toast;

public class ePsonUSB_PrinterPrintClass {
	private static PRTAndroidPrint PRT = null;
	private String ConnectType = "";

	public boolean sendEpsonPrinter(Context context, String data) {

		if (PRT != null) {
			PRT.CloseProt();
		}
		ConnectType = "USB";
		PRT = new PRTAndroidPrint(context, "USB", "DASCOM DT-210");
		// USB not need call "iniPort"
		if (!PRT.OpenPort("")) {
			Toast.makeText(context, "Conncetion Toast", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			try {
				return callprintMethod(context, data);
			} catch (UnsupportedEncodingException e) {
				return false;
			}
		}
	}

	private boolean callprintMethod(Context context, String data2) throws UnsupportedEncodingException {

//		String codeLanguage = "Chinese";
//		HashMap<String, Integer> map = getMapLanguage();
//		HashMap<String, String> codeMap = getCodeLanguage();
//		int intLanguageNum = 23;
		String codeL = "gb2312"; // chinese charset
//		if (codeMap.containsKey(codeLanguage)) {
//			codeL = codeMap.get(codeLanguage);
//		}

		if (PRT.PRTCapPrintMiniFont()) {
			String strPrintText = data2 + "\n\n";
			byte[] data = (URLEncoder.encode(context.getString(R.string.originalsize) + strPrintText, "utf-8"))
					.getBytes(codeL);
			data = (strPrintText).getBytes(codeL);
			PRT.WriteData(data, data.length);
			PRT.PRTFeedLines(20);
			PRT.PRTReset();
			PRT.PRTPaperCut(true);
			return true;
		} else {
			return false;
		}

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
		codeMap.put("CP858 [Multilingual Latin +Euro]", "iso8859-15");
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
		codeMap.put("Iran  [Persian]", "iso8859-6");
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
		map.put("CP858 [Multilingual Latin +Euro]", 19);
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
		map.put("Iran  [Persian]", 20);
		map.put("Latvia [Latvian]", 21);
		map.put("[Thai]", 26);
		map.put("[Thai2]", 45);
		return map;
	}

}
