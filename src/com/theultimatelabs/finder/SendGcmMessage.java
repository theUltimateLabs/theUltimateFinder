package com.theultimatelabs.finder;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class SendGcmMessage extends AsyncTask<String, Void, Void> {
	final static public String TAG = "SendGcmMessage";
    
	@Override
	protected Void doInBackground(String... args) {
		
		String src = args[0];
		String dst = args[1];
		String cmd = args[2];
		Log.v(TAG,cmd);
		String rsp = args[3];
		
		Sender sender = new Sender(AppConstants.API_KEY);
		Builder builder = new Message.Builder();
		builder.addData("src", src);
		builder.addData("cmd", cmd);
		builder.addData("rsp", rsp);
		Message message = builder.build();
		
		Result result = null;
		try {
			result = sender.send(message, dst, 5);
		} catch (IOException e) {			
			e.printStackTrace();
			Log.v(TAG,"Error sending message");
			return null;
		}
		
		if (result.getMessageId() != null) {
			 String canonicalRegId = result.getCanonicalRegistrationId();
			 if (canonicalRegId != null) {
			   Log.v(TAG,"same device has more than on registration ID: update database");
			 }
		} else {
		 String error = result.getErrorCodeName();
		 if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
		   Log.v(TAG,"application has been removed from device - unregister database");
		 }
		}
		return null;
	}
	
}