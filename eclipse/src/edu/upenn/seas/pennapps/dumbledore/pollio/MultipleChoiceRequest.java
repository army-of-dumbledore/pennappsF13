package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by alexsalz1 on 9/7/13.
 */
public class MultipleChoiceRequest extends Activity {
	
	private String pollid;
	private static String TAG = "PollioMCRq";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_choice_request);
        
        Intent intent  = getIntent();
        Bundle extras = intent.getExtras();
        JSONObject jsono = new JSONObject();
		try {
			jsono = new JSONObject((String)extras.get("json"));
		} catch (JSONException e1) {
			Log.e(TAG, "This can't happen");
		}
        
        String question = "ERROR", poller = "ERROR";
        ArrayList<String> ids = new ArrayList<String>(),
        		          texts = new ArrayList<String>();
        
        try {
        	pollid = jsono.getString("poll_id");
			question = jsono.getString("question");
			poller = jsono.getString("owner");
			JSONArray choices = jsono.getJSONArray("choices");
			for(int i=0; i<choices.length();i++){
				JSONObject item = choices.getJSONObject(i);
				ids.add(item.getString("id"));
				texts.add(item.getString("text"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //question text
        TextView questionText = (TextView) findViewById(R.id.question);
        questionText.setText(question);
        TextView pollerText = (TextView) findViewById(R.id.poller);
        pollerText.setText(poller);
        
        //vote buttons
        RadioButton but0 = (RadioButton) findViewById(R.id.mc0);
        RadioButton but1 = (RadioButton) findViewById(R.id.mc1);
        RadioButton but2 = (RadioButton) findViewById(R.id.mc2);
        RadioButton but3 = (RadioButton) findViewById(R.id.mc3);
        but0.setVisibility(View.GONE);
        but1.setVisibility(View.GONE);
        but2.setVisibility(View.GONE);
        but3.setVisibility(View.GONE);
        RadioButton[] buttons = {but0, but1, but2, but3}; 
        int numButtons = ids.size();
        for(int i=0; i<numButtons; i++)
        {
        	buttons[i].setVisibility(View.VISIBLE);
        	buttons[i].setText(texts.get(i));
        	buttons[i].setTag(ids.get(i));
        }
        
    }
    
    public void submit_poll(View view) {
    	new AsyncTask<Void, Void, Boolean>() {
    		@Override
    		protected Boolean doInBackground(Void... params) {
    			RadioGroup group = (RadioGroup) findViewById(R.id.mcradiogroup);
    			String choiceid = (String) findViewById(group.getCheckedRadioButtonId()).getTag();
    			JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server)+ "/polls/submit_vote",
    					 									 "user_id", Utils.getUserId(getApplicationContext()),
    					 									 "poll_id", pollid,
    					 									 "choice_id", choiceid);
    			try {
    				return json.getInt("success") == 0;
    			} catch (JSONException e) {
    				return false;
    			}
    		}
    		
    		@Override
    		protected void onPostExecute(Boolean success) {
    			if (success.booleanValue()) {
    				Toast.makeText(getApplicationContext(), "Poll submission successful!", Toast.LENGTH_SHORT).show();
    			} else {
    				Toast.makeText(getApplicationContext(), "Poll submission failed :(", Toast.LENGTH_SHORT).show();
    			}
    		}
    	}.execute(null, null, null);
    	
    	finish();
    }
}
