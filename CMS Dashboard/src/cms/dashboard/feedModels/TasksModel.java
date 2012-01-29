package cms.dashboard.feedModels;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.util.Log;


/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class TasksModel {
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
    
    public static enum Order implements Comparator<TasksModel>{
    	ByDate(){
			public int compare(TasksModel first, TasksModel second) {
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
    	},
    	ByTotalJobs()
    	{

			@Override
			public int compare(TasksModel lhs, TasksModel rhs) {
				return new Integer(lhs.NUMOFJOBS).compareTo(new Integer(rhs.NUMOFJOBS));
			}
    		
    	},
    	ByTaskName()
    	{

			@Override
			public int compare(TasksModel lhs, TasksModel rhs) {
				return lhs.TASKMONID.compareTo(rhs.TASKMONID);
			}
    		
    	};
    	
    	public abstract int compare(TasksModel lhs, TasksModel rhs);
    	
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
