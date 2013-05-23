package LocationMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLConnection 
{
	public final int timeout = 5;
	public final int batchCount = 5000;
	public int parseCount = 0;
	
	
	Connection connection = null;
	ResultSet results = null;
	
	public final String statement = "" +
	"SELECT id, interaction_geo_latitude, interaction_geo_longitude, twitter_user_location from datasift_results " +
	"WHERE country is null AND id > 25800000 AND id < 27000000" + //" + // id > 25800000 " + //
	"AND (twitter_user_location is not null or interaction_geo_latitude is not null)";
//	final String statement = "" +
//			"SELECT id, interaction_geo_latitude, interaction_geo_longitude, twitter_user_location, twitter_user_lang " +
//			"from datasift_results " +
//			"WHERE interaction_created_at > '2013-04-01 00:00:00' " +
//			"AND datasift_stream_id in (78, 88) " +
//			"AND (twitter_user_location is not null or interaction_geo_latitude is not null)";
	
	
	
	public String address = null;
	public String tableName = null;
	public String port = null;
	public String userName = null;
	public String password = null;
	
	
	
	public SQLConnection(String address, String tableName, String port, String userName, String password)
	{
		this.address = address;
		this.tableName = tableName;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	
	
	String sendString = "";
	
	

	public boolean updateRecord(Record record)
	{
		
		String tempString = record.getUpdateStatement();
		sendString += tempString;
		

		if(parseCount % this.batchCount == 0)
		{
			try
			{
				flush();
				Log.log("ParseCount = " + parseCount);
			}
			catch (Exception e)
			{
				Log.log("ERROR: stmt.executeUpdate(sendString) threw error: " + e.getMessage());
				Log.log("dumping statement : " + e.getMessage());
				Log.log(sendString);

			}
			sendString = "";
			
		}
		
		
		
		
		return true;
	}
	
	
	
	
	
	public void flush() throws SQLException
	{
		parseCount = 0;

		Statement stmt = connection.createStatement();
		stmt.executeUpdate(sendString);
		sendString = "";

	}
	
	
	public void close() throws SQLException
	{
		flush();
		connection.close();
	}
	
	
	public boolean isConnected()
	{
		boolean data = false;
		if(connection == null)
			return data;
		
		try 
		{
			data = connection.isValid(timeout);
		} 
		catch (SQLException e) 
		{
			Log.log("", e);
		}
		
		return data;
	}
	
	public ResultSet getData()
	{
		results = null;
		if(this.Connect() == false)
		{
			Log.log("ERROR: unable to get data from server");
			Mapper.Exit(4);
		}
		
		try
		{
			Statement stmt = connection.createStatement();
			Log.log("Querying Server: + statement");
			results = stmt.executeQuery(statement);
		}
		catch (Exception e)
		{
			Log.log("ERROR: Query Failed: " + e.getMessage());
			return null;
		}
		
		
		return results;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean Connect()
	{

		if(isConnected())
			return true;
		
		try
		{
		    Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException cnfe)
		{
		      Log.log("ERROR: Could not find the JDBC driver!");
		      return false;
		}
		
		String connectionString = "jdbc:postgresql://" + address +":" + port + "/" + tableName;
		Log.log("Connecting to " + connectionString);
		try 
		{
			connection = DriverManager.getConnection(connectionString, userName, password); 
			Log.log("Connection is full of success!");
		} 
		catch (SQLException e) 
		{
			Log.log("ERROR: Could not connect: " + e.getMessage());
			return false;
		}
		
		if (connection == null) //this should never happen
		{
			Log.log("ERROR: Could not connect: LocationMapper.getDataFromServer.sqlConnection == null...hmmmm this should never happen");
			return false;
		} 
		
		
		
		
		return isConnected();
	}
	
}
