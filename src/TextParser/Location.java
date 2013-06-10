package TextParser;


import java.util.HashSet;

import LatLongParser.Column;

public class Location 
{

	public String outName;

	public String countryCode = "";
	public String stateCode = "";
	public String cityCode = "";

	
	public long population = 0;

	public HashSet<String> matchNames = new HashSet<String>();
	
	public int hits = 0;

	public Column column;

	public String level = null;
	
	
	
	
	
	private void doConstruction(String country, String state, String city, String outName, long population, Column column, String level, HashSet<String> matchNames)
	{		
		this.outName = TextParser.makeSuperNice(outName);
		this.countryCode = TextParser.makeSuperNice(country);
		this.stateCode = TextParser.makeSuperNice(state);
		this.cityCode =  TextParser.makeSuperNice(city);
		this.population = population;
		this.column = column;
		this.level = level;
		
	
		for(String string : matchNames)
		{
			string = TextParser.makeSuperNice(string);
			TextParser.addMatchNames(string, this.matchNames);
		}
	
		matchNames.clear();
	}
//	public Location(String outName, String countryDOTstate, String city, long population, String level, Column column,HashSet<String> matchNames)
//	{
//		String[] strings = countryDOTstate.split("\\.");
//		
//		if(strings.length < 2)
//		{
//			
//			int asdfds =234;	
//		
//		}
//		
//		
//		
//		doConstruction(strings[0], strings[1], city, outName, population, column, level, matchNames);
//	}
//	
	public Location(String outName, String country, String state, String city, long population, String level, Column column,HashSet<String> matchNames)
	{
		doConstruction(country, state, city, outName, population, column, level, matchNames);
	}
//	public Location(String outName, String country, String state, String city, long population, Column column, String level, String otherName)
//	{
//		HashSet<String> matchNames = new HashSet<String>();
//		matchNames.add(otherName);
//		
//		doConstruction(country, state, city, outName, population, column, level, matchNames);
//	}
//	public Location(String outName, String country, String state, String city, long population, Column column, String level)
//	{
//		doConstruction(country, state, city, outName, population, column, level, null);
//	}
//
	public Location(String string) throws Exception
	{
			
			String[] strings = string.split("\t");
			
			
			String countryCode = strings[0];
			String stateCode = strings[1];
			String cityCode = strings[2];
			
			String level = strings[1];
			
			String outName = strings[3];
			
			long population = TextParser.getLong(strings[5]);
			
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
				
		
			doConstruction(countryCode, stateCode, cityCode, outName, population, column, level, matchNames);
	}
	
	public String getKey()
	{
		return countryCode + "." + stateCode + "." + cityCode;
	}
	public String getStateKey()
	{
		return countryCode + "."  + stateCode + "."  + "_";
	}
	public String getCountryKey()
	{
		return countryCode + "."  + "_" + "."  + "_";
	}

	

	@Override 
	public String toString() 
	{
		String data = "";

		data += this.countryCode + ".";
		data += this.stateCode + ".";
		data += this.cityCode + "\t";
		data += this.level + "\t";
		data += this.outName + "\t";
		data += this.column + "\t";
		data += population + "\t";
		data += ",";									//all matchnames start and end with ,

		for(String string : this.matchNames)
			data += string + ","; 
		
		return data;
	}
}
