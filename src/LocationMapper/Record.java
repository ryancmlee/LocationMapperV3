package LocationMapper;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	
	
	public String getUpdateStatement()
	{
		
		String country = countryNone;
		String state = "NULL";
		String city = "NULL";
		String zip = "NULL";

		
//		for(Entry<Column, String> entry : locData.entrySet())
//		{
//			Column key = entry.getKey();
//			String value = entry.getValue();
//			
//			if(key == Column.country && value.equals("us"))
//				value = "United States";
//			
//	
//			if(key == Column.country && country.equals(countryNone))
//			{
//				country = "'" + value + "'";
//			}
//			else if(key == Column.state_province && state.equals("NULL"))
//			{
//				state = "'" + value + "'";
//			}
//			else if(key == Column.city && city.equals("NULL"))
//			{
//				city = "'" + value + "'";
//			}
//			else if(key == Column.postal_code && zip.equals("NULL"))
//			{
//				zip = "'" + value + "'";
//			}
//			
//		}
//		
		
		
		if(textData.containsKey(Column.country))										// we have a country
		{
			Location tempCountry = textData.get(Column.country);
			country = "'" + tempCountry.countryCode + "'";
			
			if(textData.containsKey(Column.state_province))						
			{
				Location tempState = textData.get(Column.state_province);
				if(tempState.countryCode.equals(tempCountry.countryCode))				// we have a state in the country
				{
					state = "'" + tempState.stateCode + "'";
					country = "'" + tempState.countryCode + "'";
					
					
					if(textData.containsKey(Column.city))
					{
						Location tempCity = textData.get(Column.city);
						if(tempCity.stateCode.equals(tempState.stateCode))		//we have a city in the state in the country
						{
							city = "'" + tempCity.cityCode + "'";
							state = "'" + tempCity.stateCode + "'";
							country = "'" + tempCity.countryCode + "'";
						}
					}
					
				}
			}
			
			
			
		}
		else if(textData.containsKey(Column.state_province))				//we have a state
		{
			Location tempState = textData.get(Column.state_province);
			
			state = "'" + tempState.stateCode + "'";
			country = "'" + tempState.countryCode + "'";
			
			if(textData.containsKey(Column.city))
			{
				Location tempCity = textData.get(Column.city);
				if(tempCity.stateCode.equals(tempState.stateCode))		//we have a city in the state
				{
					city = "'" + tempCity.cityCode + "'";
					state = "'" + tempCity.stateCode + "'";
					country = "'" + tempCity.countryCode + "'";
				}
			}
			
		}
		else if(textData.containsKey(Column.city))
		{
			Location temp = textData.get(Column.city);
			
			city = "'" + temp.cityCode + "'";
			state = "'" + temp.stateCode + "'";
			country = "'" + temp.countryCode + "'";
		}
		
		
//		
//		for(Entry<Column, Location> entry : textData.entrySet())
//		{
//			Column key = entry.getKey();
//			String value = entry.getValue().outName;
//			
//			if(key == Column.country && value.equals("us"))
//				value = "United States";
//	
//			if(key == Column.country && country.equals(countryNone))
//			{
//				
//				country = "'" + value + "'";
//			}
//			else if(key == Column.state_province && state.equals("NULL"))
//			{
//				
//				state = "'" + value + "'";
//				country = "'" + entry.getValue().countryCode + "'";
//			}
//			else if(key == Column.city && city.equals("NULL"))
//			{
//				city = "'" + value + "'";
//				state = "'" + entry.getValue().stateCode + "'";
//				country = "'" + entry.getValue().countryCode + "'";
//			}
//			else if(key == Column.postal_code && zip.equals("NULL"))
//			{
//				zip = "'" + value + "'";
//			}
//			
//		}
			
		
		
		if(country.equals("'us'"))
			country = "'United States'";
		
//		country = country.replaceAll("'", "''");
//		state = state.replaceAll("'", "''");
//		city = city.replaceAll("'", "''");


		String sendString = "UPDATE datasift_results SET country=" + country + " , state_province=" + state + " , city=" + city + " , postal_code=" + zip + " WHERE id=" + id + "; ";
		

		return sendString;
	}
	
	public String toString()
	{
		
		return twitter_user_location;
		
	}
	
	
	
}

