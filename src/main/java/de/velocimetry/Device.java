package de.velocimetry;

public enum Device {
	FEST("FEST"),
	MOBIL("MOBIL");
	private String name;

	Device(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Device getDevice(String s) {
		if (s.equals(Device.MOBIL.name))
			return Device.MOBIL;
		if (s.equals(Device.FEST.name))
			return Device.FEST;
		return null;
	}
}
