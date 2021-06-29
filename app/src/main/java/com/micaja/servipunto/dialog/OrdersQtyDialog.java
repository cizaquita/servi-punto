package com.micaja.servipunto.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class OrdersQtyDialog extends Dialog implements android.view.View.OnClickListener,android.content.DialogInterface.OnClickListener 
{

	private Context context;
	EditText etxtEnteredValue;
	
	private Button btnZero,btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix,btnSeven,btnEight,btnNine,btnBackClear,
	   btnAllClear,btnSearch,btnEnter,btnCancel;
	
	private int position;
	private List<DTO> productList;
	ServiApplication appContext;

	public OrdersQtyDialog(Context context,int position,List<DTO> productList) 
	{
		super(context);
		this.context		= context;
		this.position		= position;
		this.productList	= productList;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		appContext = (ServiApplication) context.getApplicationContext();
		
		initUI();
	}
	
	private void initUI() 
	{
		setContentView(R.layout.sales_calcu_dialog);
		
		btnZero				= (Button) findViewById(R.id.btn_DialogCalZero);
		btnOne				= (Button) findViewById(R.id.btn_DialogCalOne);
		btnTwo				= (Button) findViewById(R.id.btn_DialogCalTwo);
		btnThree			= (Button) findViewById(R.id.btn_DialogCalThree);
		btnFour				= (Button) findViewById(R.id.btn_DialogCalFour);
		btnFive				= (Button) findViewById(R.id.btn_DialogCalFive);
		btnSix				= (Button) findViewById(R.id.btn_DialogCalSix);
		btnSeven			= (Button) findViewById(R.id.btn_DialogCalSeven);
		btnEight			= (Button) findViewById(R.id.btn_DialogCalEight);
		btnNine				= (Button) findViewById(R.id.btn_DialogCalNine);
		btnBackClear		= (Button) findViewById(R.id.btn_DialogCalClearOne);
		btnAllClear			= (Button) findViewById(R.id.btn_DialogCalClear);
		btnSearch			= (Button) findViewById(R.id.btn_DialogCalSearch);
		btnEnter			= (Button) findViewById(R.id.btn_DialogCalEnter);
		btnCancel			= (Button) findViewById(R.id.btn_DialogCancel);
		
		etxtEnteredValue	= (EditText) findViewById(R.id.etxt_DialogBarcode);
		
		btnZero.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener (this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnBackClear.setOnClickListener(this);
		btnAllClear.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		
		btnCancel.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
	
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.btn_DialogCalZero:
			setVauleToBarcode("0");
			break;
			
		case R.id.btn_DialogCalOne:
			setVauleToBarcode("1");
			break;
			
		case R.id.btn_DialogCalTwo:
			setVauleToBarcode("2");
			break;
			
		case R.id.btn_DialogCalThree:
			setVauleToBarcode("3");
			break;
			
		case R.id.btn_DialogCalFour:
			setVauleToBarcode("4");
			break;
			
		case R.id.btn_DialogCalFive:
			setVauleToBarcode("5");
			break;
			
		case R.id.btn_DialogCalSix:
			setVauleToBarcode("6");
			break;
			
		case R.id.btn_DialogCalSeven:
			setVauleToBarcode("7");
			break;
					
		case R.id.btn_DialogCalEight:
			setVauleToBarcode("8");
			break;
			
		case R.id.btn_DialogCalNine:
			setVauleToBarcode("9");
			break;
			
		case R.id.btn_DialogCalClearOne:
			if(etxtEnteredValue.getText().toString().trim().length() != 0)
				removeLastDigit();
			break;
			
		case R.id.btn_DialogCalClear:
			etxtEnteredValue.setText("");
			break;
					
		case R.id.btn_DialogCalSearch:
			break;
			
		case R.id.btn_DialogCalEnter:
			SetOrdersProductList();
			break;
			
		case R.id.btn_DialogCancel:
			this.dismiss();
			break;
			
		default:
			break;
		}
	}
	
	private void SetOrdersProductList() 
	{
		String value = etxtEnteredValue.getText().toString().trim();
		
		if(value.length() > 0 && !"0".equals(value) && !"00".equals(value)&& !"000".equals(value)&& !"0000".equals(value)&& !"00000".equals(value)&& !"000000".equals(value)&& !"0000000".equals(value)&& !"00000000".equals(value))
		{
			boolean flage=false;
			List<DTO> list = appContext.getSelectedProducts();
			SelectedProddutsDTO dto	= new SelectedProddutsDTO();
			ProductDTO productDTO = (ProductDTO) productList.get(position);
			dto.setBarcode(productDTO.getBarcode());
			dto.setIdProduct(productDTO.getProductId());
			dto.setName(productDTO.getName());
			dto.setPrice(productDTO.getPurchasePrice());
			dto.setQuantity(value);
			dto.setUnits(productDTO.getUom());
			dto.setVat(productDTO.getVat());
			if (list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					SelectedProddutsDTO ptdo=(SelectedProddutsDTO) list.get(i);
					if (ptdo.getBarcode().equals(productDTO.getBarcode())) {
						list.remove(i);
						Long qty=Long.parseLong(value)+Long.parseLong(ptdo.getQuantity());
						ptdo.setQuantity(""+qty);
						list.add(ptdo);
						appContext.setSeletedProducts(list);
						flage=true;
						break;
					}
				}
			}
			if (flage) {
				flage=false;
			}else {
				flage=false;
				list.add(dto);
				appContext.setSeletedProducts(list);
			}
			CommonMethods.showToast(context, context.getResources().getString(R.string.product_added_msg));
			
			this.dismiss();
		}
		else
			CommonMethods.showToast(context, context.getResources().getString(R.string.enter_valid_value));
	}

	

	private void removeLastDigit() 
	{
		if(etxtEnteredValue.getText().toString().trim().length() == 1)
			etxtEnteredValue.setText("");
		else
		{
			String barcode = etxtEnteredValue.getText().toString().trim();
			barcode = barcode.substring(0,barcode.length()-1);
			etxtEnteredValue.setText(barcode);
		}
		etxtEnteredValue.setSelection(etxtEnteredValue.getText().length());
	}

	private void setVauleToBarcode(String enterValue) 
	{
		String barcode = etxtEnteredValue.getText().toString().trim();
		
		if(barcode.length() == 0)
			etxtEnteredValue.setText(enterValue);
		else
		{
			barcode = barcode.concat(enterValue);
			etxtEnteredValue.setText(barcode);
		}
		etxtEnteredValue.setSelection(etxtEnteredValue.getText().length());
	}
}
