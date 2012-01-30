package cms.dashboard.feedModels;

import java.util.Collections;
import java.util.List;


public class JobsList {
	public List<JobsModel> jobs;
	
	public String plotParameters;
	public String username;

	public void sortByDate()
	{
		Collections.sort(jobs, JobsModel.Order.ByDate.dsc());
	}
	
}
