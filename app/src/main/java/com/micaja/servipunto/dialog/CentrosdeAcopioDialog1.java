package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class CentrosdeAcopioDialog1 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private Button btn_EndSaleSave, btn_EndSaleCancel;
	private EditText et_value, et_key;
	private Context context;
	private ImageView img_close;
	private String value, key;
	private boolean isValid;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	public SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;
	private Handler uiHandler;
	private TextView et_conformvalue;

	public CentrosdeAcopioDialog1(Context context, AcceptEfectivoDTO aedto,
			Handler uiHandler) {
		super(context);
		this.context = context;
		this.aedto = aedto;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		this.uiHandler = uiHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
	}

	private void initUI() {

		setContentView(R.layout.centros_de_acopiodialog1);
		img_close = (ImageView) findViewById(R.id.img_close);
		btn_EndSaleSave = (Button) findViewById(R.id.btn_EndSaleSave);
		btn_EndSaleCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		et_value = (EditText) findViewById(R.id.et_value);
		et_key = (EditText) findViewById(R.id.et_key);
		
		et_conformvalue=(TextView)findViewById(R.id.et_conformvalue);
		
		btn_EndSaleSave.setOnClickListener(this);
		btn_EndSaleCancel.setOnClickListener(this);
		img_close.setOnClickListener(this);
		
		et_conformvalue.setText(aedto.getDistribuidor());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_EndSaleSave:
			validateAndSave();
			break;
		case R.id.btn_EndSaleCancel:
			this.dismiss();
			movetoMenuActivity();
			break;
		case R.id.img_close:
			this.dismiss();
			break;
		default:
			break;
		}
	}

	private void movetoMenuActivity() {
		this.dismiss();
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, MenuActivity.class);
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			this.dismiss();
			CentrosdeAcopioDialog2 dialog = new CentrosdeAcopioDialog2(context,
					value, uiHandler, aedto);
			dialog.show();
			dialog.setCancelable(false);
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		value = et_value.getText().toString().trim();
		key = et_key.getText().toString().trim();
		isValid = true;
		aedto.setAmount(value);
		aedto.setClave(key);

		if (value.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.type_value));

		}
		if (key.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.type_key));

		} else if (key.length() < 6) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.type_key_six_digits));
		} else {
		}

		return isValid;
	}
}
