package com.tudou.proxyserver;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tudou.proxyserver.SqlString;
import com.tudou.proxyserver.DbConnectionPool;


public class ProxyServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(ProxyServlet.class);
	private static Connection conn = null;


	public void init(ServletConfig config) throws ServletException {
		Properties props = new Properties();
		try {
			props.load(ProxyServlet.class.getResourceAsStream("/log4j.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	    super.init(config);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("client get");
		conn = DbConnectionPool.getInstance().getConnection();
		if(conn !=null){
			log.error("get database connection failed!");
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject obj = new JSONObject();
		//String proxy = null;
		
		//response.setContentType("application/json");
		response.setHeader("Content-type","application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
/*			stmt = conn.prepareStatement(SqlString.SelectRadomProxy);
			rs = stmt.executeQuery();
			if (rs.next()) {
				proxy = rs.getString(1);
				obj.put("proxy", proxy);
			}*/
			stmt = conn.prepareStatement(SqlString.SelectDistinctCities);
			rs = stmt.executeQuery();
			StringBuilder cities = new StringBuilder();
			while(rs.next())
			{
				//String city = new String(rs.getString(1).getBytes(),"GBK");
				String city = rs.getString(1);
				cities.append(city).append(",");
				
			}
			if(cities.length()>0){
				cities.delete(cities.length()-1, cities.length());
			}
			obj.put("cities",cities.toString());
			conn.close();
			//obj.put("cities",new String(cities.toString().getBytes(),"UTF-8"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			log.info("send result:"+obj.toJSONString());
			out.write(obj.toJSONString());
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		conn = DbConnectionPool.getInstance().getConnection();
		if(conn !=null){
			log.error("get database connection failed!");
		}
		request.setCharacterEncoding("UTF-8");
		String location = request.getParameter("location");
		log.info("get client query city:"+location);
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject obj = new JSONObject();
		String proxy = null;
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		try {
			stmt = conn.prepareStatement(SqlString.SelectRadomProxyByCity);
			stmt.setString(1,location);
			rs = stmt.executeQuery();
			if (rs.next()) {
				proxy = rs.getString(1);
				obj.put("proxy", proxy);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			log.info("send result:"+obj.toJSONString());
			out.write(obj.toJSONString());
		}
	}
	
	
}
