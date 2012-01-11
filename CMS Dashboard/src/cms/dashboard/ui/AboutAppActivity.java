package cms.dashboard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AboutAppActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		String version = "---";
		try{
			version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}catch(Exception e){
			Log.e("Read_Manifest", "Error getting app version. Msg: "+e.toString());
		}
		
		TextView tv = (TextView)findViewById(R.id.about_version_no);
		tv.setText("v" + version);
	}
}
