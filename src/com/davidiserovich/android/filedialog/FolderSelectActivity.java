package com.davidiserovich.android.filedialog;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderSelectActivity extends Activity {
	/** The current list of files */
	File[] items;
	
	/** A string constant for the bundle extra indicating where to start the file selection browser */
	public static final String TARGET_PATH = "com.davidiserovich.FileSelectActivity.TARGET_PATH";
	
	/** Constant key for the bundle extra indicating the file that the user picked */
	public static final String SELECTED_PATH = "com.davidiserovich.FileSelectActivity.SELECTED_PATH";
	
	ListView fileList;
	
	ArrayAdapter<File> fileListAdapter;
	
	String selectedFilePath;
	File previousDirectory;
	File currentDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_selection);
		fileList = (ListView)findViewById(R.id.files_list);
		
		Intent launchingIntent = getIntent();
		String startPath = launchingIntent.getStringExtra(TARGET_PATH);
		File startFile = new File(startPath);
		if (!startFile.isDirectory()){
			Toast.makeText(this, startPath + " is not a valid directory!", Toast.LENGTH_SHORT).show();		
			startFile = new File("/");
		}
		
		currentDirectory = startFile;
		
		
		/** Set the listener to return the file's full path as the activity result if it's a file
		 *  or navigate deeper if it's a directory
		 */
		fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
		    	// The first entry is always the previous directory ..
		    	if (pos == 0){
		    		if (currentDirectory.getParent() != null){
		    			currentDirectory = currentDirectory.getParentFile();
		    			populateList();
		    		}
		    	}
		    	else {
		    		// The position in the view is one less than the position in the list of files
		    		pos -= 1;
			        File f = fileListAdapter.getItem(pos);
			        
			        if (f.isDirectory()){
			        	currentDirectory = f;
			        	populateList();
			        }
			        
			        else {
			        	selectedFilePath = f.getAbsolutePath();
			        	finish();
			        }
		    	}
		    }
		});
			
		
		
	}
	
	/** 
	 * Set the array adapter for the file list 
	 */
	private void populateList(){
		// Fill up the items list
		items = currentDirectory.listFiles();
		
		fileListAdapter = new ArrayAdapter<File>(this, R.id.files_list, items){
			@Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		    	View v = convertView;
		    	if (v == null) {
		            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            v = vi.inflate(R.layout.file_list_item, null);
		        }
		        if (v != null) {
		                TextView titleView = (TextView) v.findViewById(R.id.filename);
		                if (position == 0){
		                	titleView.setText("..");
		                }
		                titleView.setText(items[position-1].getName());                  
		        }else{
		        	Log.d("Something", "Is Wrong");
		        }
		        return v;
		    	
		    }
			
		};
		
		fileList.setAdapter(fileListAdapter);
	}
	
	@Override
	public void onPause(){
		if (selectedFilePath == null){
			setResult(RESULT_CANCELED);
		}
		else {
			Intent resultData = new Intent();
			resultData.putExtra(SELECTED_PATH, selectedFilePath);
			setResult(RESULT_OK, resultData);
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_file_selection, menu);
		return true;
	}
}
