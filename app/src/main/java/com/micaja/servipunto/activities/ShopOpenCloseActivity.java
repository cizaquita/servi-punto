/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dao.LendDeliveryDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CashDetailsDTO;
import com.micaja.servipunto.database.dto.CashFlowDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.database.dto.LendDeliveryDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.ActualBalanceDialog;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.dialog.ShopCloseDialog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.SalesCodes;

public class ShopOpenCloseActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {

	private Button btnCancel, btnShopClose;
	private TextView txtShopName, txtShopOpenDate;
	private TextView txtopeningbal, txtCardSales, txtCashsales, txtDeposits,
			txtPaymentsInc, txtPaymetsExp, txtCashPurchase, txtCreditPurExp,
			txtwithdrawls, txtTotalCash, txtTotalInc, txtTotalExp,
			txtFinalbalInc, txtFinalbalExp, txtDeliveryIn, txtDeliveryOut;
	private UserDetailsDTO userDto = new UserDetailsDTO();
	private String closebal;
	public SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.shopopen_activity);
		txtopeningbal = (TextView) findViewById(R.id.txt_openbal_income);
		txtCashsales = (TextView) findViewById(R.id.txt_cashsales_income);
		txtCardSales = (TextView) findViewById(R.id.txt_cardsales_income);
		txtDeposits = (TextView) findViewById(R.id.txt_shop_depositsInc);
		txtPaymentsInc = (TextView) findViewById(R.id.txt_shop_payments);
		txtPaymetsExp = (TextView) findViewById(R.id.txt_shop_payments_exp);
		txtCashPurchase = (TextView) findViewById(R.id.txt_cashpurchase_exp);
		txtCreditPurExp = (TextView) findViewById(R.id.txt_creditpurchase_exp);
		txtwithdrawls = (TextView) findViewById(R.id.txt_withdraw_exp);
		txtTotalInc = (TextView) findViewById(R.id.txt_total_income);
		txtTotalExp = (TextView) findViewById(R.id.txt_total_exp);
		txtTotalCash = (TextView) findViewById(R.id.txt_totalcash_income);
		txtFinalbalInc = (TextView) findViewById(R.id.txt_finalbal_income);
		txtFinalbalExp = (TextView) findViewById(R.id.txt_finalbal_exp);

		txtShopName = (TextView) findViewById(R.id.txt_shopname);
		txtShopOpenDate = (TextView) findViewById(R.id.txt_shop_datetime);

		txtDeliveryIn = (TextView) findViewById(R.id.txt_delivery_in);
		txtDeliveryOut = (TextView) findViewById(R.id.txt_delivery_out);
		btnCancel = (Button) findViewById(R.id.btn_shop_cancel);
		btnShopClose = (Button) findViewById(R.id.btn_shop_close);

		btnCancel.setOnClickListener(this);
		btnShopClose.setOnClickListener(this);

		getDataFromDB();

	}

	// Result of this method,getting all payments from DB
	private void getDataFromDB() {

		userDto = UserDetailsDAO.getInstance().getUserRecords(
				DBHandler.getDBObj(Constants.READABLE));

		txtShopName.setText(" " + userDto.getCompanyName());
		String formattedDate = Dates.formatDateToDD_MM_YYYY_HH_MM(userDto.getOpeningDateTime());
		txtShopOpenDate.setText(" " + formattedDate);

		txtopeningbal.setText("0.0");
		txtCashsales.setText("0.0");
		txtCardSales.setText("0.0");
		txtDeposits.setText("0.0");
		txtPaymentsInc.setText("0.0");
		txtPaymetsExp.setText("0.0");
		txtCashPurchase.setText("0.0");
		txtCreditPurExp.setText("0.0");
		txtwithdrawls.setText("0.0");
		txtDeliveryOut.setText("0.0");


		Double cashSale = 0.0;
		Double creditSale = 0.0;
		Double paymentsInc = 0.0;
		Double cardsale = 0.0;
		Double openBal = 0.0;
		Double deposits = 0.0;
		Double paymentsExp = 0.0;
		Double withdrawls = 0.0;
		Double cashPurchase = 0.0;
		Double creditPurchase = 0.0;
		Double deliveryOut = 0.0;
		Double deliveryIn = 0.0;

		List<DTO> cashflowdetails = CashFlowDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		for (DTO dto : cashflowdetails) {
			CashFlowDTO cashdto = (CashFlowDTO) dto;
			if (cashdto.getAmountType().equals(
					ConstantsEnum.CASH_TYPE_WITHDRAW.code())) {
				withdrawls += CommonMethods.getDoubleFormate(cashdto
						.getAmount());
			} else if (cashdto.getAmountType().equals(
					ConstantsEnum.CASH_TYPE_DEPOSIT.code())) {
				deposits += CommonMethods.getDoubleFormate(cashdto.getAmount());
			}
		}

		List<DTO> cashDetails ;

		cashDetails = UserDetailsDAO.getInstance().getShopOpenCloseDetails(
				DBHandler.getDBObj(Constants.READABLE));

		for (DTO dto : cashDetails) {

			CashDetailsDTO cashDto = (CashDetailsDTO) dto;

			if (!"".equals(cashDto.getAmount()) && cashDto.getAmount() != null) {

				if (cashDto.getAmountType().equals(
						ConstantsEnum.CASH_SHOP_OPEN.code()))
					openBal = CommonMethods.getDoubleFormate(cashDto
							.getAmount());
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.CLIENT_LEND.code()))
					paymentsExp += CommonMethods.getDoubleFormate(cashDto
							.getAmount());
				/*else if (cashDto.getAmountType().equals(ConstantsEnum.DELIVERY_LEND.code())){
					deliveryOut += CommonMethods.getDoubleFormate(cashDto.getAmount());
				}*/
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PAYMENT.code()))
					cashPurchase += (CommonMethods.getDoubleFormate(cashDto
							.getAmount()) * CommonMethods
							.getDoubleFormate(cashDto.getQuantity()));
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code()))
					creditPurchase += CommonMethods.getDoubleFormate(cashDto
							.getAmount());
				else if (cashDto.getAmountType().equals(
						ConstantsEnum.INCOME_TYPE_SALE.code())) {
					if (cashDto.getPaymentType().equals(
							ConstantsEnum.PAYMENT_TYPE_CARD.code())) {
						cardsale += CommonMethods.getDoubleFormate(cashDto
								.getAmount());
					} else if (cashDto.getPaymentType().equals(
							ConstantsEnum.PAYMENT_TYPE_CASH.code())) {
						cashSale += CommonMethods.getDoubleFormate(cashDto
								.getAmount());
					} else if (cashDto.getPaymentType().equals(
							ConstantsEnum.PAYMENT_TYPE_CREDIT.code())) {
						creditSale += CommonMethods.getDoubleFormate(cashDto
								.getAmount());
					}

				} else if (cashDto.getAmountType().equals(
						ConstantsEnum.INCOME_TYPE_DEBIT.code())) {
					paymentsInc += CommonMethods.getDoubleFormate(cashDto
							.getAmount());
				}
			}
		}

		List<DTO> deliveryPayment = DeliveryPaymentDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));

		for (DTO dto: deliveryPayment){
			DeliveryPaymentsDTO deliveryPaymentsDTO = (DeliveryPaymentsDTO) dto;
			deliveryIn += CommonMethods.getDoubleFormate(deliveryPaymentsDTO.getAmountPaid());
			if (deliveryPaymentsDTO.getIncomeType()== null)
				continue;
			if (deliveryPaymentsDTO.getIncomeType().equals(ConstantsEnum.INCOME_TYPE_SALE.code())){
				cashSale += CommonMethods.getDoubleFormate(deliveryPaymentsDTO.getAmountPaid());
			}
		}
		List<DTO> deliveryLend = LendDeliveryDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));

		for (DTO dto: deliveryLend){
			LendDeliveryDTO lendDeliveryDTO = (LendDeliveryDTO) dto;
			deliveryOut += CommonMethods.getDoubleFormate(lendDeliveryDTO.getAmount());
		}

		/*List<DTO> deliveryDTOs = DeliveryDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
		for (DTO dto: deliveryDTOs){
			DeliveryDTO deliveryDTO = (DeliveryDTO) dto;
			deliveryDTO.getBalanceAmount();
		}*/
		Double totalCash = 0.0;
		Double totalIncome = 0.0;
		Double totalExpenses = 0.0;
		Double finalBal = 0.0;

		txtDeliveryOut.setText(String.valueOf(CommonMethods.getNumSeparator(deliveryOut)));
		txtDeliveryIn.setText(String.valueOf(CommonMethods.getNumSeparator(deliveryIn)));
		txtopeningbal.setText(String.valueOf(CommonMethods
				.getNumSeparator(openBal)));
		txtCashsales.setText(String.valueOf(CommonMethods
				.getNumSeparator(cashSale)));

		txtCardSales.setText(String.valueOf(CommonMethods
				.getNumSeparator(cardsale)));
		txtDeposits.setText(String.valueOf(CommonMethods
				.getNumSeparator(deposits)));

		txtPaymentsInc.setText(String.valueOf(CommonMethods
				.getNumSeparator(paymentsInc)));
		// Se comenta esta línea a petición del cambio 4.3.1 C
		//txtPaymetsExp.setText(String.valueOf(CommonMethods.getNumSeparator(paymentsExp)));
		txtCashPurchase.setText(String.valueOf(CommonMethods
				.getNumSeparator(cashPurchase)));
		txtCreditPurExp.setText(String.valueOf(CommonMethods
				.getNumSeparator(creditPurchase)));
		// Se adiciona el valor del gasto del abono al gasto del retiro 4.3.1 C
		String newValueWithdrawl = String.valueOf((CommonMethods.getNumSeparator(withdrawls + paymentsExp)));
		txtwithdrawls.setText(newValueWithdrawl);

		totalIncome = openBal + cashSale + cardsale + deposits + paymentsInc + deliveryIn;
		totalExpenses = cashPurchase + creditPurchase + paymentsExp
				+ withdrawls + deliveryOut ;
		totalCash = totalIncome;
		if (totalIncome > totalExpenses) {
			finalBal = totalIncome - totalExpenses;
			txtFinalbalInc.setText(CommonMethods.getNumSeparator(finalBal));
			closebal = String.valueOf(finalBal);
		} else {
			finalBal = totalExpenses - totalIncome;

			txtFinalbalExp.setText(CommonMethods.getNumSeparator(finalBal));
			closebal = String.valueOf(finalBal);

		}
		txtTotalInc.setText(String.valueOf(CommonMethods
				.getNumSeparator(totalIncome)));
		txtTotalExp.setText(String.valueOf(CommonMethods
				.getNumSeparator(totalExpenses)));
		txtTotalCash.setText(String.valueOf(CommonMethods
				.getNumSeparator(totalCash)));
		shopromo();

	}

	// showing promotion
	private void shopromo() {
		if (ServiApplication.shope_open_alert) {
			ServiApplication.alert_type = 1;
			PromotionDialog dialog = new PromotionDialog(
					ShopOpenCloseActivity.this, sharedpreferences.getString(
							"promo_image_url", "") + "0",
					sharedpreferences.getString("promo_video_url", ""),
					sharedpreferences.getString("promo_promotion_des", ""),
					sharedpreferences.getLong("promoid", 0), "", 0);
			if (sharedpreferences.getString("promo_image_url", "").length() > 4
					|| sharedpreferences.getString("promo_video_url", "")
							.length() > 0) {
				dialog.show();
				dialog.setCancelable(false);
				ServiApplication.shope_open_alert = false;
			}
			dialog.setCancelable(false);
			ServiApplication.promotion_id = sharedpreferences.getLong(
					"promoid", 0);
			CommonMethods.click_promo(ShopOpenCloseActivity.this);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_shop_cancel:
			CommonMethods.startIntent(this, MenuActivity.class);
			this.finish();
			break;
		case R.id.btn_shop_close:

			if (CommonMethods.getDoubleFormate(txtDeliveryOut.getText().toString())> CommonMethods.getDoubleFormate(txtDeliveryIn.getText().toString()))
			new AlertDialog.Builder(this)
					.setTitle("Confirmar")
					.setMessage("Delivery tiene una cuenta por saldar.\n¿Desea Continuar con el cierre del Comercio?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo, int which) {
							ActualBalanceDialog dialog = new ActualBalanceDialog(
									ShopOpenCloseActivity.this, uiHandler);
							dialog.show();

						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo, int which) {
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
			else {
				ActualBalanceDialog dialog = new ActualBalanceDialog(
						ShopOpenCloseActivity.this, uiHandler);
				dialog.show();
			}


			break;
		default:
			break;
		}
	}

	// shop close dialog
	private void shopClosingConfimation(String actualBal) {
		ShopCloseDialog closeDialog = new ShopCloseDialog(this, closebal,
				userDto, actualBal);
		closeDialog.show();
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SHOP_CLOSE_ACTUAL.code()) {
				shopClosingConfimation(msg.obj.toString());
			}
		}
	};

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}
}
