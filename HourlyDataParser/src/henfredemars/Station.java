package henfredemars;

import java.util.ArrayList;

public class Station {

	private ArrayList<DataSample> samples = null;
	private double lat = 0;
	private double lon = 0;
	private String stationid;
	
	public Station(String id) {
		samples = new ArrayList<DataSample>();
		stationid = id;
	}
	
	public void setLatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public ArrayList<DataSample> getSamples() {
		return samples;
	}
	
	public void setSamples(ArrayList<DataSample> samples) {
		this.samples = samples;
	}
	
	public String getStationId() {
		return stationid;
	}
	
}
