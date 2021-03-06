/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theultimatelabs.finder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
//import static com.google.android.gcm.demo.app.CommonUtilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMService";

    public GCMIntentService() {
        super(AppConstants.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        //ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        /*displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }*/
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String cmd = intent.getStringExtra("cmd");
        String rsp = intent.getStringExtra("rsp");
        
        
        String src = intent.getStringExtra("src");
        
        if(!getSharedPreferences(AppConstants.LINKS_PREF, 0).contains(src)) {
        	Log.w(TAG,"Received a GCM message from an unexpected src: "+src);
        	return;
        }
        
        if (cmd.length()>0) {
	        Intent findeeIntent = new Intent(getBaseContext(), FindeeActivity.class);// new Intent(getBaseContext(), FindeeActivity.class);
	        findeeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        findeeIntent.fillIn(intent, 0);
	        getApplication().startActivity(findeeIntent);
        }
        if (rsp.length()>0) {
        	Intent finderIntent = new Intent(AppConstants.FINDER_UPDATE);
        	finderIntent.fillIn(intent, 0);
	        getApplication().sendBroadcast(finderIntent);//new Intent(FINDER_UPDATE));
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        /*String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);*/
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        /*displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));*/
        return super.onRecoverableError(context, errorId);
    }


}
