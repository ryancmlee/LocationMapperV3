import org.joda.time.DateTime;

import LocationMapper.Mapper;
import LocationMapper.Log;


public class EntryPoint
{


	public static void main(String[] args) 
	{
		
		try
		{
			new Mapper(args);
		}
		catch (Exception e)
		{
			Log.log("Critical ERROR: " + e  + ".  saving log and exiting...", e);
			Log.saveLog(Mapper.logDir, "_CRITIAL_ERROR_LOG_" + new DateTime() + ".log", false);
			System.exit(3);
		}
	}

}
