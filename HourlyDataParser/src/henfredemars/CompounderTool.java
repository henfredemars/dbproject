package henfredemars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//Makes weekly summaries for each of the selected stations
//climate_data,stations.dat,stations.txt,output
public class CompounderTool {
	public static final int stationIncrement = 20;
	
	public static void main(String[] args) {
		String[] allStations = readStations(args[1]).toArray(new String[0]);
		ObjectOutputStream oos = prepareOutput(args[3]);
		StationLocator sl = new StationLocator(args[2]);
		int stationIndex = 0;
		while (true) {
			if (stationIndex >= allStations.length) break;
			int maxRange = stationIndex+stationIncrement-1;
			if (maxRange > allStations.length-1) {
				maxRange = allStations.length-1;
			}
			System.out.println("Processing stations " + stationIndex + " to " +
					String.valueOf(maxRange) + "...");
			ArrayList<String> curStationIds = new ArrayList<String>(stationIncrement);
			ArrayList<DataSample> dataSamples = new ArrayList<DataSample>(stationIncrement);
			for (int i = stationIndex; i<stationIndex+stationIncrement && i<allStations.length;i++) {
				curStationIds.add(allStations[i]);
			}
			stationIndex += stationIncrement;
			ObjectInputStream ois = readFileFromBeginning(args[0]);
			while (true) {
				DataSample ds = null;
				try {
					ds = (DataSample) ois.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					//EOF
					try {
						ois.close();
					} catch (IOException e1) {
						//Do nothing
					}
					break;
				}
				if (curStationIds.contains(ds.getStationId())) {
					dataSamples.add(ds);
				}
			}
			System.out.println("Compounding station data...");
			WeekCompounder wc = new WeekCompounder(dataSamples);
			Calendar start = Calendar.getInstance();
			start.set(Calendar.YEAR,2004);
			start.set(Calendar.MONTH,Calendar.JANUARY);
			start.set(Calendar.DAY_OF_MONTH,1);
			start.set(Calendar.HOUR_OF_DAY,0);
			start.set(Calendar.MINUTE,0);
			Calendar end = Calendar.getInstance();
			end.set(Calendar.YEAR,2014);
			end.set(Calendar.MONTH,Calendar.DECEMBER);
			end.set(Calendar.DAY_OF_MONTH,31);
			end.set(Calendar.HOUR_OF_DAY,23);
			end.set(Calendar.MINUTE,59);
			wc.compoundDataByWeek(start, end);
			ArrayList<Station> compoundedStations = wc.getStations();
			wc = null; //Free ASAP
			for (Station s: compoundedStations) {
				float[] latlon = sl.getLatLon(s.getStationId());
				s.setLatLon(latlon[0],latlon[1]);
				System.out.println("Writing " + s.getStationId() + ", " + s.getSamples().size() + " samples.");
				try {
					oos.writeObject(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			oos.close();
		} catch (IOException e) {
			//Do nothing
		}
	}
	
	private static ObjectOutputStream prepareOutput(String fname) {
		ObjectOutputStream oos = null;
		GZIPOutputStream gos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(fname));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			gos = new GZIPOutputStream(fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos = new ObjectOutputStream(gos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oos;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<String> readStations(String fname) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fname));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ArrayList<String> res = (ArrayList<String>)ois.readObject();
			ois.close();
			return res;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; //Should never happen
	}
	
	private static ObjectInputStream readFileFromBeginning(String fname) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fname));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(new BufferedInputStream(fis));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(gis);
			return ois;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; //Should never happen
	}
	
}
