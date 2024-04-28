package com.slayermaster.data;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.api.OSRSWikiScraper;

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

        return assignments;
    }
}

