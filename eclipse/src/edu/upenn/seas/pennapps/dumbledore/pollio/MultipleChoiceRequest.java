package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by alexsalz1 on 9/7/13.
 */
public class MultipleChoiceRequest extends Activity {
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
        //question text
        TextView questionText = (TextView) findViewById(R.id.question);
        
        //vote buttons
        RadioButton but0 = (RadioButton) findViewById(R.id.mc0);
        RadioButton but1 = (RadioButton) findViewById(R.id.mc0);
        RadioButton but2 = (RadioButton) findViewById(R.id.mc0);
        RadioButton but3 = (RadioButton) findViewById(R.id.mc0);
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
        }
        
    }
}