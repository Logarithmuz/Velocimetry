package de.velocimetry;

import java.sql.*;
import java.util.*;

public class SQLConnection {

	private static Connection con;

	public SQLConnection() {
		super();
	}

	public void connect(String server, String database, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String options = "useUnicode=true&" +
					"useJDBCCompliantTimezoneShift=true&" +
					"useLegacyDatetimeCode=false&" +
					"serverTimezone=UTC&" +
					"useServerPrepStmts=false&" +
					"rewriteBatchedStatements=true&" +
					"useCompression=true";
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?" + options, user, password);
			System.out.println("Connection established\n");
		} catch (Exception e) {
			System.out.println("Please make sure your database is running");
			System.out.println(e);
		}
	}

	public boolean insertData(Calendar calendar, Device device, int speed_in, int speed_out) {
		boolean success = false;
		System.out.println("Date: " + calendar.getTime().toString() + ", speed_in: " + speed_in + ", speed_out: " + speed_out);


		Object param = new java.sql.Timestamp(calendar.getTime().getTime());

		// the mysql insert statement
		String query = " insert into speed_measurement (datetime, device, speed_in, speed_out)"
				+ " values (?, ?, ?, ?)";

		// create the mysql insert preparedstatement
		try (PreparedStatement preparedStmt = con.prepareStatement(query)) {

			preparedStmt.setObject(1, param);
			preparedStmt.setString(2, device.toString());
			preparedStmt.setInt(3, speed_in);
			preparedStmt.setInt(4, speed_out);

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return success;
	}

	public Map<Integer, Map<String, List<SpeedMeasurement>>> getAllEntrys() {
		Map<Integer, Map<String, List<SpeedMeasurement>>> speedMeasurementDateMap = new HashMap<Integer, Map<String, List<SpeedMeasurement>>>();

		String query = " select * from speed_measurement";
		ResultSet rs = null;
		try (Statement stmt = con.createStatement()) {
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				int id = rs.getInt("id");
				String dateString = rs.getString("date");
				int date = Integer.parseInt(dateString.replaceAll("-", ""));
				String time = rs.getString("time");
				Device device = Device.getDevice(rs.getString("device"));
				short speed_in = rs.getShort("speed_in");
				short speed_out = rs.getShort("speed_out");

				SpeedMeasurement sm = new SpeedMeasurement(id, date, time, device, speed_in, speed_out);

				if (!speedMeasurementDateMap.containsKey(date)) {
					Map<String, List<SpeedMeasurement>> speedMeasurementMap = new HashMap<String, List<SpeedMeasurement>>();
					speedMeasurementDateMap.put(date, speedMeasurementMap);
				}

				Map<String, List<SpeedMeasurement>> speedMeasurementTimeMap = speedMeasurementDateMap.get(sm.date);
				if (!speedMeasurementDateMap.get(sm.date).containsKey(sm.time)) {
					List<SpeedMeasurement> speedMeasurementList = new ArrayList<SpeedMeasurement>();
					speedMeasurementTimeMap.put(sm.time, speedMeasurementList);
				}

				List<SpeedMeasurement> speedMeasurementList = speedMeasurementTimeMap.get(sm.time);
				speedMeasurementList.add(sm);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return speedMeasurementDateMap;
	}

	public void insertData(List<SpeedMeasurement> entrysToAdd) {
		// Create SQL statement
		String SQL = "INSERT INTO speed_measurement (date, time, device, speed_in, speed_out) " +
				"VALUES(?, ?, ?, ?, ?)";

		// Create PrepareStatement object
		long startTime;
		try (PreparedStatement pstmt = con.prepareStatement(SQL)) {

			//Set auto-commit to false
			con.setAutoCommit(false);

			System.out.println("Inserting data into database");
			System.out.println("	(this could take several minutes depending on your PC and amount of entries)");
			startTime = System.currentTimeMillis();

			for (int i = 0; i < entrysToAdd.size(); i++) {
				SpeedMeasurement sm = entrysToAdd.get(i);
				pstmt.setInt(1, sm.date);
				pstmt.setString(2, sm.time);
				pstmt.setString(3, sm.device.toString());
				pstmt.setInt(4, sm.speed_in);
				pstmt.setInt(5, sm.speed_out);
				// Add it to the batch
				pstmt.addBatch();
			}
			//Create an int[] to hold returned values
			int[] count = pstmt.executeBatch();

			//Explicitly commit statements to apply changes
			con.commit();
			System.out.println("Commit executed\n" +
					"	inserted " + entrysToAdd.size() + " entries, took " + (System.currentTimeMillis() - startTime) + "ms\n");
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
}
