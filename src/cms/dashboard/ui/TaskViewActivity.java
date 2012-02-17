package cms.dashboard.ui;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONObject;

import cms.dashboard.feedModels.TasksList;
import cms.dashboard.feedModels.TasksModel;
import cms.dashboard.ioClasses.JSONFunctions;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
//	ArrayList<HashMap<String,String>> tasklist = new ArrayList<HashMap<String,String>>();
	GoogleAnalyticsTracker tracker;
	
	/* New vars */
	
	ArrayList<TasksModel> tasksArray = null;
	TasksAdapter tasksadapter;
	TasksList list;
	TasksSync loadTaskThread;
	boolean enableMenuSort=false;
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskview);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			GRID_NAME= extras.getString("GridName");
			TIME_RANGE = extras.getString("TimeRange");
		}
		
		//New code
		lv = (ListView)findViewById(R.id.TASKS_LIST);
		tasksArray = new ArrayList<TasksModel>();
		tasksadapter = new TasksAdapter(TaskViewActivity.this,R.layout.task_list_item,tasksArray);
		
		lv.setTextFilterEnabled(true);
		lv.setAdapter(tasksadapter);


		getTasks();
        tracker.startNewSession("UA-26300809-1",20, this);
        tracker.trackPageView("/TaskViewActivity");

	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
    	tracker.dispatch();
    	tracker.stopSession();
	}
	
	private class TasksSync extends AsyncTask<String, Integer, TasksList>{
		
		protected TasksList doInBackground(String... urls) {
			TasksList list = null;
			
			try{
				JSONObject json = JSONFunctions.getJSONFromURL(urls[0]);
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
			tracker.trackEvent("TaskViewActivity", "Completed", "Fetch Tasks from JSON", 0);
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,	int position, long arg3) {
					showJobs(position);
				}			
			});
		}

		protected void onCancelled() {			
    		Log.d("LoadTasks","AsyncTask Cancelled!");
		}		
		
	}
			
	private void showJobs(int index) {
		Intent jobView = new Intent(Intent.ACTION_VIEW);
    	jobView.setClassName(this, JobViewActivity.class.getName());
    	
    	//Pass selected user name and time range to TaskViewActivity
    	jobView.putExtra("taskName", tasksArray.get(index).TASKMONID.toString());
    	jobView.putExtra("timeRange", TIME_RANGE);
    	startActivity(jobView);
    	tracker.trackEvent("TaskViewActivity", "Clicked", "Task Item - Opening Jobs View", 0);
		
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
				tracker.trackEvent("AboutAppActivity", "Visited", "TaskViewActivity", 0);
				break;
			case R.id.menu_refresh:
				getTasks();
				tracker.trackEvent("TaskViewActivity", "Completed", "Tasks Refresh", 0);
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
		//Check for Data Connection before starting Async Thread. 
		if(JSONFunctions.checkDataConn(getApplicationContext())== false)
		{
			Toast.makeText(getApplicationContext(), "Sorry, unable to connect to feed server.", Toast.LENGTH_LONG).show();
			return;
		}
		
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

		try
		{
			loadTaskThread = new TasksSync();
			loadTaskThread.execute(createJsonURL(GRID_NAME, TIME_RANGE));
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					
					if(loadTaskThread.getStatus() == AsyncTask.Status.RUNNING && loadTaskThread.isCancelled() == false)
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
				tracker.trackEvent("TaskViewActivity", "Sorted", "Tasks - Sort by Name", 0);
				break;
			case 1: //Sort by Date Oldest
				Collections.sort(tasksArray,TasksModel.Order.ByDate.asc());
				tracker.trackEvent("TaskViewActivity", "Sorted", "Tasks - Sort by Date Oldest", 0);
				break;
			case 2: //Sort by Date Newest
				Collections.sort(tasksArray,TasksModel.Order.ByDate.dsc());
				tracker.trackEvent("TaskViewActivity", "Sorted", "Tasks - Sort by Date Newest", 0);
				break;
			case 3:	//Total Jobs Asc
				Collections.sort(tasksArray,TasksModel.Order.ByTotalJobs.asc());
				tracker.trackEvent("TaskViewActivity", "Sorted", "Tasks - Sort by Total Jobs Asc", 0);
				break;
			case 4: //Total Jobs Desc
				Collections.sort(tasksArray,TasksModel.Order.ByTotalJobs.dsc());
				tracker.trackEvent("TaskViewActivity", "Sorted", "Tasks - Sort by Total Jobs Desc", 0);
				break;				
		}
		
		Resources res = getResources();
		String strs[] = res.getStringArray(R.array.tasks_sort_values);
		
		tasksadapter.notifyDataSetChanged();
		lv.setSelection(0);
		Toast.makeText(getApplicationContext(), "Sorted by: "+strs[by], Toast.LENGTH_SHORT).show();
	
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
