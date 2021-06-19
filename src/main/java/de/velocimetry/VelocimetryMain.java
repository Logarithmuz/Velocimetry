package de.velocimetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class VelocimetryMain {

	public static void main(String[] args) {
		String server = null, username = null, password = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please insert server adress: ");
			server = in.readLine();
			System.out.println("Please insert username: ");
			username = in.readLine();
			System.out.println("Please insert password: ");
			password = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SQLConnection sqlConnection = new SQLConnection();
		sqlConnection.connect(server + ":3306", "data", username, password);

		List<File> files_fest = getListOfFilesInFolder("speed_data/in(fest)");
		List<File> files_mobil = getListOfFilesInFolder("speed_data/out(mobil)");

		DataImporter importer = new DataImporter(sqlConnection);
		importer.importFromFiles(files_fest, Device.FEST);
		importer.importFromFiles(files_mobil, Device.MOBIL);
	}

	public static List<File> getListOfFilesInFolder(String path) {
		File folder = new File(VelocimetryMain.class.getClassLoader().getResource(path).getPath());
		return getListOfFilesInFolder(folder);
	}

	private static List<File> getListOfFilesInFolder(final File folder) {
		List<File> files = new ArrayList<>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				files.addAll(getListOfFilesInFolder(fileEntry));
			} else {
				if (!fileEntry.getName().equals("wave.log"))
					files.add(fileEntry);
			}
		}
		return files;
	}
}
