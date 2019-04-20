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
			String options = "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?" + options, user, password);
			System.out.println("Connection established\n");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public boolean insertData(Calendar calendar, Direction direction, int speed_in, int speed_out) {
		boolean success = false;
		System.out.println("Date: " + calendar.getTime().toString() + ", speed_in: " + speed_in + ", speed_out: " + speed_out);

		String directionStr = "";
		if (direction == Direction.IN) {
			directionStr = "IN";
		}
		if (direction == Direction.OUT) {
			directionStr = "OUT";
		}

		try {
			Object param = new java.sql.Timestamp(calendar.getTime().getTime());

			// the mysql insert statement
			String query = " insert into speed_measurement (datetime, direction, speed_in, speed_out)"
					+ " values (?, ?, ?, ?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = con.prepareStatement(query);

			preparedStmt.setObject(1, param);
			preparedStmt.setString(2, directionStr);
			preparedStmt.setInt(3, speed_in);
			preparedStmt.setInt(4, speed_out);

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	public Map<Integer, List<SpeedMeasurement>> getAllEntrys() {
		Map<Integer, List<SpeedMeasurement>> speedListMap = new HashMap<Integer, List<SpeedMeasurement>>();

		try {
			String query = " select * from speed_measurement";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int i = 0;
			while (rs.next()) {
				i++;
				int id = rs.getInt("id");
				String dateString = rs.getString("date");
				int date = Integer.parseInt(dateString.replaceAll("-", ""));
				String time = rs.getString("time");
				String directionString = rs.getString("direction");
				Direction direction = (directionString.equals("IN")) ? Direction.IN : Direction.OUT;
				short speed_in = rs.getShort("speed_in");
				short speed_out = rs.getShort("speed_out");

				SpeedMeasurement sm = new SpeedMeasurement(id, date, time, direction, speed_in, speed_out);

				if (!speedListMap.containsKey(date)) {
					List<SpeedMeasurement> speedMeasurementList = new ArrayList<SpeedMeasurement>();
					speedListMap.put(date, speedMeasurementList);
				}

				speedListMap.get(date).add(sm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return speedListMap;
	}

	public void insertData(List<SpeedMeasurement> entrysToAdd) throws SQLException {
		// Create SQL statement
		String SQL = "INSERT INTO speed_measurement (date, time, direction, speed_in, speed_out) " +
				"VALUES(?, ?, ?, ?, ?)";

		// Create PrepareStatement object
		PreparedStatement pstmt = con.prepareStatement(SQL);

		//Set auto-commit to false
		con.setAutoCommit(false);

		System.out.println("Inserting data into database");
		System.out.println("	(this could take several minutes depending on your PC and amount of entries)");
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < entrysToAdd.size(); i++) {
			SpeedMeasurement sm = entrysToAdd.get(i);
			pstmt.setInt(1, sm.date);
			pstmt.setString(2, sm.time);
			String direction = (sm.direction == Direction.IN) ? "IN" : "OUT";
			pstmt.setString(3, direction);
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
	}
}
