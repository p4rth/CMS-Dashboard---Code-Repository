package cms.dashboard.ioClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * This class simplifies  
 * the interaction with Application preferences.
 * <br><br>
 * For more information on Preferences refer to {@link android.content.SharedPreferences}
 * 
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class PreferenceConnector {
	
	
	/**
	 * Preference file name
	 */
	public static final String PREF_NAME = "APP_PREF";
	
	
	/**
	 * Preference Operating Mode. 
	 * @see Context#MODE_PRIVATE
	 */
	public static final int MODE = Context.MODE_PRIVATE;
	
	
	/**
	 * Preference Key
	 */
	public static final String Name = "GRIDNAME";
	/**
	 * Preference Key
	 */
	public static final String Saved_Name = "savedGridNm";	// this key indicates if user name is saved or not

	
	/**
	 * Retrieves all Preferences from File.
	 * @param context ({@link Context})
	 * @return All saved Preferences from {@link SharedPreferences}
	 */
	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}
		
	
	/**
	 * Returns an instant of {@link Editor} through which Preferences can be modified.
	 * 
	 * @param context ({@link Context})
	 * @return Returns Preferences {@link Editor}
	 */
	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}
	
	
	/**
	 * 
	 * Writes given {@link Boolean} value to Preference file.
	 * 
	 * @param context ({@link Context})
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 */
	public static void writeBoolean(Context context, String key, boolean value)
	{
		getEditor(context).putBoolean(key, value).commit();
	}

	
	/**
	 * 
	 * Retrieves {@link Boolean} value from Preference file.
	 * 
	 * @param context ({@link Context})
	 * @param key The name of the preference to read.
	 * @param defValue Value to return if key does not exist in file.
	 * @return Returns the preference value if it exists, or defValue.
	 */
	public static boolean readBoolean(Context context, String key, boolean defValue) {
		return getPreferences(context).getBoolean(key, defValue);
	}
	
	/**
	 * 
	 * Writes given {@link String} value to Preference file.
	 * 
	 * @param context ({@link Context})
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 */
	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}
	
	/**
	 * 
	 * Retrieves {@link String} value from Preference file.
	 * 
	 * @param context ({@link Context})
	 * @param key The name of the preference to read.
	 * @param defValue Value to return if key does not exist in file.
	 * @return Returns the preference value if it exists, or defValue.
	 */
	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}
}
