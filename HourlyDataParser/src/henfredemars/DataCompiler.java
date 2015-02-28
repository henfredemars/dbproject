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

//Parse and compile data samples into a single file
//Input files must be in text form (i.e. not compressed)
public class DataCompiler {

	public static void main(String[] args) {
		File directory = new File(args[0]);
		File outputFile = new File(args[1]);
		File[] files = directory.listFiles();
		//Prepare output file
		FileOutputStream fOutStream = null;
		ObjectOutputStream oos = null;
		try {
			fOutStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e1) {
			System.out.println("DataCompiler - error writing file");
			e1.printStackTrace();
		}
		try {
			oos = new ObjectOutputStream(fOutStream);
		} catch (IOException e1) {
			System.out.println("DataCompiler - error writing file");
			e1.printStackTrace();
		}
		//Process files
		for (File file: files) {
			System.out.println("Processing file: " + file.getName());
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
				DataSample ds = new DataSample();
				String[] elements = line.split(" ");
				ds.setStationId(elements[0]);
				Calendar date = Calendar.getInstance();
				String dateStr = elements[2];
				date.set(Calendar.YEAR, Integer.valueOf(dateStr.substring(0,4)));
				date.set(Calendar.MONTH, Integer.valueOf(dateStr.substring(4,6))-1);
				date.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateStr.substring(6,8)));
				date.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateStr.substring(8,10)));
				date.set(Calendar.MINUTE, Integer.valueOf(dateStr.substring(10,12)));
				ds.setDate(date);
				try {
					ds.setRainfall(Float.valueOf(elements[28]));
				} catch (NumberFormatException e) {
					ds.setRainfall(0f);
				}
				try {
					ds.setTemperature(Float.valueOf(elements[21]));
					ds.setHumidity(Float.valueOf(elements[22]));
					ds.setPressure(Float.valueOf(elements[23]));
				} catch (NumberFormatException e) {
					System.out.println("Bad measurement discarded.");
					continue; //Bad measurement
				}
				if (ds.checkSample()==DataStatus.ALL_GOOD) {
					try {
						oos.writeObject(ds);
					} catch (IOException e) {
						System.out.println("DataCompiler - error writing object");
						e.printStackTrace();
					}
				} else {
					System.out.println("Bad measurement discarded.");
				}
			}
		}
		try {
			oos.close();
		} catch (IOException e) {
			//Do nothing
		}
	}
	
}