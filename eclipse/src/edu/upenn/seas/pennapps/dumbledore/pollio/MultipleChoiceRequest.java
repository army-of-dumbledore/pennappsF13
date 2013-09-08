package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by alexsalz1 on 9/7/13.
 */
public class MultipleChoiceRequest extends Activity {
	
	private String pollid;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_choice_request);
        
        Intent intent  = getIntent();
        Bundle extras = intent.getExtras();
        JSONObject jsono = (JSONObject)extras.get("json");
        
        String question = "ERROR";
        ArrayList<String> ids = new ArrayList<String>(),
        		          texts = new ArrayList<String>();
        
        try {
        	pollid = jsono.getString("poll_id");
			question = jsono.getString("question");
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
        
        //vote button stuffs
        
    }
    
    public void submit_poll(View view) {
    	new AsyncTask<Void, Void, Boolean>() {
    		@Override
    		protected Boolean doInBackground(Void... params) {
    			RadioGroup group = (RadioGroup) findViewById(R.id.mcradiogroup);
    			int choiceid = group.getCheckedRadioButtonId();
    			JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server)+ "/polls/submit_vote",
    					 									 "user_id", Utils.getUserId(getApplicationContext()),
    					 									 "poll_id", pollid,
    					 									 "choice_id", Integer.toString(choiceid));
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