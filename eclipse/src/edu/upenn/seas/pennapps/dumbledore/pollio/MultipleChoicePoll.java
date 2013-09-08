package edu.upenn.seas.pennapps.dumbledore.pollio;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by joe on 9/7/13.
 */
public class MultipleChoicePoll extends Activity {

    EditText po1, po2, po3, po4;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_poll);

        po1 = (EditText)findViewById(R.id.poll_option1);
        po2 = (EditText)findViewById(R.id.poll_option2);
        po3 = (EditText)findViewById(R.id.poll_option3);
        po4 = (EditText)findViewById(R.id.poll_option4);
        po2.setVisibility(View.GONE);
        po3.setVisibility(View.GONE);
        po4.setVisibility(View.GONE);

        po1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    po2.setVisibility(View.VISIBLE);
                    //po2.requestFocus();
                    hideKeyBoard();
                    handled = true;
                }
                return handled;
            }
        });
        po2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    po3.setVisibility(View.VISIBLE);
                    //po3.requestFocus();
                    hideKeyBoard();
                    handled = true;
                }
                return handled;
            }
        });
        po3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    po4.setVisibility(View.VISIBLE);
                    //po4.requestFocus();
                    hideKeyBoard();
                    handled = true;
                }
                return handled;
            }
        });
        po4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyBoard();
                    //mc_poll_done();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void hideKeyBoard()
    {
        LinearLayout mc_poll_layout = (LinearLayout)findViewById(R.id.mc_poll_layout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mc_poll_layout.getWindowToken(), 0);
    }
    
    public void mc_poll_done(final View view)
    {
    	new AsyncTask<Void, Void, Boolean>() {
    		@Override
    		protected Boolean doInBackground(Void... params) {
    			StringBuilder sb = new StringBuilder();
    			EditText[] options = {(EditText) findViewById(R.id.poll_option1),
    								  (EditText) findViewById(R.id.poll_option2),
    								  (EditText) findViewById(R.id.poll_option3),
    								  (EditText) findViewById(R.id.poll_option4)};
    			for (int i = 0; i < 4; i++) {
    				if (options[i].getVisibility() == View.VISIBLE) {
    					String choice = options[i].getText().toString();
    					if (!choice.equals("")) {
    						sb.append(options[i].getText().toString());
    						sb.append("|");
    					}
    				}
    			}
    			String userid = Utils.getUserId(getApplicationContext()),
    				   question = ((EditText)findViewById(R.id.question)).getText().toString(),
    				   choices = sb.toString().substring(0, sb.length() - 1);
    			JSONObject json = InternetUtils.json_request("http://" + getResources().getString(R.string.server) + "/polls/new_poll",
    					 								     "user_id", userid,
    					 								     "question", question,
    					 								     "choices", choices,
    					 								     "pollees", "1|2");
    			
    			try {
    				String pollid = json.getString("poll_id"); // TODO store somewheres
    				return true;
    			} catch (JSONException e) {
    				return false;
    			}
    		}
    		
    		@Override
    		protected void onPostExecute(Boolean success) {
    			if (success) {
    				Toast.makeText(getApplicationContext(), "Poll sent!", Toast.LENGTH_SHORT).show();
    			} else {
    				Toast.makeText(getApplicationContext(), "Poll not sent :(", Toast.LENGTH_SHORT).show();
    			}
    		}
    	}.execute(null, null, null);
    	
    	finish();
    }

}