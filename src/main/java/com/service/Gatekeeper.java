//package com.service;
//
//import java.io.IOException;
//import java.net.ConnectException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import com.google.gson.Gson;
//import com.mysql.cj.xdevapi.Statement;
//
//
////@WebFilter("/Gatekeeper")
//public class Gatekeeper extends HttpFilter implements Filter {
//       
//    private static final long serialVersionUID = -3257553160062404652L;
//
//	/**
//     * @see HttpFilter#HttpFilter()
//     */
//    public Gatekeeper() {
//        super();
//        // TODO Auto-generated constructor stub
//    }
//
//	/**
//	 * @see Filter#destroy()
//	 */
//	public void destroy() {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//	 */
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        httpResponse.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
//        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
//        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
//        
//        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//        httpResponse.setHeader("Pragma", "no-cache");
//        httpResponse.setHeader("Expires", "0");
//
//        // Disable ETag and Last-Modified to prevent 304 responses
//        httpResponse.setHeader("ETag", ""); 
//        httpResponse.setHeader("Last-Modified", "0");
//        if ("OPTIONS".equals(httpRequest.getMethod())) {
//            httpResponse.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new ServletException("MySQL Driver not found", e);
//        }
//        System.out.println("Gatekeeper running...");
//        System.out.println(request.getParameter("user"));
//        Cookie cks[] = httpRequest.getCookies();
//        boolean flag = false;
//        if(cks!=null) {
//        for(Cookie ck:cks) {
//        	try {
//	        	Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
//	        	PreparedStatement preparedStatement = connection.prepareStatement("select * from userLogin where id = ?");
//	        	preparedStatement.setString(1, ck.getValue());
//	        	ResultSet rSet = preparedStatement.executeQuery();
//	        	while(rSet.next()) {
//	        		if(rSet.getString("value").equals("true") && rSet.getString("username").equals(request.getParameter("user"))) {
//	        			flag = true;
//	        			System.out.println("Authenticated by gatekeeper....\n");
//	        			chain.doFilter(httpRequest, httpResponse);
//	        		}
//	        	}
//        	}
//        	catch(SQLException e) {
//				
//			}
//        	
//        }
//        }	
//        else {
////        	response.getWriter().println("No cookies ");
//        	System.out.println("No cookies in gatekeeper filter...");
//        	Map<String,String> map = new HashMap<>();
//        	Gson gson = new Gson();
//        	System.out.println("Authentication failed at gatekeeper");
//        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        	httpResponse.setContentType("application/json");
//			map.put("status", "failed");
//			map.put("message", "Unauthorized Access");
//			httpResponse.getWriter().println(gson.toJson(map));
//			httpResponse.getWriter().flush();
//        	return;
//        }
//        if(!flag) {
//        	Map<String,String> map = new HashMap<>();
//        	Gson gson = new Gson();
//        	System.out.println("Authentication failed at gatekeeper");
//        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        	httpResponse.setContentType("application/json");
//			map.put("status", "failed");
//			map.put("message", "Unauthorized Access");
//			httpResponse.getWriter().println(gson.toJson(map));
//			httpResponse.getWriter().flush();
//        	return;
//        }
//
//	}
//
//	public void init(FilterConfig fConfig) throws ServletException {
//		// TODO Auto-generated method stub
//	}
//
//}
package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.model.Database;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class Gatekeeper implements Interceptor{
	private static final long serialVersionUID = -2293124609402900717L;
	@Override 
	public String intercept(ActionInvocation invocation)throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
		if (request == null || response == null) {
            System.err.println("HttpServletRequest or HttpServletResponse is null in Gatekeeper Interceptor");
            return null;  
        }
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("ETag", ""); 
        response.setHeader("Last-Modified", "0");
        if("OPTIONS".equals(request.getMethod())) {
        	response.setStatus(HttpServletResponse.SC_OK);
        	return null;
        }
        System.out.println("Interceptor - Gatekeeper running.....");
        Cookie[] cks = request.getCookies();
        boolean flag = false;
        if(cks!=null) {
        	for(Cookie ck:cks) {
        		try {        	
        			Connection connection = Database.getConnection();
        			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM userLogin WHERE id = ?");
                    preparedStatement.setString(1, ck.getValue());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                    	System.out.println(resultSet.getString("username"));
                        if ("true".equals(resultSet.getString("value")) && request.getParameter("user").equals(resultSet.getString("username"))) {
                            flag = true;
                            break;
                        }
                    }
        		}
        		catch(SQLException e) {
        			
        		}
        	}
        }
        else {
        	System.out.println("No cookies at interceptor...");
        }
        if(!flag) {
        	Map<String,String> map = new HashMap<>();
        	map.put("status","failed");
        	map.put("message","Unauthorised access by interceptor");
        	Gson gson = new Gson();
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	response.setContentType("application/json");
        	response.getWriter().println(gson.toJson(map));
        	response.getWriter().flush();
        	return null	;
        }
		return invocation.invoke();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}





