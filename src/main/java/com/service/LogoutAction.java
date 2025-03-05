package com.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import com.google.gson.Gson;
import com.model.Database;
import com.opensymphony.xwork2.ActionSupport;


public class LogoutAction extends ActionSupport{
	private static final long serialVersionUID = 7497259291823217852L;
	public static String jsonString;
    public String getJsonString() {
    	return jsonString;
    }
    public void setJsonString(String jsonString) {
    	LogoutAction.jsonString = jsonString;
    }
//    @Override
//    public String execute() throws IOException, ServletException, ClassNotFoundException {
//    	System.out.println("Login Action Execution started");
//    	HttpServletRequest request = ServletActionContext.getRequest();
//    	HttpServletResponse response = ServletActionContext.getResponse();
//    	String method = request.getMethod().toLowerCase();
//    	System.out.println(method);
//    	switch(method) {
//    	case "options":
//    		doOptions(request,response);
//    		return null;
//    	case "get":
//    		return doGet(request,response);
//    	}
//    	return ERROR;
//    }
	public static void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setStatus(HttpServletResponse.SC_OK);
	}
//	public static String doGet(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException {
	public String doGet() throws ServletException, IOException, ClassNotFoundException {
		HttpServletRequest request = ServletActionContext.getRequest();
	    HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		Cookie[] cks = request.getCookies();
		if(cks!=null) {
			for(Cookie ck:cks) {
				try {
					Connection connection = Database.getConnection();
					PreparedStatement stmtPreparedStatement = connection.prepareStatement("update userLogin set value = 'false' where id = ?");
					stmtPreparedStatement.setString(1,ck.getValue());
					int rows = stmtPreparedStatement.executeUpdate();
					if(rows>0) {
						Map<String,String> map = new HashMap<>();
	                	Gson gson = new Gson();
	                	response.setStatus(HttpServletResponse.SC_OK);
	        			map.put("status", "success");
	        			map.put("message", "user Logout");
	        			jsonString = gson.toJson(map);
	        			System.out.println("Logout successful...");
	        			return SUCCESS;
					}
					else {
						Map<String,String> map = new HashMap<>();
	                	Gson gson = new Gson();
	                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        			map.put("status", "failed");
	        			map.put("message", "Unauthorized Access");
	        			jsonString = gson.toJson(map);
	        			System.out.println("Logout failed!");
	        			return ERROR;
					}
				}
				catch(SQLException  e) {
					
				}
			}
		}
		else {
			Map<String,String> map = new HashMap<>();
        	Gson gson = new Gson();
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			map.put("status", "failed");
			map.put("message", "Unauthorized Access");
			jsonString = gson.toJson(map);
			System.out.println("Logout failed!");
			System.out.println("Cookies not found in logout servlet");
			return ERROR;
		}
		return ERROR;
		
	}
}
