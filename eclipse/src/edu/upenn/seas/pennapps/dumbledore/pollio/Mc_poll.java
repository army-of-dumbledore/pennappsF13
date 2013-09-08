package edu.upenn.seas.pennapps.dumbledore.pollio;

import android.app.ActionBar;

import android.app.Activity;
import android.content.Context;
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
public class Mc_poll extends Activity {

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
    	Toast.makeText(getApplicationContext(), "Poll sent! J/K LOL", Toast.LENGTH_SHORT).show();
    	finish();
    }

}