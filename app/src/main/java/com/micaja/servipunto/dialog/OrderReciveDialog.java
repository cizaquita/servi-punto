package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ProductFinalActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.OrderReceiveDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class OrderReciveDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	ServiApplication appContext;
	private EditText etxtActualBalance;
	private Button btnOK;
	private TextView orderInvNo, txtDialogTitle;
	private ImageView imgClose;
	private String actualBal;
	public SharedPreferences sharedpreferences;

	public OrderReciveDialog(Context context) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
		appContext.setOrder_reciveinvoice("");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {

		setContentView(R.layout.actual_balance);

		etxtActualBalance = (EditText) findViewById(R.id.etxt_ActualBalance);
		btnOK = (Button) findViewById(R.id.btn_OK);
		orderInvNo = (TextView) findViewById(R.id.order_inv_no);
		txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setVisibility(View.VISIBLE);

		orderInvNo.setText((context.getResources()
				.getString(R.string.invoice_no)));

		txtDialogTitle.setText((context.getResources()
				.getString(R.string.inv_recieve)));
		txtDialogTitle.setBackgroundColor(context.getResources().getColor(
				R.color.inventory_tab));

		// btnOK.setBackgroundResource(R.drawable.inventory_savebtn_bg);

		btnOK.setOnClickListener(this);
		imgClose.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_OK:
			validate();
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private boolean validate() {
		actualBal = etxtActualBalance.getText().toString().trim();
		if ("".equals(actualBal)) {

			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.enter_invoice_num));

			return true;
		} else {

			if (NetworkConnectivity.netWorkAvailability(context)) {
				new GetInvoiceDetailsService().execute(actualBal);

			} else {
				CommonMethods.showCustomToast(context,
						context.getString(R.string.no_wifi_adhoc));
			}
			return false;
		}
	}

	public class GetInvoiceDetailsService extends AsyncTask<String, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		String responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			ServiApplication.orederReceivedata.clear();
		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/orders/get-orderdetails", ServiceHandler.POST,
					getjsonObj(params[0]));
			appContext.setOrder_reciveinvoice(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			if (new JSONStatus().status(responds) == 0) {
				ServiApplication.orederReceivedata = new ParsingHandler()
						.getProductList(responds);
				getBarcodeInfo();
			} else {
				CommonMethods.showCustomToast(context,
						context.getString(R.string.invalid_invoice));
			}
		}

	}

	public String getjsonObj(String string) {
		JSONObject json = new JSONObject();
		try {
			json.put("order_id", Long.parseLong(string));
			json.put("store_code", ServiApplication.store_id);
		} catch (Exception e) {
		}
		return json.toString();
	}

	public void getBarcodeInfo() {
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new GetProductData().execute();

		} else {
			CommonMethods.showCustomToast(context,
					context.getString(R.string.no_wifi_adhoc));
		}
	}

	public class GetProductData extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		String responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (DTO dto : ServiApplication.orederReceivedata) {
				OrderReceiveDTO Odto = (OrderReceiveDTO) dto;
				responds = servicehandler.makeServiceCall(ServiApplication.URL
						+ "/product-master/" + Odto.getBarcode(),
						ServiceHandler.GET);
				ProductDTO prodto = new ParsingHandler().mAsterProductInfo(responds);
				prodto.setPurchasePrice("" + Odto.getPrice());
				try {
					if (prodto.getSellingPrice().equals("0.0")
							|| prodto.getSellingPrice().equals("0")) {
						prodto.setSellingPrice("" + Odto.getPrice());
					}
				} catch (Exception e) {
					prodto.setSellingPrice("" + Odto.getPrice());
				}

				appContext.setSeletedSupplier(prodto.getSupplierId());
				ProductDTO LocalproductDto = ProductDAO.getInstance()
						.getRecordsByProductID(
								DBHandler.getDBObj(Constants.READABLE),
								prodto.getBarcode());
				try {
					if (LocalproductDto.getBarcode().length() > 0) {
						LocalproductDto.setPurchasePrice("" + Odto.getPrice());
						LocalproductDto.setSyncStatus(0);
						ProductDAO.getInstance().update_orderproduct(
								DBHandler.getDBObj(Constants.WRITABLE),
								LocalproductDto);
					} else
						ProductDAO.getInstance().insert(
								DBHandler.getDBObj(Constants.WRITABLE),
								makeProductArrayForInsert(prodto));
				} catch (Exception e) {
					ProductDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE),
							makeProductArrayForInsert(prodto));
				}
				List<DTO> ldto = new ArrayList<DTO>();
				SupplierDTO sdto = new SupplierDTO();
				sdto.setCedula(prodto.getSupplierId());
				sdto.setName(prodto.getSupplierName());
				sdto.setVisitFrequency("" + 0);
				sdto.setBalanceAmount("0");
				sdto.setCreatedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
				ldto.add(sdto);
				SupplierDTO localSDTO = SupplierDAO.getInstance()
						.getRecordsBySupplierID(
								DBHandler.getDBObj(Constants.READABLE),
								prodto.getSupplierId());
				try {
					if (localSDTO.getSupplierId().length() > 0) {

					} else
						SupplierDAO.getInstance().insert(
								DBHandler.getDBObj(Constants.WRITABLE), ldto);
				} catch (Exception e) {
					SupplierDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE), ldto);
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			if (new JSONStatus().status(responds) == 0) {
				dismissdialog();
				Intent prodIntent = new Intent(context,
						ProductFinalActivity.class);
				context.startActivity(prodIntent);
			}
		}
	}

	public List<DTO> makeProductArrayForInsert(ProductDTO prodto) {

		@SuppressWarnings("static-access")
		String fecha_final = new Dates().serverdateformate(Dates
				.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		String uom = "";
		List<DTO> productList = new ArrayList<DTO>();
		if (prodto.getUom().equals("0")) {
			uom = "";
		} else if (prodto.getUom().equals("1")) {
			uom = "Kg";
		} else if (prodto.getUom().equals("2")) {
			uom = "gm";
		} else if (prodto.getUom().equals("3")) {
			uom = "lt";
		} else if (prodto.getUom().equals("4")) {
			uom = "ml";
		}

		ProductDTO prodDTO = new ProductDTO();
		prodDTO.setBarcode(prodto.getBarcode());
		prodDTO.setName(prodto.getName());
		prodDTO.setQuantity("0");
		prodDTO.setPurchasePrice(prodto.getPurchasePrice());
		prodDTO.setSellingPrice(prodto.getSellingPrice());
		prodDTO.setVat(prodto.getVat());
		prodDTO.setSupplierId(prodto.getSupplierId());
		prodDTO.setGroupId(prodto.getGroupId());
		prodDTO.setLineId(prodto.getLineId());
		prodDTO.setUom(uom);
		prodDTO.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		prodDTO.setActiveStatus(Constants.FALSE);
		prodDTO.setSyncStatus(Constants.FALSE);
		prodDTO.setProductFlag("0");
		prodDTO.setMin_count_inventory(prodto.getMin_count_inventory());
		prodDTO.setExpiry_date(prodto.getExpiry_date());
		prodDTO.setDiscount(prodto.getDiscount());
		prodDTO.setFecha_inicial(fecha_final);
		prodDTO.setFecha_final(fecha_final);
		productList.add(prodDTO);
		return productList;
	}

	public void dismissdialog() {
		this.dismiss();
	}
}
