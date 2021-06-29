package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

import com.micaja.servipunto.adapters.DeliveryInvoiceAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;

import java.util.List;

public class DeliveryInvoiceDetailsDialog extends Dialog {
	private ListView invoicelist;
	private ServiApplication appContext;
	private DeliveryInvoiceAdapter cinvAdapter;
	private Context context;
	private String respondsFeed;

	public DeliveryInvoiceDetailsDialog(Context context, String respondsFeed) {
		super(context);
		this.context = context;
		this.respondsFeed = respondsFeed;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) context.getApplicationContext();
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.client_invoice_details);
		invoicelist = (ListView) findViewById(R.id.lv_cusinvoice);
		System.out.println(respondsFeed);
		List<DTO> list = new ParsingHandler().getCustomerInvoiceDetails(respondsFeed);
		cinvAdapter = new DeliveryInvoiceAdapter(context, list);
		invoicelist.setAdapter(cinvAdapter);
	}


}
