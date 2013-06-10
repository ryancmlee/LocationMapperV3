import org.joda.time.DateTime;

import LocationMapper.LocationMapper;
import LocationMapper.Log;


public class EntryPoint
{


	public static void main(String[] args) 
	{
		
		try
		{
			new LocationMapper(args);
		}
		catch (Exception e)
		{
			Log.log("Critical ERROR: " + e  + ".  saving log and exiting...", e);
			Log.doLog = true;
			LocationMapper.Exit(-1);
		}
	}

}
