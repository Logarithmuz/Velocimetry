package de.velocimetry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VelocimetryMain {

	public static void main(String[] args) {

		SQLConnection sqlConnection = new SQLConnection();
		sqlConnection.connect("localhost:3306", "velocimetry", "velocimetry_user", "po9KB76BVPh4p:^uOmEn");

		List<File> files_fest = getListOfFilesInFolder("speed_data/in(fest)");
		List<File> files_mobil = getListOfFilesInFolder("speed_data/out(mobil)");

		DataImporter importer = new DataImporter(sqlConnection);
		importer.importFromFiles(files_fest, Device.FEST);
		importer.importFromFiles(files_mobil, Device.MOBIL);
	}

	private static List<File> getListOfFilesInFolder(String path){
		File folder = new File(VelocimetryMain.class.getClassLoader().getResource(path).getPath());
		return getListOfFilesInFolder(folder);
	}

	private static List<File> getListOfFilesInFolder(final File folder) {
		List<File> files = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				files.addAll(getListOfFilesInFolder(fileEntry));
			} else {
				files.add(fileEntry);
			}
		}
		return files;
	}
}
