package LocationMapper;
import java.io.File;

import org.joda.time.DateTime;

import LatLongParser.LatLongParser;
import TextParser.TextParser;


public class LocationMapper 
{
	public static String workDir = "";
	public static String logDir = "";
	public static String dataDir = "data";
	
	public DateTime startDateTime;;

	public SQLConnection sqlConnection;
	
	
	public LatLongParser latLongParser;
	public TextParser textParser;
	
	
	public LocationMapper(String[] args)
	{
		Log.doConsolePrint = true;
		
		String address = null;
		String tableName = null;
		String port = null;
		String userName = null;
		String password = null;
		
		

		try
		{
			address   = args[0];
			tableName  = args[1];
			port  = args[2];
			userName  = args[3];
			password = args[4];
			

			if(args.length > 5 && (args[5] != null || args[5] != "" || args[5] != "0"))
			{
				dataDir = args[5];
				if(new File(dataDir).exists() == false)
				{
					Log.log("ERROR: unable to verify workingDir path: " + dataDir);
					throw new Exception("");
				}
			}
		}
		catch (Exception e)
		{
			Log.log("args[] error:");
			Log.log("address = args[0]");
			Log.log("tableName = args[1]");
			Log.log("port = args[2]");
			Log.log("userName = args[3]");
			Log.log("password = args[4");
			Log.log("dataDir = args[5] - OPTIONAL defualt = " + dataDir);

			Log.log("Exiting...");
			Exit(1);
		}
		
		this.startDateTime = new DateTime();
		
		Log.log("Starting : " + startDateTime);
		Log.log("address = " + address);
		Log.log("tableName = " + tableName);
		Log.log("port = " + port);
		Log.log("userName = " + userName);
		Log.log("password = " + password);
		Log.log("pdataDir = " + dataDir);
		Log.log(Log.breakString);
		
		
		//test connection to server
//		sqlConnection = new SQLConnection(address, tableName, port, userName, password);//(String address, String tableName, String port, String userName, String password)
//		if(this.sqlConnection.Connect() == false)
//		{
//			Log.log("Unable to connecto to sql server");
//			Log.log("Exiting");
//			Exit(1);
//			return;
//		}
//		
		
		
		//load textParser
		textParser = new TextParser();
		textParser.loadText(dataDir);
		textParser.doProcessing(dataDir);
		
		
		
		
		
		
		//load LatLongParser
		latLongParser = new LatLongParser();
		latLongParser.loadData(dataDir);
		
		//get data from server
		
		
		
		
		
		
		
		Exit(0);
	}
	
	
	public String makeNice(String string)
	{
		
		
		
		
		return string;
	}
	
	
	public void Exit(int status)
	{
		Log.saveLog(logDir, "log_" + new DateTime().toString().replaceAll(":", ".") + ".log", false);
		System.exit(status);
	}
	
	
	public static void main(String[] args) 
	{
		
		try
		{
			new LocationMapper(args);
		}
		catch (Exception e)
		{
			Log.log("Critical ERROR: " + e  + ".  saving log and exiting...", e);
			Log.saveLog(logDir, "_CRITIAL_ERROR_LOG_" + new DateTime() + ".log", false);
			System.exit(3);
		}
	}

	
}
