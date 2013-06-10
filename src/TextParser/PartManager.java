package TextParser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import LatLongParser.Column;
import LocationMapper.Log;
import LocationMapper.Record;




public class PartManager 
{


	
	private Part partZero = new Part("PartZero");
	public String regex;
	
	public int countryCount = 0;
	public int stateCount = 0;;
	public int cityCount = 0;;
	public int mostCommonPartSize = 0;
	Part mostCommonPart;
	
	public HashMap<String, Location> allLocations;// = new HashMap<String, Location>();
	
//	public PartManager(String primDir, String se, String regex)
//	{
//		this.regex = regex;
//		
//	
//		for(Location loc : TextParser.getAllLocationsFromMaster(primDir, se))
//		{
//			if(loc.column == Column.city)
//				cityCount++;
//			if(loc.column == Column.state_province)
//				stateCount++;
//			if(loc.column == Column.country)
//				countryCount++;
//			
//			this.addLocation(loc);
//		}
//		
//		
//		
//	}
	
	public PartManager(HashMap<String, Location> allLocations, String regex)
	{
		this.regex = regex;
	

				
		Log.log("Building Location String Graph");
		
				
		this.allLocations = new HashMap<String, Location>(allLocations.size());
		for(Location loc : allLocations.values())
			this.addLocation(loc);
		
		
		Log.log(Log.tab + countryCount + " Countries Loaded");
		Log.log(Log.tab + stateCount + " States Loaded");
		Log.log(Log.tab + cityCount + " Cities Loaded");
		Log.log(Log.tab + "Most Common Part is:" + mostCommonPart.fullPartName + " with " + mostCommonPartSize + " hits");
		Log.log("Graph Completed");
		
	}
	
	
	public ArrayList<Location> getLocations(String text)
	{
		HashSet<Location> locations = new HashSet<Location>();
		
		ArrayList<Location> returnListofLocations = new ArrayList<Location>();
		
		
		if(text == null)
			return returnListofLocations;
		
		String[] strings = text.split(regex);
		
		
		ArrayList<String> niceStrings = new ArrayList<String>();
		for(String string : strings)
		{
			string = string.trim();
			if(string.equals("") == false)
				niceStrings.add(string);
		}

		Part currPart = this.partZero;
		ArrayList<Part> prevParts = new ArrayList<Part>();
		int j = 0;

		for(int i = 0; i < niceStrings.size(); i++)
		{
			String string = niceStrings.get(i);
			
			if(currPart.NextPart(string) != null) //we have a part match
			{
				currPart = currPart.NextPart(string);
				prevParts.add(currPart);
			}
			else if (currPart.isTerminating())//we have a string match!
			{
				locations.addAll(currPart.locations);
				currPart = this.partZero;
				prevParts.clear();
				i--;
				j = i;
			}
			else //we have no match and its not the end of the text
			{
				if(currPart == this.partZero)
					j = i;
				else
				{
					//boolean isBatckTrackFound = false;
					int backTrackAmmount = 0;
					Collections.reverse(prevParts);
					for(Part part : prevParts)
					{
						backTrackAmmount++;
						
						if(part.isTerminating())
						{
							locations.addAll(part.locations);
							//j++;
							break;
						}
					}
					j++;
					i = j + prevParts.size() - backTrackAmmount;
					prevParts.clear();
					currPart = this.partZero;
				}
			}
		}
		
		if(currPart != partZero && currPart.isTerminating())
		{
			locations.addAll(currPart.locations);
		}
		
		
		returnListofLocations.addAll(locations);
		
		
		
		return returnListofLocations;
	}
	
	
	public void setMatchStrings(Record record)
	{
		
		record.possableLocations = getLocations(record.twitter_user_location);
		
	}
	
	
	
	
	
	public void addLocation(Location location)
	{
		
		this.allLocations.put(location.getKey(), location);
		
		if(location.getKey().equals("es._._"))
		{
			
			int asdf =234;
		}
		
		
		if(location.column == Column.city)
			cityCount++;
		if(location.column == Column.state_province)
			stateCount++;
		if(location.column == Column.country)
			countryCount++;
		
		
//		location.cityCode = TextParser.makeSuperNice(location.cityCode);
//		location.stateCode = TextParser.makeSuperNice(location.stateCode);
//		location.countryCode = TextParser.makeSuperNice(location.countryCode);
//		
//
//		for(int i = 0; i < location.matchNames.size(); i++)
//		{
//			String temp = location.matchNames.get(i);
//			temp = TextParser.makeSuperNice(temp);
//			location.matchNames.set(i, temp);
//		}
		
		
		
		for (String name : location.matchNames)
		{
			ArrayList<String> locParts = new ArrayList<String>();
			
			
			
			try
			{
				for (String temp: name.split(regex))
					locParts .add(temp);
			}
			catch (Exception e)
			{
				Log.log("Error in splitting location name with regex ",e);
				continue;
			}
			
			if(locParts.size() == 0)
				continue;

			Part currPart = this.partZero;
			
			for(int i = 0; i < locParts.size(); i++)
			{
				String stringPart = locParts.get(i);
				
				if(currPart.NextPart(stringPart) != null)
				{
					currPart = currPart.NextPart(stringPart);
					
					if (name.endsWith(stringPart))
					{
						currPart.locations.add(location);
						currPart.fullPartName = name;
						
						if(currPart.locations.size() > this.mostCommonPartSize)
						{
							mostCommonPartSize = currPart.locations.size();
							mostCommonPart = currPart;
						}
					}
					
				}
				else if (name.endsWith(stringPart))
				{
					Part newPart = new Part(stringPart);
					newPart.locations.add(location);
					newPart.fullPartName = name;
					currPart.nextParts.put(stringPart, newPart);
				}
				else 
				{
					Part newPart = new Part(stringPart);
					currPart.nextParts.put(stringPart, newPart);
					currPart = newPart;
				}
			}
			
			
		
			
			
		}
	}
	
	
}
