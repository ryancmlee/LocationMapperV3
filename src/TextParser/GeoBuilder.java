package TextParser;

import java.util.HashSet;

public class GeoBuilder extends Builder implements Comparable
{

	public String geonameid;//          = strings[0];			// integer id of record in geonames database
	public String name;//               = strings[1];			// name of geographical point (utf8) varchar(200)
	public String asciiname;//          = strings[2];			// name of geographical point in plain ascii characters, varchar(200)
	public String alternatenames;//     = strings[3];			// alternatenames, comma separated varchar(5000)
	public String latitude;//           = strings[4];			// latitude in decimal degrees (wgs84)
	public String longitude;//          = strings[5];			// longitude in decimal degrees (wgs84)
	public String featureClass;//       = strings[6];			// see http://www.geonames.org/export/codes.html, char(1)
	public String featureCode;//        = strings[7];			// see http://www.geonames.org/export/codes.html, varchar(10)
	public String countryCode;//     	  = strings[8];			// ISO-3166 2-letter country code, 2 characters
	public String cc3;//     			  = strings[9];			// alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
	public String admin1;//      		  = strings[10];		// fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
	public String admin2;//     		  = strings[11];		// code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 	
	public String admin3;//      		  = strings[12];		// code for third level administrative division, varchar(20)
	public String admin4;//      		  = strings[13];		// code for fourth level administrative division, varchar(20)
	private String populationString;//   = strings[14];		// bigint (8 byte int) 
	public String elevation;//          = strings[15];		// in meters, integer
	public String dem;//                = strings[16];		// digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
	public String timezone;//           = strings[17];		// the timezone id (see file timeZone.txt) varchar(40)
	public String modificationDate;//   = strings[18];		// date of last modification in yyyy-MM-dd format


	public HashSet<String> matchNames = new HashSet<String>();
	public String state = null;
	public long population = 0;



	@Override
	public String getKey() 
	{

		return countryCode + "." + admin1 + "." + asciiname;
	}

	public String getStateKey() 
	{

		return countryCode + "." + admin1 + "." + "_";
	}
	
	
	public String getCountryKey() 
	{

		return countryCode + "." + "_" + "." + "_";
	}

//	public String getKey2() 
//	{
//		return countryCode + "." + admin2 + "." + asciiname;
//	}

	@Override
	public String toString()
	{
		return countryCode + " " + admin1 + " " + admin2 + " " + asciiname + "\n";
	}



	public GeoBuilder(String data)
	{
		String[] strings = data.split("\t");

		this.geonameid          = normalize(strings[0]);		// integer id of record in geonames database
		this.name               = normalize(strings[1]);		// name of geographical point (utf8) varchar(200)
		this.asciiname          = normalize(strings[2]);		// name of geographical point in plain ascii characters, varchar(200)
		this.alternatenames     = normalize(strings[3]);		// alternatenames, comma separated varchar(5000)
		this.latitude           = normalize(strings[4]);		// latitude in decimal degrees (wgs84)
		this.longitude          = normalize(strings[5]);		// longitude in decimal degrees (wgs84)
		this.featureClass       = normalize(strings[6]);		// see http://www.geonames.org/export/codes.html, char(1)
		this.featureCode        = normalize(strings[7]);		// see http://www.geonames.org/export/codes.html, varchar(10)
		this.countryCode     	= normalize(strings[8]);		// ISO-3166 2-letter country code, 2 characters
		this.cc3     			= normalize(strings[9]);		// alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
		this.admin1     		= normalize(strings[10]);		// fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
		this.admin2     		= normalize(strings[11]);		// code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 	
		this.admin3      		= normalize(strings[12]);		// code for third level administrative division, varchar(20)
		this.admin4      		= normalize(strings[13]);		// code for fourth level administrative division, varchar(20)
		this.populationString   = normalize(strings[14]);		// bigint (8 byte int) 
		this.elevation          = normalize(strings[15]);		// in meters, integer
		this.dem                = normalize(strings[16]);		// digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
		this.timezone           = normalize(strings[17]);		// the timezone id (see file timeZone.txt) varchar(40)
		this.modificationDate   = normalize(strings[18]);		// date of last modification in yyyy-MM-dd format

		this.population = TextParser.getLong(populationString);

		if(population < TextParser.MinPop)
			return;

	
		
		
		strings = alternatenames.split(",");

		TextParser.addMatchNames(strings, matchNames);

		this.matchNames.add(name);
		this.matchNames.add(asciiname);

	}



	@Override
	public int compareTo(Object arg0) 
	{
		return toString().compareTo(arg0.toString());
	}




}
