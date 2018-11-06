package eu.rkosir.feecollector.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class Event extends EventDay implements Parcelable {

	public static final String MATCH = "Match";
	public static final String TRANING = "Traning";
	public static final String EVENT = "Event";

	private String startDateTime;
	private String endDateTime;
	private int imageResouce;
	private String name;
	private String description;
	private String placeId;

	public Event(Calendar day, String startDateTime, String endDateTime, String name, String description, int imageResource, String placeId) {
		super(day, imageResource);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.imageResouce = imageResource;
		this.name = name;
		this.description = description;
		this.placeId = placeId;
	}

	private Event(Parcel in) {
		super((Calendar) in.readSerializable(), in.readInt());
		name = in.readString();
		description = in.readString();
		placeId = in.readString();
	}

	public static final Creator<Event> CREATOR = new Creator<Event>() {
		@Override
		public Event createFromParcel(Parcel in) {
			return new Event(in);
		}
		@Override
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeSerializable(getCalendar());
		parcel.writeInt(imageResouce);
		parcel.writeString(name);
		parcel.writeString(description);
		parcel.writeString(placeId);
	}
	@Override
	public int describeContents() {
		return 0;
	}

	public int getImageResouce() {
		return imageResouce;
	}

	public void setImageResouce(int imageResouce) {
		this.imageResouce = imageResouce;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
}
