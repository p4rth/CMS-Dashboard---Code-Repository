package cms.dashboard.feedModels;

import java.util.Collections;
import java.util.List;


/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class TasksList {
	public List<TasksModel> tasks;
	
	public String plotParameters;
	public String username;

	public void sortByDate()
	{
		Collections.sort(tasks, TasksModel.Order.ByDate.dsc());
	}
	
}
