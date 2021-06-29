package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.MorePromotionAdapter;
import com.micaja.servipunto.adapters.PromotionsBannerAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class MorePromotionsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private TextView txt_promtName;
	private ListView lv_morePromotions;
	private Button btnFilter;
	private ArrayAdapter<String> filterAdapter;
	Intent intent;
	private Gallery promotion_gallery;
	final Handler handler = new Handler();
	List<DTO> promotionsList=new ArrayList<DTO>();
	private Hashtable<String, String> groupTable;
	SharedPreferences sharedpreferences;
	private int positions = 0;
	boolean flage = false;
	private String input_value;
	ServiApplication appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		intent = new Intent(this, MorePromotionsActivity.class);
		appContext = (ServiApplication) getApplicationContext();
		inItUI();
	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.more_promotion_activity);
		lv_morePromotions = (ListView) findViewById(R.id.lv_morePromotions);

		btnFilter = (Button) findViewById(R.id.btnFilter);
		btnFilter.setText(getResources().getString(R.string.spn_all));
		btnFilter.setOnClickListener(this);

		promotion_gallery = (Gallery) findViewById(R.id.promotion_gallery);
		promotion_gallery.setSoundEffectsEnabled(false);
		promotion_gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final PromotionsDTO dto = (PromotionsDTO) promotionsList
						.get(position);
				PromotionDialog dialog = new PromotionDialog(
						MorePromotionsActivity.this, dto.getImage_url(), dto
								.getVideo_url(), dto.getPromotion_des(),dto.getPromoid(),
								dto.getSupplier_code(),3);
				dialog.show();
				dialog.setCancelable(false);
				ServiApplication.promotion_id = dto.getPromoid();
				CommonMethods.click_promo(MorePromotionsActivity.this);
			}
		});
		// loadUI();
		if (appContext.getPromotionlist().size() != 0) {
			promotionsList = appContext.getPromotionlist();
			lv_morePromotions.setAdapter(new MorePromotionAdapter(
					MorePromotionsActivity.this, promotionsList));
			promotion_gallery.setAdapter(new PromotionsBannerAdapter(
					MorePromotionsActivity.this, promotionsList));
			testst();
		} else
			getDataFormCentralServer();
		

	}
	// Result of this method,getting data from Central Server
	private void getDataFormCentralServer() {
		if (NetworkConnectivity
				.netWorkAvailability(MorePromotionsActivity.this)) {

			new GetPromotions().execute();
		} else {
			CommonMethods.showCustomToast(MorePromotionsActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}
	// Result of this method,display promotion based on the filter options
	private class GetPromotions extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					MorePromotionsActivity.this);
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			if (positions == 0) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/"
										+ sharedpreferences.getString(
												"store_code", ""),
								ServiceHandler.GET);
			} else if (positions == 1) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=recent", ServiceHandler.GET);
			} else if (positions == 2) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=expiry", ServiceHandler.GET);
			} else if (positions == 3) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=product&name=" + input_value,
								ServiceHandler.GET);

			} else if (positions == 4) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=product&barcode="
										+ input_value, ServiceHandler.GET);

			} else if (positions == 5) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=product&lineid="
										+ input_value, ServiceHandler.GET);

			} else if (positions == 6) {
				ServiApplication.responds_feed = new ServiceHandler(MorePromotionsActivity.this)
						.makeServiceCall(
								ServiApplication.URL
										+ "/promotions/search/"
										+ sharedpreferences.getString(
												"store_code", "")
										+ "?sortby=supplier&code="
										+ input_value, ServiceHandler.GET);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (ServiApplication.connectionTimeOutState) {

				if (positions == 0) {
					promotionsList = new ParsingHandler()
							.getPromotionsData(ServiApplication.responds_feed);
					appContext.setPromotionlist(promotionsList);
				} else {
					promotionsList = new ParsingHandler()
							.getPromotionsDatawitharray(ServiApplication.responds_feed);
//					appContext.setPromotionlist(promotionsList);
				}

				lv_morePromotions.setAdapter(new MorePromotionAdapter(
						MorePromotionsActivity.this, promotionsList));
				promotion_gallery.setAdapter(new PromotionsBannerAdapter(
						MorePromotionsActivity.this, promotionsList));
				testst();
			}
			CommonMethods.dismissProgressDialog();
		}
	}
	// This method using for Handler message to update the UI thread and promotions gallery
	private void testst() {
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
													MorePromotionsActivity.this,
													promotionsList));
									 promotion_gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
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
		super.onResume();
		ServiApplication.galory_position = true;
		testst();
		if (ServiApplication.promotion_bye_flage) {
			CommonMethods.purchase_promo(MorePromotionsActivity.this);
			ServiApplication.promotion_bye_flage = false;
			ServiApplication.promotion_bye_click_flage = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnFilter:
			showFilters();
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);
			} catch (Exception e) {
			}

			break;

		default:
			break;
		}

	}
	// Result of this method,showing filter options
	private void showFilters() {
		final Dialog dialog = new Dialog(MorePromotionsActivity.this);
		dialog.setContentView(R.layout.promotion_custom_filters);
		dialog.setTitle(getResources().getString(R.string.promotion_filters));

		RelativeLayout layout1 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterAll);
		RelativeLayout layout2 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterRecentdate);
		RelativeLayout layout3 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterPromotionExpire);
		RelativeLayout layout4 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterProducts);
		RelativeLayout layout5 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterBarcode);
		RelativeLayout layout6 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterLine);
		RelativeLayout layout7 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterSupplier);

		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		final RadioButton radio1 = (RadioButton) dialog
				.findViewById(R.id.radio_All);
		final RadioButton radio2 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterRecentdate);
		final RadioButton radio3 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterPromotionExpire);
		final RadioButton radio4 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterProducts);
		final RadioButton radio5 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterBarcode);
		final RadioButton radio6 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterLine);
		final RadioButton radio7 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterSupplier);

		final RelativeLayout layout8 = (RelativeLayout) dialog
				.findViewById(R.id.rel_AutoComplete);
		final AutoCompleteTextView actv = (AutoCompleteTextView) dialog
				.findViewById(R.id.ac_Group);
		final Button btnOK = (Button) dialog.findViewById(R.id.btn_OK);

		@SuppressWarnings("unchecked")
		ArrayAdapter adapter = new ArrayAdapter(MorePromotionsActivity.this,
				android.R.layout.simple_list_item_1);
		actv.setAdapter(adapter);

		String selected = btnFilter.getText().toString().trim();

		if (selected.equals(getResources().getString(R.string.spn_all))) {
			radio1.setChecked(true);
		} else if (selected.equals(getResources().getString(
				R.string.spnfliter_date))) {
			radio2.setChecked(true);
		} else if (selected.equals(getResources().getString(
				R.string.spnfliter_expire))) {
			radio3.setChecked(true);
		} else if (selected.equals(getResources().getString(
				R.string.spnfliter_products)))
			radio4.setChecked(true);
		else if (selected.equals(getResources().getString(
				R.string.spnfliter_barcode)))
			radio5.setChecked(true);
		else if (selected.equals(getResources().getString(
				R.string.spnfliter_line)))
			radio6.setChecked(true);
		else {
			radio7.setChecked(true);
			layout8.setVisibility(View.VISIBLE);
			actv.setText(selected);
			actv.dismissDropDown();
		}
		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources()
							.getString(R.string.spn_all));
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					positions = 0;
					getDataFormCentralServer();
					dialog.dismiss();
				}
			}
		});
		radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_date));
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					positions = 1;
					getDataFormCentralServer();
					dialog.dismiss();
				}
			}
		});
		radio3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_expire));
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					positions = 2;
					getDataFormCentralServer();
					dialog.dismiss();
				}
			}
		});
		radio4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_products));
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					positions = 3;
				}
			}
		});
		radio5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_barcode));
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					positions = 4;
				}
			}
		});
		radio6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_line));
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio7.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					positions = 5;

				}
			}
		});
		radio7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spnfliter_supplier));
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					positions = 6;

				}
			}
		});
		layout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(true);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(false);
				dialog.dismiss();
			}
		});
		layout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(true);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(false);
				dialog.dismiss();
			}
		});
		layout3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(true);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(false);
				dialog.dismiss();
			}
		});
		layout4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(true);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(false);
				layout8.setVisibility(View.VISIBLE);
			}
		});
		layout5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(true);
				radio6.setChecked(false);
				radio7.setChecked(false);
				layout8.setVisibility(View.VISIBLE);
			}
		});
		layout6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(true);
				radio7.setChecked(false);
				layout8.setVisibility(View.VISIBLE);
			}
		});
		layout7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(true);
				layout8.setVisibility(View.VISIBLE);
			}
		});
		// if button is clicked, close the custom dialog
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String group = actv.getText().toString().trim();
				if ("".equals(group))
					CommonMethods.showCustomToast(MorePromotionsActivity.this,
							getResources().getString(R.string.invalid_msg));
				else {
					btnFilter.setText(group);
					input_value = group;
					actv.setText("");
					dialog.dismiss();
					getDataFormCentralServer();
				}
			}
		});

		dialog.show();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

}
