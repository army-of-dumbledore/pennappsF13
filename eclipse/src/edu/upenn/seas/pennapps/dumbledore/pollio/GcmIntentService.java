package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "PollioIS";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
               
                if (extras.getString("command").trim().equals("results")) {
                	
                	final String pollid = extras.getString("poll_id");
                	final String sender = extras.getString("user_id");
                	final GcmIntentService that = this;
                	
                	new AsyncTask<Void, Void, JSONObject>() {
            			@Override
            			protected JSONObject doInBackground(Void... params) {
            				JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/request_results/",
            															 "user_id", Utils.getUserId(getApplicationContext()),
            															 "poll_id", pollid);
            				
            				
            				try {
            					json.put("poll_id", pollid);
								json.put("sender", sender);
							} catch (JSONException e) {
								Log.e(TAG, "This can't happen");
							}
            				
            				return json;
            			}
            			
            			@Override
            			protected void onPostExecute(JSONObject msg) {
            				Intent another_intent = new Intent(that, MultipleChoiceResult.class);
                        	another_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // yes that's what I really want!
                        	another_intent.putExtra("json", msg.toString());
                        	Log.i(TAG, "launching...");
                        	startActivity(another_intent);
            			}
            		}.execute(null, null, null);

                } else if (extras.getString("command").trim().equals("poll")) {
                	
                	final String pollid = extras.getString("poll_id");
                	final String owner = extras.getString("owner");
                	final GcmIntentService that = this;
                	new AsyncTask<Void, Void, JSONObject>() {
                		@Override
                		protected JSONObject doInBackground(Void... params) {
                			JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/request_poll/",
                														 "poll_id", pollid);
                			try {
								json.put("poll_id", pollid);
								json.put("owner", owner);
							} catch (JSONException e) {
								Log.e(TAG, "JSONE: " + e.getMessage());
							}
                			
                			return json;
                		}
                		
                		@Override
                		protected void onPostExecute(JSONObject msg) {
                			Intent i = new Intent(that, MultipleChoiceRequest.class);
                			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                			i.putExtra("json", msg.toString());
                			startActivity(i);
                		}
                	}.execute(null, null, null);
                	
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GCMUtils.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

