package eu.rkosir.feecollector.entity;

import java.util.Calendar;

public class MemberFee {
	private final String name;
	private final String amount;
	private final Calendar date;
	private final boolean paid;

	public MemberFee (String name, String amount, Calendar date, boolean paid) {

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
}
