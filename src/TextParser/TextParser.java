package TextParser;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import LatLongParser.Column;
import LocationMapper.Log;

public class TextParser 
{
	private ArrayList<String> LoadTextFile(String primDir, String optionDir, boolean skipFirstLine)
	{
		ArrayList<String> data = new ArrayList<String>();

		String fileLocation = primDir;
		if(optionDir != null && optionDir.equals("") == false)
			fileLocation += "/" + optionDir;

		try
		{
			FileInputStream inStream = new FileInputStream(fileLocation);
			DataInputStream in = new DataInputStream(inStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));


			if(skipFirstLine)
				br.readLine();

			String string;
			while ((string = br.readLine()) != null)   
			{
				if(string.startsWith("//") || string.trim().equals(""))  //wont be null cause of   while ((string = br.readLine()) != null) 
					continue;

				data.add(string);
			}

			br.close();
			in.close();
			inStream.close();
		}
		catch (Exception e)
		{
			Log.log("ERROR in LoadTextFile ", e);
		}

		return data;
	}

	private boolean writeText(String primDir, String optionDir, ArrayList<String> data)
	{

		String fileLocation = primDir;

		if(optionDir != null && optionDir.equals("") == false)
			fileLocation += "/" + optionDir;


		try
		{

			BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileLocation)));

			for(String line : data)
			{
				out.write(line);
				out.newLine();
			}


			out.close();
		}
		catch (Exception e)
		{
			Log.log("Failed to write out " + fileLocation ,e);
			return false;
		}



		//		
		//		String fileLocation = primDir;
		//
		//		if(optionDir != null && optionDir.equals("") == false)
		//			fileLocation += "/" + optionDir;
		//		
		//		
		//		FileWriter out = null;
		//		try
		//		{
		//			File file = new File(fileLocation);
		//			out = new FileWriter(file);
		//			for(String line : data)
		//			{
		//				out.write(line + "\n");
		//			}
		//
		//			if(out != null)
		//				out.close();
		//		}
		//		catch (Exception e)
		//		{
		//			Log.log("Failed to write out " + fileLocation ,e);
		//			return false;
		//		}

		return true; 
	}


	//SE.02[0]	Blekinge[1]	Blekinge[2]	2721357[3]
	private HashMap<String, String[]> loadFIPStoNameAdmin(String data)
	{
		HashMap<String, String[]> FIPStoNameMap = new HashMap<String, String[]>();
		
		String[] strings = data.split("\t");
		
		String[] temp = new String[2];
		temp[0] =  makeNice(strings[1]);
		temp[1] =  makeNice(strings[2]);
		
		
		String CountryAndStateCode = makeNice(strings[0]);
	
		FIPStoNameMap.put(CountryAndStateCode, temp);
		
		return FIPStoNameMap;
	}

//	COUNTRY NAME||ISO 3166-2 SUB-DIVISION/STATE CODE||ISO 3166-2 SUBDIVISION/STATE NAME||ISO 3166-2 PRIMARY LEVEL NAME||SUBDIVISION/STATE ALTERNATE NAMES
//	Afghanistan[0]	AF-BDS[1]	Badakhshān[2]	Province[3]		Badaẖšan[4]
	private HashMap<String, String> loadNameToISOcdh(String data)
	{
		HashMap<String, String> nameToISOmap = new  HashMap<String, String>();

		String[] strings = data.split("\t");
		if(strings.length <= 2)		//no isoName so no point, skip it
			return nameToISOmap;
		
		String[] split = strings[1].split("-");
		if(split.length <= 1)		//no stateCode so no point, skip it
			return nameToISOmap;
		
		
		String countryName = makeNice(strings[0]);
		String countryCode = makeNice(split[0]);
		String stateCode = makeNice(split[1]);
		String isoName = makeNice(strings[2]);
		String levelName = null;
		if(strings.length > 3)			//has levelName
		{
			levelName = makeNice(strings[3]);
			
			if(levelNames.containsKey(levelName))
				levelNames.put(levelName, levelNames.get(levelName) + 1);
			else
				levelNames.put(levelName, 1);
		}

		
		if(strings.length > 4)			//has matchNames
		{
			for(String string : makeNice(strings[4]).split(","))
				nameToISOmap.put(string, stateCode);
		}
		
		nameToISOmap.put(isoName, countryCode + "."+ stateCode);	//not used anymore
		ISOtoCountryNameMmap.put(countryCode, countryName);  		//why do i have this?
		countryCodeToNameMap.put(countryCode, countryName);
		
		return nameToISOmap;
	}

	
	
	
	
	
	HashMap<String, String[]> FIPStoNameMap = new HashMap<String, String[]>();
	HashMap<String, String> nameToISOmap = new  HashMap<String, String>();
	
	
	HashMap<String, String> ISOtoCountryNameMmap = new  HashMap<String, String>();
	
	public static HashMap<String, Integer> levelNames = new HashMap<String, Integer>();
	public static HashSet<String> rejectedMatchWords = new HashSet<String>();
	public static String allowedCharacters = "[A-Za-z0-9,â€§=ÅÄÃŸƒ¡©` \\.\\-]+";
	
	//key _ _ _,  location
	public HashMap<String, Location> allLoc = new HashMap<String, Location>();
	public HashMap<String, Location> allCities = new HashMap<String, Location>();
	public HashMap<String, Location> allStates = new HashMap<String, Location>();
	public HashMap<String, Location> allCountries = new HashMap<String, Location>();

	HashMap<String, String> countryCodeToNameMap  = new  HashMap<String, String>();
	
	
	static HashSet<String> removeEndings = new HashSet<String>();
	static HashMap<String, String> makeNiceList = new HashMap<String, String>();

	String blank = "_";
	//String[] allowedLevelNames = {"principality", "Territory", "prefecture" , "State" , "Province" , "Region" , "state" , };
	public static long MinPop = 1;

	
	public void close()
	{
		allLoc = null;
		removeEndings = null;
	}
	

	
	public static void addMatchNames(String[] strings, HashSet<String> matchNames)
	{
		for	(String string : strings)
			addMatchNames(string, matchNames);
	}
	
	public static void addMatchNames(String string, HashSet<String> matchNames)
	{
		if(string.equals("_") || string.equals(" "))
			return;

		string = string.replace("'", "").replace("�", "");
		
		if(string.matches(TextParser.allowedCharacters))
		{				
			matchNames.add(string);
		}
		else
			TextParser.rejectedMatchWords.add(string);

		
	}
	
	public TextParser()
	{
	}


	
	
	private String getISOfromFIPS(String countryDOTstateCODE)
	{
		if(FIPStoNameMap.containsKey(countryDOTstateCODE))
		{
			String[] names = FIPStoNameMap.get(countryDOTstateCODE);
			
			if(names == null) return null;
			
			for(String string : names)
			{
				if(nameToISOmap.containsKey(string))
				{
					return nameToISOmap.get(string);
				}
			}
		}
		
		return null;
	}
			
	
	
	
	
	public void loadText(String textDir)
	{
		String tempFileLoc = null;
	
		Log.log("Loading individial text data files for processing and combining");


		
		tempFileLoc = textDir + "/makeNice.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			String[] strings = string.toLowerCase().split(";");
			try
			{
				String key = strings[0];
				String value = " ";
				if(strings.length > 1)
					value = strings[1];
				
				makeNiceList.put(key, value);
				
			}
			catch (Exception e)
			{
				Log.log(Log.tab + "error in loading MakeNiceList: " + e + "for: " + string);
			}
		}
	

		tempFileLoc = textDir + "/removeEndings.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
			removeEndings.add(makeNice(string));
		//Log.log(Log.tab + removeEndings.size() + " removeEndings loaded");


		
		
		
		tempFileLoc = textDir + "/raw/nameToISOcdh.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			HashMap<String, String> temp = loadNameToISOcdh(string);
			nameToISOmap.putAll(temp);
		}
//		
//		
//		Log.log(Log.tab + "Generating levelNames and count");
//		String format = "%1$03d";
//		ArrayList<String> tempLevelNames = new ArrayList<String>();
//		for (String string : levelNames.keySet())
//			tempLevelNames.add(String.format(format,levelNames.get(string)) + "=" + string);
//		Collections.sort(tempLevelNames);
//		//String out = "";
//		for (String string : tempLevelNames)
//			Log.log(Log.tab + string);
//			//out += string + "\n";
//		
//		
//		
//		
		
		tempFileLoc = textDir + "/raw/admin1CodesASCII.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			HashMap<String, String[]> temp = loadFIPStoNameAdmin(string);
			FIPStoNameMap.putAll(temp);
		}
		
		
		
		tempFileLoc = textDir + "/raw/cities15000.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			
			String[] strings = string.split("\t");

			String geonameid          = (strings[0]);		// integer id of record in geonames database
			String name               = (strings[1]);		// name of geographical point (utf8) varchar(200)
			String asciiname          = (strings[2]);		// name of geographical point in plain ascii characters, varchar(200)
			String alternatenames     = (strings[3]);		// alternatenames, comma separated varchar(5000)
			String latitude           = (strings[4]);		// latitude in decimal degrees (wgs84)
			String longitude          = (strings[5]);		// longitude in decimal degrees (wgs84)
			String featureClass       = (strings[6]);		// see http://www.geonames.org/export/codes.html, char(1)
			String featureCode        = (strings[7]);		// see http://www.geonames.org/export/codes.html, varchar(10)
			String countryCode     	= (strings[8]);		// ISO-3166 2-letter country code, 2 characters
			String cc3     			= (strings[9]);		// alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
			String state     		= (strings[10]);		// fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
			String county     		= (strings[11]);		// code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 	
			String admin3      		= (strings[12]);		// code for third level administrative division, varchar(20)
			String admin4      		= (strings[13]);		// code for fourth level administrative division, varchar(20)
			String populationString   = (strings[14]);		// bigint (8 byte int) 
			//String elevation          = (strings[15]);		// in meters, integer
			//String dem                = (strings[16]);		// digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
			//String timezone           = (strings[17]);		// the timezone id (see file timeZone.txt) varchar(40)
			//String modificationDate   = (strings[18]);		// date of last modification in yyyy-MM-dd format

			long population = TextParser.getLong(populationString);
		
			
			strings = alternatenames.split(",");
			HashSet<String> matchNames = new HashSet<String>();
			
			for(String string2 : strings)
				matchNames.add(string2);
			
			matchNames.add(name);
			matchNames.add(asciiname);
			
		
			
			
			
			Location loc = new Location(asciiname, countryCode, state, asciiname, county, population, "_", Column.city, matchNames);  //public Location(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
			this.addLoc(loc);
			
		}
		
		
		//build states
		for(Location loc : allCities.values())
		{
			
			String stateKey = loc.getStateKey();
			if(allStates.containsKey(stateKey) == false)						//no state, lets add it
			{
				{
					String[] strings = stateKey.split("\\.");
				
				
					HashSet<String> matchNames = new HashSet<String>();				//add matchNames for states
					String tempStateKey = strings[0] + "." + strings[1];
					if(FIPStoNameMap.containsKey(tempStateKey))
					{
						String[] tempStrings = FIPStoNameMap.get(tempStateKey);
						for(String string : tempStrings)
							matchNames.add(string);
					}
					
					
					
					Location newState = new Location(strings[1], strings[0], strings[1], "_", "_", 0, "_", Column.state_province, matchNames);  //(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
					this.addLoc(newState);
				}
				
	
				String countryKey = loc.getCountryKey();
				if(allCountries.containsKey(countryKey) == false)						//no country, lets add it
				{
					{
						String[] strings = countryKey.split("\\.");
						
						if(strings[0].equals(""))
						{
							int asdfasf =234;
						}
						
						HashSet<String> matchNames = new HashSet<String>();				//add matchNames for countries
						String tempCountryKey = strings[0];
						if(countryCodeToNameMap.containsKey(tempCountryKey))
						{
							String tempString = countryCodeToNameMap.get(tempCountryKey);
							matchNames.add(tempString);
						}
						
						
						
						Location newCountry = new Location(strings[0], strings[0], "_", "_", "_", 0, "_", Column.country, matchNames);  //(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
						this.addLoc(newCountry);
					}
				}
				
				
				
			}
			
		}
		
		
		
		
		
		
		
		allLoc.putAll(allCities);
		allLoc.putAll(allStates);
		allLoc.putAll(allCountries);
		
		
		

		Log.log("Load of text data complete");

		return;
	}




	public void CreateMasterOut(String textDir)
	{
		String tempFileLoc = null;


		
		
		
		
		Log.log("Proccessing text data for MasterOut");


		tempFileLoc = textDir + "/keyRemove.txt";
		Log.log(Log.tab + "Proccessing " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			String[] strings = string.split("\t");
			
//			//depreciated
//			if(strings.length == 1)// remove whole location it
//			{
//				String key = strings[0];
//
//				Location tempLocation = this.allLoc.remove(key);
//				if(tempLocation == null)
//					Log.log("ERROR could not remove location: " + key + " key not found. either wrong key or target location does not exist", true);
//
//			}
			if(strings.length == 1)// remove matchName globaly
			{
				String key = strings[0];

				for (Location loc : allLoc.values())
					loc.matchNames.remove(key);
				

			}
			if(strings.length == 2) // remove matchname
			{
				String key = strings[0];
				String[] values = strings[1].split(",");

				Location tempLocation = this.allLoc.get(key);

				if(tempLocation != null)
				{
					for(String value : values)
					{
						tempLocation.matchNames.remove(value);				
					}
				}
				else
					Log.log("ERROR could not remove matchNames from location: " + key + "key not found. either wrong key or target location does not exist", true);

			}

		}


		tempFileLoc = textDir + "/keyAdd.txt";
		Log.log(Log.tab + "Proccessing " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			String[] strings = string.split("\t");


			if(strings.length == 7)
			{
				Location tempLoc = null;
				try
				{
					tempLoc = new Location(string);
				}
				catch(Exception e)
				{
					Log.log(Log.tab + "Unabble to add Location from keyAdd.txt: "+ string , e);
					continue;
				}

				addLoc(tempLoc);
				//				if(tempLoc != null)
				//				{
				//					noDups.put(tempLoc.getKey(), tempLoc);
				//				}
			}
			else if(strings.length == 2) // add matchname
			{
				String key = strings[0];
				String[] values = strings[1].split(",");

				Location tempLoc = allLoc.get(key);
				if(tempLoc == null)
				{
					Log.log(Log.tab + "addKey: unable to find Location with key: " + key + ". no matchNames have been added for this entry", true);
					continue;
				}

				for(String value : values)
				{
					
					if(value.equals("") == false && tempLoc.matchNames.contains(value) == false)
					{
						value = TextParser.makeSuperNice(value);
						TextParser.addMatchNames(value, tempLoc.matchNames);
					}
				}
			}
			else
				Log.log(Log.tab + "addKey: Wrong number of Delimeters:" + strings.length + ".  " + string, true);
		}





		tempFileLoc = textDir + "/MasterOut.txt";
		Log.log("Writing " + tempFileLoc);
		ArrayList<String> outStrings = new ArrayList<String>(allLoc.size());
		for(Location loc : allLoc.values())
			outStrings.add(loc.toString());
		Collections.sort(outStrings);
		this.writeText(tempFileLoc, null, outStrings);
		Log.log(Log.tab + outStrings.size() + " Locations");



	}


	public static String makeNice(String string)
	{
		if(string == null)
			return "";
		
		string = string.trim();
		string = string.toLowerCase();
		
		return string;
	}
	
	public static String makeSuperNice(String string)
	{
//		if(true)
//			return makeNice(string);
		
		string = makeNice(string);
		
		if(string.equals("_"))
			return string;
		
		for(String key : makeNiceList.keySet())
			string = string.replace(key, makeNiceList.get(key));

		string = string.trim();
		
		for	(String ending : removeEndings)
		{
			if(string.endsWith(ending))
			{
				string = string.replace(ending, "");
				break;
			}
		}
		
		return string;
	}







	private boolean addLoc(Location loc)
	{
		if(loc == null)
		{
			//Log.log("DEBUG: got a null Location while adding to allLoc...");
			return false;
		}

		
		

		String key = null;
		
		if(loc.column == Column.city)
		{
			key = loc.getKey();
			this.allCities.put(key, loc);
		}
		else if(loc.column == Column.state_province)
		{
			key = loc.getStateKey();
			this.allStates.put(key, loc);
		}
		else if(loc.column == Column.country)
		{
			key = loc.getCountryKey();
			this.allCountries.put(key, loc);
		}
		
		
		//Location old = this.allLoc.put(key, loc);

//		if(old != null)
//		{
//			
//			if(old.population > loc.population)
//				this.allLoc.put(old.getKey(), old);
//			
////			Log.log("DEBUG: duplacate key found while adding to allLoc...");
////			Log.log(Log.tab	+ "Added:   " + loc.toString());
////			Log.log(Log.tab	+ "Removed: " + old.toString());
////			return true;
//		}

		return true;
	}

	
	public static long getLong(String string)
	{
		if(string == null) return 0;

		string = string.replace(",", "").trim();
		if(string.equals("") || string.equals(" "))
		{
			return 0;			
		}

		Long tempLong = 0l;
		try
		{
			tempLong = Long.parseLong(string);
		}
		catch (Exception e)
		{
			//Log.log("Unable to parse long: " + string, e);
			return 0;
		}
		
		return tempLong;
	}
}
