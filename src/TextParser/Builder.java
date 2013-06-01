package TextParser;

public abstract class Builder 
{
	
	
	public abstract String getKey();
	
	protected String normalize(String string)
	{
		String normalized;
		if(string == null || string.equals(""))
			normalized = "_";
		else
			normalized = string.trim();
		
		return normalized;
	}

}
