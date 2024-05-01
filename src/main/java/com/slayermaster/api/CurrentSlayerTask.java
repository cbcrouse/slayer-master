package com.slayermaster.api;

public class CurrentSlayerTask
{
    private final int quantity;
    private final int creatureId;
    private final String creatureName;
    private final String location;
    private final String slayerMasterName;

    public CurrentSlayerTask(int quantity, int creatureId, String creatureName, String location, String slayerMasterName)
    {
        this.quantity = quantity;
        this.creatureId = creatureId;
        this.creatureName = creatureName;
        this.location = location;
        this.slayerMasterName = slayerMasterName;
    }

    public int getTaskSize()
    {
        return quantity;
    }
    public int getCreatureId()
    {
        return creatureId;
    }
    public String getCreatureName()
    {
        return creatureName;
    }
    public String getLocation()
    {
        return location;
    }
    public String getSlayerMasterName() { return slayerMasterName; }
}
