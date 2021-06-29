package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.ReportDetailsAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.LineDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.LineDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class ReportDetailsActivity extends BaseActivity implements
		OnClickListener, OnItemSelectedListener {
	private TextView txtReportName, products, product_profit, product_quntity,
			product_totallosers, txt_todate, txt_state, txt_operator,
			txt_facturas, txt_quantity;
	private static EditText etxtFromDate, etxtToDate;
	private static View dateView, report_view, report_view2, report_view3,
			report_view4;
	private ListView lvReportDetails;
	private Button ivGo;
	private List<DTO> reportDetailsList = new ArrayList<DTO>();
	private static int selectedReport;
	private String[] report_options = { "", "product_profit",
			"supplier_profit", "products_most_sold", "profit_days",
			"transaction_box", "accounts_receivable", "accounts_payable",
			"inventory_amount", "products_expired", "sales_by_date" };
	private Spinner spn_Week;
	private ArrayAdapter<String> filterAdapter11;
	private TextView txtfromdate, txttodate;
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO userdto;
	private List<DTO> CTDto = new ArrayList<DTO>();
	private List<DTO> RepoDto = new ArrayList<DTO>();
	private Intent intent;
	private Button btnFilter;
	private String filter_type="all" , filter_id = "0";
	private String group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		userdto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		inItUI();

	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.report_detail_activity);

		etxtFromDate = (EditText) findViewById(R.id.img_fromdate);
		etxtFromDate.setOnClickListener(this);

		etxtToDate = (EditText) findViewById(R.id.img_todate);
		etxtToDate.setOnClickListener(this);

		lvReportDetails = (ListView) findViewById(R.id.lv_ReportDetail);
		txt_todate = (TextView) findViewById(R.id.txt_todate);
		ivGo = (Button) findViewById(R.id.img_go);
		ivGo.setOnClickListener(this);

		btnFilter = (Button) findViewById(R.id.spn_profitBySupp);
		btnFilter.setText(getResources().getString(R.string.spn_txt));
		btnFilter.setOnClickListener(this);

		spn_Week = (Spinner) findViewById(R.id.spn_profitWeek);
		spn_Week.setOnItemSelectedListener(this);

		txtfromdate = (TextView) findViewById(R.id.txt_fromdate);
		txttodate = (TextView) findViewById(R.id.txt_todate);

		setScreenName();
	}
	// Result of this method,loading screen visiblity
	private void setScreenName() {
		txtReportName = (TextView) findViewById(R.id.txt_reportName);
		products = (TextView) findViewById(R.id.products);
		product_profit = (TextView) findViewById(R.id.product_profit);
		product_quntity = (TextView) findViewById(R.id.product_quntity);
		report_view = (View) findViewById(R.id.report_view);
		product_totallosers = (TextView) findViewById(R.id.product_totallosers);
		report_view2 = (View) findViewById(R.id.reportview2);
		txt_state = (TextView) findViewById(R.id.product_state);
		report_view3 = (View) findViewById(R.id.reportview3);
		txt_operator = (TextView) findViewById(R.id.product_operator);
		report_view4 = (View) findViewById(R.id.reportview4);
		txt_facturas = (TextView) findViewById(R.id.product_facturas);
		selectedReport = getIntent().getExtras().getInt("SELECTED_REPORT");
		txt_quantity = (TextView) findViewById(R.id.product_quantity);
		txt_quantity.setVisibility(View.GONE);
		
		switch (selectedReport) {
		case 1: // Productos por rentabilidad
			txtReportName.setText(getString(R.string.report1));
			// Se intercambia el orden de las columnas y se cambia el código del proveedor por unidades vendidas a petición del requerimiento 4.3.7.1 C
			//products.setText(getString(R.string.product)); // producto
			//product_profit.setText(getString(R.string.supp_code)); // proveedor
			product_profit.setText(getString(R.string.product)); // producto
			products.setText(getString(R.string.lbl_sold_units)); // unidades vendidas
			product_quntity.setText(getString(R.string.profit)); // rentabilidad acumulada
			btnFilter.setVisibility(View.VISIBLE);
			break;
		case 2:
			txtReportName.setText(getString(R.string.report3));
			products.setText(getString(R.string.supplier_name));
			product_quntity.setText(getString(R.string.profit));
			product_profit.setText(getString(R.string.supp_code));
			break;

		case 3: // Producto más vendido
			txtReportName.setText(getString(R.string.report4));
			//products.setText(getString(R.string.product));
			//product_profit.setText(getString(R.string.supp_code));
			// Se intercambia el nombre de las columnas a petición del requerimiento 4.3.7.2 B
			product_profit.setText(getString(R.string.product));
			products.setText(getString(R.string.supp_code));
			product_quntity.setText(getString(R.string.menus_count));
			break;
		case 4:
			txtReportName.setText(getString(R.string.report5));
			products.setText(getString(R.string.profitability));
			product_profit.setText(getString(R.string.profit_date));
			product_quntity.setVisibility(View.GONE);

			break;

		case 5: // Histórico de Caja
			// Se agrega la columna fecha a petición del requerimiento 4.3.7.4 B
			txtReportName.setText(getString(R.string.report6));
			txt_quantity.setText(getString(R.string.date));
			txt_quantity.setVisibility(View.VISIBLE);
			//product_profit.setText(getString(R.string.amnt_reason));
			//products.setText(getString(R.string.amnt_type));
			// Se cambia el nombre de las columnas a petición del requerimiento 4.3.7.4 C
			product_profit.setText(getString(R.string.amnt_type));
			products.setText(getString(R.string.amnt_reason));
			product_quntity.setText(getString(R.string.amnt));
			txt_todate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			break;

		case 6: // Cuentas por cobrar
			txtReportName.setText(getString(R.string.report7));
			products.setText(getString(R.string.cus_name));
			//product_profit.setText(getString(R.string.cus_id));
			// Se cambia el nombre de la columna a petición del requerimiento 4.3.7.5 C
			product_profit.setText(getString(R.string.lbl_telephone));
			product_quntity.setText(getString(R.string.cus_bamount));
			break;

		case 7: // Cuentas por pagar
			txtReportName.setText(getString(R.string.report8));
			products.setText(getString(R.string.sup_name));
			product_profit.setText(getString(R.string.sup_id));
			product_quntity.setText(getString(R.string.cus_bamount));
			product_totallosers.setText(getString(R.string.supp_debt));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			break;

		case 8: // Valor del inventario en el establecimiento
			txtReportName.setText(getString(R.string.report9));
			product_profit.setText(getString(R.string.purchaseprice));
			product_quntity.setText(getString(R.string.sellingprice));
			txt_quantity.setText(R.string.txt_quantity);
			txt_quantity.setVisibility(View.VISIBLE);
			break;

		case 9: // Productos vencidos caducados
			txtReportName.setText(getString(R.string.report10));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			products.setText(getString(R.string.expired_barcode));
			product_profit.setText(getString(R.string.expired_descp));
			product_quntity.setText(getString(R.string.expired_price));
			product_totallosers.setText(getString(R.string.expired_losers));
			break;
		case 10:
			txtReportName.setText(getString(R.string.report11));
			products.setText(getString(R.string.inv_prodrname));
			product_quntity.setText(getString(R.string.sales_total));
			product_profit.setText(getString(R.string.sold_items));
			spn_Week.setVisibility(View.GONE);
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.profit_percentage));
			break;

		case 11:
			txtReportName.setText(getString(R.string.report12));
			products.setText(getString(R.string.operater));
			product_quntity.setText(getString(R.string.sumataria));
			product_profit.setText(getString(R.string.inv_quantity));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			GetConsultarTransacciones();

			break;
		case 12:
			txtReportName.setText(getString(R.string.report13));
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.report_value));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.state));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.operater));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			GetConsultarTransacciones();
			break;
		case 13:
			txtReportName.setText(getString(R.string.report14));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.aval_seats));
			product_profit.setText(getString(R.string.aval_pay));
			product_quntity.setVisibility(View.GONE);
			GetConsultarTransacciones();
			break;
		case 14:
			txtReportName.setText(getString(R.string.report15));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.cellular));
			product_quntity.setText(getString(R.string.fecha));
			product_profit.setText(getString(R.string.id_trans));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.report_value));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.state));
			report_view3.setVisibility(View.VISIBLE);
			txt_operator.setVisibility(View.VISIBLE);
			txt_operator.setText(getString(R.string.operator));
			GetConsultarTransacciones();
			break;
		case 15:
			txtReportName.setText(getString(R.string.report16));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.report_value));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.state));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.product));
			GetConsultarTransacciones();
			break;
		case 16:
			txtReportName.setText(getString(R.string.report17));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.report_value));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.state));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.report17));
			report_view3.setVisibility(View.VISIBLE);
			txt_operator.setVisibility(View.VISIBLE);
			txt_operator.setText(getString(R.string.rep_number));
			GetConsultarTransacciones();
			break;
		case 17:
			txtReportName.setText(getString(R.string.report18));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.report18));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.cellular));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.rep_cedula));
			report_view3.setVisibility(View.VISIBLE);
			txt_operator.setVisibility(View.VISIBLE);
			txt_operator.setText(getString(R.string.report_value));
			report_view4.setVisibility(View.VISIBLE);
			txt_facturas.setVisibility(View.VISIBLE);
			txt_facturas.setText(getString(R.string.state));
			GetConsultarTransacciones();
			break;
		case 18:
			txtReportName.setText(getString(R.string.report19));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.converaion));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.refer1));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.refer2));
			report_view3.setVisibility(View.VISIBLE);
			txt_operator.setVisibility(View.VISIBLE);
			txt_operator.setText(getString(R.string.report_value));
			report_view4.setVisibility(View.VISIBLE);
			txt_facturas.setVisibility(View.VISIBLE);
			txt_facturas.setText(getString(R.string.state));
			GetConsultarTransacciones();

			break;
		case 19:
			txtReportName.setText(getString(R.string.report20));
			txtfromdate.setVisibility(View.GONE);
			txttodate.setVisibility(View.GONE);
			etxtFromDate.setVisibility(View.GONE);
			etxtToDate.setVisibility(View.GONE);
			ivGo.setVisibility(View.GONE);
			products.setText(getString(R.string.id_trans));
			product_quntity.setText(getString(R.string.product));
			product_profit.setText(getString(R.string.fecha));
			report_view.setVisibility(View.VISIBLE);
			product_totallosers.setVisibility(View.VISIBLE);
			product_totallosers.setText(getString(R.string.report_value));
			report_view2.setVisibility(View.VISIBLE);
			txt_state.setVisibility(View.VISIBLE);
			txt_state.setText(getString(R.string.state));
			GetConsultarTransacciones();
			break;
		}

	}

	private void GetConsultarTransacciones() {
		if (NetworkConnectivity.netWorkAvailability(ReportDetailsActivity.this)) {
			new GetConsultarTransacciones(userdto.getUserName(),
					userdto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(ReportDetailsActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	@Override
	public void onClick(View view) {

		dateView = view;
		DatePicker datePicker = new DatePicker();
		switch (view.getId()) {
		case R.id.img_fromdate:
			datePicker.show(getFragmentManager(), "datePicker");
			break;

		case R.id.img_todate:
			datePicker.show(getFragmentManager(), "datePickerEnd");
			break;

		case R.id.img_go:
			if (etxtFromDate.getText().toString().length() == 0
					|| etxtToDate.getText().toString().length() == 0) {
				CommonMethods.showToast(this, getResources().getString(R.string.select_date));
			} else {
				pullReportDetails(etxtFromDate.getText().toString(), etxtToDate
						.getEditableText().toString());
			}

			break;
		case R.id.spn_profitBySupp:
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
	// Result of this method,filter data
	private void showFilters() {

		final Dialog dialog = new Dialog(ReportDetailsActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.report_custom_filters);
		RelativeLayout layout1 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterAll);
		RelativeLayout layout5 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterLine);
		RelativeLayout layout6 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterGroup);
		RelativeLayout layout7 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterSubgroup);
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		final RadioButton radio1 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterAll);
		final RadioButton radio5 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterLine);
		final RadioButton radio6 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterGroup);
		final RadioButton radio7 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterSubgroup);

		final RelativeLayout layout8 = (RelativeLayout) dialog
				.findViewById(R.id.rel_AutoComplete);
		final AutoCompleteTextView actv = (AutoCompleteTextView) dialog
				.findViewById(R.id.ac_Group);
		final Button btnOK = (Button) dialog.findViewById(R.id.btn_OK);

		@SuppressWarnings("unchecked")
		ArrayAdapter adapter = new ArrayAdapter(ReportDetailsActivity.this,
				android.R.layout.simple_list_item_1);
		actv.setAdapter(adapter);

		String selected = btnFilter.getText().toString().trim();
		if (selected.equals(getResources().getString(R.string.spn_all))) {
			radio1.setChecked(true);
			filter_type = "all";
			dialog.dismiss();
			filter_id = "0";
		} else if ((selected.equals(getResources().getString(R.string.rline)))) {
			radio5.setChecked(true);
			filter_type = "line";
		} else if (selected.equals(getResources().getString(R.string.rgroup))) {
			radio6.setChecked(true);
			filter_type = "group";
		} else {
			radio7.setChecked(true);
			layout8.setVisibility(View.VISIBLE);
			actv.setText(selected);
			actv.dismissDropDown();
			filter_type = "subgroup";
		}
		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources()
							.getString(R.string.spn_all));
					radio1.setChecked(true);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					actv.setText("");
					layout8.setVisibility(View.GONE);
					actv.dismissDropDown();
					filter_type = "all";
					dialog.dismiss();
				}
			}
		});

		radio5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(R.string.rline));
					radio5.setChecked(true);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio1.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					actv.setText("");
					filter_type = "line";

				}
			}
		});
		radio6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter
							.setText(getResources().getString(R.string.rgroup));
					radio6.setChecked(true);
					radio5.setChecked(false);
					radio7.setChecked(false);
					radio1.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					actv.setText("");
					filter_type = "group";

				}
			}
		});
		radio7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.subgroup));
					radio7.setChecked(true);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio1.setChecked(false);
					layout8.setVisibility(View.VISIBLE);
					actv.setText("");
					filter_type = "group";
				}
			}
		});
		layout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(true);
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(false);
				actv.setText("");

			}
		});

		layout5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio5.setChecked(true);
				radio6.setChecked(false);
				radio7.setChecked(false);
				layout8.setVisibility(View.VISIBLE);
				actv.setText("");
			}
		});
		layout6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio5.setChecked(false);
				radio6.setChecked(true);
				radio7.setChecked(false);
				layout8.setVisibility(View.VISIBLE);
				actv.setText("");
			}
		});
		layout7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio5.setChecked(false);
				radio6.setChecked(false);
				radio7.setChecked(true);
				layout8.setVisibility(View.VISIBLE);
				actv.setText("");
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				group = actv.getText().toString().trim();
				if ("".equals(group)) {
					CommonMethods.showCustomToast(ReportDetailsActivity.this,
							getResources().getString(R.string.invalid_msg));
				} else {
					btnFilter.setText(group);
					getthedatafromlocaldb(group);
					actv.setText("");
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
//group,line
	protected void getthedatafromlocaldb(String group) {
		if (filter_type.equals("group")) {
			LineDTO dto = LineDAO.getInstance().getRecordsByline_name(
					DBHandler.getDBObj(Constants.WRITABLE), group);
			try {
				if (dto.getLineId().length() > 0) {
					filter_id = dto.getLineId();
				}
			} catch (Exception e) {
				filter_id = "0";
			}
		} else if (filter_type.equals("subgroup")) {
			GroupDTO dto = GroupDAO.getInstance().getRecordsByGroup_name(
					DBHandler.getDBObj(Constants.WRITABLE), group);
			try {
				if (dto.getGroupId().length() > 0) {
					filter_id = dto.getGroupId();
				}

			} catch (Exception e) {
				filter_id = "0";
			}
		} else if (filter_type.equals("line")) {
			filter_id = "0";
		}
	}

	@SuppressWarnings("unused")
	private List<String> get_Filters() {
		List<String> list = new ArrayList<String>();

		list.add(getResources().getString(R.string.spnfliter_select));
		list.add(getResources().getString(R.string.spnfliter_week));
		list.add(getResources().getString(R.string.spnfliter_l_week));
		list.add(getResources().getString(R.string.spnfliter_b_lweek));
		return list;
	}
	// Result of this method,choose date as date picker
	public static class DatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;

			if (dateView.getId() == R.id.img_fromdate) {

				etxtFromDate.setText(date);
				if (selectedReport == 5) {
					etxtToDate.setText(date);
				}
			} else {
				etxtToDate.setText(date);
			}
		}
	}

	private void pullReportDetails(String fromDate, String toDate) {

		if (NetworkConnectivity.netWorkAvailability(ReportDetailsActivity.this)) {
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("fromDate", fromDate);
			editor.putString("toDate", toDate);
			editor.commit();
			new GetReportDetails().execute(fromDate, toDate);
		} else {
			CommonMethods.showCustomToast(ReportDetailsActivity.this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}
	// Result of this method,report details data getting  from db  
	public class GetReportDetails extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				ReportDetailsActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					ReportDetailsActivity.this);

		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.responds_feed = new ServiceHandler(
					ReportDetailsActivity.this).makeServiceCall(
					ServiApplication.URL + "/reports/" + report_options[selectedReport],
					ServiceHandler.POST, makejsonobj(params[0], params[1]));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			if (ServiApplication.connectionTimeOutState) {
				if (new ParsingHandler().getInventoryAmount(
						ServiApplication.responds_feed, selectedReport).size() > 0) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(ReportDetailsActivity.this, new ParsingHandler()
							.getInventoryAmount(ServiApplication.responds_feed, selectedReport), selectedReport));
				} else {
					lvReportDetails.setAdapter(null);
					CommonMethods.showCustomToast(ReportDetailsActivity.this,
							getResources().getString(R.string.nodata));
				}
			}

		}
	}

	private String makejsonobj(String fromDate, String toDate) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("from_date", fromDate);
			toDate = addOneDayToTwoDate(toDate);
			if (selectedReport == 1) {
				jsonObject.put("filter_type", filter_type);
				jsonObject.put("filter_id", filter_id);
				jsonObject.put("to_date", toDate);
				jsonObject.put("code", ServiApplication.store_id);
			} else if (selectedReport == 5) {
				jsonObject.put("store_code", ServiApplication.store_id);
			} else {
				jsonObject.put("to_date", toDate);
				jsonObject.put("code", ServiApplication.store_id);
			}
			return jsonObject.toString();
		} catch (JSONException e) {
			return jsonObject.toString();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public String getJsonObjforconsultarVentas() {

		JSONObject jsonobj = new JSONObject();
		try {
			if (selectedReport == 14) {
				jsonobj.put("productNumber", "1");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else if (selectedReport == 15) {
				jsonobj.put("productNumber", "3");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else if (selectedReport == 16) {
				jsonobj.put("productNumber", "8");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else if (selectedReport == 17) {
				jsonobj.put("productNumber", "4");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else if (selectedReport == 18) {
				jsonobj.put("productNumber", "15");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else if (selectedReport == 19) {
				jsonobj.put("productNumber", "82");
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			} else {
				jsonobj.put("comercioId", userdto.getComercioId());
				jsonobj.put("terminalId", userdto.getTerminalId());
				jsonobj.put("puntoDeVentaId", userdto.getPuntoredId());
				return jsonobj.toString();
			}
		} catch (Exception e) {

		}
		return jsonobj.toString();
	}

	/* call consultarTransacciones */

	private class GetConsultarTransacciones extends
			AsyncTask<Void, Void, Boolean> {

		private final String username;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean flage = false;
		private String microwInsurenceAmount_value;
		private String SoapOperatorID, encrypt_key, json,server_res;
		private boolean exception = false,exception1 = false;

		GetConsultarTransacciones(String email, String password) {
			this.username = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					ReportDetailsActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			if (selectedReport >= 14) {
				json = AESTEST.AESCrypt(getJsonObjforconsultarVentas(), key);
			} else {
				json = getJsonObjforconsultarVentas();
			}

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				if (selectedReport == 11) {
					request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
							.makeHeader(
									ReportDetailsActivity.this,
									encrypt_key,
									ServiApplication.process_id_consultarVentas,
									ServiApplication.username, json)));
					request.addProperty(MakeHeader.makepropertyInfo2(json,
							"JSON"));
				} else if (selectedReport == 12) {

					request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
							.makeHeader(
									ReportDetailsActivity.this,
									encrypt_key,
									ServiApplication.process_id_consultarTransacciones,
									ServiApplication.username, json)));
					request.addProperty(MakeHeader.makepropertyInfo2(json,
							"JSON"));
				} else if (selectedReport == 13) {
					request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
							.makeHeader(
									ReportDetailsActivity.this,
									encrypt_key,
									ServiApplication.process_id_SaldoPorCuentas,
									ServiApplication.username, json)));
					request.addProperty(MakeHeader.makepropertyInfo2(json,
							"JSON"));
				} else {
					request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
							.makeHeader(ReportDetailsActivity.this,
									encrypt_key,
									ServiApplication.process_id_1b_reports,
									ServiApplication.username, json)));
					request.addProperty(MakeHeader.makepropertyInfo2(json,
							"JSON_AES"));
				}

				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				try {
					if (new ParsingHandler()
							.getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "state").contains("SUCCESS")) {
						if (selectedReport == 11) {
							CTDto = new ParsingHandler()
									.getconsultarVentas(new ParsingHandler().getString(
											new GetDocumentObject()
													.getDocumentObj(ht.responseDump),
											"response", "data"));
						} else if (selectedReport == 12) {
							CTDto = new ParsingHandler()
									.getConsultarTransacciones(new ParsingHandler().getString(
											new GetDocumentObject()
													.getDocumentObj(ht.responseDump),
											"response", "data"));
						} else if (selectedReport == 13) {

							CTDto = new ParsingHandler()
									.getSaldoporCuentas(new ParsingHandler().getString(
											new GetDocumentObject()
													.getDocumentObj(ht.responseDump),
											"response", "data"));
						} else {
							String jsonformate = AESTEST
									.AESDecrypt(
											new ParsingHandler().getString(
													new GetDocumentObject()
															.getDocumentObj(ht.responseDump),
													"response", "data"),
											AESTEST.AESkeyFromString(sharedpreferences
													.getString("AutoGenKey", "")));
							CTDto = new ParsingHandler()
									.getProductoReportsData(jsonformate);
							Log.d("", "dhana" + jsonformate);
						}
					} else {
						exception1=true;
						server_res=new ParsingHandler().getString(new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
										"response", "data");
					}
				} catch (Exception e) {
					exception = true;
					
					Log.d("DUMP RESPONSE", "babu" + e.getMessage().toString());
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(
				SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						ReportDetailsActivity.this);
				dialog.setCancelable(false);
				dialog.show();
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(server_res);
					CommonMethods.showCustomToast(ReportDetailsActivity.this,
							json.getString("message"));
				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(ReportDetailsActivity.this);
					dialog.show();
					dialog.setCancelable(false);
				}
			}else {
				if (selectedReport == 11) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 11));
				} else if (selectedReport == 12) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 12));
				} else if (selectedReport == 13) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 13));
				} else if (selectedReport == 14) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 14));
				} else if (selectedReport == 15) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 15));
				} else if (selectedReport == 16) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 16));
				} else if (selectedReport == 17) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 17));
				} else if (selectedReport == 18) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 18));
				} else if (selectedReport == 19) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 19));
				} else if (selectedReport == 20) {
					lvReportDetails.setAdapter(new ReportDetailsAdapter(
							ReportDetailsActivity.this, CTDto, 20));
				}
			}
		}
	}
	
	public String addOneDayToTwoDate(String date) {
		String dt = date; // Start date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.add(Calendar.DATE, 1); // number of days to add
		return sdf.format(c.getTime());
	}
}
