package com.slayermaster.data;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.api.OSRSWikiScraper;
import com.slayermaster.api.SlayerLocation;

import java.util.List;

public class WikiDataLoader
{
    private final OSRSWikiScraper wikiScraper;

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
        for (SlayerAssignment assignment : assignments) {
            String taskName = assignment.getTaskName().replace(" ", "_");
            List<SlayerLocation> slayerLocations = wikiScraper.parseLocationComparisonTable(taskName);
            assignment.setSlayerLocation(slayerLocations);
        }

        return assignments;
    }
}

