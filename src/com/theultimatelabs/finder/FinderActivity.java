package com.theultimatelabs.finder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


public class FinderActivity extends Activity
{
 	public final static String TAG = "Finder";


	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finder);
		
        Log.v(TAG,"Finder");
        
        registerReceiver(mFinderReceiver, new IntentFilter(AppConstants.FINDER_UPDATE));
        final String id = GCMRegistrar.getRegistrationId(this);
        
		((Button) this.findViewById(R.id.sirenButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendGcmMessage().execute(id,id,"siren","");
			}
		});
		
		((Button) this.findViewById(R.id.bluetoothButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendGcmMessage().execute(id,id,"btScan","");
			}
		});
		
		((Button) this.findViewById(R.id.wifiButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendGcmMessage().execute(id,id,"wifi","");
			}
		});
		
		
	}
	
	 private final BroadcastReceiver mFinderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rsp = intent.getExtras().getString("rsp");
            Log.v(TAG,"rsp: "+rsp);
            ((TextView)findViewById(R.id.statusText)).setText(String.format("%s",rsp));   
            Toast.makeText(getApplicationContext(), rsp, Toast.LENGTH_LONG).show();
        }
    };	
}
