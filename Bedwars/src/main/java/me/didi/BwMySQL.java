package me.didi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BwMySQL
{

	public static String username = "";
	public static String password = "";
	public static String host = "";
	public static String database = "";
	public static String port = "";
	private static Connection con;
	
	
	public static void connect() {
		try
		{
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
