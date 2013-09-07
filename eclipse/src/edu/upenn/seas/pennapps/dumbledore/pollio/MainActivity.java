package edu.upenn.seas.pennapps.dumbledore.pollio;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void doButton(final View view) {
        Toast.makeText(getApplicationContext(), "button!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DemoActivity.class));
    }
    
}
