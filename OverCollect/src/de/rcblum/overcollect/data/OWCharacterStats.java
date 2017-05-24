package de.rcblum.overcollect.data;

import java.util.HashMap;
import java.util.Map;

public class OWCharacterStats extends OWStats {
	protected String name = null;

	Map<String, Integer> secondaryStats = new HashMap<>();

	public void setName(String name) {
		this.name = name;
	}

	public void addSecondaryStat(String name, Integer value) {
		this.secondaryStats.put(name, value);
	}

	public String getName() {
		return name;
	}

	public Map<String, Integer> getSecondaryStats() {
		return secondaryStats;
	}
}
