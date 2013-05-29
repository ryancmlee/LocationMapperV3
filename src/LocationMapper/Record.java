package LocationMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import LatLongParser.Area;
import LatLongParser.Column;
import TextParser.Location;



public class Record 
{
	
	int id;
	public float latitude;
	public float longitude;
	public String twitter_user_location;
	public String twitter_user_lang;
	
	
	
	public ArrayList<Location> possableLocations = new ArrayList<Location>();
	public HashMap<Column, String> locData = new HashMap<Column, String>();
	public HashMap<Column, Location> textData = new HashMap<Column, Location>();

	
	public Record(int id, float latitude, float longitude, String twitter_user_location, String twitter_user_lang)
	{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.twitter_user_location = twitter_user_location;
		this.twitter_user_lang = twitter_user_lang;
	}
	
	

//	public String getUpdateStatement()
//	{
//		
//		String tempStatement = "UPDATE datasift_results SET "; 
//
//		ArrayList<Column> tempColumn = new ArrayList<Column>();
//		
//		int i = 0;
//		for(Entry<Column, String> entry : locData.entrySet())
//		{
//			String value = entry.getValue();
//			Column key = entry.getKey();
//			
////			//if(locMapper.standardFormatMap.containsKey(value))
////			//	value = locMapper.standardFormatMap.get(value);
//			
//		
//			
//
//			if(value.equals("united states") || value.equals("us"))
//				tempStatement += key + "='" + "United States" + "'";
//			else
//				tempStatement += key + "='" + value.replace("'", "''") + "'";
//			
//			tempColumn.add(key);
//			
//			if(i++ < locData.size() - 1)
//				tempStatement += ",";
//			tempStatement += " ";
//			
//			
//		}	
//		
//		
//		int j = 0;
//		if(i == 0)
//		{
//			
//			for(Entry<Column, Location> entry : textData.entrySet())
//			{
//				String value = entry.getValue().outName;
//				Column key = entry.getKey();
//				
//				
//				
//				if(tempColumn.contains(key))// || key != Column.country)// remove this?
//					continue;
//				
//	//			if(locMapper.standardFormatMap.containsKey(value))
//	//				value = locMapper.standardFormatMap.get(value);
//				
//				if(value.equals("us") && key == Column.country)
//					tempStatement += key + "='" + "United States" + "'";
//				else
//					tempStatement += key + "='" + value.replace("'", "''") + "'";
//				
//				
//				if(j++ < textData.size() - 1)
//					tempStatement += ",";
//				tempStatement += " ";
//			}
//			
//		}
//
//		
//		
//		
//		if(j == 0 && i == 0)
//			tempStatement += "country='NONE' ";
//		
//
//		
//		return  tempStatement + "WHERE id=" + id + "; ";
//		
//		
//	}
	final String countryNone =  "'NONE'";
	public String getUpdateStatement()
	{
		
		String country = countryNone;
		String state = "NULL";
		String city = "NULL";
		String zip = "NULL";

		
		for(Entry<Column, String> entry : locData.entrySet())
		{
			Column key = entry.getKey();
			String value = entry.getValue();
			
	
			if(key == Column.country && country.equals(countryNone))
			{
				country = "'" + value + "'";
			}
			else if(key == Column.state_province && state.equals("NULL"))
			{
				state = "'" + value + "'";
			}
			else if(key == Column.city && city.equals("NULL"))
			{
				city = "'" + value + "'";
			}
			else if(key == Column.postal_code && zip.equals("NULL"))
			{
				zip = "'" + value + "'";
			}
			
		}
		
		for(Entry<Column, Location> entry : textData.entrySet())
		{
			Column key = entry.getKey();
			String value = entry.getValue().outName;
			
	
			if(key == Column.country && country.equals(countryNone))
			{
				country = "'" + value + "'";
			}
			else if(key == Column.state_province && state.equals("NULL"))
			{
				state = "'" + value + "'";
			}
			else if(key == Column.city && city.equals("NULL"))
			{
				city = "'" + value + "'";
			}
			else if(key == Column.postal_code && zip.equals("NULL"))
			{
				zip = "'" + value + "'";
			}
			
		}
			

		String sendString = "UPDATE datasift_results SET country=" + country + " , state_province=" + state + " , city=" + city + " , postal_code=" + zip + " WHERE id=" + id + "; ";
		

		return sendString;
	}
	
	public String toString()
	{
		
		return twitter_user_location;
		
	}
	
	
	
}

