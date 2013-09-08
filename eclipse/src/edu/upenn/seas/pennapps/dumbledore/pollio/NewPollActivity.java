package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.util.Locale;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewPollActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_poll);


    }

    public void newPoll(View view)
    {
    	Class c = null;
        if (view == findViewById(R.id.mcpollbutton)) {
        	c = MultipleChoicePoll.class;
        } /*else if (view == findViewById(R.id.binarypollbutton)) {
        	c = BinaryPoll.class;
        } else if (view == findViewById(R.id.sliderpollbutton)) {
        	c = SliderPoll.class;
        }*/ else if (view == findViewById(R.id.picturepollbutton)) {
        	c = PicturePoll.class;
        }
        startActivity(new Intent(this, c));
    }




}

