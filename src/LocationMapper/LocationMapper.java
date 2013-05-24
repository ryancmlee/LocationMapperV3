package LocationMapper;


import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;

import org.joda.time.DateTime;

import LatLongParser.Column;
import LatLongParser.LatLongParser;
import TextParser.Location;
import TextParser.PartManager;
import TextParser.TextParser;


public class LocationMapper 
{
	public static String workDir = "";
	public static String logDir = "";
	public static String dataDir = "data";
	
	public DateTime startDateTime;;

	public SQLConnection sqlConnection;
	
	
	public LatLongParser latLongParser;
	//public TextParser textParser;
	public PartManager partManager;
	
	
	
	
	
	public LocationMapper(String[] args)
	{
		Log.doConsolePrint = true;
		
		String address = null;
		String serverName = null;
		String port = null;
		String userName = null;
		String password = null;
		
		

		try
		{
			address   = args[0];
			serverName  = args[1];
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
			Log.log("serverName = args[1]");
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
		Log.log("tableName = " + serverName);
		Log.log("port = " + port);
		Log.log("userName = " + userName);
		Log.log("password = " + password);
		Log.log("pdataDir = " + dataDir);
		Log.log(Log.breakString);
		
		
		//test connection to server
		sqlConnection = new SQLConnection(address, serverName, port, userName, password);//(String address, String tableName, String port, String userName, String password)
		if(this.sqlConnection.Connect() == false)
		{
			Log.log("Unable to connecto to sql server");
			Log.log("Exiting");
			Exit(1);
			return;
		}
		
		
	
			//load textParser
			TextParser textParser = new TextParser();
			textParser.loadText(dataDir);
			textParser.CreateMasterOut(dataDir);

	
		
		
		//build partmap
		partManager = new PartManager(textParser.allLoc, "[, ]");
		
		
		

		//load LatLongParser
		latLongParser = new LatLongParser();
		latLongParser.loadData(dataDir);
		
	
		
		//get data from server
		ResultSet results = sqlConnection.getData();
		
		//Map Locations
		MapLocations(results);
		
	
		
		
		
		
		Exit(0);
	}
	
	

	
	public void MapLocations(ResultSet results)
	{
	
		
		
		try
		{
			
			while(results.next())
			{

				int id = results.getInt(1);
				float latitude = (float)results.getDouble(2);
				float longitude = (float)results.getDouble(3);
				String twitter_user_location = results.getString(4);
				twitter_user_location = makeNice(twitter_user_location);	
				String twitter_user_lang = results.getString(5);
				

				Record record = new Record(id, latitude, longitude, twitter_user_location, twitter_user_lang);
				
				
				latLongParser.setLocation(record);
				partManager.setMatchStrings(record);
				
		
				
				
				HashMap<String, Location> cities = new HashMap<String, Location>();
				HashMap<String, Location> countires = new HashMap<String, Location>();
				HashMap<String, Location>  states = new HashMap<String, Location>();
				
				for (Location loc : record.possableLocations)
				{
					loc.hits = 0;
					if(loc.column == Column.city)
					{
//						if(loc.countryCode == "us" && record.twitter_user_lang == "es")
//						{
//							
//						}
//						else
//						{
							cities.put(loc.getKey(), loc);
//						}
							
					}
					else if(loc.column == Column.state_province)
					{
						if(loc.countryCode == "us" && record.twitter_user_lang == "es")
//						{
//							
//						}
//						else
//						{
							states.put(loc.getKey(), loc);
//						}
						
					}
					else if(loc.column == Column.country)
						countires.put(loc.getKey(), loc);
				}
				
				
				
				for (Location loc : cities.values())
				{
					if(states.containsKey(loc.getStateKey()))// we have a cityLocation that is in a states we also have
					{
						loc.hits++;
						states.get(loc.getStateKey()).hits++;
					}
					if(countires.containsKey(loc.getCountryKey()))// we have a countiresLocation that is in a states we also have
					{
						loc.hits++;
						countires.get(loc.getCountryKey()).hits++;
					}
				}
				for (Location loc : states.values())
				{
					if(countires.containsKey(loc.getCountryKey()))// we have a cityLocation that is in a states we also have
					{
						loc.hits++;
						countires.get(loc.getCountryKey()).hits++;
					}
				}
				
	
				long pop = -1;
				Location highestPop = null;
				for (Location loc : cities.values())
				{
					if(loc.population > pop)
					{
						highestPop = loc;
						pop = loc.population;
					}
				}
				if(highestPop != null)
					record.locData.put(Column.city, highestPop);
				
				
				pop = -1;
				highestPop = null;
				for (Location loc : states.values())
				{
					if(loc.population > pop)
					{
						highestPop = loc;
						pop = loc.population;
					}
				}
				if(highestPop != null)
					record.locData.put(Column.state_province, highestPop);
				
				pop = -1;
				highestPop = null;
				for (Location loc : countires.values())
				{
					if(loc.population > pop)
					{
						highestPop = loc;
						pop = loc.population;
					}
				}
				if(highestPop != null)
					record.locData.put(Column.country, highestPop);
				
				
				if(record.locData.containsKey(Column.country) == false) //no country 
				{
					if(record.locData.containsKey(Column.state_province) == false)  //no state
					{
						if(record.locData.containsKey(Column.city) )//&& cities.size() <= 5) // no country or state yes city
						{
							String stateKey = record.locData.get(Column.city).getStateKey();
							Location stateLocation = partManager.allLocations.get(stateKey);
							
							if(stateLocation != null) 														//try fill state from city
								record.locData.put(Column.state_province, stateLocation);						
						}
					}
					if(record.locData.containsKey(Column.state_province)  && states.size() <= 5)  //yes state
					{
						String countryKey = record.locData.get(Column.state_province).getCountryKey();
						Location countryLocation = partManager.allLocations.get(countryKey);
						
						if(countryLocation != null) 														//try fill country from state
							record.locData.put(Column.country, countryLocation);	
					}
					
					
				}
				


				sqlConnection.updateRecord(record);

			}//while
		
			
			sqlConnection.close();
			
		}//try
		catch (Exception e)
		{
			Log.log("ERROR in MapLocations ", e);
			
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public String makeNice(String string)
	{
		
		
		
		
		return string;
	}
	
	
	public static void Exit(int status)
	{
		Log.saveLog(logDir, "log_" + new DateTime().toString().replaceAll(":", ".") + ".log", false);
		System.exit(status);
	}
	
	
	
}
