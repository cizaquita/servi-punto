package com.micaja.servipunto.dialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
import com.micaja.servipunto.utils.CommonMethods;

public class DepositoDaviplataDialog2 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private EditText edxt_document, edit_check_document, ed_first_Name,
			ed_second_Name, ed_surname, ed_second_Surname,
			edit_cellular_daviplata, edit_check_Cell, edit_address_daviplata,
			edit_city;
	private Button btn_daviplata_create, btn_daviplata_cancel;
	private Context daviplataDialog1;
	private ImageView img_clse;
	private Context context;

	private String eDocument, cnfmDocument, fName, sName, address, cNumber,
			cnfrmNumber, city, date;
	private boolean isValid;
	private Handler uiHandler;
	private DaviplataNumberDeDocumento documetn;
	private static View dateView;
	private static TextView edit_expedition_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.deposito_daviplata_dialog2);
		img_clse = (ImageView) findViewById(R.id.img_clse);
		edxt_document = (EditText) findViewById(R.id.edxt_document);
		edit_check_document = (EditText) findViewById(R.id.edit_check_document);
		ed_first_Name = (EditText) findViewById(R.id.ed_first_Name);
		ed_second_Name = (EditText) findViewById(R.id.ed_second_Name);
		ed_surname = (EditText) findViewById(R.id.ed_surname);
		ed_second_Surname = (EditText) findViewById(R.id.ed_second_Surname);
		edit_cellular_daviplata = (EditText) findViewById(R.id.edit_cellular_daviplata);
		edit_check_Cell = (EditText) findViewById(R.id.edit_check_Cell);
		edit_address_daviplata = (EditText) findViewById(R.id.edit_address_daviplata);
		edit_city = (EditText) findViewById(R.id.edit_city);
		edit_expedition_date = (TextView) findViewById(R.id.edit_expedition_date);
		btn_daviplata_create = (Button) findViewById(R.id.btn_daviplata_create);
		btn_daviplata_cancel = (Button) findViewById(R.id.btn_daviplata_cancel);
		btn_daviplata_cancel.setOnClickListener(this);
		btn_daviplata_create.setOnClickListener(this);
		img_clse.setOnClickListener(this);
		edit_expedition_date.setOnClickListener(this);
		loadUI();

	}

	private void loadUI() {
		edxt_document.setText(documetn.getDocumento());
		edit_check_document.setText(documetn.getDocumento());
		ed_first_Name.setText(documetn.getNombres());
		ed_second_Name.setText(documetn.getS_nombres());
		ed_surname.setText(documetn.getApellidos());
		ed_second_Surname.setText(documetn.getS_apellidos());
		edit_cellular_daviplata.setText(documetn.getCelular());
		edit_check_Cell.setText(documetn.getCelular());
		edit_address_daviplata.setText(documetn.getDireccion());
		edit_city.setText(documetn.getCiudad());
		edit_expedition_date.setText(documetn.getFechaExpedicion());
		enableeeditablefields();
	}

	private void enableeeditablefields() {
		if (documetn.getDocumento() != null) {
			edxt_document.setEnabled(false);
		}
	}

	public DepositoDaviplataDialog2(Context context,
			DaviplataNumberDeDocumento documetn, Handler uiHandler) {
		super(context);
		this.context = context;
		this.documetn = documetn;
		this.uiHandler = uiHandler;
		appContext = (ServiApplication) context.getApplicationContext();

	}

	@Override
	public void onClick(View v) {
		dateView = v;
		DatePicker datePicker = new DatePicker();

		switch (v.getId()) {
		case R.id.btn_daviplata_create:
			validateAndSave();
			break;
		case R.id.btn_daviplata_cancel:
			this.dismiss();
			break;
		case R.id.img_clse:
			this.dismiss();
			break;
		case R.id.edit_expedition_date:
			dateView = v;
			datePicker.show(((Activity) context).getFragmentManager(),
					"datePicker");
			break;

		default:
			break;
		}
	}

	// Choose date from Date picker
	public static class DatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;
			if (dateView.getId() == R.id.edit_expedition_date) {
				edit_expedition_date.setText(date);
			} else {
			}
		}
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			documetn.setDocumento(edxt_document.getText().toString().trim());
			documetn.setNombres(ed_first_Name.getText().toString().trim() + ","
					+ ed_second_Name.getText().toString());
			documetn.setS_nombres(ed_second_Name.getText().toString().trim());
			documetn.setApellidos(ed_surname.getText().toString().trim() + ","
					+ ed_second_Surname.getText().toString());
			documetn.setS_apellidos(ed_second_Surname.getText().toString()
					.trim());
			documetn.setCelular(edit_cellular_daviplata.getText().toString()
					.trim());
			documetn.setDireccion(edit_address_daviplata.getText().toString()
					.trim());
			documetn.setCiudad(edit_city.getText().toString().trim());
			documetn.setFechaExpedicion(edit_expedition_date.getText()
					.toString().trim());
			ServiApplication.setDocumetn(documetn);
			loadScreenData();
			this.dismiss();

		}

	}

	private void loadScreenData() {
		Message msg = new Message();
		msg.arg1 = 4516;
		uiHandler.sendMessage(msg);
	}

	private boolean validateFields(StringBuffer stringBuffer) {

		eDocument = edxt_document.getText().toString().trim();
		cnfmDocument = edit_check_document.getText().toString().trim();
		fName = ed_first_Name.getText().toString().trim();
		sName = ed_surname.getText().toString().trim();
		cNumber = edit_cellular_daviplata.getText().toString().trim();
		cnfrmNumber = edit_check_Cell.getText().toString().trim();
		address = edit_address_daviplata.getText().toString().trim();
		city = edit_city.getText().toString().trim();
		date = edit_expedition_date.getText().toString().trim();

		isValid = true;
		if (eDocument.length() == 0 && cnfmDocument.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.invalid_document));
		} else if (!eDocument.equals(cnfmDocument)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.document_errmsg));

		}
		if (fName.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.PrimerNo_errmsg));
		}
		if (sName.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.apellido_errmsg));
		}
		if (cNumber.length() == 0 && cnfrmNumber.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.invalidcellular));
		} else if (!cNumber.equals(cnfrmNumber)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.celular_msg));

		}
		if (address.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.address_msg));
		}
		if (city.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.city_msg));
		}
		if (date.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.date_msg));
		}

		return isValid;
	}

}
