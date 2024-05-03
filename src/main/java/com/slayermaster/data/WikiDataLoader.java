package com.slayermaster.data;

import com.google.inject.Inject;
import com.slayermaster.infrastructure.services.SlayerAssignment;
import com.slayermaster.infrastructure.services.OSRSWikiScraper;
import com.slayermaster.infrastructure.services.SlayerLocation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WikiDataLoader
{
	private final OSRSWikiScraper wikiScraper;

	@Inject
	public WikiDataLoader(OSRSWikiScraper wikiScraper)
	{
		this.wikiScraper = wikiScraper;
	}

	public List<SlayerAssignment> getWikiSlayerMonsters()
	{
		List<SlayerAssignment> assignments = wikiScraper.parseSlayerTaskPage();

		assignments.removeIf(assignment -> assignment.getMonsterName().equalsIgnoreCase("unknown")
			|| assignment.getMonsterName().equalsIgnoreCase("slayer"));

		// For each assignment, query the location comparison table to get the SlayerLocation
		for (SlayerAssignment assignment : assignments)
		{
			String taskName = assignment.getTaskName().replace(" ", "_");
			List<SlayerLocation> slayerLocations = wikiScraper.parseLocationComparisonTable(taskName);
			assignment.setSlayerLocation(slayerLocations);
		}

		return assignments;
	}
}

