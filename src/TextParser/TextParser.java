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
				if(string.startsWith("//") || string.equals(""))  //wont be null cause of   while ((string = br.readLine()) != null) 
					continue;

				data.add(string.trim());
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

	private Location stndFromStatoids(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex, Column column) 
	{

		String[] strings = data.split("\t");
		HashSet<String> matchNames = new HashSet<String>();


		String[] stirngs2 = strings[countryAndStateIndex].split("\\.");


		String country = stirngs2[0].trim();
		String state = stirngs2[1].trim();
		state = state.substring(state.length() - 2,state.length());


		String oName = strings[0];
		if(oNameIndex == countryAndStateIndex)
		{
			matchNames.add(oName);
			oName = state;
		}

		String city = blank;

		long pop = 0;
		if(popIndex != -1)
			pop = getPop(strings[popIndex]);



		matchNames.add(oName);
		if(aliasIndex != -1)
		{
			String tempString = strings[aliasIndex];
			matchNames.add(tempString);
		}


		return  new Location(oName, country, state, city, pop, column, matchNames);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
	}

	private Location stndFromStatoidsCountires(String data, int oNameIndex, int countryAndStateIndex, int popIndex, Column column) 
	{

		String[] strings = data.split("\t");

		String oName = strings[oNameIndex];
		if(oName.equals(""))
			return null;

		String country = strings[countryAndStateIndex];
		if(country.equals(""))
			return null;

		String state = blank;
		String city = blank;

		long pop = 0;
		if(popIndex != -1)
			pop = getPop(strings[popIndex]);

		return new Location(oName, country, state, city, pop, column, "");//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
	}

	private Location ParseUSACityToTextString(String data)
	{
		String[] strings = data.split("\t");

		String country = "us";
		String state = strings[0].trim();
		String city = strings[3].trim();
		String oName = city;

		
		

		long population = getPop(strings[6]);


		return new Location(oName, country, state, city, population, Column.city, city);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
	}

	private Location doCountryAndPop(String string1, String string2) 
	{
		ArrayList<String> lines = new ArrayList<String>();

		//		String[] strings = string1.split("\t");
		//		for(String temp : strings)
		//			lines.add(temp);
		//	
		//		strings = string2.split("\t");
		//		for(String temp : strings)
		//			lines.add(temp);

		String[] strings = (string1 + "\t" + string2).split("\t");
		for(String temp : strings)
			lines.add(temp);

		return new Location(lines.get(1), lines.get(1), blank, blank, getPop(lines.get(6)), Column.country, lines.get(4)); 
	}

	private Location fromCDHStates(String data, int oNameIndex, int countryAndStateIndex, int popIndex, Column column, int otherNamesIndex) 
	{

		String[] strings = data.split("\t");

		if(strings.length <= 2)
			return null;

		String oName = strings[oNameIndex].trim();
		if(oName.equals(""))
			return null;



		String[] tempStateAndCountry = strings[countryAndStateIndex].trim().split("-");
		String country = tempStateAndCountry[0];
		String state =  tempStateAndCountry[1];
		String city = blank;


		if(country.equals("us")) //-------------------------------------------SPECIAL FOR US, NOT TO ADD IT TWICE
			return null;



		long pop = 0;
		if(popIndex != -1)
			pop = getPop(strings[popIndex]);

		HashSet<String> matchNames = new  HashSet<String>();

		matchNames.add(oName);



		if(strings.length > 3)
		{
			String tempString = strings[otherNamesIndex].trim();
			String[] tempStrings = tempString.split(",");
			for(String string : tempStrings)
				matchNames.add(string);
		}

		return new Location(oName, country, state, city, pop, column, matchNames);//(String officialName, String country, String state, String city, long population, HashSet<String> otherNames)
	}

	private Location fromWorldCitiesPop(String data)
	{
		String[] strings = data.split(",");

		String oName = strings[1];
		String country = strings[0];

		if(country.equals("us"))
			return null;

		String state = blank;//strings[3].trim().toLowerCase();
		String city = strings[2];

		long pop = getPop(strings[4]);
		if(pop < minPop)
			return null;


		Location temp = new Location(oName, country, state, city, pop, Column.city);
		temp.matchNames.add(city);

		return temp;

	}


	//key _ _ _,  location
	public HashMap<String, Location> allLoc = new HashMap<String, Location>();

	static HashSet<String> removeEndings = new HashSet<String>();
	static HashMap<String, String> makeNiceList = new HashMap<String, String>();

	String blank = "_";
	long minPop = 3000;

	
	public void close()
	{
		allLoc = null;
		removeEndings = null;
	}
	

	public TextParser()
	{
	}


	
	public void loadText(String dataDir)
	{
		String tempFileLoc = null;
	
		Log.log("Loading individial text data files for processing and combining");


		
		tempFileLoc = dataDir + "/text/makeNice.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			String[] strings = string.split("\t");
			try
			{
				String key = strings[0];
				String value = " ";
				if(strings.length > 1)
					value = strings[1];
				
				if(key.length() > 1)
					value = " " + value + " ";
				makeNiceList.put(key, value);
			}
			catch (Exception e)
			{
				Log.log(Log.tab + "error in loading MakeNiceList: " + e + "for: " + string);
			}
		}
	
		
		
		
		
		
		tempFileLoc = dataDir + "/text/removeEndings.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, true))
			removeEndings.add(string.toLowerCase());
		//Log.log(Log.tab + removeEndings.size() + " removeEndings loaded");


		tempFileLoc = dataDir + "/text/raw/usaStates.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, false))
		{
			Location tempLoc = stndFromStatoids(string, 1,1,5,3, Column.state_province);//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
			addToAllLoc(tempLoc);
		}

		tempFileLoc = dataDir + "/text/raw/usaCityToState.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, true))
		{
			Location tempLoc = ParseUSACityToTextString(string);
			addToAllLoc(tempLoc);
		}



		tempFileLoc = dataDir + "/text/raw/countryAndPop.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		ArrayList<String> countryAndPop = LoadTextFile(tempFileLoc, null, false);
		for(int i = 0; i < countryAndPop.size();)
		{
			Location tempLoc = doCountryAndPop(countryAndPop.get(i), countryAndPop.get(i + 1));//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
			addToAllLoc(tempLoc);
			i += 2;
		}


		tempFileLoc = dataDir + "/text/raw/cdhStates.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		for(String string : LoadTextFile(tempFileLoc, null, true))
		{
			Location tempLoc = fromCDHStates(string, 2, 1, -1, Column.state_province, 3);//(String data, int oNameIndex, int countryAndStateIndex, int popIndex, int aliasIndex) 
			addToAllLoc(tempLoc);
		}



		int i = 0, j = 0;
		tempFileLoc = dataDir + "/text/raw/worldcitiespop.txt";
		Log.log(Log.tab + "Loading " + tempFileLoc);
		ArrayList<String> tempWorldCitiesPopList = LoadTextFile(tempFileLoc, null, true);
		int tempThing = tempWorldCitiesPopList.size() / 10; 
		for(String string : tempWorldCitiesPopList)
		{
			if(j++ % tempThing == 0)
			Log.log(Log.tab + Log.tab + j + "\t" + string);
		
			Location tempLoc = fromWorldCitiesPop(string);
			

			if(tempLoc != null)// tempLoc is null if  --> && tempLoc.population < minPop)
				continue;
			
			i++;
			addToAllLoc(tempLoc);
			
		}
		Log.log(Log.tab + Log.tab + i + "\tPotential Cities");


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

			if(strings.length == 1)// remove whole location it
			{
				String key = strings[0];

				Location tempLocation = this.allLoc.remove(key);
				if(tempLocation == null)
					Log.log("ERROR could not remove location: " + key + " key not found. either wrong key or target location does not exist", true);

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

				addToAllLoc(tempLoc);
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
					//value = makeNice(value);
					if(value.equals("") == false && tempLoc.matchNames.contains(value) == false)
					{
						tempLoc.matchNames.add(value);
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



	
	public static String makeSuperNice(String string)
	{
		if(string == null)
			return "";
		
		string = string.trim().toLowerCase();
		
		
		
		
		for(String key : makeNiceList.keySet())
		{
//			if(key.length() == 1)
				string = string.replace(key, makeNiceList.get(key));
//			else
//				string = string.replace(" " + key + " ", makeNiceList.get(key));
		}
		
		string = string.trim().toLowerCase();
		
		for	(String ending : removeEndings)
		{
			if(string.endsWith(ending))
			{
				string = string.replace(ending, "").trim();
				break;
			}
			
		}
		
		
	
		
		return string;
	}







	private boolean addToAllLoc(Location loc)
	{
		if(loc == null)
		{
			//Log.log("DEBUG: got a null Location while adding to allLoc...");
			return false;
		}



		Location old = this.allLoc.put(loc.getKey(), loc);

		if(old != null)
		{
			
			if(old.population > loc.population)
				this.allLoc.put(old.getKey(), old);
			
//			Log.log("DEBUG: duplacate key found while adding to allLoc...");
//			Log.log(Log.tab	+ "Added:   " + loc.toString());
//			Log.log(Log.tab	+ "Removed: " + old.toString());
//			return true;
		}

		return true;
	}

	public static long getPop(String string)
	{
		if(string == null) return 0;

		string = string.replace(",", "").trim();
		if(string.equals("") || string.equals(" "))
			return 0;

		Long tempLong = 0l;
		try
		{
			tempLong = Long.parseLong(string);
		}
		catch (Exception e)
		{
			Log.log("Unable to parse long:" + string, e);
			return 0;
		}
		return tempLong;
	}
}
