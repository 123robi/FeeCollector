package com.feecollector.android.feecollector.User.Entity;


import java.util.Date;

public class User {
	String name;
	String surname;
	Date created_at, updated_at;

	public User(String name, String surname) {
		this.name = name;
		this.surname = surname;
		this.created_at = new Date();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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
}
