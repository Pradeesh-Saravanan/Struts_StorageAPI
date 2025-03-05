package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.model.Database;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
    private static final long serialVersionUID = -602549588809691898L;
//	private static String ORIGIN_STRING = "http://127.0.0.1:5500";
	private static String ORIGIN_STRING = "http://127.0.0.1:5500";
	private static Map<String,String> map = new HashMap<>();
	private static String jsonString;
//	private static Connection getConnection() throws SQLException,ClassNotFoundException{
//		Class.forName("com.mysql.cj.jdbc.Driver");
//		return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
//	}
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
    	response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
    	response.setStatus(HttpServletResponse.SC_OK);
    }	
//    public static String doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException, ClassNotFoundException {
        
    public String doPost() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
		if ("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
	    	response.setStatus(HttpServletResponse.SC_OK);
			return SUCCESS;
		}
    	response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
    	BufferedReader stream = new BufferedReader(new InputStreamReader(request.getInputStream()));
    	StringBuilder stringBuilder = new StringBuilder();
    	String line;
    	while((line=stream.readLine())!=null) {
    		stringBuilder.append(line);
    	}
    	Gson gson = new Gson();
    	User user = gson.fromJson(stringBuilder.toString(),User.class);
    	try (Connection conn = Database.getConnection()){
    		String query = "select * from userLogin where username = ?";
    		try(PreparedStatement stmt = conn.prepareStatement(query)){
    			stmt.setString(1,user.getUsername());
    			ResultSet rs = stmt.executeQuery();
//    			Map<String,String> map = new HashMap<>();
    			if(rs.next()) {
    				if(BCrypt.checkpw(user.getPassword(),rs.getString("password"))){
    					System.out.println("Password checked");
    					String cookieQuery = "update userLogin set value = 'true' where id = ?";
    					PreparedStatement cookieQueryStatement = conn.prepareStatement(cookieQuery);
    					cookieQueryStatement.setString(1,rs.getString("id"));
    					cookieQueryStatement.executeUpdate();
    					response.setStatus(HttpServletResponse.SC_OK);
//    					response.setHeader("Set-Cookie","id="+rs.getString("id")+";Max-Age=86400;Path="+"/"+";HttpOnly=true;SameSite=None;Secure=true;");
    					response.setHeader("Set-Cookie","id="+rs.getString("id")+";Max-Age=86400;Path="+"/StorageAPI_Struts/"+";HttpOnly=true;SameSite=None;Secure=true;");
    					map.put("status","success");
    					map.put("message","login successful");
    					map.put("cookie","set at path");
    					jsonString = gson.toJson(map);
    					return SUCCESS;
    					
    				}
    				else {
    					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    					map.put("status","failed");
    					map.put("message","wrong password");
    					jsonString = gson.toJson(map);
    					System.out.print("Wrong password");
    					return ERROR;
    				}
    			}
    			else {
    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    				response.setContentType("application/json");
    				map.put("status","failed");
    				map.put("message","wrong password");
					jsonString = gson.toJson(map);
//    				response.getWriter().println(gson.toJson(map));
//    				response.getWriter().flush();
    				System.out.print("Wrong username");
    				return ERROR;
    			}
    		}
    		catch(Error e) {
    			e.printStackTrace();
    		}
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return ERROR;
    }
//	public static String doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException, ClassNotFoundException {
    public String doGet() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
		if ("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return SUCCESS;
		}
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.getWriter().println("Login servlet is running");
		try {
//		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
		Connection connection = Database.getConnection();
    	PreparedStatement preparedStatement = connection.prepareStatement("select * from userLogin where id = ?");
    	Boolean flagBoolean = false;
	    	Cookie[] cks = request.getCookies();
		    if(cks!=null)
	    	for(Cookie ck:cks) {
		    	preparedStatement.setString(1, ck.getValue());
		    	ResultSet rSet = preparedStatement.executeQuery();
		    	while(rSet.next()) {
//		    		System.out.println(ck.getValue());
		    		if(rSet.getString("value").equals("true")) {
		    			System.out.println("Authenticated at login page....");
		    			flagBoolean = true;
//		    			Map<String,String> map = new HashMap<>();
		            	Gson gson = new Gson();
		            	response.setStatus(HttpServletResponse.SC_OK);
//		            	response.setContentType("application/json");
		    			map.put("status", "success");
		    			map.put("message", "Redirected to dashboard");
		    			map.put("user",rSet.getString("username"));
    					jsonString = gson.toJson(map);
//		    			response.getWriter().println(gson.toJson(map));
//		    			response.getWriter().flush();
//		    			System.out.println("Json sent");
		    			return SUCCESS;
		    		}
		    	}
	    	}
		    else {
		    	System.out.println("No cookies in login get");
		    	return null;
		    }
		    if(!flagBoolean) {
		    	Map<String,String> map = new HashMap<>();
	        	Gson gson = new Gson();
	        	System.out.println("Authentication failed at login servlet");
	        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//	        	response.setContentType("application/json");
				map.put("status", "failed");
				map.put("message", "wrong password");
//				response.getWriter().println(gson.toJson(map));
//				response.getWriter().flush();
				jsonString = gson.toJson(map);
				return null;
		    }
		}
		catch(SQLException e) {
			
		}
		return null;
	}
//    @Override
//    public String execute() throws IOException, ServletException, ClassNotFoundException {
//    	System.out.println("Login Action Execution started");
//    	HttpServletRequest request = ServletActionContext.getRequest();
//    	HttpServletResponse response = ServletActionContext.getResponse();
//    	String method = request.getMethod().toLowerCase();
//    	System.out.println(method);
//    	switch(method) {
//    	case "post":
//    		return doPost(request,response);
////    		break;
//    	case "options":
//    		doOptions(request,response);
//    		return null;
//    	case "get":
//    		return doGet(request,response);
//    	}
//    	return ERROR;
//    }
    
    public String getJsonString() {
    	return jsonString;
    }
    public void setJsonString(String jsonString) {
    	LoginAction.jsonString = jsonString;
    }
}
