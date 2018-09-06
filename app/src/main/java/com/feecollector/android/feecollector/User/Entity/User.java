package com.feecollector.android.feecollector.User.Entity;


import java.util.Date;

public class User {
	private String name;
	private String email;
	private String password;
	private Date created_at, updated_at;
	private String facebook_json;

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.created_at = new Date();
		this.facebook_json = "null";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public String getFacebook_json() {
		return facebook_json;
	}

	public void setFacebook_json(String facebook_json) {
		this.facebook_json = facebook_json;
	}

	@Override
	public String toString() {
		return "" + getEmail() + " " + getName() + " " + getFacebook_json();
	}
}
