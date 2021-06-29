/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.PromotionsBannerAdapter;
import com.micaja.servipunto.adapters.PromotionsNewAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.TextView;

public class PromotionsActivity extends BaseActivity implements
		android.view.View.OnClickListener {

	private GridView lvPromotions;
	private TextView promotion_buytxt, txt_rotated;
	private View txtbuy_view;
	private Button btn_MorePromotions;
	Intent intent;
	private Gallery promotion_gallery;
	final Handler handler = new Handler();
	List<DTO> promotionsList=new ArrayList<DTO>();
	SharedPreferences sharedpreferences;
	boolean flage = false;
	ServiApplication appContext;
	private int ih_req_count=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		intent = new Intent(this, PromotionsActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		appContext = (ServiApplication) getApplicationContext();
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.promotion_newactivity);
		/* Get the data from database */
//		appContext.setPromotionlist(PrmotionDAO.getInstance().getRecords(
//				DBHandler.getDBObj(Constants.WRITABLE)));

		lvPromotions = (GridView) findViewById(R.id.gv_Promotions);

		promotion_buytxt = (TextView) findViewById(R.id.promotion_buytxt);
		txtbuy_view = (View) findViewById(R.id.txtbuy_view);

		btn_MorePromotions = (Button) findViewById(R.id.btn_MorePromotions);
		btn_MorePromotions.setVisibility(View.VISIBLE);
		btn_MorePromotions.setOnClickListener(this);

		promotion_buytxt.setVisibility(View.VISIBLE);
		txtbuy_view.setVisibility(View.VISIBLE);

		promotion_gallery = (Gallery) findViewById(R.id.promotion_gallery);
		promotion_gallery.setSoundEffectsEnabled(false);
		promotion_gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final PromotionsDTO dto = (PromotionsDTO) promotionsList
						.get(position);
				PromotionDialog dialog = new PromotionDialog(
						PromotionsActivity.this, dto.getImage_url(), dto
								.getVideo_url(), dto.getPromotion_des(), dto
								.getPromoid(), dto.getSupplier_code(), 2);
				dialog.show();
				dialog.setCancelable(false);
				ServiApplication.promotion_id = dto.getPromoid();
				CommonMethods.click_promo(PromotionsActivity.this);
			}
		});
		if (appContext.getPromotionlist().size() != 0) {
			lvPromotions.setAdapter(new PromotionsNewAdapter(PromotionsActivity.this, appContext.getPromotionlist()));
			promotion_gallery.setAdapter(new PromotionsBannerAdapter(PromotionsActivity.this, appContext.getPromotionlist()));
			promotionsList=appContext.getPromotionlist();
			testst();
		}else {
			getDataFromServer();
		}
	}

	private void getDataFromServer() {
		if (NetworkConnectivity.netWorkAvailability(PromotionsActivity.this)) {
			new GetPromotions().execute();
		} else {
			CommonMethods.showCustomToast(PromotionsActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	private class GetPromotions extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					PromotionsActivity.this);
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.responds_feed = new ServiceHandler(
					PromotionsActivity.this).makeServiceCall(
					ServiApplication.URL + "/promotions/"+ sharedpreferences.getString("store_code", "")+"?page="+ih_req_count,
					ServiceHandler.GET);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					try {
						lvPromotions.setAdapter(new PromotionsNewAdapter(
								PromotionsActivity.this, promotionsList));
						promotion_gallery.setAdapter(new PromotionsBannerAdapter(
								PromotionsActivity.this, promotionsList));
						JSONObject json=new JSONObject(ServiApplication.responds_feed);
						JSONObject pagenation=json.getJSONObject("paginator");
						ih_req_count=pagenation.getInt("next_page");
						PromotionsParsing(ServiApplication.responds_feed);
						appContext.setPromotionlist(promotionsList);
						getDataFromServer();
					} catch (Exception e) {
						PromotionsParsing(ServiApplication.responds_feed);
						appContext.setPromotionlist(promotionsList);
						CommonMethods.showCustomToast(PromotionsActivity.this,""+promotionsList.size());
						lvPromotions.setAdapter(new PromotionsNewAdapter(
								PromotionsActivity.this, promotionsList));
						promotion_gallery.setAdapter(new PromotionsBannerAdapter(
								PromotionsActivity.this, promotionsList));
						testst();
					}
					
				}else{
					CommonMethods.showCustomToast(PromotionsActivity.this,getResources().getString(R.string.connecterr));
				}
			}else {
				CommonMethods.showCustomToast(PromotionsActivity.this,getResources().getString(R.string.connecterr));
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_MorePromotions:
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(PromotionsActivity.this,MorePromotionsActivity.class);
			break;

		default:
			break;
		}

	}

	public void PromotionsParsing(String responds_feed) {


		try {
			JSONObject jsonobject = new JSONObject(responds_feed);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getpromotion = arrayData.getJSONObject(i);
				PromotionsDTO promotionItem = new PromotionsDTO();
				promotionItem.setImage_url(getpromotion.getString("image_url") + "0");
				promotionItem.setName(getpromotion.getString("name"));
				promotionItem.setStore_code("");
				promotionItem.setSupplier_code(getpromotion.getString("supplier_code"));

				try {
					promotionItem.setSupplier_name(getpromotion.getString("supplier_name"));
				} catch (Exception e) {
					promotionItem.setSupplier_name("");
				}

				promotionItem.setEnd_date(Dates.formateyyyyMMdd(getpromotion.getString("end_date")));
				promotionItem.setStart_date(Dates.formateyyyyMMdd(getpromotion.getString("start_date")));
				try {
					promotionItem.setPromoid(Long.parseLong(getpromotion.getString("display_order")));
				} catch (Exception e) {
					promotionItem.setPromoid(0);
				}
				if (getpromotion.getString("value").equals("null")) {
					promotionItem.setValue("0");
				} else {
					promotionItem.setValue(getpromotion.getString("value"));
				}
				try {
					promotionItem.setPromotion_des(getpromotion.getString("promotion_des"));
				} catch (Exception e) {
					promotionItem.setPromotion_des("");
				}
				promotionItem.setVideo_url(getpromotion.getString("video_url"));
				try {
					JSONArray json = getpromotion.getJSONArray("display_options");

					promotionItem.setWhere_to_show("" + json);
				} catch (Exception e) {
					promotionItem.setWhere_to_show("");
				}
				try {
					promotionItem.setPromoid(getpromotion.getLong("promoid"));
				} catch (Exception e) {
					promotionItem.setPromoid(0);
				}
				if (getpromotion.getString("active").equals("Y")) {
					try {
						Date date_two = Dates.serverdateformateDateObj(getpromotion.getString("end_date"));
						Date current_date = new Date();
						if (date_two.after(current_date) || (current_date.getDate() == date_two.getDate())) {
							promotionsList.add(promotionItem);
						}
					} catch (Exception e) {

					}
				}

			}
		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
	
		
	}

	public void testst() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(ServiApplication.timeToBlink);
				} catch (Exception e) {
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (ServiApplication.galory_position) {
								if (promotionsList.size() - 1 > promotion_gallery
										.getSelectedItemPosition()) {
									if (flage) {
										promotion_gallery.onKeyDown(
												KeyEvent.KEYCODE_DPAD_RIGHT,
												null);
										flage = false;
									} else {
										flage = true;
									}

								} else {
									promotion_gallery.setAdapter(null);
									promotion_gallery
											.setAdapter(new PromotionsBannerAdapter(
													PromotionsActivity.this,
													promotionsList));
									promotion_gallery.onKeyDown(
											KeyEvent.KEYCODE_DPAD_LEFT, null);
								}
								testst();
							} else {

							}
						} catch (Exception e) {
						}
					}
				});
			}
		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ServiApplication.galory_position = false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ServiApplication.galory_position = true;
		testst();

		if (ServiApplication.promotion_bye_flage) {
			CommonMethods.purchase_promo(PromotionsActivity.this);
			ServiApplication.promotion_bye_flage = false;
			ServiApplication.promotion_bye_click_flage = false;
		}

	}

}