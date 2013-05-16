package LocationMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLConnection 
{
	public final int timeout = 5;
	
	
	Connection connection = null;
	Statement statement = null;
	ResultSet results = null;
	
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
