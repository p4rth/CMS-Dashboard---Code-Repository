package cms.dashboard.ui;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class JSONFunctions {

	public static JSONObject getJSONFromURL(String url)
	{
		String result = "";
		JSONObject jArray = null;
		
		//HTTP get 
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(new URI(url));
			getRequest.addHeader("Accept", "application/json"); 
			HttpResponse getResponse = client.execute(getRequest);
			HttpEntity getResponseEntity = getResponse.getEntity();
			if (getResponseEntity != null) {
			result= EntityUtils.toString(getResponseEntity);}
			//Log.e("Results", result);
		}catch(Exception e){
			e.printStackTrace();
			Log.e("JSON_GET", "Error in HTTP Conn: "+ e.toString());
		}
		
		//Convert received 
		try{
			jArray = new JSONObject(result);	
		}catch(Exception e){
			Log.e("JSON_Parser", "Error Parsing JSON data: " +e.toString());
		}
		return jArray;
	}		
}
