package TextParser;

public class AdminBuilder extends Builder
{
	
	public String countryCode;
	public String region;
	public String sub;
	public String name;
	public String nameASCII;
	public String geonameid;
	
	public String data;
	
	



	@Override
	public String getKey() 
	{
		return countryCode + "." + region + "." + nameASCII;
	}

	public String getStateKey() 
	{
		return countryCode + "." + region + "." + "_";
	}
	
	public String getCountryKey() 
	{
		return countryCode + "." + region + "." + "_";
	}
	
	
	@Override
	public String toString()
	{
		return getKey() + " " + nameASCII + " " + "\n";  //data + "\n";
	}
	
	
	

	
	public AdminBuilder(String data)
	{
		this.data = data;
		String strings[] = data.split("\t");
		
		String temp = normalize(strings[0]);
		String temps[] = temp.split("\\.");

		countryCode = normalize(temps[0]);
		region = normalize(temps[1]);
		if(temps.length >= 3)
			sub = normalize(temps[2]);
		else
			sub = "_";
		
		
		this.name = normalize(strings[1]);
		this.nameASCII = normalize(strings[2]);
		this.geonameid = normalize(strings[3]);
		
		
	}

	
	
	

	
}
