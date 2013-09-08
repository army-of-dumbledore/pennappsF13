package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by alexsalz1 on 9/7/13.
 */
public class MultipleChoiceResult extends Activity {
	
	private static String TAG = "PollioMCRs";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_choice_result);
        String pollid;
        Intent intent  = getIntent();
        Bundle extras = intent.getExtras();
        JSONObject jsono = new JSONObject();
		try {
			Log.i(TAG, (String)extras.get("json"));
			jsono = new JSONObject((String)extras.get("json"));
		} catch (JSONException e1) {
			Log.e(TAG, "This can't happen");
		}
        
        String question = "ERROR", poller = "ERROR";
        ArrayList<String> counts = new ArrayList<String>(),
        		          texts = new ArrayList<String>(),
        				  backers = new ArrayList<String>();
        
        try {
        	pollid = jsono.getString("poll_id");
			question = jsono.getString("question");
			JSONArray choices = jsono.getJSONArray("choices");
			for(int i=0; i<choices.length();i++){
				JSONObject item = choices.getJSONObject(i);
				texts.add(item.getString("text"));
				counts.add(item.getString("count"));
				backers.add(item.getString("backers"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //display question
        TextView quest = (TextView) findViewById(R.id.question);
        quest.setText(question);
        //populate percentages
        TextView p0 = (TextView) findViewById(R.id.percent0);
        TextView p1 = (TextView) findViewById(R.id.percent1);
        TextView p2 = (TextView) findViewById(R.id.percent2);
        TextView p3 = (TextView) findViewById(R.id.percent3);
        //visibility
        p0.setVisibility(View.GONE);
        p1.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        p3.setVisibility(View.GONE);
        
        
        
        TextView[] perViews = {p0, p1, p2, p3};
        TextView[] choices = {(TextView)findViewById(R.id.po0), (TextView)findViewById(R.id.po1), (TextView)findViewById(R.id.po2), (TextView)findViewById(R.id.po3)};
        for(int j=0;j<4;j++)
        {
        	choices[j].setVisibility(View.GONE);
        }
        double totalVotes = 0;
        for(int i=0;i<counts.size();i++)
        {
        	totalVotes += Integer.parseInt(counts.get(i));
        }
        ArrayList<Integer> percents = new ArrayList<Integer>();
        for(int i=0;i<counts.size();i++)
        {
        	perViews[i].setVisibility(View.VISIBLE);
        	choices[i].setVisibility(View.VISIBLE);
        	double floatper = Double.parseDouble(counts.get(i))/totalVotes;
        	percents.add((int)(floatper*100));
        	perViews[i].setText(Integer.toString(percents.get(i))+"%");
        	choices[i].setText(texts.get(i));
        }
        
    }
}