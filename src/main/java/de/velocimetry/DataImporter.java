package de.velocimetry;

import java.sql.SQLException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DataImporter {

	private SQLConnection sqlConnection;
	private List<SpeedMeasurement> speedMeasurementList = new ArrayList<SpeedMeasurement>();

	public DataImporter(SQLConnection sqlConnection) {
		super();
		this.sqlConnection = sqlConnection;
		long startTime = System.currentTimeMillis();
		loadDatabase();
		System.out.println("Loaded Database, took " + (System.currentTimeMillis() - startTime) + "ms\n");
	}

	public boolean importFromFiles(List<File> files, Direction direction) {
		boolean success = false;
		List<SpeedMeasurement> entrysToAdd = new ArrayList<SpeedMeasurement>();

		System.out.println("Reading files for direction: " + direction.toString());
		for (File file : files) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));

				for (String line = in.readLine(); line != null; line = in.readLine()) {
					String[] tokens = line.split(" ");
					String date = parseDate(tokens[0]);
					String time = tokens[1];
					short speed_in = Short.parseShort(tokens[2]);
					short speed_out = Short.parseShort(tokens[4]);

					SpeedMeasurement sm = new SpeedMeasurement(date, time, direction, speed_in, speed_out);
					// todo: optimize comparison
					//if (!isInDb(sm)) {
					entrysToAdd.add(sm);
					speedMeasurementList.add(sm);
					//}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("	Found " + entrysToAdd.size() + " entries that need to be inserted");

		try {
			sqlConnection.insertData(entrysToAdd);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	private boolean isInDb(SpeedMeasurement sm) {
		for (SpeedMeasurement speedMeasurementEntry : speedMeasurementList) {
			if (speedMeasurementEntry.equals(sm)) {
				return true;
			}
		}
		return false;
	}

	private String parseDate(String token) {
		String[] dateTokens = token.split(Pattern.quote("."));
		String day = dateTokens[0];
		String month = dateTokens[1];
		String year = "20" + dateTokens[2];
		return year + "-" + month + "-" + day;
	}

	private void loadDatabase() {
		speedMeasurementList = sqlConnection.getAllEntrys();
	}

}
