package com.theultimatelabs.finder;


public class MainActivity extends Activity
{
 	public final static String TAG = "Main";
	public final static String SENDER_ID = "762418178161";
    /** Called when the activity is first created. */


	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this,SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
		}
    }
}
