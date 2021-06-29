package com.micaja.servipunto.print;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.micaja.servipunto.R;

public class DeviceListActivity extends Activity {

	public static final String TAG = "DeviceListActivity";
	public static final boolean D = true;
	//  Intentextra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	public BluetoothAdapter mBtAdapter;
	private BluetoothDevice mmDevice;
	private BluetoothSocket mmSocket;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public List<String> pairedDeviceList = null;
	public List<String> newDeviceList = null;
	public ArrayAdapter<String> mPairedDevicesArrayAdapter;
	public ArrayAdapter<String> mNewDevicesArrayAdapter;
	public static String toothAddress = null;
	public static String toothName = null;
	private Context thisCon = null;
	private String strAddressList = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.devicelistactivity);
		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				strAddressList = "";
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

		thisCon = this.getApplicationContext();

		// ʼ arryadapter ѾԵ豸ɨ赽豸
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getPairedData());
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// BroadcastReceiver
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(ACTION_PAIRING_REQUEST);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, intent);
		try {
			pairedListView.setOnItemClickListener(mDeviceClickListener);
			newDevicesListView.setOnItemClickListener(mDeviceClickListener);
		} catch (Exception excpt) {
			Toast.makeText(this,
					thisCon.getString(R.string.get_device_err) + excpt,
					Toast.LENGTH_LONG).show();
		}
	}

	//
	public List<String> getPairedData() {
		List<String> data = new ArrayList<String>();
		//
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		//
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) //
			{
				data.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			data.add(noDevices);
		}
		return data;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}


	public void doDiscovery() {
		if (D)
			Log.d(TAG, "doDiscovery()");

		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		mBtAdapter.startDiscovery();
	}

	public OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			boolean hasConnected = false;
			try {
				if (mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}

				// mvcַ
				String info = ((TextView) v).getText().toString();
				toothAddress = info.substring(info.length() - 17);
				if (!toothAddress.contains(":")) {
					return;
				}

				hasConnected = ConnectDevice();
				if (hasConnected) {
					DisConnect();
				}

				Intent intent = new Intent();
				intent.putExtra("is_connected", (hasConnected) ? "OK" : "NO");
				intent.putExtra("BTAddress", toothAddress);
				setResult(10, intent);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				finish();
			}
		}
	};

	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			BluetoothDevice device = null;

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_NONE) {
					if (device.getBluetoothClass().getMajorDeviceClass() == 1536) {
						if (!strAddressList.contains(device.getName())) {
							strAddressList += device.getAddress() + ",";
							mNewDevicesArrayAdapter.add(device.getName() + "\n"
									+ device.getAddress());
						}
					}
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					Log.d("BlueToothTestActivity", "......");
					break;
				case BluetoothDevice.BOND_BONDED:
					Log.d("BlueToothTestActivity", "");
					break;
				case BluetoothDevice.BOND_NONE:
					Log.d("BlueToothTestActivity", "");
				default:
					break;
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
				}
			}
		}
	};

	private boolean ConnectDevice() {
		boolean bRet = false;
		boolean isOldVersion = false;

		if (Build.VERSION.SDK_INT < 15) {
			isOldVersion = true;
		}

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			// 4.0.3
			mmDevice = mBtAdapter.getRemoteDevice(toothAddress);
			if (isOldVersion) {
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
			} else {
				mmSocket = mmDevice
						.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			}
		} catch (Exception e) {
			Log.d("PRTLIB",
					(new StringBuilder("BTO_ConnectDevice --> create "))
							.append(e.getMessage()).toString());
			return false;
		}
		try {
			mmSocket.connect();
			bRet = true;
		} catch (IOException e) {
			bRet = false;
			Log.d("PRTLIB", (new StringBuilder("OpenPort --> connect "))
					.append(e.getMessage()).toString());
		}

		return bRet;
	}

	private boolean DisConnect() {
		boolean bRet = true;
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			mmSocket.close();
			mmSocket = null;
		} catch (IOException e) {
			System.out.println((new StringBuilder("BTO_ConnectDevice close "))
					.append(e.getMessage()).toString());
			bRet = false;
		}
		return bRet;
	}
}
