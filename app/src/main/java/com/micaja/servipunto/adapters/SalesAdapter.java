/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.DeleteDailog;
import com.micaja.servipunto.dialog.SalesCalculatorDialog;
import com.micaja.servipunto.dialog.UnitsDialog;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.SalesEditTypes;

public class SalesAdapter extends BaseAdapter {
	private Context context;
	private List<DTO> selectedList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private String type;
	private Handler uiHandler;
	private String UOMScreen, screenMode, fromScreen;
	private UserDetailsDTO udto;
	public SharedPreferences sharedpreferences;
	public Dialog dialog;
	private int position1;
	private String value;
	private boolean flage = false;

	public SalesAdapter(Context mContext, List<DTO> selectedList,
			Handler uiHandler, String type, String UOMScreen) {
		this.context = mContext;
		this.selectedList = selectedList;
		this.uiHandler = uiHandler;
		this.type = type;
		this.UOMScreen = UOMScreen;
		sharedpreferences = mContext.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	public SalesAdapter(Context mContext, List<DTO> selectedList,
			Handler uiHandler, String type, String UOMScreen,
			String screenMode, String fromScreen) {
		this.context = mContext;
		this.selectedList = selectedList;
		this.uiHandler = uiHandler;
		this.type = type;
		this.UOMScreen = UOMScreen;
		this.screenMode = screenMode;
		this.fromScreen = fromScreen;
		sharedpreferences = mContext.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	public void setListData(List<DTO> selectedList) {
		this.selectedList = selectedList;
		System.out.println(selectedList.size());
	}

	@Override
	public int getCount() {
		return selectedList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.sales_row, null);

		final Button delete = (Button) convertView
				.findViewById(R.id.btn_SalesAdpaDelete);
		final TextView product = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaProduct);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaPrice);
		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaQty);
		final TextView units = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaUnits);

		SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
				.get(position);
		Log.v("========================", "=============Product Info=========="+ dto.getName());
		product.setText(dto.getName());

		if (fromScreen != null && fromScreen.equals("CreateDish")) {
			System.out.println("Price If:" + dto.getPrice());
			price.setText(CommonMethods.getNumSeparator(CommonMethods
					.getDoubleFormate(dto.getPrice())));
		} else {
			price.setText(CommonMethods.getNumSeparator(CommonMethods
					.getDoubleFormate(dto.getSellPrice())));
			System.out.println("Price else:" + dto.getSellPrice());
		}

		qty.setText(dto.getQuantity());

		if (screenMode != null && screenMode.equals("Edit"))
			delete.setVisibility(View.INVISIBLE);

		if (dto.getUnits() != "" && dto.getUnits() != null)
			units.setText(dto.getUnits());

		price.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(position);
				position1 = position;
				if (ServiApplication.shale_menu) {
					value = dto.getSellPrice();
				} else {
					value = dto.getPrice();
				}
				type = SalesEditTypes.PRICE.code();

				if (udto.getIs_admin().equals("Y")) {
					SalesCalculatorDialog dialog = new SalesCalculatorDialog(
							context, position, type, "", value, uiHandler);
					dialog.show();

				} else if (udto.getIs_authorized().equals("Y")) {
					checkStoreAutontication();

				} else {
					CommonMethods
							.showCustomToast(context, context.getResources()
									.getString(R.string.discount_errmsg));
				}

			}
		});

		qty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(position);
				position1 = position;
				value = dto.getQuantity();
				type = SalesEditTypes.QUANTITY.code();

				if (udto.getIs_admin().equals("Y")) {

					SalesCalculatorDialog dialog = new SalesCalculatorDialog(
							context, position, type, "", value, uiHandler);
					dialog.show();

				} else if (udto.getIs_authorized().equals("Y")) {

					checkStoreAutontication();

				} else {
					CommonMethods
							.showCustomToast(context, context.getResources()
									.getString(R.string.discount_errmsg));
				}

			}
		});

		units.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(position);
				flage = true;
				position1 = position;
				value = dto.getUnits();

				if (!"".equals(value) && value != null) {
					type = SalesEditTypes.UNITS.code();
					// UnitsDialog dialog = new UnitsDialog(context,
					// position, uiHandler, type, UOMScreen);
					// dialog.show();

					if (udto.getIs_admin().equals("Y")) {

						UnitsDialog dialog = new UnitsDialog(context, position,
								uiHandler, type, UOMScreen);
						dialog.show();

					} else if (udto.getIs_authorized().equals("Y")) {
						checkStoreAutontication();
					} else {
						CommonMethods.showCustomToast(
								context,
								context.getResources().getString(
										R.string.discount_errmsg));
					}
				}
			}
		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteDailog dialog = new DeleteDailog(context, position, "",
						uiHandler);
				dialog.show();
			}
		});

		return convertView;
	}

	protected void checkStoreAutontication() {

		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.cash_dialog);
		final TextView txt_reason = (TextView) dialog
				.findViewById(R.id.txt_reason);
		final EditText etxt_reason = (EditText) dialog
				.findViewById(R.id.etxt_reason);
		final LinearLayout layout_value = (LinearLayout) dialog
				.findViewById(R.id.layout_value);
		layout_value.setVisibility(View.GONE);
		txt_reason.setText(context.getString(R.string.store_pass));
		final TextView txt_dialog_title = (TextView) dialog
				.findViewById(R.id.txt_dialog_title);
		txt_dialog_title.setText(context.getString(R.string.alert));

		final ImageView img_close = (ImageView) dialog
				.findViewById(R.id.img_close);

		img_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// etxt_reason.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		int maxLength = 20;
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		etxt_reason.setFilters(fArray);
		etxt_reason.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		etxt_reason.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (etxt_reason.getText().toString().contains(" ")) {
					CommonMethods.showCustomToast(
							context,
							context.getResources().getString(
									R.string.no_spaces_allowed));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (etxt_reason.getText().toString().contains(" ")) {
					int lenght = etxt_reason.getText().toString().length();
					s.delete(lenght - 1, lenght);
				}
			}
		});
		final Button btn_dialog_save, btn_dialog_cancel;

		btn_dialog_save = (Button) dialog.findViewById(R.id.btn_dialog_save);
		btn_dialog_save.setText(context.getString(R.string.go));

		btn_dialog_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					if (etxt_reason.getText().length() > 0) {
						new StorePasswordCheck().execute(etxt_reason.getText()
								.toString());
					} else {
						CommonMethods.showCustomToast(
								context,
								context.getResources().getString(
										R.string.fileds_not_blank));
					}
				} else {
					CommonMethods
							.showCustomToast(context, context.getResources()
									.getString(R.string.discount_errmsg));
				}
			}
		});

		btn_dialog_cancel = (Button) dialog
				.findViewById(R.id.btn_dialog_cancel);

		btn_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private class StorePasswordCheck extends AsyncTask<String, Void, Void> {
		ServiceHandler serviceHandler;
		private String result, reqjson;;

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			serviceHandler = new ServiceHandler(context);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				JSONObject json = new JSONObject();
				json.put("store_code", udto.getNitShopId());
				json.put("store_password", params[0]);
				reqjson = json.toString();
				ServiApplication.responds_feed = serviceHandler
						.makeServiceCall(ServiApplication.URL
								+ "/store/validate-password",
								serviceHandler.POST, reqjson);
			} catch (Exception e) {
				ServiApplication.connectionTimeOutState = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					dialog.dismiss();
					if (flage) {
						UnitsDialog dialog = new UnitsDialog(context,
								position1, uiHandler, type, UOMScreen);
						dialog.show();
					} else {
						SalesCalculatorDialog dialog = new SalesCalculatorDialog(
								context, position1, type, "", value, uiHandler);
						dialog.show();
					}
				} else {
					CommonMethods.showCustomToast(context,
							context.getString(R.string.store_pass_error));
				}
			}
		}

	}

}
