package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class ConventionConfirmDetailsDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private Intent intent;
	private Button btnRegcancel, btnRegcnfm;
	private TextView txt_operator, txt_number, txt_reg_val, txt_ass_val,
			txt_keyvalue, editTxtkey, editTxtcellnumber;
	private EditText etxtKeyalue, cellNumber,etxt_key_value_val;

	private TextView txtPaquetigo, txtNumber, regValue, asstValue, txtKey,
			txtDialogTitle;

	private LinearLayout layoutKey, layoutEditkey, layoutAssistance1,layout_editkey_barcode,
			layoutCellNo;
	private ImageView imgClose;
	private boolean isValid;
	private String cellPhoneno, cnfmValue,nombreReferencia1,nombreReferencia2;
	int maxLength = 10;
	ConsultaConvenioDTO consultaConvenioDTO;

	public ConventionConfirmDetailsDialog(Context context,
			ConsultaConvenioDTO consultaConvenioDTO, String nombreReferencia1, String nombreReferencia2) {
		super(context);
		this.context = context;
		this.consultaConvenioDTO = consultaConvenioDTO;
		this.nombreReferencia1=nombreReferencia1;
		this.nombreReferencia2=nombreReferencia2;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.recharge_dialog1);
		etxtKeyalue = (EditText) findViewById(R.id.etxt_key_value);
//		etxtKeyalue.setInputType(InputType.TYPE_CLASS_NUMBER);

		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		etxtKeyalue.setFilters(FilterArray);

		cellNumber = (EditText) findViewById(R.id.cell_number);

		txt_operator = (TextView) findViewById(R.id.txt_operator);
		txt_number = (TextView) findViewById(R.id.txt_number);
		txt_reg_val = (TextView) findViewById(R.id.txt_reg_val);
		txt_ass_val = (TextView) findViewById(R.id.txt_ass_val);

		txtPaquetigo = (TextView) findViewById(R.id.txt_paquetigo);
		txtPaquetigo.setText(getContext().getString(R.string.converaion));
		txtNumber = (TextView) findViewById(R.id.txtnumber);
		txtNumber.setText(nombreReferencia1);
		regValue = (TextView) findViewById(R.id.regvalue);
		regValue.setText(nombreReferencia2);

		txt_keyvalue = (TextView) findViewById(R.id.txt_keyvalue);
		layoutEditkey = (LinearLayout) findViewById(R.id.layout_editkey);
		layoutEditkey.setVisibility(View.VISIBLE);
		layoutAssistance1 = (LinearLayout) findViewById(R.id.layout_assistance1);
		layoutAssistance1.setVisibility(View.GONE);

		layoutCellNo = (LinearLayout) findViewById(R.id.layout_cellNo);
		layoutCellNo.setVisibility(View.VISIBLE);
		editTxtcellnumber = (TextView) findViewById(R.id.edit_txtcellnumber);
		editTxtcellnumber.setText(getContext().getString(
				R.string.numero_celular));

		editTxtkey = (TextView) findViewById(R.id.edit_txtkey);
		editTxtkey.setText(getContext().getString(R.string.confirmar_valor));

		btnRegcancel = (Button) findViewById(R.id.btn_reg_cancel);
		btnRegcnfm = (Button) findViewById(R.id.btn_reg_cnfm);
		btnRegcnfm.setOnClickListener(this);
		btnRegcancel.setOnClickListener(this);

		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

		txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtDialogTitle.setText(getContext()
				.getString(R.string.convenio_dialog1));
		layout_editkey_barcode=(LinearLayout)findViewById(R.id.layout_editkey_barcode);
		etxt_key_value_val=(EditText)findViewById(R.id.etxt_key_value_val);
		loadUI();

	}

	private void loadUI() {
		txt_operator.setText(consultaConvenioDTO.getDescripcion());
		txt_number.setText(consultaConvenioDTO.getRef1());
		txt_reg_val.setText(consultaConvenioDTO.getRef2());
		layout_editkey_barcode.setVisibility(View.VISIBLE);
		etxt_key_value_val.setText(consultaConvenioDTO.getValue1());
//		etxt_key_value_val.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//		etxtKeyalue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		if (consultaConvenioDTO.getValue1().isEmpty()) {
			
		}else{
			etxt_key_value_val.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();

			break;

		case R.id.btn_reg_cnfm:
			validateAndSave();

			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	private void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			this.dismiss();
			ConventionSecondCnfmDetailsDialog dialog = new ConventionSecondCnfmDetailsDialog(
					context, consultaConvenioDTO, cnfmValue, cellPhoneno);
			dialog.show();
			dialog.setCancelable(false);
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		cellPhoneno = cellNumber.getText().toString().trim();
		cnfmValue = etxtKeyalue.getText().toString().trim();
		String val=etxt_key_value_val.getText().toString().trim();
		isValid = true;
		if (val.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.barcode_collection_value_validation));
		}

		if (cnfmValue.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.barcode_collection_conform_value_validation));
		}else{
			if (val.equals(cnfmValue)) {
				
			}else {
				isValid = false;
				stringBuffer.append("\n"
						+ getContext().getString(R.string.barcode_collection_conform_value_conform_validation));
			}
		}

		if (cellNumber.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.enter_validcelueno));
		} else {
			if (cellNumber.length() > 9) {
			} else {
				stringBuffer.append("\n"
						+ getContext().getString(R.string.enter_validphonenum));
				isValid = false;
			}
		}
//		if (cnfmValue.length() == 0 && cellNumber.length() == 0) {
//			isValid = false;
//			stringBuffer.append("\n"
//					+ getContext().getString(R.string.convention_errmsg));
//		}

		return isValid;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

}
