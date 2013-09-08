package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Main UI for the demo app.
 */
public class GCMUtils extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    static String SENDER_ID = "1033493645859";


    /**
     * Tag used on log messages.
     */
    static final String TAG = "PollioGCM";

    TextView mDisplay;
    static GoogleCloudMessaging gcm;
    static AtomicInteger msgId = new AtomicInteger();
    static SharedPreferences prefs;
    static Context context;

    static String regid, userid;
    
    static DatabaseWrangler dbh;
    static SQLiteDatabase db;
    
    public static String getUsername(Context that) {
    	// get user name magically from AccountManager
		AccountManager am = AccountManager.get(that); // "this" references the current Context
		Account[] accounts = am.getAccountsByType("com.google");
		String name = accounts[0].name;
		Log.i(TAG, "Nice to meet you, " + name);
		return name;
    }
    
    public static void initGCM(final Activity that) {
    	context = that.getApplicationContext();
        dbh = new DatabaseWrangler(context);
        db = dbh.getWritableDatabase();
        dbh.create_tables();
        
     // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices(that)) {
            gcm = GoogleCloudMessaging.getInstance(that);
            regid = getRegistrationId(context);
            userid = Utils.getUserId(context);

            if (regid.equals("")) {
                registerInBackground(that.getApplicationContext());
            } else {
            	Log.i(TAG, "saved gcm id: " + regid);
            }
        
            if (userid.equals("")) {
	        	new AsyncTask<Void, Void, String>() {
	        		@Override
	        		protected String doInBackground(Void... params) {
	        			
	        			
	        			
	        			JSONObject json = InternetUtils.json_request("http://" + context.getResources().getString(R.string.server) + "/polls/initialize/",
	                            "name", getUsername(that),
	                            "reg_id", regid);
	        			try {
							return json.getString("user_id");
						} catch (JSONException e) {
							return "The server is broke yo :(";
						}
	        		}
	        		
	        		@Override
	        		protected void onPostExecute(String result) {
	        			userid = result;
	        			Utils.setUserId(context, userid);
	        			Log.i(TAG, "new user id: " + result);
	        		}
	        	}.execute(null, null, null);
            } else {
            	Log.i(TAG, "saved user id: " + userid);
            }
        	
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mDisplay = (TextView) findViewById(R.id.display);

        context = getApplicationContext();
        dbh = new DatabaseWrangler(context);
        db = dbh.getWritableDatabase();
        dbh.create_tables();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices(this)) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            userid = Utils.getUserId(context);

            if (regid.equals("")) {
                registerInBackground(getApplicationContext());
            } else {
            	mDisplay.append("saved gcm id: " + regid + "\n");
            }
        
            if (userid.equals("")) {
	        	new AsyncTask<Void, Void, String>() {
	        		@Override
	        		protected String doInBackground(Void... params) {
	        			JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/initialize/",
	                            "name", "Reuven Rand",
	                            "reg_id", regid);
	        			try {
							return json.getString("user_id");
						} catch (JSONException e) {
							return "The server is broke yo :(";
						}
	        		}
	        		
	        		@Override
	        		protected void onPostExecute(String result) {
	        			userid = result;
	        			Utils.setUserId(context, userid);
	        			mDisplay.append("new user id: " + result + "\n");
	        		}
	        	}.execute(null, null, null);
            } else {
            	mDisplay.append("saved user id: " + userid + "\n");
            }
        	
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        
        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
        	final Bundle stuff = extras.getBundle("data");
        	if (stuff.getString("command").equals("results")) {
        		new AsyncTask<Void, Void, String>() {
        			@Override
        			protected String doInBackground(Void... params) {
        				JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/request_results/",
        															 "user_id", userid,
        															 "poll_id", stuff.getString("poll_id"));
        				
        				
        				
        				return json.toString();
        			}
        			
        			@Override
        			protected void onPostExecute(String msg) {
        				mDisplay.append(msg + "\n");
        			}
        		}.execute(null, null, null);
        				
        	}
        }
    }
    
    
    public void onClick(final View view) {
    	if (view == findViewById(R.id.create)){
    		new AsyncTask<Void, Void, String>() {
    			@Override
    			protected String doInBackground(Void... params) {
    				
    				JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/new_poll/",
    															 "user_id", userid,
    															 "question", "What color is your parachute?",
    															 "choices", "red|green|blue",
    															 "pollees", "1|2");
    				
    				return json.toString();
    			}
    			
    			@Override
    			protected void onPostExecute(String msg) {
    				mDisplay.append(msg + "\n");
    			}
    		}.execute(null, null, null);
    	} else if (view == findViewById(R.id.vote)) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    
                	JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/submit_vote/",
                												 "user_id", userid,
                												 "poll_id", "1",
                												 "choice_id", "2");
                	
                	return json.toString();
                	
                }

                @Override
                protected void onPostExecute(String msg) {
                    mDisplay.append(msg + "\n");
                }
            }.execute(null, null, null);
        } else if (view == findViewById(R.id.clear)) {
            mDisplay.setText("");
            Utils.setUserId(context, "");
        } else if (view == findViewById(R.id.app)) {
        	
        	startActivity(new Intent(context, NewPollActivity.class));
        	
        	/*new AsyncTask<Void, Void, String>() {
        		@Override
        		protected String doInBackground(Void... params) {
        			JSONObject json = InternetUtils.json_request("http://" + SERVER + "/polls/");
        			
        			try {
						return json.getString("something");
					} catch (JSONException e) {
						return "The server is broken yo :(";
					}
        		}
        		
        		@Override
        		protected void onPostExecute(String msg) {
        			mDisplay.append(msg);
        		}
        	}.execute(null, null, null);
        	*/
        	
        }
    }
    
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity that) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(that);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, that,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
     public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.equals("")) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GCMUtils.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public static void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    JSONObject json = InternetUtils.json_request("http://" + context.getResources().getString(R.string.server) + "/polls/initialize",
                    		                                     "name", getUsername(context),
                    		                                     "reg_id", regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);

    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
