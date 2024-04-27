package com.slayermaster;

import java.util.List;

public class Monster
{
    private String name;
    private List<String> locations;
    private String recommendedLocation;
    private String[] requiredItems;
    private String recommendedGear;
    private String attackStyle;

    // Constructor
    public Monster(String name, List<String> locations, String recommendedLocation, String[] requiredItems, String recommendedGear, String attackStyle)
    {
        this.name = name;
        this.locations = locations;
        this.recommendedLocation = recommendedLocation;
        this.requiredItems = requiredItems;
        this.recommendedGear = recommendedGear;
        this.attackStyle = attackStyle;
    }

    // Getters
    public String getName() { return name; }
    public List<String> getLocations() { return locations; }
    public String getRecommendedLocation() { return recommendedLocation; }
    public String[] getRequiredItems() { return requiredItems; }
    public String getRecommendedGear() { return recommendedGear; }
    public String getAttackStyle() { return attackStyle; }
}


