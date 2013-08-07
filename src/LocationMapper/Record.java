package LocationMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import LatLongParser.Column;
import TextParser.Location;



public class Record 
{
	final static String countryNone =  "'NONE'";
	
	
	int id;
	public float latitude;
	public float longitude;
	public String twitter_user_location;
	public String twitter_user_lang;
	
	
	
	public ArrayList<Location> textData = new ArrayList<Location>();
	public HashMap<Column, String> latLongData = new HashMap<Column, String>();
	public HashMap<Column, Location> probableLocations = new HashMap<Column, Location>();

	
	public Record(int id, float latitude, float longitude, String twitter_user_location, String twitter_user_lang)
	{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.twitter_user_location = twitter_user_location;
		this.twitter_user_lang = twitter_user_lang;
	}
	
	
	
	public String getUpdateStatement()
	{
		
		String country = countryNone;
		String state = "NULL";
		String city = "NULL";
		String zip = "NULL";
		String county = "NULL";
		
		
		if(latLongData.size() >= 1)
		{
			for(Entry<Column, String> entry : latLongData.entrySet())
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
		}
		else
		{
			Location probLoc = null;
			if(probableLocations.containsKey(Column.city))
			{
				probLoc = probableLocations.get(Column.city);
				city = "'" + probLoc.cityCode + "'";
				state = "'" + probLoc.stateCode + "'";
				country = "'" + probLoc.countryCode + "'";
			}
			else if (probableLocations.containsKey(Column.state_province))
			{
				probLoc = probableLocations.get(Column.state_province);
				state = "'" + probLoc.stateCode + "'";
				country = "'" + probLoc.countryCode + "'";
			}
			else if(probableLocations.containsKey(Column.country))										// we have a country
			{
				probLoc = probableLocations.get(Column.country);
				country = "'" + probLoc.countryCode + "'";
			}
			
			
			if(probLoc != null && probLoc.county != null &&  probLoc.county.equals("") == false )
				county = "'" + probLoc.county  + "'";;
		}
		
	
		probableLocations.clear();
		textData.clear();
		latLongData.clear();
		

		if(country.equals("'us'"))
			country = "'United States'";
		

		String sendString = "UPDATE datasift_results SET country=" + country + " , state_province=" + state + " , city=" + city + " , postal_code=" + zip  + " , full_address=" + county + " WHERE id=" + id + "; ";
		

		return sendString;
	}
	
	public String toString()
	{
		
		return twitter_user_location;
		
	}
	
	
	
}

