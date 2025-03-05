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
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.*;

import com.google.gson.*;
import com.model.Database;
import com.opensymphony.xwork2.ActionSupport;	

public class RegistrationAction extends ActionSupport {
	private static final long serialVersionUID = -5926468460905343227L;
	private static String jsonString;
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods","POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, access-control-allow-methods");
		response.setStatus(HttpServletResponse.SC_OK);
	}
//	protected String doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
    public String doPost() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,access-control-allow-methods");
		response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS");
		if("OPTIONS".equals(request.getMethod()))
		{
			response.setStatus(HttpServletResponse.SC_OK);
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line ;
		while((line=reader.readLine())!=null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		User user = gson.fromJson(sb.toString(),User.class);
		Map<String,String> map = new HashMap<>();
		try(Connection conn = Database.getConnection()){
			String query = "select username from userLogin where username = ?";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setString(1, user.getUsername());
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
//					response.setContentType("application/json");
					map.put("status", "failed");
					map.put("message", "Username already exists");
					jsonString = gson.toJson(map);
//					response.getWriter().println(gson.toJson(map));
//					response.getWriter().flush();
					return ERROR;
				}
				else {
					String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
					String idString = UUID.randomUUID().toString();
					query = "insert into userLogin(username,password,id,value) values(?,?,?,'false')";
					try(PreparedStatement insert =conn.prepareStatement(query)){
						insert.setString(1, user.getUsername());
						insert.setString(2, hashed);
						insert.setString(3, idString);
						insert.executeUpdate();
//						response.setContentType("application/json");
						map.put("status", "success");
						map.put("message","User added to database");
//						response.getWriter().println(gson.toJson(map));
//						response.getWriter().flush();
						jsonString = gson.toJson(map);
						return SUCCESS;
					}
					catch(SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
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
//    	case "options":
//    		doOptions(request,response);
//    		return null;
//    	}
//    	return ERROR;
//    }
    
    public String getJsonString() {
    	return jsonString;
    }
    public void setJsonString(String jsonString) {
    	RegistrationAction.jsonString = jsonString;
    }
}
