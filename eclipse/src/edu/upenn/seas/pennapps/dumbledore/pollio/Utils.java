package edu.upenn.seas.pennapps.dumbledore.pollio;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	
	static final String PREFS_NAME = "edu.upenn.seas.pennapps.dumbledore.pollio.prefs";
    static final String PREFS_USERID = "userid";
	
	public static String getUserId(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    	
    	return prefs.getString(PREFS_USERID, "");
    }
    public static void setUserId(Context context, String userid) {
    	final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    	
    	prefs.edit().putString(PREFS_USERID, userid).commit();
    }

}
