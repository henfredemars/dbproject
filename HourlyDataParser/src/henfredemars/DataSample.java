package henfredemars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Calendar;

//Single data point from one station at one hour
public class DataSample implements DataSampleInterface, Serializable, Comparable<DataSample> {
	
	private static final long serialVersionUID = 0L;
	private static final int none = -10101;
	
	private String station;
	private float temperature = none;
	private float humidity = none;
	private float windSpeed = none;
	private float pressure = none;
	private float rainfall = none;
	private Calendar date = null;

	public DataSample() {
		//Nothing to do
	}
	
	public DataSample(DataSample ds) {
		this.station = ds.station;
		this.temperature = ds.temperature;
		this.humidity = ds.humidity;
		this.windSpeed = ds.windSpeed;
		this.pressure = ds.pressure;
		this.rainfall = ds.rainfall;
		this.date = ds.date;
	}
	
	public void setStationId(String station) {
		this.station = station;
	}
	
	public void setTemperature(float tempInF) {
		protectNone(tempInF);
		this.temperature = tempInF;
	}
	
	public void setHumidity(float humidity) {
		protectNone(humidity);
		this.humidity = humidity;
	}
	
	public void setWindSpeed(float windSpeedInMPH) {
		protectNone(windSpeedInMPH);
		this.windSpeed = windSpeedInMPH;
	}
	
	public void setPressure(float seaLevelPressureInMillibars) {
		protectNone(seaLevelPressureInMillibars);
		this.pressure = seaLevelPressureInMillibars;
	}
	
	public void setRainfall(float hourlyInches) {
		protectNone(hourlyInches);
		this.rainfall = hourlyInches;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public String getStationId() {
		return station;
	}
	
	public float getTemperature() {
		return temperature;
	}
	
	public float getHumidity() {
		return humidity;
	}
	
	public float getWindSpeed() {
		return windSpeed;
	}
	
	public float getPressure() {
		return pressure;
	}
	
	public float getRainfall() {
		return rainfall;
	}
	
	public Calendar getDate() {
		return date;
	}
	
	public DataStatus checkSample() {
		if (station==null || station.trim().isEmpty()) return DataStatus.MISSING_STATION;
		if (temperature==none) return DataStatus.MISSING_TEMPERATURE;
		if (humidity==none) return DataStatus.MISSING_HUMIDITY;
		if (windSpeed==none) return DataStatus.MISSING_WINDSPEED;
		if (pressure==none) return DataStatus.MISSING_PRESSURE;
		if (rainfall==none) return DataStatus.MISSING_RAINFALL;
		if (date==null) return DataStatus.MISSING_DATE;
		
		if (temperature<-160 || temperature>160)
			return DataStatus.OUT_OF_RANGE_TEMPERATURE;
		if (humidity<-160 || humidity>160)
			return DataStatus.OUT_OF_RANGE_HUMIDITY;
		if (windSpeed<0 || windSpeed>400)
			return DataStatus.OUT_OF_RANGE_WINDSPEED;
		if (pressure<200 || pressure >1400)
			return DataStatus.OUT_OF_RANGE_PRESSURE;
		if (rainfall<0 || rainfall>15)
			return DataStatus.OUT_OF_RANGE_RAINFALL;
		if (station.length()!=6 || station.equals("******") || station.equals("999999"))
			return DataStatus.BAD_STATION;
		
		return DataStatus.ALL_GOOD;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof DataSample)) return false;
		DataSample os = (DataSample) other;
		if (station!=os.station) return false;
		if (date!=null && os.date!=null && !date.equals(os.date)) return false;
		if (date==null && os.date!=null) return false;
		if (date!=null && os.date==null) return false;
		if (temperature!=os.temperature) return false;
		if (humidity!=os.humidity) return false;
		if (windSpeed!=os.windSpeed) return false;
		if (pressure!=os.pressure) return false;
		if (rainfall!=os.rainfall) return false;
		return true;
	}
	
	private static void protectNone(float val) {
		if (val==none) throw new RuntimeException("DataSample - attempt to set reserved value");
	}
	
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(128+32);
		bb.putFloat(temperature);
		bb.putFloat(humidity);
		bb.putFloat(windSpeed);
		bb.putFloat(pressure);
		bb.putFloat(rainfall);
		oos.writeObject(station);
		oos.writeObject(bb.array());
		oos.writeObject(date);
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ByteBuffer bb = ByteBuffer.allocate(128+32);
		station = (String)ois.readObject();
		bb.put((byte[])ois.readObject());
		date = (Calendar)ois.readObject();
		bb.position(0);
		temperature = bb.getFloat();
		humidity = bb.getFloat();
		windSpeed = bb.getFloat();
		pressure = bb.getFloat();
		rainfall = bb.getFloat();
	}

	@Override
	public int compareTo(DataSample ds) {
		return date.compareTo(ds.getDate());
	}
	
}
