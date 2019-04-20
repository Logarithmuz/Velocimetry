package de.velocimetry;

public class SpeedMeasurement {

	private int id;
	public int date;
	public String time;
	public Direction direction;
	public short speed_in;
	public short speed_out;

	public SpeedMeasurement(int id, int date, String time, Direction direction, short speed_in, short speed_out) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.direction = direction;
		this.speed_in = speed_in;
		this.speed_out = speed_out;
	}

	public SpeedMeasurement(int date, String time, Direction direction, short speed_in, short speed_out) {
		this.date = date;
		this.time = time;
		this.direction = direction;
		this.speed_in = speed_in;
		this.speed_out = speed_out;
	}

	public boolean equals(SpeedMeasurement sm) {
		return (sm.date == this.date)
				&& (sm.time.equals(this.time))
				&& (sm.direction == this.direction)
				&& (sm.speed_in == this.speed_in)
				&& (sm.speed_out == this.speed_out);
	}


}
