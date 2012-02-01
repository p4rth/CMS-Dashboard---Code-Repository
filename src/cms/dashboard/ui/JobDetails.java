package cms.dashboard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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
	//private ListView lv;

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
			GRID_END_STATUS = setGridStatus(extras.getString("gridEndStatus"));
			RETRIES = extras.getString("retries");
			SITE = extras.getString("site");
			SUBMITTED = editDateString(extras.getString("submitted"));
			STARTED = editDateString(extras.getString("started"));
			FINISHED = editDateString(extras.getString("finished"));	    	
		}
				
		bindToLables();
	}

	private String editDateString(String string) {
		
		if(string.startsWith("1970-01-01"))
			string = "Unknown";
		else
			string = string.replace("T", " at ");
		
		return string;
	}

	private String setGridStatus(String stat) {//any more than just U or D?

		if(stat.equals("U"))
			return "Unknown";
		else if(stat.equals("D"))
			return "Done";
		
		return stat;
	}

	private void bindToLables() {
		
		TextView tv = (TextView) findViewById(R.id.textView1Value);
		tv.setText(TASK_NAME);
		
		tv = (TextView) findViewById(R.id.textView2Value);
		tv.setText(TIME_RANGE);
		
		tv = (TextView) findViewById(R.id.textView3Value);
		tv.setText(ID_IN_TASK);

		tv = (TextView) findViewById(R.id.textView4Value);
		tv.setText(APPL_STATUS);
		
		tv = (TextView) findViewById(R.id.textView5Value);
		tv.setText(APPL_EXIT_CODE);
		
		tv = (TextView) findViewById(R.id.textView6Value);
		tv.setText(GRID_END_STATUS);
		
		tv = (TextView) findViewById(R.id.textView7Value);
		tv.setText(RETRIES);
		
		tv = (TextView) findViewById(R.id.textView8Value);
		tv.setText(SITE);
		
		tv = (TextView) findViewById(R.id.textView9Value);
		tv.setText(SUBMITTED);
		
		tv = (TextView) findViewById(R.id.textView10Value);
		tv.setText(STARTED);
		
		tv = (TextView) findViewById(R.id.textView11Value);
		tv.setText(FINISHED);
		
		
		
//		lv = (ListView) findViewById(R.id.jobDetailsListView);
//		ArrayList<String> jobsDetailsArray = new ArrayList<String>();
//		
//		populateArray(jobsDetailsArray);
//		
//		ArrayAdapter<String> jobsadapter = new ArrayAdapter<String>(JobDetails.this, R.layout.jobdetail, jobsDetailsArray);
//		
//		lv.setTextFilterEnabled(true);
//		lv.setAdapter(jobsadapter);		
	}

//	private void populateArray(ArrayList<String> jobsDetailsArray) {
//		jobsDetailsArray.add(TASK_NAME);
//		jobsDetailsArray.add(TIME_RANGE);
//		jobsDetailsArray.add(ID_IN_TASK);
//		jobsDetailsArray.add(APPL_STATUS);
//		jobsDetailsArray.add(APPL_EXIT_CODE);
//		jobsDetailsArray.add(GRID_END_STATUS);
//		jobsDetailsArray.add(RETRIES);
//		jobsDetailsArray.add(SITE);
//		jobsDetailsArray.add(SUBMITTED);
//		jobsDetailsArray.add(STARTED);
//		jobsDetailsArray.add(FINISHED);		
//	}
}
