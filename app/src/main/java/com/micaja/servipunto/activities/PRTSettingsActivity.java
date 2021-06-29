package com.micaja.servipunto.activities;

import PRTAndroidSDK.PRTAndroidPrint;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

public class PRTSettingsActivity extends BaseActivity {

	private Spinner spnPrinterList=null;
	private Button btnBT=null;
	
	private String ConnectType="";
	private Context thisCon=null;
	private ArrayAdapter arrPrinterList;
	
	private BluetoothAdapter mBluetoothAdapter;
	private String strBTAddress="";
	private TextView txtTips=null;
	private ServiApplication appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prtsdkapp);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		appContext = (ServiApplication) getApplicationContext();
		
		spnPrinterList = (Spinner) findViewById(R.id.spn_printer_list);		
		btnBT = (Button) findViewById(R.id.btnBT);
		txtTips = (TextView) findViewById(R.id.txtTips);
		appContext = (ServiApplication) getApplicationContext();
		//Enable Bluetooth
		EnableBluetooth();	
		
		thisCon=this.getApplicationContext();
		
		//Add Printer List		
		InitCombox();
									
		btnBT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{  									
				if(appContext.getPRT()!=null)
				{					
					appContext.getPRT().CloseProt();					
				}
				
				Intent serverIntent = new Intent(PRTSettingsActivity.this,DeviceListActivity.class);
				serverIntent.putExtra("isFrom", "BTPrinter");
				startActivityForResult(serverIntent, 10);
				ConnectType="Bluetooth";
				return;				
	        }
        });
		
						
	}

	
	
	//call back by scan bluetooth printer
	@Override  
  	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
  	{  
  		try
  		{  		   
	  		switch(resultCode)
	  		{
	  			case 10:			  	        	
	  				String strIsConnected=data.getExtras().getString("is_connected");
	  	        	if (strIsConnected.equals("NO"))
	  	        	{
	  	        		txtTips.setText(thisCon.getString(R.string.scan_error));
	  	        		Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
  	                	return;
	  	        	}
	  	        	else
	  	        	{	  	        		
	  	        		strBTAddress=data.getExtras().getString("BTAddress");
	  	        		if(strBTAddress==null)
	  					{
	  						return;
	  					}
	  					else if(!strBTAddress.contains(":"))
	  					{
	  						return;
	  					}
	  					else if(strBTAddress.length()!=17)
	  					{
	  						return;
	  					}
	  					
	  	        		appContext.setPRT(new PRTAndroidPrint(thisCon,"Bluetooth",spnPrinterList.getSelectedItem().toString().trim()));						
	  	        		appContext.getPRT().InitPort();	
	  					
	  					if(!appContext.getPRT().OpenPort(strBTAddress))
	  					{
	  						Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
	  						txtTips.setText(thisCon.getString(R.string.scan_error));
	  						appContext.setPRT(null);
	  	                	return;
	  					}
	  					else
	  					{
	  						Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
	  						txtTips.setText(thisCon.getString(R.string.scan_success));
	  	                	return;
	  					}
	  	        	}	  	            		
  			}
  		}
  		catch(Exception e)
  		{
  			e.printStackTrace();
  		}
        super.onActivityResult(requestCode, resultCode, data);  
  	} 
	
	//add printer list
	//this version support these printer only.
	//if input other printer,SDK can't connect printer.
	private void InitCombox()
	{
		arrPrinterList = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list, android.R.layout.simple_spinner_item);		
		arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//set adapter to spinner
		spnPrinterList.setAdapter(arrPrinterList);		
	}
	
	//EnableBluetooth
	private boolean EnableBluetooth()
    {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
        {
            if(mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try 
    		{
    			Thread.sleep(500);
    		} 
    		catch (InterruptedException e) 
    		{			
    			e.printStackTrace();
    		}
            if(!mBluetoothAdapter.isEnabled())
            {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } 
        else
        {
            Log.d("PRTLIB", "BTO_EnableBluetooth --> mBluetoothAdapter is null");
        }
        return bRet;
    }
				
}

