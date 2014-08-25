/* ********************************************************************** 
 * MainActivity
 * Author: Chuck Bolin
 * Date:   3.29.2014
 * Purpose: This is the main activity for the application. It uses data
 *          from other activities and pushes it into a SQLite DB.
 *          
 * **********************************************************************/

package com.chuckbolin.weblinkmanager;
/*
 *   Useful references
 *   http://commonsware.com/Android/Android_3-6-CC.pdf
 *   http://hmkcode.com/android-simple-sqlite-database-tutorial/
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

//MainActivity - program begins here
public class MainActivity extends Activity {
	
	private static String TAG = "MainActivity"; 
	private static int ADD_REQUEST_CODE = 115;  //arbitrary numbers to differentiate return
	private static int EDIT_REQUEST_CODE = 120;
	private static int EXPORT_IMPORT_CODE = 125;
	final String textSource = "http://mush4brains.com/files/weblinksmanager/weblinklist.txt";
	public int countRows = 0;
	public boolean syncSuccess = false;
	
	MySQLiteHelper mDB = new MySQLiteHelper(this); //object used for all database operations
		
    ListView mListView;			//stores list of all listboxes
    int mSelectedItem = -1;     //>= 0 means this is the selected item within the list
    Button mButtonEdit;
    Button mButtonDelete;
    Button mButtonEmail;
    Button mButtonShow;
    Button mButtonAdd;
    LinearLayout mLayout;
    Vibrator mVibrator;
    long mSyncDateTimeLong;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
				
		//read preferences "lastsyncdate" and set data member, used for ActionBar
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		Editor editor = pref.edit();
		long value = pref.getLong("lastsyncdate", 0);
		//Toast.makeText(MainActivity.this, "DateTime: " + Long.toString(value), Toast.LENGTH_SHORT).show();
		
		if (value == 0){
			mSyncDateTimeLong = 0;
		  Toast.makeText(MainActivity.this, "Select Options|Sync to Internet!" + Long.toString(value), Toast.LENGTH_SHORT).show();	
		}else{
			mSyncDateTimeLong = value;			
		}

		
		//adds actionbar
		ActionBar actionBar = this.getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
		if(mSyncDateTimeLong == 0){
		  actionBar.setSubtitle("Last Sync: None");
		}
		else{
		  SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		  String dateString = formatter.format(new Date(mSyncDateTimeLong));
		  actionBar.setSubtitle("Last sync: " + dateString);
		}
		
		mVibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
		
		//adds temporary list of items to list view
		mListView = (ListView)findViewById(R.id.listView);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);		

		//connect data members to views
		mButtonShow = (Button)findViewById(R.id.buttonShow);
		mButtonEmail = (Button)findViewById(R.id.buttonEmail);
		mButtonAdd = (Button)findViewById(R.id.buttonAdd);
		mButtonEdit = (Button)findViewById(R.id.buttonEdit);
		mButtonDelete = (Button)findViewById(R.id.buttonDelete);
	    mLayout = (LinearLayout)findViewById(R.id.layoutButtons);
		hideIdRelatedButtons();
				
		UpdateListView(); //updates listview with database data
		
		//button callbacks
		//============================================================= SHOW BUTTON
		mButtonShow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){		
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonShow.startAnimation(shake);
				LaunchLinkInWebBrowser();
			}
		});		
		
		//============================================================= EMAIL BUTTON
		mButtonEmail.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonEmail.startAnimation(shake);
				
				if (mSelectedItem > -1){
				  WebItem webItem = new WebItem();
				  webItem = mDB.getWebItem(mSelectedItem);
				  
				  //reference: http://www.mkyong.com/android/how-to-send-email-in-android/
				  Intent i = new Intent(Intent.ACTION_SEND);
				  i.setType("message/rfc822");
				  i.putExtra(Intent.EXTRA_SUBJECT, "Link Sent From \"WebLinkManager App\"");
				  
				  String message = "Hi,\n" + "Give this link a try!\n" 
						  + "Description: " + webItem.getDescription() + "\n"
						  + "Web Link:    " + webItem.getUrl() + "\n\n"
						  + "Regards,\n" + "WebLinkManager.apk";
						  				  
				  i.putExtra(Intent.EXTRA_TEXT, message);
				  hideIdRelatedButtons();
				  try{
					  startActivity(Intent.createChooser(i, "Choose an Email client:"));					  
				  }catch(Exception e){
					  
				  }finally{
					  Toast.makeText(getApplicationContext(), "Could not send email", Toast.LENGTH_SHORT).show();
				  }
				}
			}
		});

		//============================================================= ADD BUTTON
		mButtonAdd.setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View v){
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonAdd.startAnimation(shake);

				Intent intent = new Intent(getApplicationContext(), AddActivity.class);
				hideIdRelatedButtons();
				startActivityForResult(intent, ADD_REQUEST_CODE); //115 is just a number, used down below				
			}
		});		
			    
		//============================================================= EDIT BUTTON
		mButtonEdit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){	
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonEdit.startAnimation(shake);
				
				if (mSelectedItem > -1){
					WebItem webItem = new WebItem();
					webItem = mDB.getWebItem(mSelectedItem);				
					Intent intent = new Intent(getApplicationContext(), EditActivity.class);
					intent.putExtra("id", Integer.toString(mSelectedItem));
					intent.putExtra("description", webItem.getDescription());
					intent.putExtra("url", webItem.getUrl());	
					hideIdRelatedButtons();
					startActivityForResult(intent, EDIT_REQUEST_CODE); //115 is just a number, used down below				
				}
			}
		});
	    
		//============================================================= DELETE BUTTON
		mButtonDelete.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v){
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonDelete.startAnimation(shake);
				
				//a link in the database has been selected and is valid
				if (mSelectedItem > -1){
					
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
			        alertDialog.setTitle("Confirm Delete...");
			        alertDialog.setMessage("Delete id (" + Integer.toString(mSelectedItem) + ")?");
			        alertDialog.setIcon(R.drawable.ic_launcher);
			        hideIdRelatedButtons();
			        
			        // Setting Positive "Yes" Button
			        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
							mDB.deleteLink(mSelectedItem);
							UpdateListView();
							unselectListView();
			            }
			        });
			 
			        // Setting Negative "NO" Button
			        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            	//do nothing, canceled
			            }
			        });
			 
			        // Showing Alert Message
			        alertDialog.show();
			        
				}else{
					Toast.makeText(getApplicationContext(), "Nothing selected!",  Toast.LENGTH_SHORT).show();
				}
			}
		});		

		//sets up call back for mListView, responds to list clicks
		//============================================================= LISTVIEW ITEM CLICK
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){

				//Log.d(TAG, "setOnItemClickListener");
				String itemValue = (String)mListView.getItemAtPosition(position);
				
				//listview contains 'id:description', must extract id
				int colon = itemValue.indexOf(':');
				if (colon > -1){
					int itemId = Integer.parseInt(itemValue.substring(0,colon));
					if (itemId > -1){
						mSelectedItem = itemId;//this number is database record id
						LaunchLinkInWebBrowser();
					}
				}else{
				    Toast.makeText(getApplicationContext(), "Nothing to select", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//sets up call back for mListView, responds to list long clicks 
		//============================================================= LISTVIEW ITEM LONG CLICK
		mListView.setOnItemLongClickListener(new OnItemLongClickListener(){
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id){

				String itemValue = (String)mListView.getItemAtPosition(position);
				
				//listview contains 'id:description', must extract id
				int colon = itemValue.indexOf(':');
				if (colon > -1){
					int itemId = Integer.parseInt(itemValue.substring(0,colon));
					if (itemId > -1){
						mSelectedItem = itemId;//this number is database record id
						//Toast.makeText(getApplicationContext(),  "Id: " + Integer.toString( mSelectedItem) + " Selected!", Toast.LENGTH_SHORT).show();
						showIdRelatedButtons();
					}
				}else{
				    Toast.makeText(getApplicationContext(), "Nothing to select", Toast.LENGTH_SHORT).show();
				}
				
				Log.d(TAG, Integer.toString(mSelectedItem ));
				return true;
			}
		});
		
		mLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				unselectListView();
			}			
		});		
	}
	
	//responds to other activities returning to this activity
	//******************************************************************************
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		 Log.d(TAG,"onActivityResult entered");
		 switch(requestCode){
		 
			 case 115: //return from Add activity
				 if  (resultCode == RESULT_OK){
					 Bundle res = data.getExtras();
					 String url = res.getString("url");
					 String description = res.getString("description");
					 WebItem webItem = new WebItem(url, description);
					 mDB.addWebItem(webItem);
					 UpdateListView();
    				 
				 }
				 else if(resultCode == RESULT_CANCELED){
					 Log.d(TAG, "add canceled");			 
					 
				 }
				 break;
			 case 120: //return from Update activity
				 if  (resultCode == RESULT_OK){
					 Bundle res = data.getExtras();
					 String id = res.getString("id");
					 String url = res.getString("url");
					 String description = res.getString("description");
					 WebItem webItem = new WebItem(Integer.parseInt(id), url, description);
					 mDB.updateWebItem(webItem);

					 UpdateListView();
					 Log.d(TAG, "update (url): " + url);
					 Log.d(TAG, "update (description): " + description);
					 
				 }
				 else if(resultCode == RESULT_CANCELED){
					 Log.d(TAG, "update canceled");			 
					 
				 }				 
				 break;
			 case 125: //EXPORT_IMPORT_CODE:
				 
				 break;
		 }
		 unselectListView();
	 }
	
	//queries database and loads data into listview
	//****************************************************************
	private void UpdateListView(){
		//reads database and loads into webItems list
		List<WebItem> webItems = mDB.getAllLinks();
		
		//loop through database items and populate listview with id and description
		List<String> items = new LinkedList<String>();
		for (int i = 0; i < webItems.size(); ++i){
			WebItem w = webItems.get(i);
			if (i == 0)
				mSelectedItem = w.getId(); //assign as default selected
			String s = Integer.toString(w.getId()) + ": " + w.getDescription();
			items.add(s);
		}
	    String[] values = items.toArray(new String[items.size()]);

	    //bind data to listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1,
				values);		
		mListView.setAdapter(adapter);		
	}
	
	
	private void startWebPage(String page){
		Uri webpage = Uri.parse(page);
		Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);
		Intent chooserIntent = webIntent.createChooser(webIntent, "MyBrowser");
		startActivity(chooserIntent);		
	}
	
	//displays option menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}	

	//manages option menu selection
	@Override
	 public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		
		//============================================================= ACTION DLETE ALL LINKS MENU OPTION
		case R.id.action_delete:
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
	        alertDialog.setTitle("Confirm Delete...");
	        alertDialog.setMessage("Delete All Links?");
	        alertDialog.setIcon(R.drawable.ic_launcher);
	        
	        // Setting Positive "Yes" Button
	        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
					mDB.deleteAllLinks();
					UpdateListView();
					SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
					Editor editor = pref.edit();

					ActionBar actionBar = MainActivity.this.getActionBar();
 				    actionBar.setSubtitle("Last Sync: None");					
					long value = 0;
					editor.putLong("lastsyncdate", value);
					editor.commit();						
					Toast.makeText(MainActivity.this, "All Links Deleted", Toast.LENGTH_SHORT).show();
	            }
	        });
	 
	        // Setting Negative "NO" Button
	        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	//do nothing, canceled
	            }
	        });
	 
	        // Showing Alert Message
	        alertDialog.show();			
			
			return true;
			
		//============================================================= ACTION SYNC MENU OPTION
		case R.id.action_sync:
			Toast.makeText(MainActivity.this, "Sync to Internet selected", Toast.LENGTH_SHORT).show();
			
			//calls the tread 'Task' shown at bottom of this file
			Thread thread = new Thread(new Task());
			thread.start();			
			
			try {
				thread.join();
				UpdateListView(); //updates listview with database data
				if(syncSuccess){
					
					//write current time in milliseconds to lastsyncdate
					SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
					Editor editor = pref.edit();
					Date date = new Date(System.currentTimeMillis());
					long value = date.getTime();
					editor.putLong("lastsyncdate", value);
					editor.commit();					
					mSyncDateTimeLong = value;
					
					//update actionbar subtitle
					ActionBar actionBar = this.getActionBar();
       			    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					String dateString = formatter.format(new Date(mSyncDateTimeLong));
					actionBar.setSubtitle("Last sync: " + dateString);
					Toast.makeText(MainActivity.this, "Sync Complete: " + Integer.toString(countRows) , Toast.LENGTH_SHORT).show();
				}	
				else{
					Toast.makeText(MainActivity.this, "Sync failed to connect!", Toast.LENGTH_SHORT).show();
				}
				
				countRows = 0;
			} catch (InterruptedException e) {
				Toast.makeText(MainActivity.this, "Nothing to Sync", Toast.LENGTH_SHORT).show();
			}
			
			return true;
		
		//============================================================= ACTION IMPORT FILE MENU OPTION
		case R.id.action_import:
			Toast.makeText(MainActivity.this, "Import File (not programmed)", Toast.LENGTH_SHORT).show();

			return true;
		
		//============================================================= ACTION EXPORT FILE MENU OPTION
		case R.id.action_export:
			Toast.makeText(MainActivity.this, "Export to File (not programmed)", Toast.LENGTH_SHORT).show();
			return true;			
			
		//============================================================= ACTION EMAIL EXPORT MENU OPTION
		case R.id.action_email:
			Toast.makeText(MainActivity.this, "Export Via Email Selected", Toast.LENGTH_SHORT).show();
			
			List<WebItem> webItems = mDB.getAllLinks();
			
			  //reference: http://www.mkyong.com/android/how-to-send-email-in-android/
			  Intent intent = new Intent(Intent.ACTION_SEND);
			  intent.setType("message/rfc822");
			  intent.putExtra(Intent.EXTRA_SUBJECT, "Database Export from \"WebLinkManager App\" (rows): " + Integer.toString(webItems.size()));
			  
			  WebItem webItem;
			  StringBuilder sb = new StringBuilder();
			  for (int i = 0; i < webItems.size(); ++i){
				  webItem = webItems.get(i);
				  sb.append(webItem.getDescription() + " || " + webItem.getUrl() + "\n");
			  }
			  
			  String message = sb.toString();
					  				  
			  intent.putExtra(Intent.EXTRA_TEXT, message);
			  hideIdRelatedButtons();
			  try{
				  startActivity(Intent.createChooser(intent, "Choose an Email client:"));					  
			  }catch(Exception e){
				  
			  }finally{
				  Toast.makeText(getApplicationContext(), "Could not send email", Toast.LENGTH_SHORT).show();
			  }			  
			
			return true;
		case R.id.action_about:
			Toast.makeText(MainActivity.this, "About selected", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	public void LaunchLinkInWebBrowser(){
		//if an item was selected in list view it is now launched into the browser
		 if (mSelectedItem > -1){
			 WebItem webItem = mDB.getWebItem(mSelectedItem);
			 
			 if (webItem != null) {
				 String url = webItem.getUrl();
				 
				 if (null != url && !url.isEmpty()) {
					 startWebPage(webItem.getUrl());
				 }
			 }
			 	
		 }else{
			 Toast.makeText(getApplicationContext(), "Nothing selected!", Toast.LENGTH_SHORT).show();	 
		 }		
	}
	
	public void showIdRelatedButtons(){
		mButtonShow.setVisibility(Button.VISIBLE);
		mButtonEmail.setVisibility(Button.VISIBLE);
		mButtonEdit.setVisibility(Button.VISIBLE);
		mButtonDelete.setVisibility(Button.VISIBLE);
		mButtonAdd.setVisibility(Button.INVISIBLE);
	}
	
	public void hideIdRelatedButtons(){
		mButtonShow.setVisibility(Button.INVISIBLE);
		mButtonEmail.setVisibility(Button.INVISIBLE);
		mButtonEdit.setVisibility(Button.INVISIBLE);
		mButtonDelete.setVisibility(Button.INVISIBLE);
		mButtonAdd.setVisibility(Button.VISIBLE);		
	}
	
	public void unselectListView(){
		mListView.clearChoices();
		mListView.requestLayout();
		mSelectedItem = -1;
	}	
	
	//this is secondary thread that runs to read the URL text file
	//=============================================================================== THREAD FOR SYNC TO INTERNET
	class Task implements Runnable {
        @Override
        public void run() {
        	
        	//let's load everything in the database into a List<WebItems>
        	List<WebItem> webItems = mDB.getAllLinks();

        	try{
    			URL url = new URL(textSource);
    			try{
    			  InputStreamReader isr =  new InputStreamReader(url.openStream());

    			  BufferedReader in = new BufferedReader(isr);
    			  String line;    			
    			
    			  //loops through text file
    			  while((line = in.readLine()) != null){
    				
    				//locate delimiter ||
    				int pos = line.indexOf("||");
    				
    				//if delimiter is found, extract left and right sides, while trimming
    				if (pos > 0){
    					String stringDescription = line.substring(0, pos -1 ).trim();
    					String stringURL = line.substring(pos + 2, line.length()).trim();
    					if(stringDescription.length() > 0 && stringURL.length() > 0){
    						
    						//load this text file row into webItem
    						WebItem webItem = new WebItem(stringURL, stringDescription);
    						boolean bFound = false;
    						
    						//loop through webItems for a match, if so then don't add to database
    						for(int i = 0; i < webItems.size(); ++i){
    							WebItem w = webItems.get(i);  
    							
    							//if there is a match between the database URL and the text file URL, do nothing
    							if(stringURL.equalsIgnoreCase(w.getUrl())){    								
    								bFound = true;
    								break;
    							}    							
    						}
    						
    						//alright to add URL
    						if (bFound == false){
    						  mDB.addWebItem(webItem);
    						  countRows++;
    						}
    					}//if (string	
    				}//if (pos
    			}// while
    			in.close();
    			syncSuccess = true;
    		}catch(MalformedURLException e){
    			Toast.makeText(getApplicationContext(), "Malformed URL", Toast.LENGTH_SHORT).show();
    		}catch(IOException e){
    			Toast.makeText(getApplicationContext(), "Problem reading URL text file", Toast.LENGTH_SHORT).show();
    		}
        	Log.d(TAG,"Step 3");	
			}catch(Exception e){
			}
        } //run		
    }//class Task
}

