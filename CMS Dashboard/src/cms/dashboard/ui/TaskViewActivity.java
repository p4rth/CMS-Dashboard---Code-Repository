package cms.dashboard.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;



/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class TaskViewActivity extends Activity {
	
	private String GRID_NAME = "";
	private String TIME_RANGE = "";
	private ProgressDialog progressDialog;
	ArrayList<HashMap<String,String>> tasklist = new ArrayList<HashMap<String,String>>();
	private loadTasks loadTasksAsync;
	
	/* New vars */
	
	ArrayList<TasksModel> tasksArray = null;
	TasksAdapter tasksadapter;
	TasksList list;
	TasksSync loadTaskThread;
	boolean enableMenuSort=false;
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskview);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			GRID_NAME= extras.getString("GridName");
			TIME_RANGE = extras.getString("TimeRange");
		}
		
		//progressDialog = ProgressDialog.show(this,"","Loading Tasks for "+GRID_NAME);
		//new loadTasks().execute(null);				
		
		//getTasks();
		
		
		//New code
		lv = (ListView)findViewById(R.id.TASKS_LIST);
		tasksArray = new ArrayList<TasksModel>();
		tasksadapter = new TasksAdapter(TaskViewActivity.this,R.layout.task_list_item,tasksArray);
		
		lv.setTextFilterEnabled(true);
		lv.setAdapter(tasksadapter);


		getTasks();
	}
	
	private class TasksSync extends AsyncTask<String, Integer, TasksList>{

		protected TasksList doInBackground(String... urls) {
			TasksList list = null;
			
			try{
				JSONObject json = JSONFunctions.getJSONFromURL(urls[0]);
				JSONArray jsonarr = json.getJSONArray("tasks");
				//String jsonStr = "{\"tasks\":" + jsonarr.toString() + "}";
				String jsonStr = json.toString();				
				Gson gson = new Gson();
				list = gson.fromJson(jsonStr, TasksList.class);			
			}
			catch(JsonSyntaxException e)
			{
				Log.e("JSON_Syntax", e.toString());
			}
			catch(Exception e)
			{
				Log.e("Gson Parsing", e.toString());
			}						
			return list;
		}
		
		protected void onProgressUpdate(Integer... progress){
			
		}
		protected void onPostExecute(TasksList taskslist)
		{
			if(taskslist.tasks.size()==0)
			{
				enableMenuSort = false;
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "No Tasks found", Toast.LENGTH_SHORT).show();
				return;
			}
			tasksArray.clear();
			taskslist.sortByDate();
			for(TasksModel tm: taskslist.tasks)
			{
				tasksArray.add(tm);
			}
			tasksadapter.notifyDataSetChanged();
			enableMenuSort = true;
			progressDialog.dismiss();
			Toast.makeText(getApplicationContext(), String.valueOf(tasksArray.size()) +" Tasks found", Toast.LENGTH_SHORT).show();
		}

		protected void onCancelled() {
    		Log.d("LoadTasks","AsyncTask Cancelled!");
		}		
		
	}
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	
	/**
	 * Disable/Enable Sort MenuItem based on Tasks
	 * */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.getItem(1).setEnabled(enableMenuSort);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId())
		{
			case R.id.menu_about:
				startActivity(new Intent(this,AboutAppActivity.class));
				break;
			case R.id.menu_refresh:
				getTasks();
				break;
			case R.id.menu_sort:
				Resources res = getResources();
				String strs[] = res.getStringArray(R.array.tasks_sort_values);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Sort By:");
				builder.setItems(strs, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int item) {
						sortList(item);
					}
				} );
				AlertDialog alert = builder.create();
				alert.show();
				break;
		}
		return true;
	}
	
	public void scrollToTop(View v)
	{
		lv.setSelection(0);
	}
	
	
	//Gets tasks for given parameters
	private void getTasks()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("Loading Tasks. Please wait...");
		progressDialog.setOnCancelListener(new OnCancelListener() {
		
			public void onCancel(DialogInterface dialog) {
				loadTaskThread.cancel(true);
	    		Toast.makeText(getApplicationContext(), "Cancelled!\n\nYou can Reload from Menu.", Toast.LENGTH_SHORT).show();
			}
		});
		
		

		progressDialog.show();

//		OLD CODE
//		loadTasksAsync = new loadTasks();
//		loadTasksAsync.execute(null);
		try
		{
			loadTaskThread = new TasksSync();
			loadTaskThread.execute(createJsonURL(GRID_NAME, TIME_RANGE));
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					if(loadTaskThread.getStatus() == AsyncTask.Status.RUNNING)
					{
						loadTaskThread.cancel(true);
						progressDialog.dismiss();
						Toast.makeText(getApplicationContext(), "The Connection has timed out", Toast.LENGTH_SHORT).show();
					}
					
				}
			}, Integer.parseInt(getString(R.string.json_timeout)));
		}
		catch(Exception e)
		{
			Log.e("GsonPre", e.toString());
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void sortList(int by)
	{
		switch(by)
		{
			case 0:	//Sort by Task Name
				Collections.sort(tasksArray,TasksModel.Order.ByTaskName.asc());
				break;
			case 1: //Sort by Date Oldest
				Collections.sort(tasksArray,TasksModel.Order.ByDate.asc());
				break;
			case 2: //Sort by Date Newest
				Collections.sort(tasksArray,TasksModel.Order.ByDate.dsc());
				break;
			case 3:	//Total Jobs Asc
				Collections.sort(tasksArray,TasksModel.Order.ByTotalJobs.asc());
				break;
			case 4: //Total Jobs Desc
				Collections.sort(tasksArray,TasksModel.Order.ByTotalJobs.dsc());
				break;				
		}
		
		Resources res = getResources();
		String strs[] = res.getStringArray(R.array.tasks_sort_values);
		
		tasksadapter.notifyDataSetChanged();
		lv.setSelection(0);
		Toast.makeText(getApplicationContext(), "Sorted by: "+strs[by], Toast.LENGTH_SHORT).show();
	
	}
	
	
	
	
	
	private class loadTasks extends AsyncTask<Void,Void,JSONObject>
	{
		@Override
		protected JSONObject doInBackground(Void... params) {
			
			String jsonURL = createJsonURL(GRID_NAME,TIME_RANGE);
			
			JSONObject json = JSONFunctions.getJSONFromURL(jsonURL);
			tasklist.clear();
			
            try{
            	JSONArray usernm = json.getJSONArray("tasks");
            	for(int i=0;i<usernm.length();i++)
            	{
            		HashMap<String,String> map = new HashMap<String, String>();
            		JSONObject e = usernm.getJSONObject(i);
            		map.put("localId", String.valueOf(i));
            		map.put("TOTAL_NO", "Total: " + e.getString("NUMOFJOBS"));
            		map.put("SUCCESS_NO", "Successful: "+e.getString("SUCCESS"));
            		map.put("COMPLETED", "Completed: "+e.getString("SUCCDONE") + " out of " + e.getString("NUMOFJOBS"));
            		map.put("TASK_SCREEN_NAME", e.getString("TASKMONID"));
            		map.put("RUNNING_NO", "Running: "+ e.getString("RUNNING"));
            		map.put("TASK_SUBMIT_DATE", getElapsedTime(e.getString("TaskCreatedTimeStamp")));
            		map.put("PENDING_NO", "Pending: " + e.getString("PENDING"));
            		map.put("UNKNOWN_NO", "Unknown: " + e.getString("UNKNOWN"));
            		map.put("FAILED_NO", "Failed: "+e.getString("FAILED"));
            		tasklist.add(map);
            	}
            }catch (Exception e){
            	Log.e("TASK_List", "Error parsing JSON: " + e.toString());
            }			
			return json;
		}
		
		public String getElapsedTime(String dt) {
			dt = dt.substring(0,dt.indexOf("T"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date created;
			try{
				created = formatter.parse(dt);
			}
			catch(Exception e){
				Log.e("Date_Diff", "Cannot convert string to Date. || " + e.toString());
				return dt;
			}
		    long duration = System.currentTimeMillis() - created.getTime();
		    long days = TimeUnit.MILLISECONDS.toDays(duration);
		    if (days > 0) {
		        return days + " days ago";
		    }
		    if(days == 0)
		    {
		    	return "Today";
		    }
		    return dt;
		}

		
		@Override
		protected void onPostExecute(JSONObject json)
		{
			
			if(tasklist.size()>0)
			{
				//TASK_SCREEN_NAME
				Collections.sort(tasklist, new MapComparator("TASK_SUBMIT_DATE"));
				
				ListAdapter adapter =new SimpleAdapter(TaskViewActivity.this,
	   							tasklist,R.layout.task_list_item,
	   							new String[] {"TOTAL_NO","SUCCESS_NO","COMPLETED","TASK_SCREEN_NAME","RUNNING_NO","TASK_SUBMIT_DATE","PENDING_NO","UNKNOWN_NO","FAILED_NO"},
	   							new int[] {R.id.TOTAL_NO,R.id.SUCCESS_NO,R.id.COMPLETED,R.id.TASK_SCREEN_NAME,R.id.RUNNING_NO,R.id.TASK_SUBMIT_DATE,R.id.PENDING_NO,R.id.UNKNOWN_NO,R.id.FAILED_NO});
	
				
				ListView mainListView = (ListView)findViewById(R.id.TASKS_LIST);
				mainListView.setAdapter(adapter);								
				final ListView lv = (ListView)findViewById(R.id.TASKS_LIST);
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(new OnItemClickListener() {
			  	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
			  		@SuppressWarnings("unchecked")
						HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);	
			  		
			  		Toast.makeText(getApplicationContext(), 
			  						o.get("TASK_SCREEN_NAME") + "\n\n" + o.get("COMPLETED"), 
			  						Toast.LENGTH_SHORT).show();
			  		
			  		//TODO implement Jobs View
			  		//TODO enable following code to load jobs 
			  	      		//Intent jobsActivity = new Intent(Intent.ACTION_VIEW);
			  	      		//jobsActivity.setClass(this, jobactivityclass.class.getName());	      		
			  	      		//jobsActivity.putExtra("TASK_NAME", o.get("TASK_SCREEN_NAME"));
			  	      		//startActivity(jobsActivity);	      			      		
			  	}
				});
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), String.valueOf(tasklist.size()) +" Tasks found", Toast.LENGTH_SHORT).show();
			}
			//No Tasks Found
			else
			{
				ArrayList<HashMap<String,String>> noTasks = new ArrayList<HashMap<String,String>>();
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("No_Tasks",getString(R.string.no_tasks_msg));
				noTasks.add(map);
				ListAdapter adapter = new SimpleAdapter(TaskViewActivity.this,
												noTasks,R.layout.no_tasks_item,
												new String[]{"No_Tasks"},
												new int[]{R.id.no_tasks_found});
				ListView mainListView = (ListView)findViewById(R.id.TASKS_LIST);
				mainListView.setAdapter(adapter);
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "No Tasks found", Toast.LENGTH_SHORT).show();								
			}
		}
		
		
		@Override
		protected void onCancelled() {
    		Log.d("LoadTasks","AsyncTask Cancelled!");
    		Toast.makeText(getApplicationContext(), "Cancelled!\n\nYou can Reload from Menu.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	private String createJsonURL(String _gridName, String _timeRange)
	{
		String _url = "";
		_url = "http://dashb-cms-job.cern.ch/dashboard/request.py/taskstablejson?&typeofrequest=A&timerange="+ 
					_timeRange + "&usergridname="+ _gridName;		
		Log.d("TaskURL", _url);
		return _url;
	}

}
