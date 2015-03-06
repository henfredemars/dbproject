package henfredemars;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//Compound hourly data into week data by station
public class WeekCompounder {
	
	private ArrayList<DataSample> hourData = null;
	private HashMap<String,Station> stations = null;
	
	public WeekCompounder(ArrayList<DataSample> hourData) {
		this.hourData = hourData;
		this.stations = new HashMap<String,Station>();
		populateStations();
	}
	
	private void populateStations() {
		//Make list of IDs and Stations from IDs
		for (DataSample ds: hourData) {
			if (stations.containsKey(ds.getStationId())) continue;
			stations.put(ds.getStationId(),new Station(ds.getStationId()));
		}
		//Sort data into each station
		for (DataSample ds: hourData) {
			Station st = stations.get(ds.getStationId());
			st.getSamples().add(ds);
		}
	}
	
	public boolean verifyData() {
		for (DataSample ds: hourData) {
			if (ds.checkSample()!=DataStatus.ALL_GOOD) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<Station> getStations() {
		ArrayList<Station> res = new ArrayList<Station>();
		for (String station: stations.keySet()) {
			res.add(stations.get(station));
		}
		return res;
	}
	
	public void compoundDataByWeek(Calendar start, Calendar end) {
		for (String stationid: stations.keySet()) {
			Station st = stations.get(stationid);
			Calendar s = (Calendar) start.clone();
			Calendar e = (Calendar) start.clone();
			e.add(Calendar.DAY_OF_YEAR, 7);
			ArrayList<DataSample> stationData = st.getSamples();
			ArrayList<DataSample> newStationData = new ArrayList<DataSample>();
			while (s.before(end)) {
				ArrayList<DataSample> thisWeek = new ArrayList<DataSample>();
				for (DataSample ds: stationData) {
					if (ds.getDate().before(e) && (ds.getDate().after(s) || ds.getDate().equals(s))) {
						thisWeek.add(ds);
					}
				}
				if (!thisWeek.isEmpty()) {
					DataSample sum = compressWeek(thisWeek);
					newStationData.add(sum);
				}
				s = (Calendar) e.clone();
				e.add(Calendar.DAY_OF_YEAR, 7);
			}
			st.setSamples(newStationData);
		}
	}
	
	private DataSample compressWeek(ArrayList<DataSample> week) {
		if (week.size()==0) {
			return new DataSample();
		}
		DataSample ds = new DataSample(week.get(0));
		double temperature = ds.getTemperature();
		double humidity = ds.getHumidity();
		double windSpeed = ds.getWindSpeed();
		double pressure = ds.getPressure();
		double rainfall = ds.getRainfall();
		long numSamples = 1;
		for (int i = 1; i<week.size();i++) {
			DataSample wds = week.get(i);
			numSamples++;
			temperature += wds.getTemperature();
			humidity += wds.getHumidity();
			windSpeed += wds.getWindSpeed();
			pressure += wds.getPressure();
			rainfall += wds.getRainfall();
		}
		temperature /= numSamples;
		humidity /= numSamples;
		windSpeed /= numSamples;
		pressure /= numSamples;
		rainfall /= numSamples;
		ds.setTemperature((float) temperature);
		ds.setHumidity((float) humidity);
		ds.setWindSpeed((float) windSpeed);
		ds.setPressure((float) pressure);
		ds.setRainfall((float) rainfall);
		return ds;
	}

}
