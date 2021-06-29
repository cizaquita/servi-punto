package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MorePromotionsActivity;
import com.micaja.servipunto.activities.OrdersProductsActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class MorePromotionAdapter extends BaseAdapter {
	private Context context;
	ServiApplication appContext;
	private LayoutInflater inflater;
	private List<DTO> promotionsList = new ArrayList<DTO>();
	private Intent intent;

	public MorePromotionAdapter(Context context, List<DTO> promotionsList) {
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		intent = new Intent(context, MorePromotionsActivity.class);
		this.promotionsList = promotionsList;
	}

	@Override
	public int getCount() {
		return promotionsList.size();
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
		convertView = inflater.inflate(R.layout.more_promotion_row, null);
		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_morepromotionName);

		final TextView description = (TextView) convertView
				.findViewById(R.id.txt_morepromDesp);
		final TextView supplier = (TextView) convertView
				.findViewById(R.id.txt_morepromotionSupp);
		final TextView sdate = (TextView) convertView
				.findViewById(R.id.txt_morepromotionSdate);

		final TextView edate = (TextView) convertView
				.findViewById(R.id.txt_morepromotionEdate);
		final ImageView img_buy = (ImageView) convertView
				.findViewById(R.id.img_morepromotionBuy);
		final TextView txt_value = (TextView) convertView
				.findViewById(R.id.txt_value);
		final PromotionsDTO dto = (PromotionsDTO) promotionsList.get(position);

		name.setText(dto.getName());
		description.setText(dto.getPromotion_des());
		sdate.setText(dto.getStart_date());
		edate.setText(dto.getEnd_date());
		supplier.setText(dto.getSupplier_code() + "-" + dto.getSupplier_name());
		txt_value.setText(dto.getValue());
		// SupplierDTO supDTO =
		// SupplierDAO.getInstance().getRecordsBySupplierID(
		// DBHandler.getDBObj(Constants.READABLE), dto.getSupplier_code());
		//
		// if (supDTO.getName() != null) {
		// supplier.setText(supDTO.getName() + "-" + dto.getSupplier_code());
		// } else {
		// supplier.setText(dto.getSupplier_code());
		// }

		img_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new GetProductCatlog().execute("" + dto.getPromoid(),
							dto.getSupplier_code());
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PromotionDialog dialog = new PromotionDialog(context, dto
						.getImage_url(), dto.getVideo_url(), dto
						.getPromotion_des(), dto.getPromoid(), dto
						.getSupplier_code(), 3);
				dialog.show();
				dialog.setCancelable(false);
				ServiApplication.promotion_id = dto.getPromoid();
				CommonMethods.click_promo(context);
			}
		});

		return convertView;
	}

	private class GetProductCatlog extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(context);
		String responds = null, supplier_code;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.promotion_id = Long.parseLong(params[0]);
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/products/get-by-promotion/" + params[0],
					ServiceHandler.GET);
			supplier_code = params[1];
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				try {
					if (new JSONObject(responds).getString("status")
							.equals("1")) {
						ServiApplication.responds_feed = responds;
						List<DTO> list = new ParsingHandler().get_by_promotion(
								ServiApplication.responds_feed, supplier_code);
						appContext.setOrderSupplierList(list);

						try {
							appContext.pushActivity(intent);
							appContext.setSeletedSupplier(String.valueOf(0));
							CommonMethods.openNewActivity(context,
									OrdersProductsActivity.class);
							ServiApplication.promotion_bye_click_flage = true;
						} catch (Exception e) {
						}
					} else {

						CommonMethods.showCustomToast(context, context
								.getResources().getString(R.string.nodata));

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else {
				ServiApplication.responds_feed = null;
			}
			CommonMethods.dismissProgressDialog();
		}
	}
}
