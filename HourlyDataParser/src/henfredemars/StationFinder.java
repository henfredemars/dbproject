package henfredemars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;

//Find the stations that exist in the database and its intersection with a set of sample stations
public class StationFinder {

	public static void main(String[] args) {
		//Setup IO
		StationLocator sl = new StationLocator("StationData1999.txt");
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		HashSet<String> stations = new HashSet<String>();
		try {
			fin = new FileInputStream(new File(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("StationFinder - could not open file");
			e.printStackTrace();
		}
		try {
			ois = new ObjectInputStream(fin);
		} catch (IOException e) {
			System.out.println("StationFinder - could not open file");
			e.printStackTrace();
		}
		//Filter samples for their stations that we can identify
		while (true) {
			DataSample ds = null;
			try {
				ds = (DataSample)ois.readObject();
			} catch (IOException e) {
				//End of file, we hope
				try {
					ois.close();
					break;
				} catch (IOException e1) {
					//Do nothing
				}
				break;
			} catch (ClassNotFoundException e) {
				//Shouldn't happen
				e.printStackTrace();
			}
			if (sl.knowsStation(ds.getStationId())) {
				stations.add(ds.getStationId());
			}
		}
		//Write out list of known stations
		Util.writeFile(new ArrayList<String>(stations),"stationfilter.dat");
	}
}
