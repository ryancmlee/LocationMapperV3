package LocationMapper;


import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	public String dataDir = "D:\\data"; // D:\\data
	public String textDir = dataDir + "/text";
	
	public DateTime startDateTime;;

	public SQLConnection sqlConnectionIN;
	public SQLConnection sqlConnectionOUT;
	
	
	public LatLongParser latLongParser;
	//public TextParser textParser;
	public PartManager partManager;
	
	
	
	
	
	public LocationMapper(String[] args)
	{
		Log.doConsolePrint = true;
		Log.doLog = true;
		
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
					Log.log("ERROR: unable to verify dataDir path: " + dataDir);
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
		
		this.logDir = dataDir + "/logs";
		this.textDir = dataDir + "/text";
		
		
		this.startDateTime = new DateTime();
		
		Log.log("Starting : " + startDateTime);
		Log.log("address = " + address);
		Log.log("tableName = " + serverName);
		Log.log("port = " + port);
		Log.log("userName = " + userName);
		Log.log("password = " + password);
		Log.log("dataDir = " + dataDir);
		Log.log(Log.breakString);
		
		
		
		
//		test connection to server
		sqlConnectionIN = new SQLConnection(address, serverName, port, userName, password);//(String address, String tableName, String port, String userName, String passwo
		if(this.sqlConnectionIN.Connect() == false)
		{
			Log.log("Unable to connect to to sqlServer IN");
			Log.log("Exiting");
			Exit(1);
			return;
		}
		try {
			sqlConnectionIN.connection.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		sqlConnectionOUT = new SQLConnection(address, serverName, port, userName, password);//(String address, String tableName, String port, String userName, String password)
		if(this.sqlConnectionOUT.Connect() == false)
		{
			Log.log("Unable to connect to to sqlServer OUT");
			Log.log("Exiting");
			Exit(1);
			return;
		}
		
		
	
		//load textParser
		TextParser textParser = new TextParser();
		textParser.loadText(textDir);
		textParser.CreateMasterOut(textDir);
	

		//build partmap
		partManager = new PartManager(textParser.allLoc, "[, \\./\\\\]");
		
		
		

		//load LatLongParser
		latLongParser = new LatLongParser();
		//latLongParser.loadData(dataDir);
		
	
		
		//get data from server
		ResultSet results = sqlConnectionIN.getData();
		
		Log.log("Got results!");
		Log.log("Processing Records");

		//Map Locations
		MapLocations(results);
		
	
		try 
		{
			Log.log("Total ParseCount = " + sqlConnectionOUT.parseCount);
			results.close();
			
		} 
		catch (SQLException e)
		{
			Log.log("error closing reslutSet", e);
		}
		
		
		
		Exit(0);
	}
	
	
	
	
	public void ProcessAndUpdate(int id, float latitude, float longitude, String twitter_user_location, String twitter_user_lang)
	{

		
		twitter_user_location = TextParser.makeSuperNice(twitter_user_location);	
		
		

		Record record = new Record(id, latitude, longitude, twitter_user_location, twitter_user_lang);
		
		//set from lat long
		latLongParser.setPossableLocations(record);
	
		//set from text
		record.textData = partManager.getLocations(record.twitter_user_location);
//		partManager.setMatchStrings(record);

	
		HashMap<String, Location> cities = new HashMap<String, Location>();
		HashMap<String, Location>  states = new HashMap<String, Location>();
		HashMap<String, Location> countries = new HashMap<String, Location>();
		
		
		for (Location loc : record.textData)
		{
			if(loc.column == Column.city)
			{
				cities.put(loc.getKey(), loc);	
			}
			else if(loc.column == Column.state_province)
			{
				states.put(loc.getKey(), loc);
			}
			else if(loc.column == Column.country)
			{
				countries.put(loc.getKey(), loc);
			}	
		}
		
		//cityOnly  by population
		if (countries.size() == 0 && states.size() == 0 && cities.size() >= 1)		
		{
			Location cityOnly = null;
			for (Location city : cities.values())
			{
				if(cityOnly == null)
					cityOnly = city;
				else if(cityOnly.population < city.population)
					cityOnly = city;
			}
			if(cityOnly != null)
				record.probableLocations.put(Column.city, cityOnly);
		}
		
		//stateOnly  by population
		if (countries.size() == 0 && states.size() >= 1 && cities.size() == 0)		
		{
			Location stateOnly = null;
			for (Location state : states.values())
			{
				if(stateOnly == null)
					stateOnly = state;
				else if(stateOnly.population < state.population)
					stateOnly = state;
			}
			if(stateOnly != null)
				record.probableLocations.put(Column.state_province, stateOnly);
		}
		
		//countryOnly  by US then population
		if (countries.size() >= 1 && states.size() == 0 && cities.size() == 0)		
		{
			Location countryOnly = null;
			for (Location country : countries.values())
			{
				if(countryOnly == null)
					countryOnly = country;
				else if(countryOnly.countryCode.equals("us"))
				{
					countryOnly = country;
					break;
				}
				else if(countryOnly.population < country.population)
					countryOnly = country;
			}
			if(countryOnly != null)
				record.probableLocations.put(Column.country, countryOnly);
		}
		
		//city and state  by cityPopulation
		if (countries.size() == 0 && states.size() >= 1 && cities.size() >= 1)		
		{
			Location cityStateCombo = null;
			for (Location city : cities.values())
			{
				Location state = states.get(city.getStateKey());
				if(state != null)										//have city state combo
				{
					if(cityStateCombo == null)
						cityStateCombo = city;
					else if(cityStateCombo.population < city.population)
						cityStateCombo = city;
				}
			}
			if(cityStateCombo != null)
				record.probableLocations.put(Column.city, cityStateCombo);
		}
		
		
		//state and County  by statePopulation
		if (countries.size() >= 1 && states.size() >= 1 && cities.size() == 0)		
		{
			Location stateCountyCombo = null;
			for (Location state : states.values())
			{
				Location country = countries.get(state.getCountryKey());
				if(country != null)										//have stateCountry Combo
				{
					if(stateCountyCombo == null)
						stateCountyCombo = state;
					else if(stateCountyCombo.population < state.population)
						stateCountyCombo = state;
				}
			}
			if(stateCountyCombo != null)
				record.probableLocations.put(Column.state_province, stateCountyCombo);
		}
		
		
		
		//cityCountry  by cityPopulation
		if (countries.size() >= 1 && states.size() == 0 && cities.size() >= 1)		
		{
			Location cityCountryCombo = null;
			for (Location city : cities.values())
			{
				Location country = countries.get(city.getCountryKey());
				if(country != null)										//have city state combo
				{
					if(cityCountryCombo == null)
						cityCountryCombo = city;
					else if(cityCountryCombo.population < city.population)
						cityCountryCombo = city;
				}
			}
			if(cityCountryCombo != null)
				record.probableLocations.put(Column.city, cityCountryCombo);
		}
		
		
		//all  by cityPopulation
		if (countries.size() >= 1 && states.size() >= 1 && cities.size() >= 1)		
		{
			Location allCombo = null;
			for (Location city : cities.values())
			{
				Location state = states.get(city.getStateKey());
				Location country = countries.get(city.getCountryKey());
				if(state != null && country != null)										//have city state combo
				{
					if(allCombo == null)
						allCombo = city;
					else if(allCombo.population < city.population)
						allCombo = city;
				}
			}
			if(allCombo != null)
				record.probableLocations.put(Column.state_province, allCombo);
		}
		
		
//				
//		
//		for(Location country : countries.values())					//for each country
//		{
//			for (Location state : states.values())					//states in country
//			{
//				if(country.getKey() == state.getCountryKey())
//				{
//					country.states.put(state.getKey(), state);
//				}
//			}
//			
//			for (Location city : cities.values())
//			{
//				if(country.getKey() == city.getCountryKey())		//city in country
//				{
//					country.cities.put(city.getKey(), city);
//				}
//			}
//			
//			
//			
//			
//			
//		}
//		
//		
//		
//		
//		
//		if(countries.size() >= 0)			//yes country
//		{
//			
//			for (Location state : states.values())
//			{
//				String countryCode = state.getCountryKey();
//				Location country = countries.get(countryCode);
//				
//				if(country != null)								//merge countries and states
//				{
//					country.states.put(state.getKey(), state);
//					state.countires.put(countryCode, country);
//				}
//			}
//			
//			
//			
//		}
//		
//		
//		
//		
//		
//		
//		
//		
//		for (Location city : cities.values())
//		{
//			Location state = states.get(city.getStateKey());
//			if(state != null)
//			{
//				city.states.put(state.getKey(), state);
//				state.cities.put(city.getKey(), city);
//			}
//			
//			Location country = countries.get(city.getCountryKey());
//			if(country != null)
//			{
//				city.states.put(country.getKey(), country);
//				country.cities.put(city.getKey(), city);
//			}
//			
//		}
////		
//		for (Location state : states.values())
//		{
//			Location state = states.get(city.getStateKey());
//			if(state != null)
//			{
//				city.states.put(state.getKey(), state);
//				state.cities.put(city.getKey(), city);
//			}
//		}
//		
		
		
		
		
		
//		boolean hasES = false;
//		if(record.twitter_user_lang.equals("es"))
//		{
//			for ( String string : new ArrayList<String>(cities.keySet()))
//			{
//				if(cities.get(string).countryCode.equals("es"))
//				{
//					hasES = true;
//					break;
//				}
//			}
//			
//			if(hasES)
//			{
//				for ( String string : new ArrayList<String>(cities.keySet()))
//				{
//					if(cities.get(string).countryCode.equals("es") == false)
//					{
//						cities.remove(string);
//					}
//				}
//			}
//			else
//			{
//				for ( String string : new ArrayList<String>(states.keySet()))
//				{
//					if(states.get(string).countryCode.equals("es"))
//					{
//						hasES = true;
//						break;
//					}
//				}
//			}
//			
//			if(hasES)
//			{
//				for ( String string : new ArrayList<String>(states.keySet()))
//				{
//					if(states.get(string).countryCode.equals("es") == false)
//					{
//						states.remove(string);
//					}
//				}
//				for ( String string : new ArrayList<String>(countires.keySet()))
//				{
//					if(countires.get(string).countryCode.equals("es") == false)
//					{
//						countires.remove(string);
//					}
//				}
//			}
//		}
		
		
		
		
		
//		long pop = -1;
//		Location highestPop = null;
//		for (Location loc : cities.values())
//		{
//			if(loc.population > pop)
//			{
//				highestPop = loc;
//				pop = loc.population;
//			}
//		}
//		record.textData.put(Column.city, highestPop);
		
		
		
		
//		
//		
//		for (Location loc : cities.values())
//		{
//			String key = loc.getStateKey();
//			if(states.containsKey(loc.getStateKey()))// we have a cityLocation that is in a states we also have
//			{
//				loc.hits++;
//				states.get(loc.getStateKey()).hits++;
//			}
//			key = loc.getCountryKey();
//			if(countires.containsKey(loc.getCountryKey()))// we have a countiresLocation that is in a states we also have
//			{
//				loc.hits++;
//				countires.get(loc.getCountryKey()).hits++;
//			}
//		}
//		for (Location loc : states.values())
//		{
//			String key = loc.getCountryKey();
//			if(countires.containsKey(loc.getCountryKey()))// we have a cityLocation that is in a states we also have
//			{
//				loc.hits++;
//				countires.get(loc.getCountryKey()).hits++;
//			}
//		}
//		

		
//		
//		long pop = -1;
//		Location highestPop = null;
//		for (Location loc : cities.values())
//		{
//			if(loc.population > pop)
//			{
//				highestPop = loc;
//				pop = loc.population;
//			}
//			
////			if(loc.getCountryKey().equals("US._._") )
////				record.textData.put(Column.city, loc);
//		}
//		if(highestPop != null)
//			record.textData.put(Column.city, highestPop);
//		
//		
//		pop = -1;
//		highestPop = null;
//		for (Location loc : states.values())
//		{
//			if(loc.population > pop)
//			{
//				highestPop = loc;
//				pop = loc.population;
//			}
//			
////			if(loc.getCountryKey().equals("us._._") )
////				record.textData.put(Column.city, loc);
//		}
//		if(highestPop != null)
//			record.textData.put(Column.state_province, highestPop);
//		
//		pop = -1;
//		highestPop = null;
//		for (Location loc : countires.values())
//		{
//			if(loc.population > pop)
//			{
//				highestPop = loc;
//				pop = loc.population;
//			}
//		}
//		if(highestPop != null)
//			record.textData.put(Column.country, highestPop);
//		
		
		
		
	
		
		
//		
//		
//		if(record.textData.containsKey(Column.country) == false) //no country 
//		{
//			if(record.textData.containsKey(Column.state_province) == false)  //no state
//			{
//				if(record.textData.containsKey(Column.city) )//&& cities.size() <= 5) // no country or state yes city
//				{
//					String stateKey = record.textData.get(Column.city).getStateKey();
//					Location stateLocation = partManager.allLocations.get(stateKey);
//					
//					if(stateLocation != null) 														//try fill state from city
//						record.textData.put(Column.state_province, stateLocation);
//					else
//					{
//						int asdf = 234;
//					}
//				}
//			}
//			if(record.textData.containsKey(Column.state_province)  && states.size() <= 5)  //yes state
//			{
//				String countryKey = record.textData.get(Column.state_province).getCountryKey();
//				Location countryLocation = partManager.allLocations.get(countryKey);
//				
//				if(countryLocation != null) 														//try fill country from state
//					record.textData.put(Column.country, countryLocation);	
//			}
//		}
		


		sqlConnectionOUT.updateRecord(record);
		

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
				String twitter_user_lang = results.getString(5);
				
			
				this.ProcessAndUpdate(id, latitude, longitude, twitter_user_location, twitter_user_lang);
			}
		
			
			sqlConnectionIN.close();
			sqlConnectionOUT.close();
			
		}//try
		catch (Exception e)
		{
			Log.log("ERROR in MapLocations ", e);
			
		}
		
		
		
	}
	
	
	
	
	
	
	
	public static void Exit(int status)
	{
		if(Log.doLog)
			Log.saveLog(logDir, "log_" + new DateTime().toString().replaceAll(":", ".") + ".log", false);
		System.exit(status);
	}
	
	
	
}
