/* ********************************************************************** 
 * MySQLiteHelper.java
 * Author: Chuck Bolin
 * Date:   3.29.2014
 * Purpose: This class manages various database operations such as
 *          add, edit, and delete records.
 *          
 * **********************************************************************/
/*
 * References
 * http://hmkcode.com/android-simple-sqlite-database-tutorial/
 */
package com.chuckbolin.weblinkmanager;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "LinkDB";
	private static final String TABLE_LINKS = "webLinks";
	private static final String KEY_ID = "id";
	private static final String KEY_URL = "url";
	private static final String KEY_DESCRIPTION = "description";
	private static final String[] COLUMNS = {KEY_ID,KEY_URL,KEY_DESCRIPTION};
	private static String TAG = "MySQLiteHelper";
	
	//constructor
	public MySQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_WEBLINKS_TABLE = "CREATE TABLE " + TABLE_LINKS + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url TEXT, "+
                "description TEXT )";
 
        // create books table
        db.execSQL(CREATE_WEBLINKS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKS);
 
        // create fresh books table
        this.onCreate(db);		
	}

	//adds webItem data to the database
	//************************************************************ ADD WEB ITEM
	public void addWebItem(WebItem webItem){
		//Log.d(TAG,"addWebItem " + webItem.getDescription() + "  " + webItem.getUrl());
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_URL, webItem.getUrl());
		values.put(KEY_DESCRIPTION, webItem.getDescription());
		db.insert(TABLE_LINKS, null,  values);
		db.close();		
	}
	
	//updates webItem data in the database
	//************************************************************ UPDATE WEB ITEM
	public void updateWebItem(WebItem webItem){
		Log.d(TAG,"updateWebItem " + webItem.getDescription() + "  " + webItem.getUrl());
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		//values.put(KEY_ID, webItem.getId());
		values.put(KEY_URL, webItem.getUrl());
		values.put(KEY_DESCRIPTION, webItem.getDescription());
		int i = db.update(TABLE_LINKS,
				values,
				KEY_ID+" =?",
				new String[]{String.valueOf(webItem.getId())});
		db.close();		
	}		
	
	//returns a row of data
	//************************************************************ GET WEB ITEM
	public WebItem getWebItem(int id){

		WebItem webItem = new WebItem();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =
				db.query(TABLE_LINKS,
						COLUMNS,
						" id = ?",
						new String[] {String.valueOf(id)},
						null,
						null,
						null,
						null);
	
		if (cursor != null)
			cursor.moveToFirst();
		
		Log.d(TAG, "getWebItem cursor.getCount(): " + Integer.toString(cursor.getCount()));
		if (cursor.getCount() > 0){
			webItem.setId(cursor.getInt(0));
			webItem.setUrl(cursor.getString(1));
			webItem.setDescription(cursor.getString(2));
		}
		
		return webItem;
	}
	
	//Returns all data from database as a List
	//************************************************************ GETS ALL LINKS
	public List<WebItem> getAllLinks(){
		Log.d(TAG, "getAllLinks");
		
		List<WebItem> webItems = new LinkedList<WebItem>();
		String query = "SELECT * FROM " + TABLE_LINKS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		WebItem webItem = null;
		if(cursor.getCount() > 0){
			if (cursor.moveToFirst()){
				do {
					webItem = new WebItem();
					webItem.setId(Integer.parseInt(cursor.getString(0)));
					webItem.setUrl(cursor.getString(1));
					webItem.setDescription(cursor.getString(2));
					webItems.add(webItem);
				}while(cursor.moveToNext());			
			}
		}
		
		return webItems;		
	}
	
	//Deletes a record from the database
	//************************************************************	DELETES LINK
	public void deleteLink(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LINKS,
				KEY_ID+" = ?",
				new String[]{String.valueOf(id) });
		db.close();

	}
	
	//Deletes all records from the database
	//************************************************************	DELETES ALL LINKS
	public void deleteAllLinks(){
		SQLiteDatabase db = this.getWritableDatabase();	    

		//drop (delete) database table
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKS);
	    
	    //create table again
        String CREATE_WEBLINKS_TABLE = "CREATE TABLE " + TABLE_LINKS + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url TEXT, "+
                "description TEXT )";
 
        // create links table
        db.execSQL(CREATE_WEBLINKS_TABLE);
        
		db.close();
	}
	
}
