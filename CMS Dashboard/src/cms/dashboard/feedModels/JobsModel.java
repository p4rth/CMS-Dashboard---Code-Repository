package cms.dashboard.feedModels;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.util.Log;


public class JobsModel {
	public int NUMOFJOBS;
    public String INPUTCOLLECTION;
    public int SUCCESS;
    public int SUCCDONE;
    public String TASKMONID;
    public int RUNNING;
    public String TaskCreatedTimeStamp;
    public int PENDING;
    public int UNKNOWN;
    public String TASKID;    
    public int FAILED;
    
    public static enum Order implements Comparator<JobsModel>{
    	ByDate(){
			public int compare(JobsModel first, JobsModel second) {
			    Date date1, date2;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.FF");
				try{
					date1 = formatter.parse(first.TaskCreatedTimeStamp);
					date2 = formatter.parse(second.TaskCreatedTimeStamp);
				}
				catch(Exception e){
					Log.e("Date_Diff", "Cannot convert string to Date. || " + e.toString());
					return 1;
				}

				return date1.compareTo(date2);
			}

			@Override
			public int compare(TasksModel lhs, TasksModel rhs) {
				// TODO Auto-generated method stub
				return 0;
			}
    	},
    	ByTotalJobs()
    	{

			@Override
			public int compare(JobsModel lhs1, JobsModel rhs1) {
				return new Integer(lhs1.NUMOFJOBS).compareTo(new Integer(rhs1.NUMOFJOBS));
			}

			@Override
			public int compare(TasksModel lhs, TasksModel rhs) {
				return new Integer(lhs.NUMOFJOBS).compareTo(new Integer(rhs.NUMOFJOBS));
			}
    		
    	},
    	ByTaskName()
    	{
			public int compare(JobsModel lhs, JobsModel rhs) {
				return lhs.TASKMONID.compareTo(rhs.TASKMONID);
			}

			@Override
			public int compare(TasksModel lhs, TasksModel rhs) {
				// TODO Auto-generated method stub
				return 0;
			}
    		
    	};
    	
    	public abstract int compare(TasksModel lhs, TasksModel rhs);
    	
    	public int compare(JobsModel lhs1, JobsModel rhs1) {
			// TODO Auto-generated method stub
			return 0;
		}

		public Comparator asc()
    	{
    		return this;
    	}
    	
    	public Comparator dsc()
    	{
    		return Collections.reverseOrder(this);
    	}
    	
    }
    
}
