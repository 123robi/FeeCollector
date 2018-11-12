package eu.rkosir.feecollector.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable{
	private String name;
	private String email;
	private String phoneNumber;
	private String address;
	private String password;
	private Date created_at, updated_at;
	private String facebook_json;
	private String role;

	public User(String name, String email, String phoneNumber, String address ,String password) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.password = password;
		this.created_at = new Date();
		this.role = null;
	}
	public User(String name, String email, String phoneNumber, String address) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.created_at = new Date();
		this.role = null;
	}

	public static final Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
	protected User(Parcel in) {
		name = in.readString();
		email = in.readString();
		phoneNumber = in.readString();
		address = in.readString();
		password = in.readString();
		facebook_json = in.readString();
		role = in.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(name);
		parcel.writeString(email);
		parcel.writeString(phoneNumber);
		parcel.writeString(address);
		parcel.writeString(password);
		parcel.writeString(facebook_json);
		parcel.writeString(role);
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
