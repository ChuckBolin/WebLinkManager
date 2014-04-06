/* ********************************************************************** 
 * AddActivity
 * Author: Chuck Bolin
 * Date:   3.29.2014
 * Purpose: This activity provides UI for entering url and description.
 *          Data is return to the MainActivity.
 *          
 * **********************************************************************/
package com.chuckbolin.weblinkmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	//used for LogCat
	private static String TAG = "AddActivity";//AddActivity.class.getCanonicalName();
	
	//references to AddActivity UI views
	Button mButtonCancel;
	Button mButtonAdd;
	EditText mEditURL;
	EditText mEditDescription;
	Vibrator mVibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
		actionBar.setSubtitle("Add a New Web Link");
		actionBar.setTitle("Add a Link");
		
		mVibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
		
		mEditURL = (EditText)findViewById(R.id.editURL);
		mEditDescription = (EditText)findViewById(R.id.editDescription);
		mButtonAdd = (Button)findViewById(R.id.buttonAdd);
		mButtonCancel = (Button)findViewById(R.id.buttonCancel);
		
		//returns data to MainActivity when add button is clicked
		//========================================================= ADD BUTTON
		mButtonAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonAdd.startAnimation(shake);
				
			  if (mEditDescription.length() < 1){
				  Toast.makeText(getApplicationContext(), "Description is empty!", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  if (mEditURL.length() < 1){
				  Toast.makeText(getApplicationContext(), "Web link is empty!", Toast.LENGTH_SHORT).show();
				  return;
			  }			  

			  //boolean validUrl = Patterns.WEB_URL.matcher(mEditURL.toString()).matches();		
			  if (isValidUrl(mEditURL.getText().toString())){			  
				  //attaches edittext values to a bundle, passes to intent, and returns intent
				  Bundle bundle = new Bundle();		
				  bundle.putString("description", mEditDescription.getText().toString() );
				  bundle.putString("url", mEditURL.getText().toString());
				  Intent intent = new Intent();
				  intent.putExtras(bundle);
				  setResult(RESULT_OK,intent);
				  finish();
			  }else{
				  Toast.makeText(getApplicationContext(), "Web Link is incorrectly formatted!", Toast.LENGTH_SHORT).show();
			  }
			  
			}			
		});
		
		//cancels add operation
		//========================================================= CANCEL BUTTON
		mButtonCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mVibrator.vibrate(50);
				Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				mButtonCancel.startAnimation(shake);

			  Intent intent = new Intent();
			  setResult(RESULT_CANCELED, intent);
			  finish();
			}			
		});
		
	}
	
	//http://alvinalexander.com/java/jwarehouse/android/core/java/android/webkit/URLUtil.java.shtml
    public  boolean isValidUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }

        return (URLUtil.isAssetUrl(url) ||
        		/*URLUtil.isResourceUrl(url) ||*/
        		URLUtil.isFileUrl(url) ||
        		URLUtil.isAboutUrl(url) ||
        		URLUtil.isHttpUrl(url) ||
        		URLUtil.isHttpsUrl(url) ||
        		URLUtil.isJavaScriptUrl(url) ||
        		URLUtil.isContentUrl(url));
    }		
}
