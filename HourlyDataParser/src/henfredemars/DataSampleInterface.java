package henfredemars;

import java.util.Calendar;

//Interface to the DataSample data structure
public interface DataSampleInterface {
	
	public void setStationId(String station);
	public void setTemperature(double tempInF);
	public void setHumidity(double relativeHumidity);
	public void setWindSpeed(double windSpeedInMPH);
	public void setPressure(double seaLevelPressureInMillibars);
	public void setRainfall(double hourlyInches);
	public void setDate(Calendar date);
	
	public String getStationId();
	public double getTemperature();
	public double getHumidity();
	public double getWindSpeed();
	public double getPressure();
	public double getRainfall();
	public Calendar getDate();
	
	//BE CAREFUL the you use new Calendar instances for each DataSample
	
	public DataStatus checkSample();

}
