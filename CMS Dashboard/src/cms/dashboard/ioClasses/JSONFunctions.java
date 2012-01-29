package cms.dashboard.ioClasses;

import java.net.URI;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class JSONFunctions {
	
	//This function checks for Data Connection.
	public static boolean checkDataConn(Context context)
	{
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info == null)
            return false;

        return connectivityManager.getActiveNetworkInfo().isConnected();
	}

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
		}
		catch(UnknownHostException e)
		{
			Log.e("JSON_GET", "Cannot find Host: "+e.toString());
			return jArray;
		}
		catch(Exception e){
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
