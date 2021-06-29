/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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

import com.micaja.servipunto.activities.AddDeliveryActivity;
import com.micaja.servipunto.activities.DeliveryActivity;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;


import com.micaja.servipunto.database.dto.AddDeliveryScreenData;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.PayDeliveryDialog;
import com.micaja.servipunto.dialog.PayDialog;
import com.micaja.servipunto.dialog.PayDialog.onSaveListener;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAdapter extends BaseAdapter implements onSaveListener, PayDeliveryDialog.onSaveListener {

	private Context context;
	private List<DTO> deliveryResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;
	onSaveListener listener;
	public Dialog dialog;
	public DeliveryDTO dto1;
	private UserDetailsDTO udto;
	public SharedPreferences sharedpreferences;
	public String pay_type;

	public DeliveryAdapter(Context context, List<DTO> deliveryResultsList) {
		this.context = context;
		this.deliveryResultsList = deliveryResultsList;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	@Override
	public int getCount() {
		return deliveryResultsList.size();
	}

	public void setData(List<DTO> deliveryResultsList) {
		this.deliveryResultsList = deliveryResultsList;
		notifyDataSetChanged();

	}

	@Override
	public Object getItem(int position) {
		return deliveryResultsList.get(position);
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

			convertView = inflater.inflate(R.layout.client_row, null);
		}

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_Name);
		name.setOnClickListener(mItemClickListener);
		name.setTag(position);
		final TextView payment = (TextView) convertView
				.findViewById(R.id.txt_Payment);
		payment.setOnClickListener(mItemClickListener);
		payment.setTag(position);
		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_Date);
		date.setOnClickListener(mItemClickListener);
		date.setTag(position);
		ImageView ivEmail = (ImageView) convertView
				.findViewById(R.id.img_Email);
		ivEmail.setTag(position);
		ImageView ivEdit = (ImageView) convertView.findViewById(R.id.img_Edit);
		ivEdit.setTag(position);
		ImageView ivLendDelivery = (ImageView) convertView
				.findViewById(R.id.img_LendMoney);
		ImageView ivPay = (ImageView) convertView.findViewById(R.id.img_Pay);
		final DeliveryDTO dto = (DeliveryDTO) deliveryResultsList.get(position);

		name.setText(dto.getName() + "-" + dto.getTelephone());
		payment.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(dto.getBalanceAmount())));

		if (!"".equals(dto.getLastPaymentDate())
				&& dto.getLastPaymentDate() != null) {
			// date.setText(dto.getLastPaymentDate().split(":")[0]);
			try {
				date.setText(Dates.changeFormate(dto.getLastPaymentDate()));
			} catch (Exception e) {
				date.setText("");
			}

		} else
			date.setText(dto.getLastPaymentDate());

		if (dto.getBalanceAmount().equals("")
				|| dto.getBalanceAmount().equals("0")
				|| dto.getBalanceAmount().equals("0.0")) {
			ivPay.setVisibility(View.INVISIBLE);
		} else {
			ivPay.setVisibility(View.VISIBLE);

		}

		ivEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int itempos = (Integer) v.getTag();
					DeliveryDTO data = (DeliveryDTO) deliveryResultsList.get(itempos);
					Intent email = new Intent(Intent.ACTION_SEND);
					email.setType("message/rfc822");
					email.putExtra(Intent.EXTRA_EMAIL,
							new String[] { data.getEmail() });

					context.startActivity(Intent.createChooser(email,
							"Choose an Email client"));
				} catch (Exception e) {
				}

			}
		});
		ivEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int itempos = (Integer) v.getTag();
					DeliveryDTO data = (DeliveryDTO) deliveryResultsList.get(itempos);
					appContext.mAddDeliveryScreenData = new AddDeliveryScreenData();
					appContext.mAddDeliveryScreenData.mScreenData = data;
					appContext.mAddDeliveryScreenData.mBackStackClass = DeliveryActivity.class;

					appContext.pushActivity(new Intent(context,
							MenuActivity.class));

					Intent intent = new Intent(context, AddDeliveryActivity.class);

					context.startActivity(intent);

				} catch (Exception e) {
				}
			}
		});

		ivLendDelivery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (udto.getIs_admin().equals("Y")) {
					PayDeliveryDialog dialog = new PayDeliveryDialog(context, dto,
							ConstantsEnum.DELIVERY_LEND.code());
					dialog.setListener(DeliveryAdapter.this);
					dialog.show();

				} else if (udto.getIs_authorized().equals("Y")) {
					pay_type=ConstantsEnum.DELIVERY_LEND.code();
					checkStoreAutontication();
					dto1 = dto;

				} else {
					CommonMethods
							.showCustomToast(context, context.getResources()
									.getString(R.string.discount_errmsg));
				}

			}
		});
		ivPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			if (udto.getIs_admin().equals("Y")) {
				PayDeliveryDialog dialog = new PayDeliveryDialog(context, dto,
						ConstantsEnum.DELIVERY_PAYMENT.code());
				dialog.setListener((PayDeliveryDialog.onSaveListener) DeliveryAdapter.this);
				dialog.show();

				} else if (udto.getIs_authorized().equals("Y")) {
					pay_type=ConstantsEnum.DELIVERY_PAYMENT.code();
					checkStoreAutontication();
					dto1 = dto;

				} else {
					CommonMethods
							.showCustomToast(context, context.getResources()
									.getString(R.string.discount_errmsg));
				}
			
			
				

			}
		});
		return convertView;
	}

	@Override
	public void onSave() {

		DeliveryAdapter.this.notifyDataSetChanged();
	}

	private OnClickListener mItemClickListener;

	public void setItemListener(OnClickListener mListItemClickListener) {
		this.mItemClickListener = mListItemClickListener;
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

					PayDeliveryDialog dialog = new PayDeliveryDialog(context, dto1,
							pay_type);
					dialog.setListener((PayDeliveryDialog.onSaveListener) DeliveryAdapter.this);

					dialog.show();
				} else {
					CommonMethods.showCustomToast(context,
							context.getString(R.string.store_pass_error));
				}
			}
		}

	}
	

}
