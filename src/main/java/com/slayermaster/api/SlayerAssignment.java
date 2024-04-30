package com.slayermaster.api;

import java.util.Optional;

public class SlayerAssignment
{
    private String slayerLevel;
    private String taskName;
    private String monsterName;
    private String[] locations;
    private String[] requiredItems;
    private String attribute;
    private String attackStyle;
    private String[] alternatives;
    private String[] slayerMasters;

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

    // Getters
    public String getSlayerLevel() { return slayerLevel; }

    public String getTaskName() { return taskName; }

    public String getMonsterName()
    {
        return monsterName;
    }

    public String[] getLocations()
    {
        return locations;
    }

    public String[] getRequiredItems()
    {
        return requiredItems;
    }

    public String getAttribute() { return attribute; }

    public String getAttackStyle()
    {
        return attackStyle;
    }

    public String[] getAlternatives()
    {
        return alternatives;
    }

    public String[] getSlayerMasters()
    {
        return slayerMasters;
    }
}

