package eu.rkosir.feecollector.entity;

public class Place {

	private final int id;
	private final String name;
	private final String address;
	private final int team_id;

	public Place(int id, String name, String address, int team_id){

		this.id = id;
		this.name = name;
		this.address = address;
		this.team_id = team_id;
	}

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
	@Override
	public String toString() {
		return getName();
	}
}
