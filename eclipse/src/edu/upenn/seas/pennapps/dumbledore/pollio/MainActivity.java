package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static final String PREFS_NAME = "edu.upenn.seas.pennapps.dumbledore.pollio.prefs";
    static final String PREFS_USERID = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_register);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void doButton(final View view) {
        Toast.makeText(getApplicationContext(), "button!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, GCMUtils.class));
    }
    
    public void doLogin(final View view) {
    	getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(PREFS_USERID, ((EditText)findViewById(R.id.userid)).getText().toString()).commit();
    	doButton(view);
    }
    
}
