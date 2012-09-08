package com.theultimatelabs.finder;


import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


public class MainActivity extends Activity
{
 	public final static String TAG = "Main";

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			Log.v(TAG, "Registering");
			GCMRegistrar.register(this,AppConstants.SENDER_ID);
			return;
		} else {
			Log.v(TAG, "Already registered");			
		}
		Log.v(TAG, String.format("ID: %s",regId));
		
		Button findButton = (Button) this.findViewById(R.id.findButton);
		findButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),FinderActivity.class));
			}
		});
		
		//Link with another device
		getSharedPreferences(AppConstants.LINKS_PREF, 0).edit().putString(regId, "Rob's Phone").commit();
	    
    }
	
	
   
}
