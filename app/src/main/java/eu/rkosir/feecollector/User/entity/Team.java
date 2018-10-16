package eu.rkosir.feecollector.User.entity;

public class Team {
	private int id;
	private String name;

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

	private boolean admin;
	private String connection_number;

	public Team(int id, String name, boolean admin, String connection_number) {

		this.id = id;
		this.name = name;
		this.admin = admin;
		this.connection_number = connection_number;
	}
}
