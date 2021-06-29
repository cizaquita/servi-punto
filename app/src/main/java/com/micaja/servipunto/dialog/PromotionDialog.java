package com.micaja.servipunto.dialog;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.activities.MorePromotionsActivity;
import com.micaja.servipunto.activities.OrdersProductsActivity;
import com.micaja.servipunto.activities.PromotionsActivity;
import com.micaja.servipunto.activities.ShopOpenCloseActivity;
import com.micaja.servipunto.database.dto.DTO;
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

public class PromotionDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btn_videoview, btn_buy;
	ServiApplication appContext;
	// private VideoView video_url;
	private ImageView promotion_image, img_close;

	private String image_url, videlo_url, promotion_des, Supplier_code;
	private long promotion_id;
	int class_name;
	private ImageLoaderConfiguration config;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	Bitmap bitmap;
	private File cacheDir;
	private TextView tex_promotion_des;
	private Intent intent;

	public PromotionDialog(Context context, String Image_url, String Video_url,
			String Promotion_Ddes, long promotion_id, String Supplier_code,
			int class_name) {

		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();

		this.videlo_url = Video_url;
		this.image_url = Image_url;
		this.promotion_des = Promotion_Ddes;
		this.promotion_id = promotion_id;
		this.Supplier_code = Supplier_code;
		this.class_name = class_name;

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

		if (class_name == 0) {
			intent = new Intent(context, ShopOpenCloseActivity.class);
		} else if (class_name == 1) {
			intent = new Intent(context, MenuActivity.class);
		} else if (class_name == 2) {
			intent = new Intent(context, PromotionsActivity.class);
		} else if (class_name == 3) {
			intent = new Intent(context, MorePromotionsActivity.class);
		}
		
		appContext.setOrder_product(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	// Result of this method,registration for all UI views.
	@SuppressLint("ResourceAsColor")
	private void initUI() {

		setContentView(R.layout.promotion_alertdialog);

		btn_videoview = (Button) findViewById(R.id.btn_videoview);
		// video_url = (VideoView) findViewById(R.id.promotion_vedio);
		promotion_image = (ImageView) findViewById(R.id.promotion_image);
		img_close = (ImageView) findViewById(R.id.img_close);
		btn_buy = (Button) findViewById(R.id.btn_buy);
		tex_promotion_des = (TextView) findViewById(R.id.tex_promotion_des);
		tex_promotion_des.setText(promotion_des);
		tex_promotion_des.setBackgroundColor(android.R.color.transparent);
		btn_buy.setOnClickListener(this);
		img_close.setOnClickListener(this);
		btn_videoview.setOnClickListener(this);

		if (0 != videlo_url.length()) {
			btn_videoview.setVisibility(View.VISIBLE);
			btn_videoview.setClickable(true);
		} else {
			btn_videoview.setVisibility(View.GONE);
			btn_videoview.setClickable(false);
		}
		loadUI();

	}

	// load image ,image_url  to the screen
	private void loadUI() {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float screenWidth = outMetrics.widthPixels;
		double width = screenWidth / 1;
		double hight = screenWidth / 1;

		promotion_image.setScaleType(ImageView.ScaleType.FIT_XY);
		promotion_image.setLayoutParams(new RelativeLayout.LayoutParams(
				(int) width, (int) hight));

		imageLoader.displayImage(image_url, promotion_image, options,
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
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_buy:
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new GetProductCatlog()
						.execute("" + promotion_id, Supplier_code);
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
			break;

		case R.id.btn_videoview:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(ServiApplication.URL + videlo_url),
					"video/*");
			v.getContext().startActivity(intent);
			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}
// get promotion based on prodcts
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
