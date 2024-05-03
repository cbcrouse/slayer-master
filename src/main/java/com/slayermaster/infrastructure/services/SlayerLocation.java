package com.slayermaster.infrastructure.services;

import lombok.Getter;

@Getter
public class SlayerLocation
{
	private final String locationName;
	private final String mapLink;
	private final String amount;
	private final boolean isMulticombat;
	private final boolean isCannonable;
	private final boolean isSafespottable;
	private final String notes;

	public SlayerLocation(String locationName, String mapLink, String amount, boolean isMulticombat, boolean isCannonable, boolean isSafespottable, String notes)
	{
		this.locationName = locationName;
		this.mapLink = mapLink;
		this.amount = amount;
		this.isMulticombat = isMulticombat;
		this.isCannonable = isCannonable;
		this.isSafespottable = isSafespottable;
		this.notes = notes;
	}

	@Override
	public String toString()
	{
		return "SlayerLocation{" +
			"locationName='" + locationName + '\'' +
			", mapLink='" + mapLink + '\'' +
			", amount=" + amount +
			", multicombat=" + isMulticombat +
			", cannonable=" + isCannonable +
			", safespottable=" + isSafespottable +
			", notes='" + notes + '\'' +
			'}';
	}
}
