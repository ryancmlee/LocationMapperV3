package LatLongParser;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import LocationMapper.Log;
import LocationMapper.Record;
import diewald_shapeFile.files.dbf.DBF_File;
import diewald_shapeFile.files.shp.SHP_File;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import diewald_shapeFile.files.shx.SHX_File;
import diewald_shapeFile.shapeFile.ShapeFile;



public class LatLongParser 
{
	Area world = new Area("Earth", Column.planet, null, null);
	
	
	public void setPossableLocations(Record record)
	{
		Area area = world;
		
		while((area = area.getFirstAreaWithin(record.longitude, record.latitude)) != null)
			if(record.locData.containsKey(area.column) == false)
				record.locData.put(area.column, area.officialName);
		
		
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
					ShapeFile shapeFile = new ShapeFile(dataDir + "/germany", "post_pl").READ(); 
			    	for(int i = 0; i < shapeFile.getDBF_recordCount(); i++)
			    	{
			    		String officialName = makeNice(shapeFile.getDBF_record(i,0));
						tempArea.subAreas.add(new Area( officialName, Column.postal_code, (ShpPolygon)shapeFile.getSHP_shape(i), tempArea));	
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
			Log.log(Log.tab +"Failed to load Germany: " + e.getMessage());
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
			Log.log(Log.tab + "Failed to load USA: "+e.getMessage());
			return false;
		}
		
		
		
		return true;
	}
	
	private void loadUSAStatesAndZips(String directory, Area primArea) throws Exception
	{
		Area usaArea = primArea;
		HashMap<String, Area> stateNumbertoStateAreaMap = new HashMap<String, Area>();
			
	    ShapeFile statesShapeFile = new ShapeFile(directory, "tl_2010_us_state10").READ(); //load State data  tl_2010_us_state10
	    		
  
    	for(int i = 0; i < statesShapeFile.getDBF_recordCount(); i++)
    	{
    		String name = makeNice(statesShapeFile.getDBF_record(i,5));
    		String stateNumber = makeNice(statesShapeFile.getDBF_record(i,2));// stateNumber = usaShapeFile.getDBF_record(i,2)
    	
			Area stateArea = new Area( name, Column.state_province, (ShpPolygon)statesShapeFile.getSHP_shape(i), usaArea); //(String name, String type, ShpPolygon shape, Area bigArea)
    	    stateNumbertoStateAreaMap.put(stateNumber, stateArea); 
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
		    	Area stateArea = stateNumbertoStateAreaMap.get(fipsNumber);
		    	
		    	//Log.log(Log.tab + Log.tab + Log.tab + i + "\tloading: " + stateArea.officialName + "  fips:\t" + fipsNumber);
		    	
		    	if ( stateArea != null) // make sure we got the state
		    	{
		    		for (int j = 0; j < stateShape.getSHP_shapeCount(); j++) // for each zip, add it. -- does not check for repeats
			    	{
		    			String officialName = makeNice(stateShape.getDBF_record(j, 1));
		    			Area zipArea = new Area(officialName, Column.postal_code, (ShpPolygon)stateShape.getSHP_shape(j), stateArea);//(String name, String type, ShpPolygon shape, Area bigArea)
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
