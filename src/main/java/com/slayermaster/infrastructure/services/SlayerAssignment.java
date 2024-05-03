package com.slayermaster.infrastructure.services;

import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class SlayerAssignment
{
	// Getters
	private final String slayerLevel;
	private final String taskName;
	private final String monsterName;
	private List<SlayerLocation> slayerLocations;
	private final String[] locations;
	private final String[] requiredItems;
	private final String attribute;
	private final String attackStyle;
	private final String[] alternatives;
	private final String[] slayerMasters;

	// Constructor
	public SlayerAssignment(String slayerLevel,
							String taskName,
							String monsterName,
							String[] locations,
							String[] requiredItems,
							String attribute,
							String attackStyle,
							String[] alternatives,
							String[] slayerMasters)
	{
		// Use Optional.ofNullable to handle potential null values
		this.slayerLevel = Optional.ofNullable(slayerLevel).orElse("1");
		this.taskName = Optional.ofNullable(taskName).orElse("Unknown");
		this.monsterName = Optional.ofNullable(monsterName).orElse("Unknown");
		this.locations = Optional.ofNullable(locations).orElse(new String[]{"Unknown"});
		this.requiredItems = Optional.ofNullable(requiredItems).orElse(new String[]{});
		this.attribute = Optional.ofNullable(attribute).orElse("None");
		this.attackStyle = Optional.ofNullable(attackStyle).orElse("Unknown");
		this.alternatives = Optional.ofNullable(alternatives).orElse(new String[]{});
		this.slayerMasters = Optional.ofNullable(slayerMasters).orElse(new String[]{"Unknown"});
	}

	// Setters
	public void setSlayerLocation(List<SlayerLocation> slayerLocations)
	{
		this.slayerLocations = slayerLocations;
	}
}

