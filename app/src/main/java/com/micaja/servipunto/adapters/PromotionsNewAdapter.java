package com.micaja.servipunto.adapters;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class PromotionsNewAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> promotionsList;
	private LayoutInflater inflater;
	ServiApplication appContext;
	private ImageLoaderConfiguration config;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	Bitmap bitmap;
	private File cacheDir;
	private Intent intent;

	public PromotionsNewAdapter(Context context, List<DTO> promotionsList) {
		this.context = context;
		this.promotionsList = promotionsList;
		appContext = (ServiApplication) context.getApplicationContext();

		cacheDir = StorageUtils.getOwnCacheDirectory(context,
				context.getPackageName());
		imageLoader = ImageLoader.getInstance();
		config = new ImageLoaderConfiguration.Builder(context)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.enableLogging().build();
		options = new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.init(config);
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
		convertView = inflater.inflate(R.layout.promotionnewrow, null);
		final TextView provider = (TextView) convertView
				.findViewById(R.id.tex_promotion_des);
		final Button btn_vedioview1 = (Button) convertView
				.findViewById(R.id.btn_vedioview1);
		final Button btn_buy = (Button) convertView.findViewById(R.id.btn_buy);

		final PromotionsDTO dto = (PromotionsDTO) promotionsList.get(position);
		final ImageView img_cart = (ImageView) convertView
				.findViewById(R.id.promotion_image);

		provider.setText(dto.getSupplier_code() + "-" + dto.getSupplier_name());

		btn_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				appContext.setOrder_product(true);
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new GetProductCatlog().execute("" + dto.getPromoid(),
							dto.getSupplier_code());
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}

			}
		});

		if (dto.getVideo_url().length() > 0) {
			btn_vedioview1.setVisibility(View.VISIBLE);
		} else {
			btn_vedioview1.setVisibility(View.GONE);
		}

		btn_vedioview1.setOnClickListener(new OnClickListener() {

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

		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float screenWidth = outMetrics.widthPixels;
		double width = screenWidth / 1;
		double hight = screenWidth / 5;

		img_cart.setScaleType(ImageView.ScaleType.FIT_XY);

		img_cart.setLayoutParams(new RelativeLayout.LayoutParams((int) width,
				(int) hight));

		imageLoader.displayImage(dto.getImage_url(), img_cart, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						bitmap = loadedImage;
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PromotionDialog dialog = new PromotionDialog(context, dto
						.getImage_url(), dto.getVideo_url(), dto
						.getPromotion_des(), dto.getPromoid(), dto
						.getSupplier_code(), 2);
				dialog.show();
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
