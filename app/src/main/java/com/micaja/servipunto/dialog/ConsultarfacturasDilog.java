package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.ConsultarFacturasAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultarFacturasDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.Constants;

public class ConsultarfacturasDilog extends Dialog implements
		android.view.View.OnClickListener {

	private String spnKey;
	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;

	private Button btn_abnor, btn_pagar_total_factura, btn_cancel;
	private ImageView img_close;
	public SharedPreferences sharedpreferences;
	ConsultarFacturasDTO dto;
	UserDetailsDTO udto;
	ListView listView1;
	private TextView txt_dialog_title;

	public ConsultarfacturasDilog(Context context, ConsultarFacturasDTO dto) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		this.dto = dto;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.consultarfacturas_dilog1);
		loadUI();
	}

	private void loadUI() {

		listView1 = (ListView) findViewById(R.id.listView1);

		listView1.setAdapter(new ConsultarFacturasAdapter(context, dto,udto));

		btn_abnor = (Button) findViewById(R.id.btn_abnor);
		btn_abnor.setOnClickListener(this);

		btn_pagar_total_factura = (Button) findViewById(R.id.btn_pagar_total_factura);
		btn_pagar_total_factura.setOnClickListener(this);

		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		img_close = (ImageView) findViewById(R.id.img_close);
		img_close.setOnClickListener(this);
		txt_dialog_title = (TextView) findViewById(R.id.txt_dialog_title);
		txt_dialog_title.setText("FACTURA PENDIENTE");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_abnor:
			this.dismiss();
			RegisterPagos dialog = new RegisterPagos(context,
					dto.getSaldoTotal(), false);
			dialog.show();
			dialog.setCancelable(false);
			break;

		case R.id.btn_pagar_total_factura:
			this.dismiss();
			RegisterPagos dialog1 = new RegisterPagos(context,
					dto.getSaldoTotal(), true);
			dialog1.show();
			dialog1.setCancelable(false);
			break;

		case R.id.btn_cancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}
}
