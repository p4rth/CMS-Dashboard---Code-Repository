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

import cms.dashboard.feedModels.JobsModel;
import cms.dashboard.feedModels.TasksModel;
import cms.dashboard.ui.R;

public class JobsAdapter extends ArrayAdapter {
	/**
	 * Author Ben Taliadoros [ee08bbt@brunel.ac.uk]
	 */
	int resource;
	String response;
	Context context;
	private LayoutInflater mInFlater;
	
	public int idInTask;
	public String applStatus;
	public int applExitCode;
	public String gridEndStatus;
	public int retries;
	public String site;
	public String submitted;
	public String started;
	public String finished;
	
	public JobsAdapter(Context context, int resource, List objects){
		super(context, resource,objects);
		this.resource = resource;
		mInFlater = LayoutInflater.from(context);
	}
	
	static class ViewHolder{
		TextView idInTask;
		TextView applStatus;
	}
	public View getView(int position,View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		JobsModel jm = (JobsModel)getItem(position);
		//TasksModel tm = (TasksModel)getItem(position);
		if(convertView==null)
		{
			convertView = mInFlater.inflate(R.layout.job_list_item, null);
			holder = new ViewHolder();
			
			holder.idInTask = (TextView)convertView.findViewById(R.id.ID_IN_TASK);
			holder.applStatus = (TextView)convertView.findViewById(R.id.STATUS);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.idInTask.setText("ID: "+ jm.EventRange);
		holder.applStatus.setText("Status: " + getStatus(jm.STATUS));
		holder.applStatus.setTextColor(getStatusColour(jm.STATUS));

		assignAttributes(jm);
		
		return convertView;
	}
	
	private void assignAttributes(JobsModel jm) {
		applExitCode = jm.JobExecExitCode;
		gridEndStatus = jm.STATUS;
		retries = jm.resubmissions;
		site = jm.Site;
		submitted = jm.submitted;
		started = jm.started;
		finished = jm.finished;
	}

	public static String getStatus(String _value)
	{
		if(_value.equals("S"))
		{
			return "Successful";
		}
		else if (_value.equals("R"))
		{
			return "Running";
		}
		else if(_value.equals("F"))
		{
			return "Failed";
		}
		else if(_value.equals("U"))
		{
			return "Unknown";
		}
		else if(_value.equals("P"))
		{
			return "Pending";
		}
		return _value;
				
	}
	private int getStatusColour(String _value)
	{
		if(_value.equals("S"))
		{
			return Color.parseColor("#98CB98");
		}
		else if (_value.equals("R"))
		{
			return Color.parseColor("#CCCCFE");
		}
		else if(_value.equals("F"))
		{
			return Color.parseColor("#FF0000");
		}
		else if(_value.equals("U"))
		{
			return Color.parseColor("#DDFEAA");
		}
		else if(_value.equals("P"))
		{
			return Color.parseColor("#FEFE98");
		}
		return Color.parseColor("#FFFFFF");
				
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
	    long days = seconds/86400;
	    long minutes = seconds/60;
	    long hours = minutes/60;

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
