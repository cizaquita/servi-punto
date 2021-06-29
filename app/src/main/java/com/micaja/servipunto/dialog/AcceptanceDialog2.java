package com.micaja.servipunto.dialog;

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
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;

public class AcceptanceDialog2 extends Dialog implements
		android.view.View.OnClickListener {

	private TextView ed_authnumber;
	private Button btn_confirm_cancel, btn_confirm_print;
	private String authnumber;
	private boolean isValid;
	private Context context;
	private ImageView img_close;
	ServiApplication appContext;
	private UserDetailsDTO dto;
	Handler uiHandler;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private String server_res,fecha, hora,transactionId,value,distibuter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	public AcceptanceDialog2(Context acceptanceDialog1, Handler uiHandler,
			AcceptEfectivoDTO aedto2, String value, String server_res, String distibuter) {
		super(acceptanceDialog1);
		this.context = acceptanceDialog1;
		appContext = (ServiApplication) context.getApplicationContext();
		this.uiHandler = uiHandler;
		this.aedto = aedto2;
		this.server_res=server_res;
		this.value=value;
		this.distibuter=distibuter;
	}

	private void initUI() {
		setContentView(R.layout.acceptance_dialog2);
		img_close = (ImageView) findViewById(R.id.img_close);
		ed_authnumber = (TextView) findViewById(R.id.ed_authnumber);
		btn_confirm_print = (Button) findViewById(R.id.btn_confirm_print);
		btn_confirm_cancel = (Button) findViewById(R.id.btn_confirm_cancel);
		btn_confirm_print.setOnClickListener(this);
		btn_confirm_cancel.setOnClickListener(this);
		img_close.setOnClickListener(this);

		try {
			ed_authnumber.setText(aedto.getIdCentroAcopio());
			try {
				JSONObject json=new JSONObject(server_res);
				transactionId=json.getString("message");
				fecha =Dates.get_2bpunthodate(json.getString("fecha"));
				hora=Dates.get_2bpunthohovers(json.getString("fecha"));
			} catch (Exception e) {
			}
			
			
		} catch (Exception e) {
		}

	}

	@Override
	public void onClick(View v) {
		{
			switch (v.getId()) {
			case R.id.btn_confirm_print:
				printOperation();
				break;
			case R.id.btn_confirm_cancel:
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
		+ context.getResources().getString(R.string.twob_line4)+"   "+sharedpreferences.getString("name_of_store", "")+"\n"
		+ context.getResources().getString(R.string.valor)+"      $"+ CommonMethods.getNumSeparator(Double.parseDouble(value)) +"\n"
		+ context.getResources().getString(R.string.accp_fechatrans)+"      "+fecha+"\n"
		+ context.getResources().getString(R.string.accp_horatrans)+"       "+hora+"\n"
		+ context.getResources().getString(R.string.acceptency_message)+"\n"
		+ aedto.getIdCentroAcopio()+"\n\n"
		+ context.getResources().getString(R.string.twob_line10)+"       "+distibuter+"\n\n\n";
		
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
