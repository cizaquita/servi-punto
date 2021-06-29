package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.micaja.servipunto.activities.OrdersProductsActivity;
import com.micaja.servipunto.activities.PromotionsActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class PromotionsAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> promotionsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;
	private Intent intent;

	public PromotionsAdapter(Context context, List<DTO> promotionsList) {
		this.context = context;
		this.promotionsList = promotionsList;
		appContext = (ServiApplication) context.getApplicationContext();
		intent = new Intent(context, PromotionsActivity.class);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.promotion_row, null);
		final TextView provider = (TextView) convertView
				.findViewById(R.id.txt_providername);

		final TextView product_details = (TextView) convertView
				.findViewById(R.id.txt_promotionname);
		final TextView txt_video = (TextView) convertView
				.findViewById(R.id.txt_video);
		final PromotionsDTO dto = (PromotionsDTO) promotionsList.get(position);
		final ImageView img_cart = (ImageView) convertView
				.findViewById(R.id.img_promotionCart);
		final View view_txt = (View) convertView.findViewById(R.id.cart_view);
		img_cart.setVisibility(View.VISIBLE);
		view_txt.setVisibility(View.VISIBLE);
		provider.setText(dto.getSupplier_code() + "-" + dto.getSupplier_name());
		// SupplierDTO supDTO =
		// SupplierDAO.getInstance().getRecordsBySupplierID(
		// DBHandler.getDBObj(Constants.READABLE), dto.getSupplier_code());
		//
		// if (supDTO.getName() != null) {
		// provider.setText(supDTO.getName());
		// } else {
		// provider.setText(dto.getSupplier_code());
		// }
		if (dto.getImage_url().length() > 0) {
			product_details.setText(context.getResources().getString(
					R.string.view_image));
		}
		if (dto.getVideo_url().length() > 0) {
			txt_video.setText(context.getResources().getString(
					R.string.view_video));
		} else {
		}
		product_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PromotionDialog dialog = new PromotionDialog(context, dto
						.getImage_url(), dto.getVideo_url(), dto
						.getPromotion_des(), dto.getPromoid(), dto
						.getSupplier_code(), 2);
				dialog.show();

			}
		});
		txt_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dto.getVideo_url().length() > 0) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(ServiApplication.URL
							+ dto.getVideo_url()), "video/*");
					v.getContext().startActivity(intent);
				} else {
				}

			}
		});

		img_cart.setOnClickListener(new OnClickListener() {

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
			supplier_code = params[1];
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/products/get-by-promotion/" + params[0],
					ServiceHandler.GET);
			ServiApplication.promotion_id = Long.parseLong(params[0]);
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
				}

			} else {
				ServiApplication.responds_feed = null;
			}
			CommonMethods.dismissProgressDialog();
		}
	}
}