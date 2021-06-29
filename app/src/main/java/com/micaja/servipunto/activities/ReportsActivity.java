/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dao.UserModuleIDDAO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.Constants;

public class ReportsActivity extends BaseActivity implements OnClickListener {
	private LinearLayout llReport1, llReport3, llReport4, llReport5, llReport6, llReport7, llReport8, llReport9,
			llReport10, llReport11, llReport12, llReport13, llReport14, llReport15, llReport16, llReport17, llReport18,
			llReport19, llReport20;
	private Intent intent;

	LinearLayout cashboxReport, serviReport;
	private UserDetailsDTO userDTO = new UserDetailsDTO();
	public SharedPreferences sharedpreferences;
	private List<String> userModulesList;
	private UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		userDTO = dto;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		intent = new Intent(this, AddProductActivity.class);
		appContext = (ServiApplication) getApplicationContext();

		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.report_activity);
		cashboxReport = (LinearLayout) findViewById(R.id.cashbox_Report);
		llReport1 = (LinearLayout) findViewById(R.id.ll_Report1);
		llReport1.setOnClickListener(this);
		llReport3 = (LinearLayout) findViewById(R.id.ll_Report3);
		llReport3.setOnClickListener(this);

		llReport4 = (LinearLayout) findViewById(R.id.ll_Report4);
		llReport4.setOnClickListener(this);

		llReport5 = (LinearLayout) findViewById(R.id.ll_Report5);
		llReport5.setOnClickListener(this);

		llReport6 = (LinearLayout) findViewById(R.id.ll_Report6);
		llReport6.setOnClickListener(this);

		llReport7 = (LinearLayout) findViewById(R.id.ll_Report7);
		llReport7.setOnClickListener(this);

		llReport8 = (LinearLayout) findViewById(R.id.ll_Report8);
		llReport8.setOnClickListener(this);

		llReport9 = (LinearLayout) findViewById(R.id.ll_Report9);
		llReport9.setOnClickListener(this);

		llReport10 = (LinearLayout) findViewById(R.id.ll_Report10);
		llReport10.setOnClickListener(this);

		llReport11 = (LinearLayout) findViewById(R.id.ll_Report11);
		llReport11.setOnClickListener(this);

		llReport12 = (LinearLayout) findViewById(R.id.ll_Report12);
		llReport12.setOnClickListener(this);

		llReport13 = (LinearLayout) findViewById(R.id.ll_Report13);
		llReport13.setOnClickListener(this);

		llReport14 = (LinearLayout) findViewById(R.id.ll_Report14);
		llReport14.setOnClickListener(this);

		llReport15 = (LinearLayout) findViewById(R.id.ll_Report15);
		llReport15.setOnClickListener(this);

		llReport16 = (LinearLayout) findViewById(R.id.ll_Report16);
		llReport16.setOnClickListener(this);

		llReport17 = (LinearLayout) findViewById(R.id.ll_Report17);
		llReport17.setOnClickListener(this);

		llReport18 = (LinearLayout) findViewById(R.id.ll_Report18);
		llReport18.setOnClickListener(this);

		llReport19 = (LinearLayout) findViewById(R.id.ll_Report19);
		llReport19.setOnClickListener(this);

		llReport20 = (LinearLayout) findViewById(R.id.ll_Report20);
		llReport20.setOnClickListener(this);

		cashboxReport = (LinearLayout) findViewById(R.id.cashbox_Report);
		cashboxReport.setOnClickListener(this);

		serviReport = (LinearLayout) findViewById(R.id.servi_Report);
		serviReport.setOnClickListener(this);

		getDataFromDB();
	}

	// Result of this method,click events
	@Override
	public void onClick(View view) {
		Intent intent = new Intent(this, ReportDetailsActivity.class);

		switch (view.getId()) {
		case R.id.ll_Report1:
			intent.putExtra("SELECTED_REPORT", 1);
			break;

		case R.id.ll_Report3:
			intent.putExtra("SELECTED_REPORT", 2);
			break;

		case R.id.ll_Report4:
			intent.putExtra("SELECTED_REPORT", 3);
			break;

		case R.id.ll_Report5:
			intent.putExtra("SELECTED_REPORT", 4);
			break;

		case R.id.ll_Report6:
			intent.putExtra("SELECTED_REPORT", 5);
			break;

		case R.id.ll_Report7:
			intent.putExtra("SELECTED_REPORT", 6);
			break;

		case R.id.ll_Report8:
			intent.putExtra("SELECTED_REPORT", 7);
			break;

		case R.id.ll_Report9:
			intent.putExtra("SELECTED_REPORT", 8);
			break;
		case R.id.ll_Report10:
			intent.putExtra("SELECTED_REPORT", 9);
			break;

		case R.id.ll_Report11:
			intent.putExtra("SELECTED_REPORT", 10);
			break;
		case R.id.ll_Report12:
			intent.putExtra("SELECTED_REPORT", 11);
			break;

		case R.id.ll_Report13:
			intent.putExtra("SELECTED_REPORT", 12);
			break;

		case R.id.ll_Report14:
			intent.putExtra("SELECTED_REPORT", 13);
			break;

		case R.id.ll_Report15:
			intent.putExtra("SELECTED_REPORT", 14);
			break;

		case R.id.ll_Report16:
			intent.putExtra("SELECTED_REPORT", 15);
			break;

		case R.id.ll_Report17:
			intent.putExtra("SELECTED_REPORT", 16);
			break;

		case R.id.ll_Report18:
			intent.putExtra("SELECTED_REPORT", 17);
			break;

		case R.id.ll_Report19:
			intent.putExtra("SELECTED_REPORT", 18);
			break;

		case R.id.ll_Report20:
			intent.putExtra("SELECTED_REPORT", 19);
			break;
		default:
			break;

		}
		appContext.pushActivity(new Intent(this, ReportsActivity.class));
		startActivity(intent);
	}

	// Result of this method,getting data from DB based on user
	private void getDataFromDB() {
		userDTO = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		userModulesList = UserModuleIDDAO.getInstance().getUserModuleCodes(DBHandler.getDBObj(Constants.READABLE));
		if (userDTO.getPuntoredId().length() > 1) {

			llReport1.setVisibility(View.VISIBLE);
			llReport3.setVisibility(View.VISIBLE);
			llReport4.setVisibility(View.VISIBLE);
			llReport5.setVisibility(View.VISIBLE);
			llReport6.setVisibility(View.VISIBLE);
			llReport7.setVisibility(View.VISIBLE);
			llReport8.setVisibility(View.VISIBLE);
			llReport9.setVisibility(View.VISIBLE);
			llReport10.setVisibility(View.VISIBLE);
			llReport11.setVisibility(View.VISIBLE);
			llReport12.setVisibility(View.VISIBLE);
			llReport13.setVisibility(View.VISIBLE);
			llReport14.setVisibility(View.VISIBLE);
			llReport15.setVisibility(View.VISIBLE);
			llReport16.setVisibility(View.VISIBLE);
			llReport17.setVisibility(View.VISIBLE);
			llReport18.setVisibility(View.VISIBLE);
			llReport19.setVisibility(View.VISIBLE);
			llReport20.setVisibility(View.VISIBLE);
			cashboxReport.setVisibility(View.VISIBLE);
			serviReport.setVisibility(View.VISIBLE);
			llReport16.setVisibility(View.GONE);
			llReport18.setVisibility(View.GONE);
			llReport19.setVisibility(View.GONE);
			llReport20.setVisibility(View.GONE);

//			tempreroryhide();
			
			ShowAllReports();//after first release 
		} else {
			cashboxReport.setVisibility(View.GONE);
			llReport12.setVisibility(View.GONE);
			llReport13.setVisibility(View.GONE);
			llReport14.setVisibility(View.GONE);
			llReport15.setVisibility(View.GONE);
			llReport16.setVisibility(View.GONE);
			llReport17.setVisibility(View.GONE);
			llReport18.setVisibility(View.GONE);
			llReport19.setVisibility(View.GONE);
			llReport20.setVisibility(View.GONE);

			serviReport.setVisibility(View.VISIBLE);

			llReport1.setVisibility(View.VISIBLE);
			llReport3.setVisibility(View.VISIBLE);
			llReport4.setVisibility(View.VISIBLE);
			llReport5.setVisibility(View.VISIBLE);
			llReport6.setVisibility(View.VISIBLE);
			llReport7.setVisibility(View.VISIBLE);
			llReport8.setVisibility(View.VISIBLE);
			llReport9.setVisibility(View.VISIBLE);
			llReport10.setVisibility(View.VISIBLE);
			llReport11.setVisibility(View.VISIBLE);

		}

		// cashboxReport.setVisibility(View.GONE);
		// serviReport.setVisibility(View.GONE);
	}

	private void ShowAllReports() {
		llReport16.setVisibility(View.VISIBLE);
		llReport18.setVisibility(View.VISIBLE);
		llReport19.setVisibility(View.VISIBLE);
		llReport20.setVisibility(View.VISIBLE);
	}

	private void tempreroryhide() {
		serviReport.setVisibility(View.GONE);
		llReport1.setVisibility(View.GONE);
		llReport3.setVisibility(View.GONE);
		llReport4.setVisibility(View.GONE);
		llReport5.setVisibility(View.GONE);
		llReport6.setVisibility(View.GONE);
		llReport7.setVisibility(View.GONE);
		llReport8.setVisibility(View.GONE);
		llReport9.setVisibility(View.GONE);
		llReport10.setVisibility(View.GONE);
		llReport11.setVisibility(View.GONE);

	}

}