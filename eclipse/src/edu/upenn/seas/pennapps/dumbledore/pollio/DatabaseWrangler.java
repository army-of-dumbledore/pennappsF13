package edu.upenn.seas.pennapps.dumbledore.pollio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseWrangler extends SQLiteOpenHelper {

	private static final String TAG = "PollioDB";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "edu.upenn.seas.pennapps.dumbledore.pollio.sqlite";
	private Context context;
	private SQLiteDatabase db;
	
	public DatabaseWrangler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		this.db = db;
	}
	
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		this.db = db;
	}
	
	public void create_tables() {
		Log.i(TAG, "Creating tables..");
		String[] tables = context.getResources().getStringArray(R.array.db_create);
		for (String table : tables) {
			try {
				db.execSQL(table);
			} catch (SQLiteException e) {
				Log.w(TAG, "SQLE: " + e.getMessage());
			}
		}
	}
	
	public void users() {
	}
	
	public void new_poll() {
	}
	
	public void new_result() {
	}

}
