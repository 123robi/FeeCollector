package eu.rkosir.feecollector.entity;

public class Team {
	private int id;
	private String name;
	private int members;
	private final String currency_code;
	private final String currency_symbol;
	private boolean admin;
	private String connection_number;

	public Team(int id, String name,String currency_code, String currency_symbol, boolean admin, String connection_number) {

		this.id = id;
		this.name = name;
		this.currency_code = currency_code;
		this.currency_symbol = currency_symbol;
		this.admin = admin;
		this.connection_number = connection_number;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getConnection_number() {
		return connection_number;
	}

	public void setConnection_number(String connection_number) {
		this.connection_number = connection_number;
	}

	public int getMembers() {
		return members;
	}

	public void setMembers(int members) {
		this.members = members;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public String getCurrency_symbol() {
		return currency_symbol;
	}
}
