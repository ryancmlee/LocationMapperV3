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

//	private Location stndFromStatoids(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex, Column column) 
//	{
//
//		String[] strings = data.split("\t");
//		HashSet<String> matchNames = new HashSet<String>();
//
//
//		String[] stirngs2 = strings[countryAndStateIndex].split("\\.");
//
//
//		String country = stirngs2[0].trim();
//		String state = stirngs2[1].trim();
//		state = state.substring(state.length() - 2,state.length());
//
//
//		String oName = strings[0];
//		if(oNameIndex == countryAndStateIndex)
//		{
//			matchNames.add(oName);
//			oName = state;
//		}
//
//		String city = blank;
//
//		long pop = 0;
//		if(popIndex != -1)
//			pop = getLong(strings[popIndex]);
//
//
//
//		matchNames.add(oName);
//		if(aliasIndex != -1)
//		{
//			String tempString = strings[aliasIndex];
//			matchNames.add(tempString);
//		}
//
//
//		return  new Location(oName, country, state, city, pop, column, matchNames);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
//	}
//
//	private Location stndFromStatoidsCountires(String data, int oNameIndex, int countryAndStateIndex, int popIndex, Column column) 
//	{
//
//		String[] strings = data.split("\t");
//
//		String oName = strings[oNameIndex];
//		if(oName.equals(""))
//			return null;
//
//		String country = strings[countryAndStateIndex];
//		if(country.equals(""))
//			return null;
//
//		String state = blank;
//		String city = blank;
//
//		long pop = 0;
//		if(popIndex != -1)
//			pop = getLong(strings[popIndex]);
//
//		return new Location(oName, country, state, city, pop, column, "");//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
//	}
//
//	private Location ParseUSACityToTextString(String data)
//	{
//		String[] strings = data.split("\t");
//
//		String country = "us";
//		String state = strings[0].trim();
//		String city = strings[3].trim();
//		String oName = city;
//
//		
//		
//
//		long population = getLong(strings[6]);
//
//
//		return new Location(oName, country, state, city, population, Column.city, city);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
//	}
//
//	private Location doCountryAndPop(String string1, String string2) 
//	{
//		ArrayList<String> lines = new ArrayList<String>();
//
//		//		String[] strings = string1.split("\t");
//		//		for(String temp : strings)
//		//			lines.add(temp);
//		//	
//		//		strings = string2.split("\t");
//		//		for(String temp : strings)
//		//			lines.add(temp);
//
//		String[] strings = (string1 + "\t" + string2).split("\t");
//		for(String temp : strings)
//			lines.add(temp);
//
//		return new Location(lines.get(1), lines.get(1), blank, blank, getLong(lines.get(6)), Column.country, lines.get(4)); 
//	}
//
//	private Location fromCDHStates(String data, int oNameIndex, int countryAndStateIndex, int popIndex, Column column, int otherNamesIndex) 
//	{
//		
//		
//		
//		String[] strings = data.split("\t");
//		
//		if(strings.length <= 2)
//			return null;
//		
//		
//		
//		
//		String[] tempStateAndCountry = strings[1].trim().split("-");
//		String country = tempStateAndCountry[0];
//		String state =  tempStateAndCountry[1];
//		String city = blank;
//		
//		String oName = state;
//		
//	
//		long pop = 0;
//		
//		
//		HashSet<String> matchNames = new  HashSet<String>();
//		String officialName = strings[2].trim();
//		if(officialName.equals(""))
//			return null;
//		else
//			TextParser.addMatchNames(officialName, matchNames);
//		
//		
//		if(strings.length >= 4)
//		{
//			strings = strings[3].split(",");
//			
//			TextParser.addMatchNames(strings, matchNames);
//		}
//		
//		
//		
//		
//		
//		return new Location(oName, country, state, city, pop, column, matchNames);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
////
////		String[] strings = data.split("\t");
////
////		if(strings.length <= 2)
////			return null;
////
////		String oName = strings[oNameIndex].trim();
////		if(oName.equals(""))
////			return null;
////
////
////
////		String[] tempStateAndCountry = strings[countryAndStateIndex].trim().split("-");
////		String country = tempStateAndCountry[0];
////		String state =  tempStateAndCountry[1];
////		String city = blank;
////
////
////		if(country.equals("us")) //-------------------------------------------SPECIAL FOR US, NOT TO ADD IT TWICE
////			return null;
////
////
////
////		long pop = 0;
////		if(popIndex != -1)
////			pop = getLong(strings[popIndex]);
////
////		HashSet<String> matchNames = new  HashSet<String>();
////
////		matchNames.add(oName);
////
////
////
////		if(strings.length > 3)
////		{
////			String tempString = strings[otherNamesIndex].trim();
////			String[] tempStrings = tempString.split(",");
////			for(String string : tempStrings)
////				matchNames.add(string);
////		}
//
//		
//	}
//
//	private Location fromWorldCitiesPop(String data)
//	{
//		String[] strings = data.split(",");
//
//		String oName = strings[1];
//		String country = strings[0];
//
//		if(country.equals("us"))
//			return null;
//
//		String state = blank;//strings[3].trim().toLowerCase();
//		String city = strings[2];
//
//		long pop = getLong(strings[4]);
//		if(pop < MinPop)
//			return null;
//
//
//		Location temp = new Location(oName, country, state, city, pop, Column.city);
//		temp.matchNames.add(city);
//
//		return temp;
//
//	}

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
			
	
	
	
	
	public void loadText(String dataDir)
	{
		String tempFileLoc = null;
	
		Log.log("Loading individial text data files for processing and combining");


		
		tempFileLoc = dataDir + "/text/makeNice.txt";
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
	

		tempFileLoc = dataDir + "/text/removeEndings.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
			removeEndings.add(makeNice(string));
		//Log.log(Log.tab + removeEndings.size() + " removeEndings loaded");


		
		
		
		tempFileLoc = dataDir + "/text/raw/nameToISOcdh.txt";
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
		
		tempFileLoc = dataDir + "/text/raw/admin1CodesASCII.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			HashMap<String, String[]> temp = loadFIPStoNameAdmin(string);
			FIPStoNameMap.putAll(temp);
		}
		
		
		
		tempFileLoc = dataDir + "/text/raw/cities1000.txt";
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
			String admin1     		= (strings[10]);		// fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
			String admin2     		= (strings[11]);		// code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 	
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
			
		
			
			
			
			Location loc = new Location(asciiname, countryCode, admin1, asciiname, population, "_", Column.city, matchNames);  //public Location(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
			this.addLoc(loc);
			
			
//			//set Specifics
//			if(admin1.equals(""))
//			{
//				Log.log("admin1 is blank for " + string);
//			}
//			else if(admin1.equals("ENG"))
//			{
//				admin1 = "EN";
//			}
//			
//				
//
//			String countryDOTstateCODE = countryCode + "." + admin1;
//			
//			
//			String stateISO = this.getISOfromFIPS(countryDOTstateCODE);
//			
//			
//			if(stateISO == null)
//			{
//				
//				Log.log("if(FIPStoNameMap.containsKey(admin1) == false)  " + countryDOTstateCODE + "||" + y++);
//			}
//			else
//			{
//				t++;
//				
//				
//				Location loc = new Location(asciiname, stateISO, asciiname, population, "_lvl_", Column.city, matchNames);  //public Location(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
//				this.addToAllLoc(loc);
//			}
			
			
			
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
					
					
					
					Location newState = new Location(strings[1], strings[0], strings[1], "_", 0, "_", Column.state_province, matchNames);  //(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
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
						
						
						
						Location newCountry = new Location(strings[0], strings[0], "_", "_", 0, "_", Column.country, matchNames);  //(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
						this.addLoc(newCountry);
					}
				}
				
				
				
				
				
			}
			
			
			
			
			
			
		}
		
		
		
		
		
		
		
		allLoc.putAll(allCities);
		allLoc.putAll(allStates);
		allLoc.putAll(allCountries);
		
		
//		
//		tempFileLoc = dataDir + "/text/raw/countryAndPop.txt";
//		Log.log(Log.tab + "Loading " + tempFileLoc);
//		ArrayList<String> countryAndPop = LoadTextFile(tempFileLoc, null, false);
//		for(int i = 0; i < countryAndPop.size();)
//		{
//			Location tempLoc = doCountryAndPop(countryAndPop.get(i), countryAndPop.get(i + 1));//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
//			addToAllLoc(tempLoc);
//			i += 2;
//		}
//		
//		 
//		
////		
////
////		tempFileLoc = dataDir + "/text/raw/usaStates.txt";
////		Log.log(Log.tab + "Loading " + tempFileLoc);
////		for(String string : LoadTextFile(tempFileLoc, null, false))
////		{
////			Location tempLoc = stndFromStatoids(string, 1,1,5,3, Column.state_province);//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
////			addToAllLoc(tempLoc);
////		}
////
////		tempFileLoc = dataDir + "/text/raw/usaCityToState.txt";
////		Log.log(Log.tab + "Loading " + tempFileLoc);
////		for(String string : LoadTextFile(tempFileLoc, null, true))
////		{
////			Location tempLoc = ParseUSACityToTextString(string);
////			addToAllLoc(tempLoc);
////		}
////
////
//////
//////		tempFileLoc = dataDir + "/text/raw/countryAndPop.txt";
//////		Log.log(Log.tab + "Loading " + tempFileLoc);
//////		ArrayList<String> countryAndPop = LoadTextFile(tempFileLoc, null, false);
//////		for(int i = 0; i < countryAndPop.size();)
//////		{
//////			Location tempLoc = doCountryAndPop(countryAndPop.get(i), countryAndPop.get(i + 1));//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
//////			addToAllLoc(tempLoc);
//////			i += 2;
//////		}
////
////
//		tempFileLoc = dataDir + "/text/raw/cdhStates.txt";
//		Log.log(Log.tab + "Loading " + tempFileLoc);
//		for(String string : LoadTextFile(tempFileLoc, null, true))
//		{
//			Location tempLoc = fromCDHStates(string, 2, 1, -1, Column.state_province, 3);//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
//			addToAllLoc(tempLoc);
//		}
////
////
////
////		int i = 0, j = 0;
////		tempFileLoc = dataDir + "/text/raw/worldcitiespop.txt";
////		Log.log(Log.tab + "Loading " + tempFileLoc);
////		ArrayList<String> tempWorldCitiesPopList = LoadTextFile(tempFileLoc, null, true);
////		int tempThing = tempWorldCitiesPopList.size() / 10; 
////		for(String string : tempWorldCitiesPopList)
////		{
////			if(j++ % tempThing == 0)
////			Log.log(Log.tab + Log.tab + j + "\t" + string);
////		
////			Location tempLoc = fromWorldCitiesPop(string);
////			
////
////			if(tempLoc != null)// tempLoc is null if  --> && tempLoc.population < minPop)
////				continue;
////			
////			i++;
////			addToAllLoc(tempLoc);
////			
////		}
////		Log.log(Log.tab + Log.tab + i + "\tPotential Cities");
////
////		
//		
//		
//		
//		
//		ArrayList<GeoBuilder> geoBuilders = new ArrayList<GeoBuilder>();
//		tempFileLoc = dataDir + "/text/raw/cities1000.txt";
//		Log.log(Log.tab + "Loading " + tempFileLoc);
//		for(String string : LoadTextFile(tempFileLoc, null, true))
//		{
//			GeoBuilder gb = new GeoBuilder(string);
//		
//			if(gb.population < MinPop)
//				continue;
//		
//			
//			geoBuilders.add(gb);
//		}
//		geoBuilders.trimToSize();
//		Collections.sort(geoBuilders);
//		
//		
//		tempFileLoc = dataDir + "/text/rejectedMatchWords.txt";
//		Log.log(Log.tab + Log.tab + "Writing " + tempFileLoc);
//		ArrayList<String> tempList = new ArrayList<String>();
//		tempList.addAll(rejectedMatchWords);
//		tempList.trimToSize();
//		Collections.sort(tempList);
//		this.writeText(tempFileLoc, null, tempList);
//		Log.log(Log.tab + Log.tab + tempList.size() + " rejected matchWords");
//		
//		
//		
//		HashMap<String, AdminBuilder>  adminBuilder1 = new HashMap<String, AdminBuilder>();
//		tempFileLoc = dataDir + "/text/raw/admin1CodesASCII.txt";
//		Log.log(Log.tab + "Loading " + tempFileLoc);
//		for(String string : LoadTextFile(tempFileLoc, null, true))
//		{
//			AdminBuilder ab = new AdminBuilder(string);
//			String key = ab.getStateKey();
//			adminBuilder1.put(key, ab);
//		}
//		
//		
////		HashMap<String, AdminBuilder> adminBuilder2 = new HashMap<String, AdminBuilder>();
////		tempFileLoc = dataDir + "/text/raw/admin2Codes.txt";
////		Log.log(Log.tab + "Loading " + tempFileLoc);
////		for(String string : LoadTextFile(tempFileLoc, null, true))
////		{
////			AdminBuilder ab = new AdminBuilder(string);
////			String key = ab.getStateKey();
////			adminBuilder2.put(key, ab);
////		}
//
//		
//		HashSet<String> stateCodes = new HashSet<String>();
//		Log.log(Log.tab + "Building Cities");
//		for(GeoBuilder gb : geoBuilders)
//		{
//		
//			
//			if(gb.admin1.equals("") == false)
//			{
//				gb.state = gb.admin1;
//			}
////			else if(gb.admin2.equals("") == false)
////			{
////				gb.state = gb.admin2;
////			}
//			else
//				Log.log("error in BUILDING cities");
//			
//			
//			Location tempLoc = new Location(gb.asciiname, gb.countryCode, gb.state, gb.asciiname, gb.population, Column.city, gb.matchNames);//(String outName, String country, String state, String city, long population, Column column, String otherName)
//			tempLoc.geoKey = gb.getKey();
//			tempLoc.geoNameID = gb.geonameid;
//			
//			if(tempLoc.population > MinPop)
//			{
//			
//				addToAllLoc(tempLoc);
//				
//				
//				String temp = gb.getStateKey();
//				stateCodes.add(temp);
//			
//			
//			}
//			
//			
////			if(temp.equals("KM.00._"))
////			{
////				
////				
////				int asdasdfads = 1234;
////			}
//			
//			
////			
////			String state = gb.admin1;
////			
////			String key = gb.getKey();
////			String key2 = gb.getKey2();
////			if(adminBuilder1.containsKey(key))
////			{
////				String tempKey = gb.getKey();
////				state = adminBuilder1.get(tempKey).region;
////			}
////			else if(adminBuilder2.containsKey(key2))
////			{
////				String tempKey = gb.getKey2();
////				state = adminBuilder2.get(tempKey).region;
////			}
////		
////			
////			Location tempLoc = new Location(gb.asciiname, gb.countryCode, state, gb.asciiname, getLong(gb.populationString), Column.city, gb.matchNames);//(String outName, String country, String state, String city, long population, Column column, String otherName)
////			tempLoc.geoKey = gb.getKey();
////			tempLoc.geoNameID = gb.geonameid;
////			addToAllLoc(tempLoc);
////			
////		}
////		
////		
////		
////		
////		
////		
////		Log.log(Log.tab + "Building States");
////		for(String string : stateCodes)
////		{
////			if(adminBuilder1.containsKey(string))
////			{
////				AdminBuilder ab = adminBuilder1.get(string);
////
////				Location tempLoc = new Location(ab.nameASCII, ab.countryCode, ab.region, ab.nameASCII, 0, Column.state_province, ab.nameASCII);//(String outName, String country, String state, String city, long population, Column column, String otherName)
////				tempLoc.geoKey = ab.getStateKey();
////				tempLoc.geoNameID = ab.geonameid;
////				addToAllLoc(tempLoc);
////			}
////			else
////			{
////				int asdf= 2134;
////				
////				
////			}
////		}
		
		
		
		
		
		
		
		
		
		

		Log.log("Load of text data complete");

		return;
	}




	public void CreateMasterOut(String dataDir)
	{
		String tempFileLoc = null;


		
		
		
		
		Log.log("Proccessing text data for MasterOut");


		tempFileLoc = dataDir + "/text/keyRemove.txt";
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


		tempFileLoc = dataDir + "/text/keyAdd.txt";
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





		tempFileLoc = dataDir + "/text/MasterOut.txt";
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
