package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
import com.micaja.servipunto.utils.CommonMethods;

public class DepositoDaviplataDialog1 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private EditText edxt_document, edit_check_document, ed_first_Name,
			ed_second_Name, ed_surname, ed_second_Surname,
			edit_cellular_daviplata, edit_check_Cell, edit_address_daviplata,
			edit_city;
	private Button btn_daviplata_update, btn_daviplata_cancel;
	private Context context;
	private ImageView img_clse;
	private DaviplataNumberDeDocumento documetn;
	private boolean isValid;
	private String eDocument, cnfrmDocument, fName, sName, surName, celular,
			cnfrmCelular, address, city;
	private Handler uiHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.deposito_daviplata_dialog1);
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
		btn_daviplata_update = (Button) findViewById(R.id.btn_daviplata_update);
		btn_daviplata_cancel = (Button) findViewById(R.id.btn_daviplata_cancel);
		btn_daviplata_cancel.setOnClickListener(this);
		btn_daviplata_update.setOnClickListener(this);
		img_clse.setOnClickListener(this);
		loadUi();
	}

	private void loadUi() {
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

		enableeeditablefields();

	}

	private void enableeeditablefields() {
		if (documetn.getNombres() != "null") {
			edxt_document.setFocusable(false);
			edit_check_document.setFocusable(false);
			ed_first_Name.setFocusable(false);
			ed_second_Name.setFocusable(false);
			ed_surname.setFocusable(false);
			ed_second_Surname.setFocusable(false);
		} else {

		}

	}

	public DepositoDaviplataDialog1(Context context,
			DaviplataNumberDeDocumento documetn, Handler uiHandler) {
		super(context);
		this.context = context;
		this.documetn = documetn;
		this.uiHandler = uiHandler;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_daviplata_update:
			getDataFromView();
			break;
		case R.id.btn_daviplata_cancel:
			this.dismiss();
			break;
		case R.id.img_clse:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void getDataFromView() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validationFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			documetn.setDocumento(edxt_document.getText().toString().trim());
			documetn.setNombres(ed_first_Name.getText().toString().trim());
			documetn.setS_nombres(ed_second_Name.getText().toString().trim());
			documetn.setApellidos(ed_surname.getText().toString().trim());
			documetn.setS_apellidos(ed_second_Surname.getText().toString()
					.trim());
			documetn.setCelular(edit_cellular_daviplata.getText().toString()
					.trim());
			documetn.setDireccion(edit_address_daviplata.getText().toString()
					.trim());
			documetn.setCiudad(edit_city.getText().toString().trim());

			ServiApplication.setDocumetn(documetn);
			messageevent();
			this.dismiss();
		}

	}

	private void messageevent() {
		Message msg = new Message();
		msg.arg1 = 4516;
		uiHandler.sendMessage(msg);
	}

	private boolean validationFields(StringBuffer stringBuffer) {

		eDocument = edxt_document.getText().toString().trim();
		cnfrmDocument = edit_check_document.getText().toString().trim();
		fName = ed_first_Name.getText().toString().trim();
		surName = ed_surname.getText().toString().trim();
		celular = edit_cellular_daviplata.getText().toString().trim();
		cnfrmCelular = edit_check_Cell.getText().toString().trim();
		address = edit_address_daviplata.getText().toString().trim();
		city = edit_city.getText().toString().trim();

		isValid = true;

		if (eDocument.length() == 0 && cnfrmDocument.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.invalid_document));
		} else if (!eDocument.equals(cnfrmDocument)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.document_errmsg));
		}
		if (fName.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.PrimerNo_errmsg));
		}
		if (surName.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.apellido_errmsg));
		}
		if (celular.length() == 0 && cnfrmCelular.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.invalidcellular));
		} else if (!celular.equals(cnfrmCelular)) {
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

		return isValid;
	}
}
