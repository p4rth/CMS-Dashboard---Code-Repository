package cms.dashboard.ioClasses;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Parth Patel [parthpatel32@gmail.com]
 *
 */
public class MapComparator implements Comparator<Map<String, String>>{

	private final String key;
	
	public MapComparator(String key)
	{
		this.key = key;
	}
	
	public int compare(Map<String, String> first, 
						Map<String, String> second) 
	{
		String firstValue = first.get(key);
		String secondValue = second.get(key);
		return firstValue.compareTo(secondValue);
	}
}
