package cms.dashboard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class JobDetails extends Activity{

	private String TASK_NAME;
	private String TIME_RANGE;
	private String ID_IN_TASK;
	private String APPL_STATUS;
	private String APPL_EXIT_CODE;
	private String GRID_END_STATUS;
	private String RETRIES;
	private String SITE;
	private String SUBMITTED;
	private String STARTED;
	private String FINISHED;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.jobdetails);
	
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			TASK_NAME = extras.getString("taskName");
			TIME_RANGE = extras.getString("timeRange");
			ID_IN_TASK = extras.getString("idInTask");
			APPL_STATUS = extras.getString("applStatus");
			APPL_EXIT_CODE = extras.getString("applExitCode");
			GRID_END_STATUS = extras.getString("gridEndStatus");
			RETRIES = extras.getString("retries");
			SITE = extras.getString("site");
			SUBMITTED = extras.getString("submitted");
			STARTED = extras.getString("started");
			FINISHED = extras.getString("finished");
	    	
		}
		
		bindToListView();
	}

	private void bindToListView() {
		lv = (ListView) findViewById(R.id.jobDetailsListView);
		lv.
		
	}
}
