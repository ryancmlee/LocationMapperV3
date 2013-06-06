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


	public String geoKey = null;
	public String geoNameID = null;
	
	
	
	
	
	
	
	private void doConstruction(String country, String state, String city, String outName, long population, Column column, HashSet<String> matchNames)
	{
		
		String[] temps = outName.split(",");	//take the first part of a name... forgot why i do this
		if(temps.length > 1)
			outName = temps[0];
		
		this.outName = TextParser.makeSuperNice(outName).replace("'", "");
		this.countryCode = TextParser.makeSuperNice(country);
		this.stateCode = TextParser.makeSuperNice(state);
		this.cityCode =  TextParser.makeSuperNice(city);
		this.population = population;
		this.column = column;

		
		if(matchNames != null)
		{
			for(String string : matchNames)
				if(string.equals("_") == false)
					this.matchNames.add(TextParser.makeSuperNice(string));
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
				
		
			doConstruction(countryCode, stateCode, cityCode, outName, population, column, matchNames);
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
		data += this.cityCode + ".";
		data += this.geoNameID + "\t";
		data += this.outName + "\t";
		data += this.column + "\t";
		data += population + "\t";

		for(String string : this.matchNames)
			data += string + ","; 

		data = data.substring(0, data.length() - 1);
		
		return data;
	}
}
