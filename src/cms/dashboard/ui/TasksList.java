package cms.dashboard.ui;

import java.util.Collections;
import java.util.List;

public class TasksList {
	public List<TasksModel> tasks;
	
	public String plotParameters;
	public String username;

	public void sortByDate()
	{
		Collections.sort(tasks, TasksModel.Order.ByDate.dsc());
	}
	
}
