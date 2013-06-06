package TextParser;

import java.util.HashMap;
import java.util.HashSet;


public class Part
{

	public String fullPartName;
	public String partialPartName;
	

	
	HashSet<Location> locations = new HashSet<Location>();

	public HashMap<String, Part> nextParts = new HashMap<String, Part>();
	
	public Part prevPart;
	
	
	public Part(String fullPartName)
	{
		this.fullPartName = fullPartName;
	}
	public Part(Location location, String partialPartName)
	{
		HashSet<Location> locations = new HashSet<Location>();
		locations.add(location);
		javalame(locations, partialPartName);
	}
	public Part(HashSet<Location> locations, String partialPartName)
	{
		javalame(locations, partialPartName);
	}
	private void javalame(HashSet<Location> locations, String partialPartName)
	{
		this.locations = locations;
		this.partialPartName = partialPartName;
	}
	
	public boolean isTerminating()
	{
		//return this.fullPartName != null;
		return (this.locations.size() > 0);
	}
	public boolean isStarting()
	{
		return (this.prevPart != null);
	}
	
	
	
	
	private String myToString(String lastLine)
	{
		String data = lastLine + " ";

		data += partialPartName;
		
	
		lastLine = data;
		data += "\n";
		for(Part part : nextParts.values())
		{
			data += part.myToString(lastLine);
		}
		
		return data;
	}
	
	
	@Override public String toString()
	{
		
//		String string = partialPartName + " ";
//		String lastLine = "";
//		
//		lastLine = string;
//		for(Part part : nextParts.values())
//		{
//			
//			string += part.myToString(lastLine);
//
//			string += "\n";
//
//		}
		
		
		
		
		return partialPartName + "|" + fullPartName;
		//return myToString("");
	}

	
	public Part NextPart(String nextString)
	{
		if(this.nextParts != null && this.nextParts.containsKey(nextString))
		{
			return this.nextParts.get(nextString);
		}
		return null;
	}
	
//	public boolean hasNextPart(String nextString)
//	{
//		if(this.nextParts != null && this.nextParts.containsKey(nextString))
//		{
//			//outPart =  this.nextParts.get(nextString);
//			return true;
//		}
//		//outPart = this;
//		return false;
//	}
	
}
	