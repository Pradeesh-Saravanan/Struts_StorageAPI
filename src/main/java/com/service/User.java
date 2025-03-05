package com.service;

public class User {
	String username;
	String password;

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