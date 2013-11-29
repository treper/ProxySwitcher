package com.tudou.proxyserver;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.tudou.encoding.EncodingDetector;

public class DbConnectionPool{
	
	private static DbConnectionPool instance = null;
	private static BoneCP connectionPool = null;
	
	private DbConnectionPool(){
	}
	
	public static synchronized  DbConnectionPool getInstance() 
	{
		if(null == instance)
		{
			instance = new DbConnectionPool();
			instance.init();
		}
		return instance;
		
	}
	
	private boolean init() 
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			
			BoneCPConfig config = new BoneCPConfig();
			Properties prop = new Properties();
			InputStream fis = DbConnectionPool.class.getResourceAsStream("/db.properties");
			//FileInputStream fis = new FileInputStream("db.properties");
			prop.load(fis);
			String url = prop.getProperty("url");
			String username = prop.getProperty("username");
			String password = prop.getProperty("password");

//			config.setJdbcUrl("jdbc:mysql://10.5.20.62:3306/piracyfinder?useUnicode=true&characterEncoding=UTF8");
//			config.setUsername("proxyserver");
//			config.setPassword("proxyserver");	
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			//config.setMinConnectionsPerPartition(30);
			config.setMaxConnectionsPerPartition(10);
			config.setAcquireIncrement(5);  
			config.setPartitionCount(4);
			connectionPool = new BoneCP(config);
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			return false;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}

		if(connectionPool!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Connection getConnection()
	{
		Connection conn = null;
		try{
		if(null!=connectionPool)
		{
			conn = connectionPool.getConnection();
		}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		return conn;
	}
	
	public void shutDown()
	{
		if(connectionPool!=null)
		{
			connectionPool.shutdown();
		}
	}
	
	public static void main(String args[])
	{
		DbConnectionPool pldbcp = DbConnectionPool.getInstance();
		Connection conn = pldbcp.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
/*			stmt = conn.prepareStatement(SqlString.SelectRadomProxyByCity);
			String location = "北京";
			stmt.setString(1, location);
			rs = stmt.executeQuery();
			if(rs.next())
			{
				String p = rs.getString("proxy");
				System.out.print(p);
			}*/
			stmt = conn.prepareStatement(SqlString.SelectDistinctCities);
			rs = stmt.executeQuery();
			StringBuilder cities = new StringBuilder();
			while(rs.next())
			{
				String city = rs.getString(1);
				cities.append(city).append(",");
				
			}
			cities.delete(cities.length()-1, cities.length());
			System.out.print(cities);
			EncodingDetector.detect(cities.toString());
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		pldbcp.shutDown();
	}
}
