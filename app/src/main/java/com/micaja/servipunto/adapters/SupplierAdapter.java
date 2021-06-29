/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.AddSupplierActivity;
import com.micaja.servipunto.activities.SuppliersActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.dialog.PayDialog;
import com.micaja.servipunto.dialog.PayDialog.onSaveListener;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;


public class SupplierAdapter extends BaseAdapter implements onSaveListener {

	private Context context;
	private List<DTO> supplierResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;

	public SupplierAdapter(Context context, List<DTO> supplierResultsList) {
		this.context = context;
		this.supplierResultsList = supplierResultsList;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return supplierResultsList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.supplier_row, null);
		}

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_Name);
		final TextView payment = (TextView) convertView
				.findViewById(R.id.txt_Payment);
		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_Date);
		final ImageView ivEdit = (ImageView) convertView
				.findViewById(R.id.img_Edit);
		final ImageView ivPay = (ImageView) convertView
				.findViewById(R.id.img_Pay);

		final SupplierDTO dto = (SupplierDTO) supplierResultsList.get(position);

		// name.setText(dto.getName()+" - "+dto.getTelephone());
		name.setText(dto.getName());
		payment.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(dto.getBalanceAmount())));

		if (dto.getLastPaymentDate().split(" ")[0].contains("null")) {
			
		} else {
			//date.setText(dto.getLastPaymentDate().split(" ")[0]);
			String formatedDate = Dates.formatDateToDD_MM_YYYY(dto.getLastPaymentDate().split(" ")[0]);
			date.setText(formatedDate);
		}


		ivEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				appContext.pushActivity(new Intent(context,
						SuppliersActivity.class));
				Intent intent = new Intent(context, AddSupplierActivity.class);
				intent.putExtra(ConstantsEnum.SUPPLIER_MODE.code(),
						ConstantsEnum.EDIT_SUPPLIER.code());
				intent.putExtra(ConstantsEnum.SUPPLIER_ID.code(),
						dto.getSupplierId());
				context.startActivity(intent);

			}
		});

		ivPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showFilters();
			}

			private void showFilters() {
				final Dialog dialog = new Dialog(context);
				ServiApplication.SUPPLIER_PAYMENT_TYPE = ServiApplication.CASH;
				dialog.setContentView(R.layout.supplier_pay_clickevnt_dialog);
				dialog.setTitle((R.string.supp_filters));
				/*
				RelativeLayout layout1 = (RelativeLayout) dialog
						.findViewById(R.id.rel_FilterCash);
				RelativeLayout layout2 = (RelativeLayout) dialog
						.findViewById(R.id.rel_FilterDataphone);
				RelativeLayout layout3 = (RelativeLayout) dialog
						.findViewById(R.id.rel_payment);
				layout3.setVisibility(View.GONE);
				RelativeLayout layout4 = (RelativeLayout) dialog
						.findViewById(R.id.rel_bank_check);
				final AutoCompleteTextView actv = (AutoCompleteTextView) dialog
						.findViewById(R.id.ac_Group);
				final RadioButton radio3 = (RadioButton) dialog
						.findViewById(R.id.radio_payment);
				final RadioButton radio2 = (RadioButton) dialog
						.findViewById(R.id.radio_FilterDataphone);
				*/
				// Se agregó otro radio button a petición del requerimiento 4.3.3.3 A
				final RadioButton radio1 = (RadioButton) dialog.findViewById(R.id.radio_Cash);
				final RadioButton radioBankCheck = (RadioButton) dialog.findViewById(R.id.radio_bank_check);

				radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean checked) {
						if (checked) {
							radioBankCheck.setChecked(false);
							dialog.dismiss();
							cashPayment();
							ServiApplication.SUPPLIER_PAYMENT_TYPE = ServiApplication.CASH;
						}
					}
				});

				radioBankCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean checked) {
						if (checked) {
							radio1.setChecked(false);
							dialog.dismiss();
							//cashPayment();
							ServiApplication.SUPPLIER_PAYMENT_TYPE = ServiApplication.BANK_CHECK;
						}
					}
				});
				/*
				radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean checked) {
						if (checked) {
							radio1.setChecked(false);
							dialog.dismiss();
							cashPayment();
							ServiApplication.SUPPLIER_PAYMENT_TYPE = ServiApplication.DATAPHONE;
						}
					}
				});

				radio3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
												 boolean checked) {

						if (checked) {
							radio3.setChecked(false);
							dialog.dismiss();
							cashPayment();
							ServiApplication.SUPPLIER_PAYMENT_TYPE = ServiApplication.PAYMENT;
						}
					}
				});
				*/
				dialog.show();

			}

			private void cashPayment() {

				PayDialog dialog = new PayDialog(context, dto,
						ConstantsEnum.SUPPLIER_PAY.code());

				dialog.setListener(SupplierAdapter.this);
				dialog.show();

			}

		});

		if (dto.getBalanceAmount().equals("")
				|| dto.getBalanceAmount().equals("0")
				|| dto.getBalanceAmount().equals("0.0"))
			ivPay.setVisibility(View.INVISIBLE);
		else
			ivPay.setVisibility(View.VISIBLE);

		return convertView;
	}

	@Override
	public void onSave() {

		SupplierAdapter.this.notifyDataSetChanged();
	}

}
