package com.slayermaster.data;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.api.OSRSWikiScraper;

import java.util.ArrayList;
import java.util.List;

public class WikiDataLoader
{
    private OSRSWikiScraper wikiScraper;

    public WikiDataLoader(OSRSWikiScraper wikiScraper)
    {
        this.wikiScraper = wikiScraper;
    }

    public List<Monster> getWikiSlayerMonsters()
    {
        List<SlayerAssignment> assignments = wikiScraper.parseSlayerTaskPage();
        List<Monster> monsters = new ArrayList<>();

        for (SlayerAssignment assignment : assignments)
        {
            if (assignment.getMonsterName().equalsIgnoreCase("unknown")
                || assignment.getMonsterName().equalsIgnoreCase("slayer"))
                continue;
            // Convert each SlayerAssignment into a Monster object
            // Assuming that the Monster class has been updated to accommodate the new data
            Monster monster = convertToMonster(assignment);
            monsters.add(monster);
        }

        return monsters;
    }

    private Monster convertToMonster(SlayerAssignment assignment)
    {
        // Conversion logic here
        // Create a new Monster object using the data from the SlayerAssignment
        return new Monster(
                assignment.getMonsterName(),
                assignment.getLocations(),
                assignment.getLocations()[0],
                assignment.getAlternatives(),
                assignment.getRequiredItems(),
                "N/A",
                assignment.getAttackStyle());
    }
}

