package LatLongParser;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;

import LocationMapper.Log;
import LocationMapper.Record;
import TextParser.Location;
import diewald_shapeFile.files.dbf.DBF_Field;
import diewald_shapeFile.files.dbf.DBF_File;
import diewald_shapeFile.files.shp.SHP_File;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import diewald_shapeFile.files.shx.SHX_File;
import diewald_shapeFile.shapeFile.ShapeFile;



public class LatLongParser 
{
	Area world = new Area("Earth", Column.planet, null, null);
	
	public static HashSet<Area> hitAreas = new HashSet<Area>();
	
	public void setPossableLocations(Record record)
	{
		Area area = world;
		
		//returns each area it hits  so like, us then ca then 95032
		while((area = area.getFirstAreaWithin(record.longitude, record.latitude)) != null)
		{
			area.hits++;
			hitAreas.add(area);
		}
		
		
	}
	
	public void setPossableLocations(Location location)
	{
		Area area = world;
		
		//returns each area it hits  so like, us then ca then 95032
		while((area = area.getFirstAreaWithin(location.lon, location.lat)) != null)
		{
			if(area.officialName.equals("us"))
			{
				int asdf = 234;
				
			}
			
			area.hits++;
			hitAreas.add(area);
		}
		
		
	}
	
	
	public boolean loadData(String dataDir)
	{
		SHP_File.LOG_INFO = false;
		SHP_File.LOG_ONLOAD_HEADER = false;
		SHP_File.LOG_ONLOAD_CONTENT = false;
		
		SHX_File.LOG_INFO = false;
		SHX_File.LOG_ONLOAD_HEADER = false;
		SHX_File.LOG_ONLOAD_CONTENT = false;
		
		DBF_File.LOG_INFO = false;
		DBF_File.LOG_ONLOAD_HEADER = false;
		DBF_File.LOG_ONLOAD_CONTENT = false;
		
		
		Log.log("Loading Shapefiles...");
		Log.log(Log.tab + "Loading countires");
		try
		{
		
			ShapeFile countriesShapeFile = new ShapeFile(dataDir, "TM_WORLD_BORDERS-0.3").READ();
	    	for(int i = 0; i < countriesShapeFile.getDBF_recordCount(); i++)
	    	{
	    		String field = countriesShapeFile.getDBF_record(i, 0);
	    		String field2 = countriesShapeFile.getDBF_record(i, 1);
	    		String field3 = countriesShapeFile.getDBF_record(i, 2);
	    		String field4 = countriesShapeFile.getDBF_record(i, 3);
	    		String field5 = countriesShapeFile.getDBF_record(i, 4);
	    		String field6 = countriesShapeFile.getDBF_record(i, 5);
	    		String field7 = countriesShapeFile.getDBF_record(i, 6);
	    		String field8 = countriesShapeFile.getDBF_record(i, 7);
	    		String field9 = countriesShapeFile.getDBF_record(i, 8);
	    		
	    		
	    		String officialName = makeNice(countriesShapeFile.getDBF_record(i,1));
	    		world.subAreas.add(new Area(officialName, Column.country, (ShpPolygon)countriesShapeFile.getSHP_shape(i), world));
	    	}
	    }
		catch (Exception e)
		{
			Log.log(Log.tab +"Failed to Load all countires: " + e.getMessage());
			return false;
		}
		
		
		Log.log(Log.tab + "Loading Germany");
		try 
		{
			boolean tempSucsess = false;
			for(Area tempArea : world.subAreas)
			{
				if(tempArea.officialName.equals("de"))
				{
					ShapeFile deShapeFile = new ShapeFile(dataDir + "/germany", "post_pl").READ(); 
			    	for(int i = 0; i < deShapeFile.getDBF_recordCount(); i++)
			    	{
			    		
			    		String zip = deShapeFile.getDBF_record(i, 0);
			    		String zip2 = deShapeFile.getDBF_record(i, 1);
			    		String name = deShapeFile.getDBF_record(i, 2);
//			    		String field3 = deShapeFile.getDBF_record(i, 3);
//			    		String fips2 = deShapeFile.getDBF_record(i, 4);
//			    		String abrName = deShapeFile.getDBF_record(i, 5);
//			    		String fullName = deShapeFile.getDBF_record(i, 6);
//			    		String field7 = deShapeFile.getDBF_record(i, 7);
//			    		String field8 = deShapeFile.getDBF_record(i, 8);
//			    		String field9 = deShapeFile.getDBF_record(i, 9);
//			    		String field10 = deShapeFile.getDBF_record(i, 10);
//			    		String field11 = deShapeFile.getDBF_record(i, 11);
//			    		String lat = deShapeFile.getDBF_record(i, 12);
//			    		String lon = deShapeFile.getDBF_record(i, 13);
			    		
			    		
			    		String officialName = makeNice(deShapeFile.getDBF_record(i,0));
			    		Area deArea = new Area( officialName, Column.postal_code, (ShpPolygon)deShapeFile.getSHP_shape(i), tempArea);
			    		deArea.fips = "de";
			    		deArea.zip = zip;
			    		
						tempArea.subAreas.add(deArea);	
						tempSucsess = true;
			    	}
			    	break;
				}
			}
			if (!tempSucsess)
				throw new Exception(Log.tab +"failed to find country with name: de");
		}
		catch(Exception e)
		{
			Log.log(Log.tab +"Failed to load Germany: " + e);
			return false;
		}
		
		
		Log.log(Log.tab + "Loading USA (takes time)...");
		try 
		{
			
			
			
			boolean tempSucsess = false;
			for(Area tempArea : world.subAreas)
			{
				if(tempArea.officialName.equals("us"))
				{
					this.loadUSAStatesAndZips(dataDir + "/usa", tempArea);
					tempSucsess = true;
					break;
				}
			}
			if (!tempSucsess)
				throw new Exception("failed to find country with name: us");
		}
		catch(Exception e)
		{
			Log.log(Log.tab + "Failed to load USA: "+e);
			return false;
		}
		
		
		
		return true;
	}
	
	private void loadUSAStatesAndZips(String directory, Area primArea) throws Exception
	{
		Area usaArea = primArea;
		HashMap<String, Area> stateFIPStoStateAreaMap = new HashMap<String, Area>();
			
	    ShapeFile statesShapeFile = new ShapeFile(directory, "tl_2010_us_state10").READ(); //load State data  tl_2010_us_state10
	    		
  
    	for(int i = 0; i < statesShapeFile.getDBF_recordCount(); i++)
    	{
    		
    	
    		
    		
    		String field = statesShapeFile.getDBF_record(i, 0);
    		String field1 = statesShapeFile.getDBF_record(i, 1);
    		String fips = statesShapeFile.getDBF_record(i, 2);
    		String field3 = statesShapeFile.getDBF_record(i, 3);
    		String fips2 = statesShapeFile.getDBF_record(i, 4);
    		String abrName = statesShapeFile.getDBF_record(i, 5);
    		String fullName = statesShapeFile.getDBF_record(i, 6);
    		String field7 = statesShapeFile.getDBF_record(i, 7);
    		String field8 = statesShapeFile.getDBF_record(i, 8);
    		String field9 = statesShapeFile.getDBF_record(i, 9);
    		String field10 = statesShapeFile.getDBF_record(i, 10);
    		String field11 = statesShapeFile.getDBF_record(i, 11);
    		String lat = statesShapeFile.getDBF_record(i, 12);
    		String lon = statesShapeFile.getDBF_record(i, 13);

    		
    		
    		
    		
    		String name = makeNice(statesShapeFile.getDBF_record(i,5));
    		String stateNumber = makeNice(statesShapeFile.getDBF_record(i,2));// stateNumber = usaShapeFile.getDBF_record(i,2)
    	
			Area stateArea = new Area( name, Column.state_province, (ShpPolygon)statesShapeFile.getSHP_shape(i), usaArea); //(String name, String type, ShpPolygon shape, Area bigArea)
    	    stateArea.lat = Float.parseFloat(lat);
    	    stateArea.lon = Float.parseFloat(lon);
    	    stateArea.fips = fips;
			
			stateFIPStoStateAreaMap.put(stateNumber, stateArea); 
    		usaArea.subAreas.add(stateArea);
    	}
	    	
	    	
		    //parse state --> zip map
	    	String tempDir = directory +  "/states";
		    File[] stateFiles = finder(tempDir);
		   
		    //for each stateFile
		    for (int i = 0; i < stateFiles.length; i++)
		    {
	
		    	String tempString = stateFiles[i].getName();
		    	tempString = tempString.substring(0, tempString.length() - 4);
		    	ShapeFile stateShape = new ShapeFile(tempDir, tempString).READ();//open it
		    	
		    	
		    	//bad code, w/e, gets the stateArea by grabbing the first zipcode
		    	String fipsNumber = stateShape.getDBF_record(0,0);
		    	Area stateArea = stateFIPStoStateAreaMap.get(fipsNumber);
		    	
		    	//Log.log(Log.tab + Log.tab + Log.tab + i + "\tloading: " + stateArea.officialName + "  fips:\t" + fipsNumber);
		    	
		    	if ( stateArea != null) // make sure we got the state
		    	{
		    		for (int j = 0; j < stateShape.getSHP_shapeCount(); j++) // for each zip, add it. -- does not check for repeats
			    	{
		    			
		    			String field = stateShape.getDBF_record(j, 0);
		        		String zip = stateShape.getDBF_record(j, 1);
		        		String field2 = stateShape.getDBF_record(j, 2);
		        		String field3 = stateShape.getDBF_record(j, 3);
		        		String field4 = stateShape.getDBF_record(j, 4);
		        		String field5 = stateShape.getDBF_record(j, 5);
		        		String field6 = stateShape.getDBF_record(j, 6);
		        		String field7 = stateShape.getDBF_record(j, 7);
		        		String lat = stateShape.getDBF_record(j, 8);
		        		String lon = stateShape.getDBF_record(j, 9);
		        		String field10 = stateShape.getDBF_record(j, 10);

		        		
		    			
		    			String officialName = makeNice(stateShape.getDBF_record(j, 1));
		    			Area zipArea = new Area(officialName, Column.postal_code, (ShpPolygon)stateShape.getSHP_shape(j), stateArea);//(String name, String type, ShpPolygon shape, Area bigArea)
		    			zipArea.lat = Float.parseFloat(lat);
		    			zipArea.lon = Float.parseFloat(lon);
		    			zipArea.fips = stateArea.fips;
		    			zipArea.zip = zip;
		    			
		    			stateArea.subAreas.add(zipArea);
			    	}	
		    	}
		    	else
		    		Log.log("Could not find USA state for state with FIPS number=" + fipsNumber);
		    	
		    }
		
		  
	}
	
	
	
	
	public File[] finder(String dirName)
    {
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() 
    	{ 
    		public boolean accept(File dir, String filename)
            { 
        	 	return filename.endsWith(".shp"); 
            }
    	} );
    }	
	public String makeNice(String string)
	{
		if(string == null)
			return "";
		
		string = string.trim();
		string = string.toLowerCase();
		
		return string;
	}
}
