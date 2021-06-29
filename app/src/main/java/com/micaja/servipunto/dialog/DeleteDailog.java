package com.micaja.servipunto.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesEditTypes;

public class DeleteDailog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnDeleteYes, btnDeleteNo;
	private int position;
	private String screen;
	ServiApplication appContext;
	private Handler uiHandler;

	public DeleteDailog(Context context, int position, String screen,
			Handler uiHandler) {
		super(context);
		this.context = context;
		this.position = position;
		this.screen = screen;
		this.uiHandler = uiHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		appContext = (ServiApplication) context.getApplicationContext();

		initUI();
	}

	// Result of this method,registration for all UI views.
	private void initUI() {
		setContentView(R.layout.sales_delete_dialog);

		btnDeleteYes = (Button) findViewById(R.id.btn_DeleteYes);
		btnDeleteNo = (Button) findViewById(R.id.btn_DeleteNo);

		btnDeleteYes.setOnClickListener(this);
		btnDeleteNo.setOnClickListener(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_DeleteYes:
			deleteRowFromList();
			break;

		case R.id.btn_DeleteNo:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void deleteRowFromList() {
		List<DTO> list = appContext.getSelectedProducts();

		if (!screen.equals(SalesEditTypes.MENUS_INVENTORY.code()))
			deleteFromTable(list.get(position));

		list.remove(position);
		appContext.setSeletedProducts(list);
		this.dismiss();
		sendMessage();
	}

	// selected product delete from table
	private void deleteFromTable(DTO dto) {
		SelectedProddutsDTO selectedProddutsDTO = (SelectedProddutsDTO) dto;
		DishProductsDTO dishProductsDTO = new DishProductsDTO();

		dishProductsDTO
				.setDishProductId(selectedProddutsDTO.getIdDishProduct());
		dishProductsDTO.setProductId(selectedProddutsDTO.getIdProduct());
		dishProductsDTO.setQuantity(selectedProddutsDTO.getQuantity());
		dishProductsDTO.setSyncStatus(0);
		dishProductsDTO.setUom(selectedProddutsDTO.getUnits());

		DishProductsDAO.getInstance().delete(
				DBHandler.getDBObj(Constants.WRITABLE), dishProductsDTO);
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.DELETE.code();
		uiHandler.sendMessage(msg);
	}
}