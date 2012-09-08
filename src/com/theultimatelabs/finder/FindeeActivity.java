package com.theultimatelabs.finder;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


public class FindeeActivity extends Activity
{
 	public final static String TAG = "Findee";
    private Ringtone mRingtone = null;
    
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findee);
		
        Intent intent = getIntent();
        final String id = GCMRegistrar.getRegistrationId(getApplicationContext());
        
        final String src = intent.getStringExtra("src");
       
        String sender = getSharedPreferences(AppConstants.LINKS_PREF, 0).getString(src, "Unknown");
                
        String cmd = intent.getStringExtra("cmd");
        
        ((TextView)this.findViewById(R.id.statusText)).setText(String.format("Received %s command from %s",cmd,sender));
        
        if (cmd.equals("siren")) { 
        	Log.v(TAG,"got siren instruction");
        	Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if(alert == null){
                // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if(alert == null){  // I can't see this ever being null (as always have a default notification) but just incase
                    // alert backup is null, using 2nd backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
                }
            }
            mRingtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
            Log.v(TAG,"playing siren");
            mRingtone.play();
            new SendGcmMessage().execute(id,src,"","sirening");
        }
        else if (cmd.equals("btScan")) {
        	/*Intent discoverableIntent = new
			Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);*/
        
        	if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
        		BluetoothAdapter.getDefaultAdapter().enable();
        	        //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	        //startActivityForResult(enableBtIntent, 0);
        	}
        	        
        	// Create a BroadcastReceiver for ACTION_FOUND
        	final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        	    public void onReceive(Context context, Intent intent) {
        	        String action = intent.getAction();
        	        // When discovery finds a device
        	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        	            // Get the BluetoothDevice object from the Intent
        	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        	            short RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -1);
    		            Log.v(TAG,device.getName() + " " + device.getAddress() + " " + new Short(RSSI).toString() + "dB");
    		            new SendGcmMessage().execute(id,src,"",device.getName() + " " + device.getAddress() + " " + new Short(RSSI).toString() + "dB");
        	        }
        	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        	        	new SendGcmMessage().execute(id,src,"","btScanDone");
        	        }
        	    }
        	};
        	// Register the BroadcastReceiver
        	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        	registerReceiver(btReceiver, filter); // Don't forget to unregister during onDestroy
        	
        	if(BluetoothAdapter.getDefaultAdapter().startDiscovery()) {
        		Log.v(TAG,"Start bluetooth scan started");
        		new SendGcmMessage().execute(id,src,"","btScanning");
        	} 
        	else {
        		Log.v(TAG,"not able to start bluetooth scan");
        	}
        }
        else if (cmd.equals("wifi")) {
        	Log.v(TAG,"get wifi ip");
        	new SendGcmMessage().execute(id,src,"",getIpAddr());
        	
        	final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        	    public void onReceive(Context context, Intent intent) {
        	        String action = intent.getAction();
        	        // When discovery finds a device
        	        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
        	        	WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        	        	List<ScanResult> scanResults = wifi.getScanResults();
        	        	for(ScanResult result: scanResults) {
        	        		new SendGcmMessage().execute(id,src,"",result.SSID + " " + result.level + "dB");
        	        	}
        	        }
        	    }
        	};
        	
        	// Register the BroadcastReceiver
        	registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        	WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        	wifi.startScan();
        	
        	
        }
        else if (cmd.equals("movement")) {
        	
        }
        else if (cmd.equals("gps")) {
        	
        }
        
        Button foundButton = (Button) this.findViewById(R.id.foundButton);
        foundButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendGcmMessage().execute(GCMRegistrar.getRegistrationId(getApplicationContext()),src,"","found");
				finish();
			}        	
        });
        
        
		
    }
	
	public String getIpAddr() {
	   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	   int ip = wifiInfo.getIpAddress();

	   String ipString = String.format(
	   "%d.%d.%d.%d",
	   (ip & 0xff),
	   (ip >> 8 & 0xff),
	   (ip >> 16 & 0xff),
	   (ip >> 24 & 0xff));

	   return ipString.toString();
	}
	
	@Override
	public void onStop() {
		if (mRingtone != null) {
			mRingtone.stop();
		}
		super.onStop();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
	}
	
}
