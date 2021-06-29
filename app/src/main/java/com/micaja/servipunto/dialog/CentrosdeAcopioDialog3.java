package com.micaja.servipunto.dialog;

import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CentrosdeAcopioDialog3 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private Button btn_centros_print, btn_centros_leave;
	private Context context;
	private TextView successful_msg, transaction_message;
	private String serverresponds, message, fecha, hora,value,transaccionId;
	private Handler uiHandler;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.centros_de_acopiodialog3);
		btn_centros_print = (Button) findViewById(R.id.btn_centros_print);
		btn_centros_leave = (Button) findViewById(R.id.btn_centros_leave);
		successful_msg = (TextView) findViewById(R.id.successful_msg);
		successful_msg.setVisibility(View.GONE);
		transaction_message = (TextView) findViewById(R.id.transaction_message);
		btn_centros_print.setOnClickListener(this);
		btn_centros_leave.setOnClickListener(this);

		try {
			JSONObject json=new JSONObject(serverresponds);
			System.out.println(serverresponds);
			message=json.getString("message");
			transaccionId=json.getString("transaccionId");
			transaction_message.setText(json.getString("message")+" "+transaccionId);
			fecha =json.getString("fecha");
			hora=json.getString("hora");
		} catch (Exception e) {
		}

	}

	public CentrosdeAcopioDialog3(Context context, String serverresponds,
			Handler uiHandler, AcceptEfectivoDTO aedto, String value) {
		super(context);
		this.context = context;
		this.serverresponds = serverresponds;
		this.uiHandler = uiHandler;
		this.aedto = aedto;
		this.value=value;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_centros_print:
			printOperation();
			break;
		case R.id.btn_centros_leave:
			homeactivityLoad();
			this.dismiss();
			break;
		case R.id.img_close:
			homeactivityLoad();
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void homeactivityLoad() {
		Message message = new Message();
		message.arg1 = 142536;
		uiHandler.sendMessage(message);
	}

	private void printOperation() {
		ServiApplication.himp_print_flage = false;
		ServiApplication.flage_for_log_print=false;
		ServiApplication.setUiHandler(uuiHandler);
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.print_flage_eps=context.getResources().getString(R.string.twob_line1)+"\n"
		+ context.getResources().getString(R.string.twob_line2)+"\n\n"
		+ context.getResources().getString(R.string.twob_line3)+"\n"
		+ context.getResources().getString(R.string.twob_line4)+"  "+sharedpreferences.getString("name_of_store", "")+"\n"
		+ context.getResources().getString(R.string.valor)+"      $"+ CommonMethods.getNumSeparator(Double.parseDouble(value))+"\n"
		+ context.getResources().getString(R.string.accp_fechatrans)+"      "+fecha+"\n"
		+ context.getResources().getString(R.string.accp_horatrans)+"       "+hora+"\n"
		+ context.getResources().getString(R.string.centro_efectivo_message)+"\n"
		+ transaccionId+"\n\n"
		+ context.getResources().getString(R.string.twob_line10)+"     "+aedto.getDistribuidor()+"\n\n\n";
		ServiApplication.print_flage=ServiApplication.print_flage_eps;
		
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			homeactivityLoad();
		} else {
		
		Intent serverIntent = new Intent(context, PRTSDKApp.class);
		serverIntent.putExtra("babu", "print");
		((Activity) context).startActivityForResult(serverIntent, 10);
		}
	}
	
	private final Handler uuiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				homeactivityLoad();
			} else {
			}
		}
	};

}
