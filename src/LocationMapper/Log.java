package LocationMapper;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;


public class Log 
{
	static public String tab = "   ";
	static public String breakString = "--------------------------------------------------------"; 
	static DateTime startTime = new DateTime();
	public static boolean doConsolePrint = true;
	
	private static String log = "";
	
	
	public static void log(String data)
	{
		String out = "<" + new DateTime() + "> " + data;
		
		if(doConsolePrint)
			System.out.println(out);
		
		log += out + "\r\n";
	}
	
	public static void log(String data, Exception e)
	{
		log(data);
		log(e + "");
		log(getStackTrace(e));
	}
	
	
	public static String getStackTrace(Exception e)
	{
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	
	public static boolean saveLog(String directory, String name, boolean doAppend)
	{
		Interval interval = new Interval(startTime, new DateTime());
		String totalRunTime = interval.toString();
		
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
			  out.write(log);
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
