package eu.rkosir.feecollector.helper;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class MyEventDay extends EventDay implements Parcelable {

	private String mNote;
	private int imageResouce;
	public MyEventDay(Calendar day, int imageResource, String note) {
		super(day, imageResource);
		mNote = note;
		this.imageResouce = imageResource;
	}
	String getNote() {
		return mNote;
	}
	private MyEventDay(Parcel in) {
		super((Calendar) in.readSerializable(), in.readInt());
		mNote = in.readString();
	}
	public static final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
		@Override
		public MyEventDay createFromParcel(Parcel in) {
			return new MyEventDay(in);
		}
		@Override
		public MyEventDay[] newArray(int size) {
			return new MyEventDay[size];
		}
	};
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeSerializable(getCalendar());
		parcel.writeInt(imageResouce);
		parcel.writeString(mNote);
	}
	@Override
	public int describeContents() {
		return 0;
	}
}
