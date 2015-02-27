package henfredemars;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

public class WeekCompounderTest {
	
	@Test
	public void testWeekCompounder() {
		ArrayList<DataSample> hourData = new ArrayList<DataSample>();
		for (int i = 0; i < 3; i++) {
			DataSample ds = new DataSample();
			ds.setTemperature(50);
			ds.setHumidity(0.5f);
			ds.setPressure(1000);
			ds.setRainfall(1);
			ds.setStationId(String.valueOf(i));
			ds.setWindSpeed(5);
			ds.setDate(Calendar.getInstance());
			if (ds.checkSample()!=DataStatus.ALL_GOOD) {
				throw new RuntimeException("WC::test:: Bad data sample gen");
			}
			hourData.add(ds);
		}
		WeekCompounder wc = new WeekCompounder(hourData);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		wc.compoundDataByWeek(yesterday, Calendar.getInstance());
		ArrayList<Station> stns = wc.getStations();
		assertTrue(stns.size()==3);
		Station one = stns.get(0);
		assertTrue(one.getSamples().size()==1);
		DataSample ds = one.getSamples().get(0);
		assertTrue(ds.getTemperature()==50);
	}
}
