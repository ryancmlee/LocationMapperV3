package TextParser;


import java.util.ArrayList;
import java.util.HashSet;

import LatLongParser.Column;

public class Location 
{

	public String outName;

	public String countryCode = "";
	public String stateCode = "";
	public String cityCode = "";

	public long population = 0;

	public ArrayList<String> matchNames = new ArrayList<String>();
	
	public int hits = 0;

	public Column column;



	private void doConstruction(String country, String state, String city, String outName, long population, Column column, HashSet<String> matchNames)
	{
		
		String[] temps = outName.split(",");	//take the first part of a name... forgot why i do this
		if(temps.length > 1)
			outName = temps[0];
		
		this.outName = outName.trim();
		this.countryCode = country;
		this.stateCode = state;
		this.cityCode =  city;
		this.population = population;
		this.column = column;

		
		if(matchNames != null)
		{
			for(String string : matchNames)
				this.matchNames.add(string);
		}
	
	}
	public Location(String outName, String country, String state, String city, long population, Column column, HashSet<String> matchNames)
	{
		doConstruction(country, state, city, outName, population, column, matchNames);
	}
	public Location(String outName, String country, String state, String city, long population, Column column, String otherName)
	{
		HashSet<String> matchNames = new HashSet<String>();
		matchNames.add(otherName);
		
		doConstruction(country, state, city, outName, population, column, matchNames);
	}
	public Location(String outName, String country, String state, String city, long population, Column column)
	{
		doConstruction(country, state, city, outName, population, column, null);
	}

	public Location(String string) throws Exception
	{
			
			String[] strings = string.split("\t");
			
			String outName = strings[3];
			String countryCode = strings[0];
			String stateCode = strings[1];
			String cityCode = strings[2];
			long population = TextParser.getPop(strings[5]);
			Column column = Column.valueOf(strings[4]);
			HashSet<String> matchNames = new HashSet<String>();
			
			if(strings.length > 6)
			{
				for (String temp: strings[6].split(","))
				{
					//temp = temp.trim().toLowerCase();
					//if(loc.removeMatchNames.contains(temp) == false)
					matchNames.add(temp);
				}
			}
		
			if(matchNames.size() == 0)
			{
				//Log.log("ERROR in creating new location from string.  location.matchNames.size == 0. for: " + string);
				throw new Exception("ERROR in creating new location from string.  location.matchNames.size == 0. for: " + string);
			}
				
		
			doConstruction(countryCode, stateCode, cityCode, outName, population, column, matchNames);
	}
	
	public String getKey()
	{
		return countryCode + stateCode + cityCode;
	}
	public String getStateKey()
	{
		return countryCode + stateCode + "_";
	}
	public String getCountryKey()
	{
		return countryCode + "_" + "_";
	}

	
	
//	public static String makeNice(String string)
//	{
//		
//		if(string == null)
//			return "";
//		
//		string = string.trim();
//		string = string.toLowerCase();
//		
//		
//		for	(String ending : TextParser.removeEndings)
//		{
//			if(string.endsWith(ending))
//			{
//				string = string.replace(ending, "");
//				return string.trim();
//			}
//		}
//		
//		
//		return string;
//	}


	@Override 
	public String toString() 
	{
		String data = "";

		data += this.countryCode + "\t";
		data += this.stateCode + "\t";
		data += this.cityCode + "\t";
		data += this.outName + "\t";
		data += this.column + "\t";
		data += population + "\t";

		for(String string : this.matchNames)
			data += string + ","; 

		return data;
	}
}
