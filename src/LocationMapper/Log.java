package LocationMapper;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Period;


public class Log 
{
	static public String tab = "   ";
	static public String breakString = "--------------------------------------------------------"; 
	static DateTime startTime = new DateTime();
	public static boolean doConsolePrint = true;
	public static boolean doLog = true;
	
	//private static String log = "";
	
	
	private static ArrayList<String> log = new ArrayList<String>();
	
//	public static void logQue(String data)
//	{
//		log(data, false);
//	}
//	
//	
	
	
	public static void logSilent(String data)
	{
		log.add(data);
	}
	
	
	public static void log(String data)
	{
		log(data, false);
	}
	public static void log(String data, boolean doError)
	{
		String out = "<" + new DateTime() + "> " + data;
		
		if(doConsolePrint)
		{
			if(doError)
				System.err.println(out);
			else
				System.out.println(out);
		}
		if(doLog)
			log.add(out);
	}
	
	public static void log(String data, Exception e)
	{
		log(data);
		log(e + "");
		log(getStackTrace(e));
	}
	
	public static void logNow(String data, String name)
	{
		
		String saveLoc = LocationMapper.logDir + "/" + name;
		
		
		 try
		 {
			  FileWriter fstream = new FileWriter(saveLoc, false);
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			  out.write(data);
			  out.close();
			  fstream.close();
		 }
		 catch (Exception e)
		 {
			  System.err.println("Error: " + e.getMessage());
		 }
		
	}
	
	public static String getStackTrace(Exception e)
	{
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	
	public static boolean saveLog(String directory, String name, boolean doAppend)
	{
		Period period = new Period(startTime, new DateTime());
		String totalRunTime = period.toString();
		

		
		String saveLoc;
		if(directory == null || directory.equals(""))
			saveLoc = name;
		else
			saveLoc =  directory + "/" + name;
		
		
		log(breakString);
		log("Total Run time: " + totalRunTime);
		log("Saving log: " + saveLoc);
		log(breakString);
		log(breakString);		
		
		
		
		 try
		 {
			  FileWriter fstream = new FileWriter(saveLoc, doAppend);
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			  for(String string : log)
				  out.write(string + "\n");
			  out.close();
			  fstream.close();
		 }
		 catch (Exception e)
		 {
			  System.err.println("Error: " + e.getMessage());
		 }
		 
		 
		 return true;
	}
	
	
}
