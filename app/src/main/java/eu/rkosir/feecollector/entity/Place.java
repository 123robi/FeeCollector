package eu.rkosir.feecollector.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable{

	private final int id;
	private final String name;
	private final String address;
	private final int team_id;
	private String latlng;

	public Place(int id, String name, String address, String latlng, int team_id){

		this.id = id;
		this.name = name;
		this.address = address;
		this.team_id = team_id;
		this.latlng = latlng;
	}

	protected Place(Parcel in) {
		id = in.readInt();
		name = in.readString();
		address = in.readString();
		team_id = in.readInt();
		latlng = in.readString();
	}

	public static final Creator<Place> CREATOR = new Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		@Override
		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getTeam_id() {
		return team_id;
	}

	public String getLatlng() {
		return latlng;
	}

	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeString(name);
		parcel.writeString(address);
		parcel.writeString(latlng);
		parcel.writeInt(team_id);
	}
}
