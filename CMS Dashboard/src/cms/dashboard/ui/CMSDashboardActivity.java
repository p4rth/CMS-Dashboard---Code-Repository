package cms.dashboard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


public class CMSDashboardActivity extends Activity {
	
	private String gridName, timeRange;
	private Boolean bGridNm, bTimeRange;
	static final private int CHOOSE_GRIDNAME = 0;	//Activity Request Code, used in selectGridNm
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startmain);
        bGridNm = bTimeRange = false;
        
        loadTimeRange();
        
        loadPreference();       
    }
    
    //Load Menu for activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_main, menu);
    	
    	//Hide Refresh and Sort Item
    	menu.getItem(0).setVisible(false);
    	menu.getItem(0).setEnabled(false);
    	menu.getItem(1).setVisible(false);
    	menu.getItem(1).setEnabled(false);
    	
    	return true;    	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId())
    	{
    		case R.id.menu_about:
    			this.startActivity(new Intent(this,AboutAppActivity.class));
    			break;
    	}
    	return true;
    }
        
    //Read Preferences
    //Also adjusts UI based on values retrieved
    private void loadPreference ()
    {
    	boolean _nmSaved;
    	_nmSaved = PreferenceConnector.readBoolean(this, PreferenceConnector.Saved_Name, false);
    	if(_nmSaved == true)
    	{
    		gridName = PreferenceConnector.readString(this, PreferenceConnector.Name, null);
			bGridNm = true;
			Button btn = (Button)findViewById(R.id.btn_gridname);
			btn.setText(gridName);
			CheckBox checkbox = (CheckBox)findViewById(R.id.checkRememberNm);
			checkbox.setChecked(true);
    	}
    }
    
    //Save selected GridName in Preferences
    private void savePrefGridName()
    {
    	if(bGridNm != false)
    	{
    		PreferenceConnector.writeString(this, PreferenceConnector.Name, gridName);
    		PreferenceConnector.writeBoolean(this, PreferenceConnector.Saved_Name, true);
    	}
    	
    }
    
    //Clear save preferences from Preference file
    private void removePrefGridName()
    {
    	PreferenceConnector.getEditor(this).remove(PreferenceConnector.Saved_Name).commit();
    	PreferenceConnector.getEditor(this).remove(PreferenceConnector.Name).commit();
    }
    
    
    //OnClick Listener for Remember Name CheckBox
    public void rememberName(View v)
    {
    	if(((CheckBox)v).isChecked())	{ if(bGridNm == true){savePrefGridName();} }
    	else { removePrefGridName();}
    }
    
    
    private void loadTimeRange()
    {
    	//First load items
    	//Values are defined as String Array in strings.xml. 
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTimeRange);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.date_prompt_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Assign OnClickListener to TimeRange Spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				switch((int)id)
				{
					//lastDay, last2Days, last3Days, lastWeek, last2Weeks, lastMonth 
					case 0:
						timeRange = "lastDay";
						bTimeRange = true;
						break;
					case 1:
						timeRange = "last2Days";
						bTimeRange = true;
						break;
					case 2:
						timeRange = "last3Days";
						bTimeRange = true;
						break;
					case 3:
						timeRange = "lastWeek";
						bTimeRange = true;
						break;
					case 4:
						timeRange = "last2Weeks";
						bTimeRange = true;
						break;
					case 5:
						timeRange = "lastMonth";
						bTimeRange = true;
						break;
				}															
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Not needed! Optional method, but must exit in code. 
				// Refere to Android Docs for more info.
			}
			});
    }
    
    //OnClick Listener for Select User Name button
    public void selectGridNm(View v)
    {
    	Intent getGridNm = new Intent(this, GridNamePicker.class);    	
    	startActivityForResult(getGridNm, CHOOSE_GRIDNAME);
    }
    
    
    //Check for returned results from Select Grid Name activity. 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	//Check that the right activity has returned results
    	if(requestCode == CHOOSE_GRIDNAME)
    	{
    		//Check if results was ok?
    		if(resultCode == RESULT_OK)
    		{
    			gridName = data.getExtras().getString("GridNm");
    			bGridNm = true;
    			Button btn = (Button)findViewById(R.id.btn_gridname);
    			btn.setText(gridName);

    			//Save Selected name if Remember Name is ticked.
    			CheckBox checkbox = (CheckBox)findViewById(R.id.checkRememberNm);
        		if(checkbox.isChecked()==true){ savePrefGridName(); }

    		}
    	}
    	
    }
    
    
    //Load TaskViewActivity. 
    public void loadTasks(View v)
    {
    	//Check if both GridName and TimeRange are selected
    	if(bGridNm==true && bTimeRange==true)
    	{
	    	Intent taskView= new Intent(Intent.ACTION_VIEW);
	    	taskView.setClassName(this, TaskViewActivity.class.getName());
	    	
	    	//Pass selected user name and time range to TaskViewActivity
	    	taskView.putExtra("GridName", gridName);
	    	taskView.putExtra("TimeRange",timeRange);
	    	startActivity(taskView);
    	}
    	else
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Please select a User to continue.")
    			   .setTitle("Error")
    		       .setCancelable(false)
    		       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		AlertDialog alert = builder.create();
    		alert.show();
    	}
    	
    }
    
    
     
}