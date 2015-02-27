package henfredemars;

import java.util.Calendar;

//Interface to the DataSample data structure
public interface DataSampleInterface {
	
	public void setStationId(String station);
	public void setTemperature(float tempInF);
	public void setHumidity(float humidity);
	public void setWindSpeed(float windSpeedInMPH);
	public void setPressure(float seaLevelPressureInMillibars);
	public void setRainfall(float hourlyInches);
	public void setDate(Calendar date);
	
	public String getStationId();
	public float getTemperature();
	public float getHumidity();
	public float getWindSpeed();
	public float getPressure();
	public float getRainfall();
	public Calendar getDate();
	
	//BE CAREFUL that you use new Calendar instances for each DataSample
	
	public DataStatus checkSample();

}
