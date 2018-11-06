package eu.rkosir.feecollector.entity;

public class Place {

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
}
