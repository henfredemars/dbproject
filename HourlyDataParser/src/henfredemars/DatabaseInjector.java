package henfredemars;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

class DatabaseInjector {

	public static void main(String[] args) {
		//Initialization
		ObjectInputStream ois = prepareInput(args[0]);
		HashSet<Station> stations = new HashSet<Station>(400);
		Scanner scanner = new Scanner(System.in);
		long insertedTuples = 0;
		try { //Initialize the driver
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Connect to the database through SSH tunnel
		String username = getUsername(scanner);
		String password = getPassword(scanner);
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/orcl",
					username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//Prepare general statement object
		Statement st = null;
		try {
			st = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//Insert each sample into the database
		while (true) {
			Station station = null;
			try {
				station = (Station) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				//EOF
				break;
			}
			stations.add(station);
			for (DataSample ds: station.getSamples()) {
				Calendar date = ds.getDate();
				try {
					String query = ("INSERT INTO CLIMATE_DATA VALUES(" + 
							ds.getStationId() + String.valueOf(ds.getPressure()) +
							String.valueOf(ds.getHumidity()) + "," +
							String.valueOf(ds.getRainfall()) + "," +
							String.valueOf(ds.getWindSpeed()) + "," +
							String.valueOf(ds.getTemperature()) + "," +
							String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + "-" +
							getMonthString(date.get(Calendar.MONTH)) + "-" +
							String.valueOf(date.get(Calendar.YEAR)) + ");");
					System.out.println(query);
					st.executeQuery(query);
					insertedTuples++;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		//Insert station data
		System.out.println("Now inserting station data...");
		for (Iterator<Station> iter = stations.iterator();iter.hasNext();) {
			Station station = iter.next();
			try {
				String query = ("INSERT INTO STATION_TABLE VALUES(" +
						String.valueOf(station.getStationId()) + "," +
						String.valueOf(station.getLat()) + "," +
						String.valueOf(station.getLon()) + ");");
				System.out.println(query);
				st.executeQuery(query);
				insertedTuples++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Inserted " +insertedTuples+ " tuples.");
		try {
			ois.close();
		} catch (IOException e) {
			//Do nothing
			e.printStackTrace();
		}
	}
	
	private static String getUsername(Scanner scanner) {
		String username = scanner.nextLine();
		System.out.println("Got username: " +username);
		return username;
	}
	
	private static String getPassword(Scanner scanner) {
		String password = scanner.nextLine();
		System.out.println("Got password: " +password);
		return password;
	}
	
	private static String getMonthString(int month) {
		String[] monthArray = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP",
				"OCT","NOV","DEC"};
		return monthArray[month];
	}
	
	private static ObjectInputStream prepareInput(String fname) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fname));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(new BufferedInputStream(fis));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(gis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ois;
	}
	
}
