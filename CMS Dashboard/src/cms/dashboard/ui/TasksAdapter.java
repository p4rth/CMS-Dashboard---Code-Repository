package cms.dashboard.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cms.dashboard.feedModels.TasksModel;
import cms.dashboard.ui.R;

/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class TasksAdapter extends ArrayAdapter {
	int resource;
	String response;
	Context context;
	private LayoutInflater mInFlater;
	
	public TasksAdapter(Context context, int resource, List objects){
		super(context, resource,objects);
		this.resource = resource;
		mInFlater = LayoutInflater.from(context);
	}
	
	static class ViewHolder{
		TextView totalJobs;
		TextView successJobs;
		TextView completedJobs;
		TextView taskName;
		TextView runnigJobs;
		TextView submitDate;
		TextView pendingJobs;
		TextView unknownJobs;
		TextView failedJobs;
	}
	public View getView(int position,View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		TasksModel tm = (TasksModel)getItem(position);
		
		if(convertView==null)
		{
			convertView = mInFlater.inflate(R.layout.task_list_item, null);
			holder = new ViewHolder();
			
			holder.totalJobs = (TextView)convertView.findViewById(R.id.TOTAL_NO);
			holder.successJobs = (TextView)convertView.findViewById(R.id.SUCCESS_NO);
			holder.completedJobs = (TextView)convertView.findViewById(R.id.COMPLETED);
			holder.taskName = (TextView)convertView.findViewById(R.id.TASK_SCREEN_NAME);
			holder.runnigJobs = (TextView)convertView.findViewById(R.id.RUNNING_NO);
			holder.submitDate = (TextView)convertView.findViewById(R.id.TASK_SUBMIT_DATE);
			holder.pendingJobs = (TextView)convertView.findViewById(R.id.PENDING_NO);
			holder.unknownJobs = (TextView)convertView.findViewById(R.id.UNKNOWN_NO);
			holder.failedJobs = (TextView)convertView.findViewById(R.id.FAILED_NO);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.totalJobs.setText("Total: "+tm.NUMOFJOBS);
		holder.successJobs.setText("Successful: "+tm.SUCCESS);
		holder.completedJobs.setText(getStatus(tm.SUCCDONE, tm.NUMOFJOBS));
//		if(holder.completedJobs.getText() == "Done"){
//			holder.completedJobs.setBackgroundColor(getStatusBg(tm.SUCCDONE, tm.NUMOFJOBS));
//		}
		holder.taskName.setText(tm.TASKMONID);
		holder.runnigJobs.setText("Running: "+tm.RUNNING);
		holder.submitDate.setText(getElapsedTime(tm.TaskCreatedTimeStamp));
		holder.pendingJobs.setText("Pending: "+tm.PENDING);
		holder.unknownJobs.setText("Unknown: "+tm.UNKNOWN);
		holder.failedJobs.setText("Failed: "+tm.FAILED);
		
		return convertView;
	}
	
	private String getStatus(int _succDone, int _totalJobs)
	{
		if(_succDone == _totalJobs)
		{
			return "Done";
		}
		else
		{
			return "Completed: " + _succDone + " out of " + _totalJobs;
		}
				
	}
	
	private int getStatusBg(int _succDone,int _totalJobs)
	{
		if(_succDone == _totalJobs)
		{
			return Color.parseColor("#98CB98");
		}
		else
		{
			return Color.parseColor("#00ffff");
		}
	}
	
	public static String getElapsedTime(String dt) {
	    Date created;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.FF");
		try{
			created = formatter.parse(dt);
		}
		catch(Exception e){
			Log.e("Date_Diff", "Cannot convert string to Date. || " + e.toString());
			return dt;
		}

		long duration = System.currentTimeMillis() - created.getTime();
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
	    long days = seconds/86400;//TimeUnit.MILLISECONDS.toDays(duration);
	    long minutes = seconds/60;//TimeUnit.MILLISECONDS.toMinutes(duration);
	    long hours = minutes/60;//TimeUnit.MILLISECONDS.toHours(duration);
	    if (days > 0) {
	        return days + " Days";
	    }
	    if (hours > 0) {
	        return hours + " Hrs";
	    }
	    if (minutes > 0) {
	        return minutes + " Mins";
	    }

	    return seconds + " Secs";
	}

	
	
	
}
