/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.adapters.CashAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dto.CashDetailsDTO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.dialog.CashDialog;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener1;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class CashActivity extends BaseActivity implements OnClickListener,
		OnDialogSaveClickListener1 {

	private ListView lvCash;
	private Button btnWithdraw, btnDeposit;
	private CashDialog dialog;
	private List<DTO> cashList = new ArrayList<DTO>();
	private CashAdapter cashAdapter;
	private EditText etxtcashMoneybox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {

		setContentView(R.layout.cash_activity);

		lvCash = (ListView) findViewById(R.id.lv_Cash);
		etxtcashMoneybox = (EditText) findViewById(R.id.cash_Moneybox);
		btnWithdraw = (Button) findViewById(R.id.btn_CashWithdraw);
		btnDeposit = (Button) findViewById(R.id.btn_CashDeposit);

		btnWithdraw.setOnClickListener(this);
		btnDeposit.setOnClickListener(this);
		cashAdapter = new CashAdapter(this);

		getDataFromDB();

	}

	private void getDataFromDB() {

		cashList = CashFlowDAO.getInstance().getCashRecords(
				DBHandler.getDBObj(Constants.READABLE));
		setDataToViews();

	}

	private void setDataToViews() {

		Double cashBoxValue = 0.0;
		List<DTO> clearCashList = new ArrayList<DTO>(cashList);
		for (DTO dto : cashList) {

			CashDetailsDTO cashDto = (CashDetailsDTO) dto;

			if (!"".equals(cashDto.getAmount()) && cashDto.getAmount() != null) {
				if (cashDto.getPaymentType().equals(ConstantsEnum.PAY_DELIVERY.code()) || cashDto.getAmount().equals("0.0") || cashDto.getAmount().equals("0")) {
					clearCashList.remove(dto);
					continue;
				}

				if (cashDto.getAmountType().equals(
						ConstantsEnum.CASH_TYPE_WITHDRAW.code())
						|| cashDto.getAmountType().equals(
								ConstantsEnum.CLIENT_LEND.code()) || cashDto.getAmountType().equals(
										ConstantsEnum.DELIVERY_LEND.code()))
					cashBoxValue -= CommonMethods.getDoubleFormate(cashDto
							.getAmount());

				else if (cashDto.getAmountType().equals(
						ConstantsEnum.INCOME_TYPE_DEBIT.code())
						|| cashDto.getAmountType().equals(
								ConstantsEnum.INCOME_TYPE_SALE.code())
						|| cashDto.getAmountType().equals(
								ConstantsEnum.CASH_TYPE_DEPOSIT.code())
						|| cashDto.getAmountType().equals(
								ConstantsEnum.CASH_SHOP_OPEN.code())
						|| cashDto.getAmountType().equals(
						ConstantsEnum.DELIVERY_PAYMENT.code()))
					cashBoxValue += CommonMethods.getDoubleFormate(cashDto
							.getAmount());
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PAYMENT.code()))
					cashBoxValue -= (CommonMethods.getDoubleFormate(cashDto
							.getAmount()) * CommonMethods
							.getDoubleFormate(cashDto.getQuantity()));
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code()))
					cashBoxValue -= CommonMethods.getDoubleFormate(cashDto
							.getAmount());

			}

		}
		etxtcashMoneybox.setText(CommonMethods.getNumSeparator(cashBoxValue));
		cashAdapter = new CashAdapter(this);
		cashAdapter.setData(clearCashList);
		lvCash.setAdapter(cashAdapter);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_CashWithdraw:
			// To navigate to Cash screen to Cash Withdraw dialog
			dialog = new CashDialog(this, getResources().getString(
					R.string.btn_cashWithdraw));
			dialog.setOnDialogSaveClickListener(this);
			dialog.show();

			break;

		case R.id.btn_CashDeposit:
			// To navigate to Cash screen to Cash Deposit dialog
			dialog = new CashDialog(this, getResources().getString(
					R.string.btn_cashdeposit));
			dialog.setOnDialogSaveClickListener(this);
			dialog.show();

			break;

		default:
			break;
		}
	}

	// cash Dialog Call back
	@Override
	public void onSaveClick() {
		getDataFromDB();

	}

	private void getClientpay() {

		List<DTO> list = new ArrayList<DTO>();
		list = ClientPaymentDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		for (DTO dto : list) {
			ClientPaymentsDTO pDAO = (ClientPaymentsDTO) dto;
		}
	}

	private void getDeliveryPay() {

		List<DTO> list = new ArrayList<DTO>();
		list = DeliveryPaymentDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		for (DTO dto : list) {
			DeliveryPaymentsDTO pDAO = (DeliveryPaymentsDTO) dto;
		}
	}

}