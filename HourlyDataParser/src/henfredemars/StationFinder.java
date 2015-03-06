package henfredemars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;

//Find the stations that exist in the database and its intersection with a set of sample stations
//climate_data.dat, stations.txt, output
public class StationFinder {

	public static void main(String[] args) {
		//Setup IO
		System.out.println("Enumerating stations from location database...");
		StationLocator sl = new StationLocator(args[1]);
		FileInputStream fin = null;
		BufferedInputStream bis = null;
		GZIPInputStream gis = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		HashMap<String,Long> stations = new HashMap<String,Long>();
		
		//Output
		try {
			fos = new FileOutputStream(new File(args[2]));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			oos = new ObjectOutputStream(fos);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Input
		try {
			fin = new FileInputStream(new File(args[0]));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bis = new BufferedInputStream(fin);
		try {
			gis = new GZIPInputStream(bis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ois = new ObjectInputStream(gis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Read DataSample input
		long sampleLocation = 0;
		while (true) {
			DataSample ds = null;
			try {
				ds = (DataSample) ois.readObject();
				sampleLocation++;
				if (sampleLocation%1000000==0) {
					System.out.println("Scanning Sample: " + sampleLocation);
				}
			} catch (IOException e) {
				//Done reading
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!(ds.checkSample()==DataStatus.ALL_GOOD)) continue;
			if (!sl.knowsStation(ds.getStationId())) continue;
			if (!sl.inTargetArea(ds.getStationId())) continue;
			if (stations.containsKey(ds.getStationId())) {
				stations.put(ds.getStationId(),stations.get(ds.getStationId())+1);
			} else {
				stations.put(ds.getStationId(),1L);
			}
		}
		
		//Remove stations with few samples
		ArrayList<Long> stationCounts = new ArrayList<Long>();
		for (String station: stations.keySet()) {
			stationCounts.add(stations.get(station));
		}
		System.out.println(stations.size() + " stations found.");
		System.out.println("Sorting stations by number of samples...");
		Collections.sort(stationCounts);
		long median = stationCounts.get((int)(stationCounts.size()*0.7));
		System.out.println("Median is about " + median + " samples per station.");
		for (Iterator<String> iter = stations.keySet().iterator();iter.hasNext();) {
			String station = iter.next();
			if (stations.get(station) < median) {
				iter.remove();
			}
		}
		System.out.println("Found " + stations.size() + " stations meeting criteria.");
		
		//Write out list of known stations
		try {
			oos.writeObject(new ArrayList<String>(stations.keySet()));
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
