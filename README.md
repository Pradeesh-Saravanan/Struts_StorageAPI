# Struts_StorageAPI

# Week 03

## Starting up with struts framework (Only server side)

## Jar files needed 

- **Inside Struts lib directory**

- **Struts.xml inside the WEB-INF/Classes

# 03/03/2025

- **LoginAction:** Replaced Login servlet with LoginAction 
- **Filters:** Used Struts filter instead of servlet filters
- **Struts.xml:** Directs the request to action based on request urls 

# 04/03/2025

## Updates made 

- **Moved the entire StorageAPI project to Struts framework**
- **Used Struts for controller module**
- **By implementing ActionSupport class**
- **Replaced Servlet filters with Struts Interceptors**
- **Used Struts xml to manage url mapping:** extended json-default package
- **Results for every action is sent as a JSON object**
- **Secured for CORS access and masquerade request by using Interceptors**