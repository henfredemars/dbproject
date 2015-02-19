package henfredemars;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;


//Utility class for less-experienced Java programmers in our group
public class Util {

	//Read s UTF8-encoded text file as a (clean) array of strings
	//If you get garbage, make a new method and try a different encoding
	public static String[] readTextfile(String fileName) {
		String[] sr = null;
		try {
			sr = (new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+
					File.separator+
					fileName)), StandardCharsets.UTF_8)
			.split("\n"));
		} catch (IOException e) {
			System.out.println("Util::readTextFile - Error reading file");
			e.printStackTrace();
		}
		for (int i = 0; i<sr.length; i++) {
			sr[i] = sr[i].trim();
		}
		return sr;
	}

	//Write ArrayList<DataSample> to file
	public static void writeFile(ArrayList<DataSample> data,String fileName) {
		FileOutputStream fOutStream = null;
		ObjectOutputStream oos = null;
		deleteIfExists(fileName);
		try {
			fOutStream = new FileOutputStream(System.getProperty("user.dir")+
					File.separator + fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Util::writeFile - Failed to open file for writing");
			e.printStackTrace();
		}
		try {
			oos = new ObjectOutputStream(fOutStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos.writeObject(data);
		} catch (IOException e) {
			System.out.println("Util::writeFile - Error writing data to file");
			e.printStackTrace();
		}
		try {
			oos.close();
			fOutStream.close();
		} catch (IOException e) {
			//Close if needed
		}
	}
	
	public static void deleteIfExists(String fileName) {
		File fileTemp = new File(System.getProperty("user.dir")+File.separator+fileName);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
	}

	//Read ArrayList<DataSample> from binary file
	@SuppressWarnings("unchecked")
	public static ArrayList<DataSample> readFile(String fileName) {
		FileInputStream fInStream = null;
		ObjectInputStream ois = null;
		ArrayList<DataSample> data = null;
		try {
			fInStream = new FileInputStream(System.getProperty("user.dir")+
					File.separator + fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Util::readFile - Failed to open file for reading");
			e.printStackTrace();
		}
		try {
			ois = new ObjectInputStream(fInStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			data = (ArrayList<DataSample>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Util::readFile - Error reading object from file");
			e.printStackTrace();
		}
		if (data==null) {
			System.out.println("Util::readFile - WARNING: Returning no data");
		}
		try {
			ois.close();
			fInStream.close();
		} catch (IOException e) {
			//Close if we need to close it
		}
		return data;
	}
	
	//Write ArrayList<DataSample> with default fileName
	public static void writeFile(ArrayList<DataSample> data) {
		writeFile(data,"samples.dat");
	}
	
	//Read ArrayList<DataSample> with default fileName
	public static ArrayList<DataSample> readFile() {
		return readFile("samples.dat");
	}
	
	@Test
	public void testFileIO() {
		ArrayList<DataSample> data = new ArrayList<DataSample>();
		data.add(new DataSample());
		writeFile(data);
		File file = new File(System.getProperty("user.dir")+File.separator+"samples.dat");
		assertEquals(file.exists(),true);
		readFile();
		deleteIfExists("samples.dat");
		assertEquals(file.exists(),false);
	}

}
