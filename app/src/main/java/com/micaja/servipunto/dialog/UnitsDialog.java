package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.SalesCodes;

public class UnitsDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Spinner spnUnits;

	private Button btnUnitsSave, btnUnitsCancel;

	ServiApplication appContext;
	private Handler uiHandler;
	private int position;
	private ArrayAdapter<String> unitsAdapter;
	private ImageView imgClose;
	private String type, UOMScreen;

	public UnitsDialog(Context context, int position, Handler uiHandler,
			String type, String UOMScreen) {
		super(context);
		this.context = context;
		this.uiHandler = uiHandler;
		this.position = position;
		this.type = type;
		this.UOMScreen = UOMScreen;
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
		setContentView(R.layout.units_dialog);

		spnUnits = (Spinner) findViewById(R.id.spn_UnitsDialog);
		btnUnitsSave = (Button) findViewById(R.id.btn_UnitsSave);
		btnUnitsCancel = (Button) findViewById(R.id.btn_UnitsCancel);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

		btnUnitsSave.setOnClickListener(this);
		btnUnitsCancel.setOnClickListener(this);

		loadUI();
	}

	private void loadUI() {
		List<DTO> list = appContext.getSelectedProducts();
		SelectedProddutsDTO dto = (SelectedProddutsDTO) list.get(position);
		unitsAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getUOMs(dto));
		unitsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnUnits.setAdapter(unitsAdapter);
		spnUnits.setSelection(getUnitsIndex(dto.getUnits()));
	}
// UOM spinner based on options
	private List<String> getUOMs(SelectedProddutsDTO dto) {
		List<String> list = new ArrayList<String>();

		list.add(context.getResources().getString(R.string.select_units));

		if ("kg".equalsIgnoreCase(dto.getUnits())
				|| "gm".equalsIgnoreCase(dto.getUnits())) {
			list.add("kg");
			list.add("gm");
		} else if ("lt".equalsIgnoreCase(dto.getUnits())
				|| "ml".equalsIgnoreCase(dto.getUnits())) {
			list.add("lt");
			list.add("ml");
		}
		return list;
	}
//UOM get position
	private int getUnitsIndex(String units) {
		int position = 0;
		if ("kg".equalsIgnoreCase(units))
			position = 1;
		else if ("gm".equalsIgnoreCase(units))
			position = 2;
		else if ("lt".equalsIgnoreCase(units))
			position = 1;
		else
			position = 2;
		return position;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_UnitsSave:
			changeUnitsInList();
			break;

		case R.id.btn_UnitsCancel:
			this.dismiss();
			break;

		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void changeUnitsInList() {
		String selected = spnUnits.getSelectedItem().toString().trim();

		if (!selected.equals(context.getResources().getString(
				R.string.select_units))) {
			List<DTO> list = appContext.getSelectedProducts();

			SelectedProddutsDTO dto = (SelectedProddutsDTO) list.get(position);
			String productId = dto.getIdProduct();

			// logic for temp purpose
			if (productId.contains("-"))
				productId = dto.getBarcode();

			dto.setUnits(selected);

			if (UOMScreen.equals(ConstantsEnum.ORDERS.code())) {
				int position = Integer
						.parseInt(appContext.getSeletedSupplier());
				SupplierDTO supplierDTO = (SupplierDTO) appContext
						.getOrderSupplierList().get(position);
				List<DTO> productList = supplierDTO.getProductsList();
				ProductDTO supplierProduDTO = getSupplierProd(productList, dto);
				System.out.println("supplierProduDTO :" + supplierProduDTO);
				if (supplierProduDTO != null)
					dto.setPrice(CommonMethods.getUOMPriceConverter(
							supplierProduDTO.getPurchasePrice(),
							supplierProduDTO.getUom(), dto.getUnits(),
							dto.getQuantity()));
			} else {
				ProductDTO productDTO = ProductDAO.getInstance()
						.getPriceUOMByBarcode(
								DBHandler.getDBObj(Constants.READABLE),
								productId);

				if (productDTO.getSellingPrice() != null
						&& productDTO.getUom() != null) {
					System.out.println("Sales Code ************"
							+ CommonMethods.getUOMPriceConverter(
									productDTO.getSellingPrice(),
									productDTO.getUom(), dto.getUnits(),
									dto.getQuantity()));
					if (UOMScreen.equals(ConstantsEnum.SALES.code())) {
						dto.setPrice(CommonMethods.getUOMPriceConverter(
								productDTO.getSellingPrice(),
								productDTO.getUom(), dto.getUnits(),
								dto.getQuantity()));
						dto.setSellPrice(CommonMethods.getUOMPriceConverter(
								productDTO.getSellingPrice(),
								productDTO.getUom(), dto.getUnits(),
								dto.getQuantity()));
					}

					else
						dto.setPrice(CommonMethods.getUOMPriceConverter(
								productDTO.getPurchasePrice(),
								productDTO.getUom(), dto.getUnits(),
								dto.getQuantity()));
				}
			}

			list.set(position, dto);

			appContext.setSeletedProducts(list);

			this.dismiss();

			sendMessage();
		} else
			CommonMethods.showToast(context,
					context.getResources().getString(R.string.select_units));
	}

	private ProductDTO getSupplierProd(List<DTO> productList,
			SelectedProddutsDTO dto) {
		// ProductDTO productDTO = new ProductDTO();

		for (int i = 0; i < productList.size(); i++) {
			ProductDTO productDTO = (ProductDTO) productList.get(i);
			System.out.println("Product Id List :" + productDTO.getProductId());
			System.out.println("Product Id Selected :" + dto.getIdProduct());
			if (productDTO.getProductId().equals(dto.getIdProduct())) {
				return productDTO;
			}
		}
		return null;
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.EDIT.code();
		uiHandler.sendMessage(msg);
	}
}