/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.CashDetailsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;

public class CashAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> cashResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private onCashChangeListener listener;

	public CashAdapter(Context context) {
		this.context = context;

	}

	public CashAdapter(Context context, List<DTO> cashResultsList) {
		this.context = context;
		this.cashResultsList = cashResultsList;
	}

	public interface onCashChangeListener {
		public void onCashFlowChabged(Double value);
	}

	public void setOnCashChangeListener(onCashChangeListener listener) {
		this.listener = listener;

	}

	public void setData(List<DTO> cashResultsList) {
		this.cashResultsList = cashResultsList;
		notifyDataSetChanged();
	}

	public void refreshList() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return cashResultsList.size();
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

			convertView = inflater.inflate(R.layout.cash_row, null);
		}

		final ImageView type = (ImageView) convertView
				.findViewById(R.id.img_cash_type);
		final TextView description = (TextView) convertView
				.findViewById(R.id.txt_cash_description);
		final TextView valueMovement = (TextView) convertView
				.findViewById(R.id.txt_cash_valuemovement);
		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_cash_datetime);

		CashDetailsDTO dto = (CashDetailsDTO) cashResultsList.get(position);

		if (dto.getAmount().equals("") || dto.getAmount() == null)
			valueMovement.setText("0");
		else if (dto.getAmountType().equals(
				ConstantsEnum.SUPPLIER_PAYMENT.code()))
			valueMovement
					.setText(String.valueOf(CommonMethods
							.getNumSeparator(CommonMethods.getDoubleFormate(dto
									.getAmount())
									* CommonMethods.getDoubleFormate(dto
											.getQuantity()))));
		else
			valueMovement.setText(CommonMethods.getNumSeparator(CommonMethods
					.getDoubleFormate(dto.getAmount())));

		// Se comenta a petición del requerimiento 4.3.5.1 A
		/*
		date.setText(dto.getDateTime().split(" ")[0] + " "
				+ context.getResources().getString(R.string.hour) + " "
				+ dto.getDateTime().split(" ")[1]);
		*/

		String dateDto = dto.getDateTime().split(" ")[0] + " " + dto.getDateTime().split(" ")[1]; // date + hour
		dateDto = Dates.formatDateToDD_MM_YYYY_HH_MM_SS(dateDto);
		date.setText(dateDto);

		if (dto.getAmountType().equals(ConstantsEnum.INCOME_TYPE_SALE.code())) {
			if (dto.getReason() == null)
				description.setText(context.getResources().getString(
						R.string.sale_clients)
						+ ": "
						+ context.getResources().getString(
								R.string.cleint_occasional));
			else
				description.setText(context.getResources().getString(
						R.string.sale_clients)
						+ ": " + dto.getReason());

		}

		if (dto.getAmountType().equals(ConstantsEnum.CASH_TYPE_WITHDRAW.code()))
			description.setText(context.getResources().getString(
					R.string.cash_withdrawl)
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals(
				ConstantsEnum.CASH_TYPE_DEPOSIT.code()))
			description.setText(context.getResources().getString(
					R.string.cash_deposit)
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals(
				ConstantsEnum.INCOME_TYPE_DEBIT.code()))
			description.setText(context.getResources().getString(
					R.string.pay_clients)
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals(
				ConstantsEnum.DELIVERY_PAYMENT.code()))
			description.setText("Abono Delivery"
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals(
				ConstantsEnum.SUPPLIER_PAYMENT.code()))
			description.setText(context.getResources().getString(
					R.string.cash_orderbuy)
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals(
				ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code()))
			description.setText(context.getResources().getString(
					R.string.cash_orderbuy)
					+ ": " + dto.getReason());
		else if (dto.getAmountType()
				.equals(ConstantsEnum.CASH_SHOP_OPEN.code()))
			description.setText(context.getResources().getString(
					R.string.cash_storeopen)
					+ ": "
					+ context.getResources().getString(R.string.openingbal));
		else if (dto.getAmountType().equals(ConstantsEnum.CLIENT_LEND.code()))
			description.setText(context.getResources().getString(
					R.string.cash_lend)
					+ ": " + dto.getReason());
		else if (dto.getAmountType().equals((ConstantsEnum.DELIVERY_LEND.code())))
			description.setText("Prestamo Delivery"
					+ ": " + dto.getReason());

		if (dto.getAmountType().equals(ConstantsEnum.CASH_TYPE_WITHDRAW.code())
				|| dto.getAmountType().equals(ConstantsEnum.CLIENT_LEND.code())
				|| dto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PAYMENT.code())
				|| dto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code())
				|| dto.getAmountType().equals(ConstantsEnum.DELIVERY_LEND.code()))
			type.setImageResource(R.drawable.withdraw);
		else if (dto.getAmountType().equals(
				ConstantsEnum.CASH_TYPE_DEPOSIT.code())
				|| dto.getAmountType().equals(
						ConstantsEnum.CASH_SHOP_OPEN.code()))
			type.setImageResource(R.drawable.deposit);
		else if (dto.getPaymentType().equals(
				ConstantsEnum.PAYMENT_TYPE_CARD.code())) {
			type.setImageResource(R.drawable.payment_card);

		} else if (dto.getAmountType().equals(
				ConstantsEnum.INCOME_TYPE_DEBIT.code())
				|| dto.getAmountType().equals(
						ConstantsEnum.INCOME_TYPE_SALE.code())
				||dto.getAmountType().equals(ConstantsEnum.DELIVERY_PAYMENT)) {
			type.setImageResource(R.drawable.deposit);
		}

		return convertView;
	}

}
