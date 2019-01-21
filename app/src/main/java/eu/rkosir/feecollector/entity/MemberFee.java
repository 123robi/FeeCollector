package eu.rkosir.feecollector.entity;

import java.util.Calendar;

public class MemberFee {
	private int id;
	private String name;
	private String amount;
	private Calendar date;
	private boolean paid;

	public MemberFee (int id, String name, String amount, Calendar date, boolean paid) {
		this.id = id;

		this.name = name;
		this.amount = amount;
		this.date = date;
		this.paid = paid;
	}

	public String getName() {
		return name;
	}

	public String getAmount() {
		return amount;
	}

	public Calendar getDate() {
		return date;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
