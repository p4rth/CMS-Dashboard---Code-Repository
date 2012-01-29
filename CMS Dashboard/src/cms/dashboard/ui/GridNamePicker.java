package cms.dashboard.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import cms.dashboard.ioClasses.JSONFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * This class manages Grid Name Picker Activity. 
 * 
 * This activity loads, presents all users from set JSON feed into simple List View.
 * 
 * @author Parth Patel [parthpatel32@gmail.com]
 * @see CMSDashboardActivity#selectGridNm(View)
 *
 */
public class GridNamePicker extends Activity {
	
	/**	Progress Dialog, used when loading task.	*/
	private ProgressDialog progressDialog;
	
	
	/** is an ArrayList which will hold our Grid Names read from JSON feed. */
	ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
	
	/**	is an instance of Async Task used to requests and parse JSON  
	 * @see taskLoadGridNames	*/
	private taskLoadGridNames loadNamesTask;
	
	
	
	/** 
	 * Called when the activity is first created. Responsible for initialising the UI.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridnmpicker);
		setTitle("Select a User:");
      
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("Loading Users. Please wait...");
		progressDialog.setOnCancelListener(new OnCancelListener() {
		
			public void onCancel(DialogInterface dialog) {
				loadNamesTask.cancel(true);
			}
		});

		progressDialog.show();
      
      
      
      
      //Start Async task to Load Grid Names
      loadNamesTask = new taskLoadGridNames();
      loadNamesTask.execute(null);
            
      //Assign onclick Listener
      assignItemClickListener();
	}
			
	
	
	/**
	 * Assigns On Click Listener to ListView. 
	 */
	private void assignItemClickListener()
	{
	      //Assign OnClick Listener to List Items
	      final ListView lv = (ListView)findViewById(R.id.listView);
	      lv.setTextFilterEnabled(true);
	      lv.setOnItemClickListener(new OnItemClickListener() {
	      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
	      		@SuppressWarnings("unchecked")
					HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);	
	      		
	      		
	      		
	      		//Name Selected
	      		Intent answer = new Intent(Intent.ACTION_VIEW);
	      		
	      		answer.putExtra("GridNm", o.get("GridName"));
	      		//loadTasks(o.get("GridName"),"last2Weeks"); //last3Days,last2Weeks
				setResult(RESULT_OK, answer);
				finish();
	      	
	      	}
			});
		
	}
	
	
	
    /**
     * Background thread to load and parse JSON. <br><br>
     * 
     * @author Parth Patel [parthpatel32@gmail.com]
     * @see android.os.AsyncTask
     */
    private class taskLoadGridNames extends AsyncTask<Void,Void,Void>
    {
    	/**
    	 * @see android.os.AsyncTask#doInBackground(Params[])
    	 * 
    	 */
    	@Override
    	protected Void doInBackground(Void... params)
    	{
    		JSONObject json = JSONFunctions.getJSONFromURL(getString(R.string.URL_JSON_GridName));
            
            try{
            	JSONArray usernm = json.getJSONArray("users");
            	for(int i=0;i<usernm.length();i++)
            	{
            		HashMap<String,String> map = new HashMap<String, String>();
            		JSONObject e = usernm.getJSONObject(i);
            		map.put("id", String.valueOf(i));
            		map.put("GridName",e.getString("GridName"));
            		
            		mylist.add(map);
            	}
            }catch (Exception e){
            	Log.e("GridNm_Picker", "Error parsing JSON: " + e.toString());
            }
			return null;
        }
    	
    	/**
    	 * Handles cancel request. 
    	 *  
    	 * @see android.os.AsyncTask#onCancelled()
    	 */
    	@Override
    	protected void onCancelled() {
    		Log.d("LoadGridNames","AsyncTask Cancelled!");
    		Toast.makeText(getApplicationContext(), "Loading Cancelled!", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    	/**
    	 * Method will be called automatically once <code>{@link #doInBackground(Void...)}</code> is complete.<br> 
    	 * In this instance the method creates <code>{@link android.widget.ListAdapter}</code> of Grid Names and sets it to {@link android.widget.ListView} 
    	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
    	 */
    	@Override
    	protected void onPostExecute(Void result) {    		
    		ListAdapter adapter =new SimpleAdapter(GridNamePicker.this,mylist,R.layout.username_item,new String[] {"GridName"},new int[] {R.id.item_title});
            ListView mainListView = (ListView)findViewById(R.id.listView);
            mainListView.setAdapter(adapter);
            progressDialog.dismiss();	
    	}
    }  

}
