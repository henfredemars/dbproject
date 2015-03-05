package henfredemars;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.HashSet;

//Parse and compile data samples into a single file
//Input files must be in text form (i.e. not compressed)
public class DataCompiler {

	public static void main(String[] args) {
		StationLocator sl = new StationLocator(args[2]);
		File directory = new File(args[0]);
		File outputFile = new File(args[1]);
		File[] files = directory.listFiles();
		long numberOfGoodRecords = 0;
		long numberOfBadRecords = 0;
		long totalNumberOfRecords = 0;
		long filesProcessed = 0;
		HashSet<String> stationSet = new HashSet<String>();
		//Prepare output file
		FileOutputStream fOutStream = null;
		GZIPOutputStream goos = null;
		ObjectOutputStream oos = null;
		try {
			fOutStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e1) {
			System.out.println("DataCompiler - error writing file");
			e1.printStackTrace();
		}
		try {
			goos = new GZIPOutputStream(fOutStream);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			oos = new ObjectOutputStream(goos);
		} catch (IOException e1) {
			System.out.println("DataCompiler - error writing file");
			e1.printStackTrace();
		}
		//Process files
		for (File file: files) {
			if (!sl.inTargetArea(file.getName().split("-")[0])) continue;
			filesProcessed++;
			List<String> lines = null;
			try {
				lines = Files.readAllLines(file.toPath(),StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.out.println("DataCompiler - failed to read file lines");
				e.printStackTrace();
			}
			for (String line: lines) {
				if (line.contains("WBAN")) {
					continue;
				}
				totalNumberOfRecords++;
				DataSample ds = new DataSample();
				String[] elements = line.split(" +");
				if (elements.length<28) {
					numberOfBadRecords++;
					continue;
				}
				ds.setStationId(elements[0]);
				Calendar date = Calendar.getInstance();
				String dateStr = elements[2];
				if (dateStr.length()<12) {
					numberOfBadRecords++;
					continue;
				}
				date.set(Calendar.YEAR, Integer.valueOf(dateStr.substring(0,4)));
				date.set(Calendar.MONTH, Integer.valueOf(dateStr.substring(4,6))-1);
				date.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateStr.substring(6,8)));
				date.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateStr.substring(8,10)));
				date.set(Calendar.MINUTE, Integer.valueOf(dateStr.substring(10,12)));
				ds.setDate(date);
				try {
					ds.setRainfall(Float.valueOf(elements[28].replace('T',' ').split(" ")[0]));
				} catch (NumberFormatException e) {
					if (!elements[29].contains("*") || elements[29].contains("T")) {
						ds.setRainfall(0f);
					} else if (!elements[30].contains("*") || elements[30].contains("T")) {
						ds.setRainfall(0f);
					} else {
						numberOfBadRecords++;
						continue;
					}
				}
				try {
					ds.setWindSpeed(Float.valueOf(elements[4]));
					ds.setTemperature(Float.valueOf(elements[21]));
					ds.setHumidity(Float.valueOf(elements[22]));
					ds.setPressure(Float.valueOf(elements[25]));
				} catch (NumberFormatException e) {
					numberOfBadRecords++;
					continue; //Bad measurement
				}
				if (ds.checkSample()==DataStatus.ALL_GOOD) {
					try {
						numberOfGoodRecords++;
						oos.writeObject(ds);
						stationSet.add(ds.getStationId());
						if (numberOfGoodRecords % 1000==0) {
							oos.reset();
						}
					} catch (IOException e) {
						System.out.println("DataCompiler - error writing object");
						e.printStackTrace();
					}
				} else {
					numberOfBadRecords++;
					if (totalNumberOfRecords % 100000!=0) continue;
					System.out.println("Bad measurement discarded DS: " + ds.checkSample());
				}
			}
			if (filesProcessed%25==0) {
				System.out.println("-------------------------------");
				System.out.println("GoodRecords:    " + numberOfGoodRecords);
	               		System.out.println("BadRecords:     " + numberOfBadRecords);
      	        		System.out.println("TotalRecords:   " + totalNumberOfRecords);
				System.out.println("Stations:       " + stationSet.size());
       		        	System.out.println("FilesProcessed: " + filesProcessed);
			}
		}
		System.out.println("-------------------------------");
		System.out.println("GoodRecords:    " + numberOfGoodRecords);
		System.out.println("BadRecords:     " + numberOfBadRecords);
		System.out.println("TotalRecords:   " + totalNumberOfRecords);
		System.out.println("Stations:       " + stationSet.size());
		System.out.println("FilesProcessed: " + filesProcessed);
		try {
			oos.close();
		} catch (IOException e) {
			//Do nothing
		}
	}

}
