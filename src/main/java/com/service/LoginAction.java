package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
    private static final long serialVersionUID = -602549588809691898L;
	private static String ORIGIN_STRING = "http://127.0.0.1:5500";
	private static Connection getConnection() throws SQLException,ClassNotFoundException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
	}
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
    	response.setHeader("Access-Control-Allow-Headers","Content-Type");
    	response.setStatus(HttpServletResponse.SC_OK);
    }	
    public static void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException, ClassNotFoundException {
        response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
    	response.setHeader("Access-Control-Allow_Methods","GET,OPTIONS,POST");
    	BufferedReader stream = new BufferedReader(new InputStreamReader(request.getInputStream()));
    	StringBuilder stringBuilder = new StringBuilder();
    	String line;
    	while((line=stream.readLine())!=null) {
    		stringBuilder.append(line);
    	}
    	Gson gson = new Gson();
    	User user = gson.fromJson(stringBuilder.toString(),User.class);
    	try (Connection conn = getConnection()){
    		String query = "select * from userLogin where username = ?";
    		try(PreparedStatement stmt = conn.prepareStatement(query)){
    			stmt.setString(1,user.getUsername());
    			ResultSet rs = stmt.executeQuery();
    			Map<String,String> map = new HashMap<>();
    			if(rs.next()) {
    				if(BCrypt.checkpw(user.getPassword(),rs.getString("password"))){
    					System.out.println("Password checked");
    					String cookieQuery = "update userLogin set value = 'true' where id = ?";
    					PreparedStatement cookieQueryStatement = conn.prepareStatement(cookieQuery);
    					cookieQueryStatement.setString(1,rs.getString("id"));
    					cookieQueryStatement.executeUpdate();
    					response.setStatus(HttpServletResponse.SC_OK);
    					response.setHeader("Set-Cookie","id="+rs.getString("id")+";Max-Age=86400;SameSite=None;Secure=true;");
    					response.setContentType("application/json");
    					map.put("status","success");
    					map.put("message","login successful");
    					response.getWriter().println(gson.toJson(map));
    					response.getWriter().flush();
    				}
    				else {
    					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    					response.setContentType("application/json");
    					map.put("status","failed");
    					map.put("message","wrong password");
    					response.getWriter().println(gson.toJson(map));
    					response.getWriter().flush();
    					System.out.print("Wrong password");
    				}
    			}
    			else {
    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				response.setContentType("application/json");
    				map.put("status","failed");
    				map.put("message","wrong password");
    				response.getWriter().println(gson.toJson(map));
    				response.getWriter().flush();
    				System.out.print("Wrong username");
    			}
    		}
    		catch(Error e) {
    			e.printStackTrace();
    		}
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
    }

    public String execute() throws IOException, ServletException, ClassNotFoundException {
    	System.out.println("Execution started");
    	HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
    	String method = request.getMethod();
    	System.out.println(method);
    	switch(method) {
    	case "POST":
    		doPost(request,response);
    		break;
    	case "OPTIONS":
    		doOptions(request,response);
    		break;
    	}
    	return SUCCESS;
    }
	public static class User {
		private String username;
		private String password;

		public User(String username, String password) {
			this.username = username.trim();
			this.password = password.trim();
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}
}
