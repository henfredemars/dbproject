package henfredemars;

import java.util.HashMap;

//Locate stations by their id
public class StationLocator {
	
	HashMap<String,String[]> lookupTable = null;
	
	public StationLocator(String fileName) {
		lookupTable = new HashMap<String,String[]>();
		String[] f = Util.readTextfile(fileName);
		for (String s: f) {
			if (f.length!=0) {
				String[] as = s.split("|");
				if (as[0].replace("\"", "")=="REGION") continue;
				String[] latlon = as[12].split(" ");
				lookupTable.put(as[1].replace("\"", ""), latlon);
			}
		}
	}
	
	public int[] getLatLon(String stationid) {
		String[] s = lookupTable.get(stationid);
		int[] res = {Integer.valueOf(s[0]),Integer.valueOf(s[1])};
		return res;
	}

}
