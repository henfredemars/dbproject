package henfredemars;

import java.io.Serializable;
import java.util.ArrayList;

public class Station implements Serializable{

	private static final long serialVersionUID = 0L;
	
	private ArrayList<DataSample> samples = null;
	private float lat = 0;
	private float lon = 0;
	private String stationid;
	
	public Station(String id) {
		samples = new ArrayList<DataSample>();
		stationid = id;
	}
	
	public void setLatLon(float lat, float lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public float getLat() {
		return lat;
	}
	
	public float getLon() {
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
