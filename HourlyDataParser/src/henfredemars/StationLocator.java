package henfredemars;

import java.util.HashMap;

//Locate stations by their id
public class StationLocator {
	
	private HashMap<String,float[]> lookupTable = null;
	
	public StationLocator(String fileName) {
		lookupTable = new HashMap<String,float[]>();
		String[] f = Util.readTextfile(fileName);
		int tableStart = -1;
		for (int i = 0; i<f.length; i++) {
			if (f[i].startsWith("010010")) {
				tableStart = i;
				break;
			}
		}
		if (tableStart==-1) {
			throw new RuntimeException("StationLocator - table not found");
		}
		for (int i = tableStart; !f[i].startsWith("997000"); i++) {
			String latString = f[i].substring(59,63);
			String lonString = f[i].substring(65,70);
			if (latString.trim().isEmpty()) continue;
			if (lonString.trim().isEmpty()) continue;
			float latitude = Integer.valueOf(latString)/100.0f;
			float longitude = Integer.valueOf(lonString)/100.0f;
			if (f[i].charAt(63)=='S') {
				latitude *= -1;
			}
			if (f[i].charAt(70)=='W') {
				longitude *= -1;
			}
			if (f[i].charAt(63)==' ') continue;
			if (f[i].charAt(70)==' ') continue;
			float[] farray = {latitude,longitude};
			lookupTable.put(f[i].split(" +")[0], farray);
		}
	}
	
	public float[] getLatLon(String stationid) {
		return lookupTable.get(stationid);
	}
	
	public boolean inTargetArea(String stationid) {
		if (!knowsStation(stationid)) return false;
		float[] latlon = getLatLon(stationid);
		boolean latInRange = ((latlon[0] < 49) && (latlon[0] > 25));
		boolean lonInRange = ((latlon[1] < -75) && (latlon[1] > -125));
		return latInRange && lonInRange;
	}
	
	public boolean knowsStation(String stationid) {
		return lookupTable.containsKey(stationid);
	}

}
