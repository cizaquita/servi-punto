package com.micaja.servipunto.service;

import com.micaja.servipunto.utils.SyncDataFromServerChangedClass12;

import android.app.IntentService;
import android.content.Intent;

public class PullTheDataFromCentralServer extends IntentService{

	public PullTheDataFromCentralServer() {
		super("");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		new SyncDataFromServerChangedClass12(PullTheDataFromCentralServer.this, 1);
	}

}
