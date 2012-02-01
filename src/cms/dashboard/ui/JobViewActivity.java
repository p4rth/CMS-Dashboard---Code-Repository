package cms.dashboard.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import cms.dashboard.feedModels.JobsList;
import cms.dashboard.feedModels.JobsModel;
import cms.dashboard.ioClasses.JSONFunctions;

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

public class JobViewActivity extends Activity {
	
	private String GRID_NAME = "";
	private String TASK_NAME = "";
	private String TIME_RANGE = "";
	private ProgressDialog progressDialog;
	ArrayList<HashMap<String,String>> jobList = new ArrayList<HashMap<String,String>>();
	//private loadTasks loadJobsAsync;
	
	/* New vars */
	
	ArrayList<JobsModel> jobsArray = null;
	JobsAdapter jobsadapter;
	JobsList list;
	JobsSync loadJobThread;
	boolean enableMenuSort=false;
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobview);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			TASK_NAME = extras.getString("taskName");
			TIME_RANGE = extras.getString("timeRange");
		}
		
		//New code
		lv = (ListView)findViewById(R.id.JOBS_LIST);
		jobsArray = new ArrayList<JobsModel>();
		jobsadapter = new JobsAdapter(JobViewActivity.this,R.layout.job_list_item, jobsArray);
		
		lv.setTextFilterEnabled(true);
		lv.setAdapter(jobsadapter);


		getJobs();
	}
	
	private class JobsSync extends AsyncTask<String, Integer, JobsList>{

		protected JobsList doInBackground(String... urls) {
			JobsList list = null;
			
			try{
				JSONObject json = JSONFunctions.getJSONFromURL(urls[0]);
				String jsonStr = json.toString();		
				Gson gson = new Gson();
				//Log.e("test", jsonStr);
				list = gson.fromJson(jsonStr, JobsList.class);	
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
		protected void onPostExecute(JobsList jobslist)
		{
			if(jobslist.jobs.size()==0)
			{
				enableMenuSort = false;
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "No Tasks found", Toast.LENGTH_SHORT).show();
				return;
			}
			jobsArray.clear();
			for(JobsModel tm: jobslist.jobs)
			{
				jobsArray.add(tm);
			}
			jobsadapter.notifyDataSetChanged();
			enableMenuSort = true;
			progressDialog.dismiss();
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,	int position, long arg3) {
					showJobDetails(position);
				}	
			});
			Toast.makeText(getApplicationContext(), String.valueOf(jobsArray.size()) +" Jobs found", Toast.LENGTH_SHORT).show();
			
		}

		protected void onCancelled() {
    		Log.d("LoadTasks","AsyncTask Cancelled!");
		}		
		
	}
	
	private void showJobDetails(int position) {
		
		Intent jobDetailView = new Intent(Intent.ACTION_VIEW);
    	jobDetailView.setClassName(this, JobDetails.class.getName());
    	
    	//Pass selected variables to JobDetails
      	jobDetailView.putExtra("taskName", TASK_NAME);
    	jobDetailView.putExtra("timeRange", TIME_RANGE);
       	jobDetailView.putExtra("idInTask", jobsArray.get(position).EventRange + "");
    	jobDetailView.putExtra("applStatus", JobsAdapter.getStatus(jobsArray.get(position).STATUS));
    	jobDetailView.putExtra("applExitCode", jobsArray.get(position).JobExecExitCode + "");
    	jobDetailView.putExtra("gridEndStatus", jobsArray.get(position).GridEndId);
      	jobDetailView.putExtra("retries", jobsArray.get(position).resubmissions + "");
    	jobDetailView.putExtra("site", jobsArray.get(position).Site);
      	jobDetailView.putExtra("submitted", jobsArray.get(position).submitted);
    	jobDetailView.putExtra("started", jobsArray.get(position).started);
      	jobDetailView.putExtra("finished", jobsArray.get(position).finished);
    	
    	
    	startActivity(jobDetailView);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.job_menu, menu);
		return true;
	}
	
	
	/**
	 * Disable/Enable Sort MenuItem based on Tasks
	 * */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);	
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
				getJobs();
				break;
		}
		return true;
	}
	
	public void scrollToTop(View v)
	{
		lv.setSelection(0);
	}
	
	
	//Gets tasks for given parameters
	private void getJobs()
	{
		if(JSONFunctions.checkDataConn(getApplicationContext())== false)
		{
			Toast.makeText(getApplicationContext(), "Sorry, unable to connect to feed server.", Toast.LENGTH_LONG).show();
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("Loading Jobs. Please wait...");
		progressDialog.setOnCancelListener(new OnCancelListener() {
		
			public void onCancel(DialogInterface dialog) {
				loadJobThread.cancel(true);
	    		Toast.makeText(getApplicationContext(), "Cancelled!\n\nYou can Reload from Menu.", Toast.LENGTH_SHORT).show();
			}
		});
		
		progressDialog.show();
		
		try
		{
			loadJobThread = new JobsSync();
			loadJobThread.execute(createJsonURL(GRID_NAME, TIME_RANGE));
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					if(loadJobThread.getStatus() == AsyncTask.Status.RUNNING)
					{
						loadJobThread.cancel(true);
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
				Collections.sort(jobsArray,TasksModel.Order.ByTaskName.asc());
				break;
			case 1: //Sort by Date Oldest
				Collections.sort(jobsArray,TasksModel.Order.ByDate.asc());
				break;
			case 2: //Sort by Date Newest
				Collections.sort(jobsArray,TasksModel.Order.ByDate.dsc());
				break;
			case 3:	//Total Jobs Asc
				Collections.sort(jobsArray,TasksModel.Order.ByTotalJobs.asc());
				break;
			case 4: //Total Jobs Desc
				Collections.sort(jobsArray,TasksModel.Order.ByTotalJobs.dsc());
				break;				
		}
		
		Resources res = getResources();
		String strs[] = res.getStringArray(R.array.tasks_sort_values);
		
		jobsadapter.notifyDataSetChanged();
		lv.setSelection(0);
		Toast.makeText(getApplicationContext(), "Sorted by: "+strs[by], Toast.LENGTH_SHORT).show();
	
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
	
	private String createJsonURL(String _gridName, String _timeRange)
	{
		String _url = "";
		_url = "http://dashb-cms-job.cern.ch/dashboard/request.py/taskjobsjson?&timerange=" + TIME_RANGE + 
				"&what=all&taskmonid=" + TASK_NAME;
		Log.d("TaskURL", _url);
		return _url;
	}

}
