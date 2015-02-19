package henfredemars;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Calendar;

import org.junit.Test;

//Single data point from one station at one hour
public class DataSample implements DataSampleInterface, Serializable {
	
	private static final long serialVersionUID = 0L;
	private static final int none = -10101;
	
	private String station;
	private double temperature = none;
	private double humidity = none;
	private double windSpeed = none;
	private double pressure = none;
	private double rainfall = none;
	private Calendar date = null;

	public DataSample() {
		//Nothing to do
	}
	
	public void setStationId(String station) {
		this.station = station;
	}
	
	public void setTemperature(double tempInF) {
		protectNone(tempInF);
		this.temperature = tempInF;
	}
	
	public void setHumidity(double relativeHumidity) {
		protectNone(relativeHumidity);
		this.humidity = relativeHumidity;
	}
	
	public void setWindSpeed(double windSpeedInMPH) {
		protectNone(windSpeedInMPH);
		this.windSpeed = windSpeedInMPH;
	}
	
	public void setPressure(double seaLevelPressureInMillibars) {
		protectNone(seaLevelPressureInMillibars);
		this.pressure = seaLevelPressureInMillibars;
	}
	
	public void setRainfall(double hourlyInches) {
		protectNone(hourlyInches);
		this.rainfall = hourlyInches;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public String getStationId() {
		return station;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public double getHumidity() {
		return humidity;
	}
	
	public double getWindSpeed() {
		return windSpeed;
	}
	
	public double getPressure() {
		return pressure;
	}
	
	public double getRainfall() {
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
		if (humidity<0 || humidity>1)
			return DataStatus.OUT_OF_RANGE_HUMIDITY;
		if (windSpeed<0 || windSpeed>400)
			return DataStatus.OUT_OF_RANGE_WINDSPEED;
		if (pressure<200 || pressure >1400)
			return DataStatus.OUT_OF_RANGE_PRESSURE;
		if (rainfall<0 || rainfall>15)
			return DataStatus.OUT_OF_RANGE_RAINFALL;
		
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
	
	private static void protectNone(double val) {
		if (val==none) throw new RuntimeException("DataSample - attempt to set reserved value");
	}
	
	
	@Test
	public void testDataChecking() {
		DataSample ds = new DataSample();
		assertTrue(ds.equals(ds));
		assertTrue(ds.checkSample()==DataStatus.MISSING_STATION);
		ds.setStationId("XXXXX");
		assertTrue(ds.checkSample()==DataStatus.MISSING_TEMPERATURE);
		ds.setTemperature(60);
		ds.setHumidity(0.5);
		ds.setWindSpeed(6);
		assertTrue(ds.checkSample()==DataStatus.MISSING_PRESSURE);
		ds.setPressure(1000);
		assertTrue(ds.checkSample()==DataStatus.MISSING_RAINFALL);
		try {
			ds.setPressure(none);
			fail("Should fail to set value to none");
		} catch (RuntimeException e) {
			//Good, good
		}
		assertTrue(ds.getPressure()==1000);
		Calendar date = Calendar.getInstance();
		date.set(1901,Calendar.DECEMBER,1,0,0);
		ds.setDate(date);
		ds.setRainfall(0);
		assertTrue(ds.checkSample()==DataStatus.ALL_GOOD);
		ds.setRainfall(-1);
		assertTrue(ds.checkSample()==DataStatus.OUT_OF_RANGE_RAINFALL);
		ds.setDate(null);
		assertTrue(ds.checkSample()==DataStatus.MISSING_DATE);
	}
	
}
