package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.model.Database;
public class DashboardAction extends ActionSupport{

	private static final long serialVersionUID = -929286120995712454L;
//	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/demo"; 
//    private static final String JDBC_USER = "root"; 
//    private static final String JDBC_PASSWORD = "password"; 
    private static final String ORIGIN_STRING = "http://127.0.0.1:5500";
    private static Map<String,String> map;
    private static String jsonString;
//    private static final String ORIGIN_STRING = "*";

//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//    }
    
    protected String doOptions(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK); 
        return SUCCESS;
    }
//    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
    public String doGet() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
    	response.setHeader("Access-Control-Allow-Origin", ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return ERROR;
        }
        System.out.println("Dashboard is running");
        response.setContentType("application/json");
//		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		map = new HashMap<>();
		try (Connection conn = Database.getConnection()) {
			PreparedStatement stmt = null;
			String query = "";
			if(request.getParameter("keyTitle")!=null) {
				query = "SELECT * FROM posts WHERE title LIKE ? and created_by = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, "%" + request.getParameter("keyTitle") + "%");
				stmt.setString(2,  request.getParameter("user"));
			}
			else if(request.getParameter("keyContent")!=null) {
				query = "SELECT * FROM posts WHERE body LIKE ? and created_by = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, "%"+request.getParameter("keyContent")+"%");
				stmt.setString(2, request.getParameter("user"));
			}
			else {
				query = "SELECT title,body FROM posts where created_by = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, request.getParameter("user"));
			}
			ResultSet rs = stmt.executeQuery();
			List<Post> posts = new ArrayList<>();
			while (rs.next()) {
				String title = rs.getString("title");
				String body = rs.getString("body");
				posts.add(new Post(title, body));
//				map.put(title,body);
			}
//			jsonString = gson.toJson(map);
			jsonString = gson.toJson(posts);
//			System.out.println(jsonString);
//			out.print(json);
//			out.flush();
			return SUCCESS;
			
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
				e.printStackTrace();
//				out.print(request.getParameter("keyTitle"));
//				out.print(request.getParameter("keyContent"));
//				out.print("{\"error\": \"Database error\"}")	;
			}
		System.out.println("get executed...");
		return null;
    }

//    protected String doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
    public String doPost() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();    
    	response.setHeader("Access-Control-Allow-Origin", ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equals(request.getMethod())) {
        	doOptions(request,response);
            response.setStatus(HttpServletResponse.SC_OK);
            return SUCCESS;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);

        try (Connection conn =Database.getConnection()) {
            String query = "INSERT INTO posts (title, body,created_by) VALUES (?, ?,?);";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.getTitle());
                stmt.setString(2, post.getBody());
                stmt.setString(3, request.getParameter("user"));
                stmt.executeUpdate();
            }
            catch(SQLException e) {
            	e.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_CREATED); 
            return SUCCESS;
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            map.put("Error","Database error");
			jsonString = gson.toJson(map);
			return ERROR;
//            PrintWriter out = response.getWriter();
//            out.print("{\"error\": \"Database error\"}");
//            out.flush();
        }
    }

//    protected String doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        
    public String doPut() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setHeader("Access-Control-Allow-Origin", ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equals(request.getMethod())) {
        	doOptions(request,response);
            response.setStatus(HttpServletResponse.SC_OK);
            return ERROR;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);

        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE posts SET body = ? WHERE title = ? and created_by = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.getBody());
                stmt.setString(2, post.title); 
                stmt.setString(3, request.getParameter("user")); 
                

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    response.setStatus(HttpServletResponse.SC_OK);  
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);  
                }
            }
            return SUCCESS;
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
//            PrintWriter out = response.getWriter();
//            out.print("{\"error\": \"Database error\"}");
//            out.flush();
            map.put("Error","Database error");
			jsonString = gson.toJson(map);

        }
        return ERROR;
    }

//    protected String doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        
    public String doDelete() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setHeader("Access-Control-Allow-Origin", ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return ERROR;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);
        try (Connection conn = Database.getConnection()) {
            String query = "DELETE FROM posts WHERE title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.title);  
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT); 
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                }
                return SUCCESS;
            }
            catch(SQLException e) {
            	e.printStackTrace();
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
//            PrintWriter out = response.getWriter();
//            out.print("{\"error\": \"Database error\"}");
//            out.flush();
            map.put("Error","Database error");
			jsonString = gson.toJson(map);

        }
        return ERROR;
    }
//	public String execute() throws IOException,ServletException,ClassNotFoundException{
//		System.out.println("Dashboard Action Execution started");
//    	HttpServletRequest request = ServletActionContext.getRequest();
//    	HttpServletResponse response = ServletActionContext.getResponse();
//    	String method = request.getMethod();
//    	switch(method) {
//    	case "POST":
//    		return doPost(request,response);
//    	case "OPTIONS":
//    		return doOptions(request,response);
////    	case "GET":
////    		return doGet(request,response);
//    	case "PUT":
//    		return doPut(request,response);
//    	case "DELETE":
//    		return doDelete(request,response);
//    	}
//    	return ERROR;
//	}
    public String getJsonString() {
    	return jsonString;
    }
    public void setJsonString(String jsonString) {
    	DashboardAction.jsonString = jsonString;
    }

}
