package com.micaja.servipunto.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class PromotionsBannerAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> promotionsList=new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;
	private ImageLoaderConfiguration config;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	Bitmap bitmap;
	private File cacheDir;

	public PromotionsBannerAdapter(Context context, List<DTO> promotionsList) {
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
		convertView = inflater.inflate(R.layout.promotion_banner_rowitem, null);
		final TextView provider = (TextView) convertView
				.findViewById(R.id.iv_promotion_des);
		final PromotionsDTO dto = (PromotionsDTO) promotionsList.get(position);
		final ImageView img_cart = (ImageView) convertView
				.findViewById(R.id.iv_banner_row_image);

		provider.setText(dto.getPromotion_des());

		Display display =((Activity) context).getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float screenWidth = outMetrics.widthPixels;
		double width = screenWidth / 0.5;
		double hight = screenWidth / 0.5;

		img_cart.setScaleType(ImageView.ScaleType.FIT_XY);
		img_cart.setLayoutParams(new RelativeLayout.LayoutParams(
				(int) width, (int) hight));
		
		imageLoader.displayImage(dto.getImage_url(),
						img_cart, options, new ImageLoadingListener() {

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

		return convertView;
	}

}