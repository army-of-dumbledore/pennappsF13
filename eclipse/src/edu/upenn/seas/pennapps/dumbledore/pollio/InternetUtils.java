package edu.upenn.seas.pennapps.dumbledore.pollio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;


public class InternetUtils {
	
	private static final String TAG = "PollioIJP";

	public static JSONObject json_request(String... params) {
		
        String responseString = null;
        HttpURLConnection conn = null;
        
        StringBuilder uri = new StringBuilder();
        uri.append(params[0]);
        if (params.length > 1) {
        	try {
        		uri.append("?");
        		for (int i = 1; i < params.length-1; i += 2) {
        			uri.append(URLEncoder.encode(params[i], "UTF-8"));
        			uri.append("=");
        			uri.append(URLEncoder.encode(params[i+1], "UTF-8"));
        			if (i < params.length-2) {
        				uri.append("&");
        			}
        		}
        	} catch (UnsupportedEncodingException e) {
        		Log.e(TAG, "This will absolutely never happen");
        	}
        }
        
        try {
        	URL url = new URL(uri.toString());
        	conn = (HttpURLConnection)url.openConnection();
        	BufferedReader fuckyoujava = new BufferedReader(new InputStreamReader(new BufferedInputStream(conn.getInputStream())));
        	StringBuilder response = new StringBuilder();
        	String read = fuckyoujava.readLine();
        	while (read != null) {
        		response.append(read);
        		read = fuckyoujava.readLine();
        	}
        	responseString = response.toString();
        } catch (IOException e) {
			Log.e(TAG, "IOE: " + e.getMessage());
		} finally {
        	conn.disconnect();
        }
        
        JSONObject jObject = null;
        
        try {
			jObject = new JSONObject(responseString);
			
		} catch (JSONException e) {
			Log.e(TAG, "JsonE: " + e.getMessage());
		}
        
        return jObject;
		
	}
	
}
