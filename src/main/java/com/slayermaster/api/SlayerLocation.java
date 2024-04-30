package com.slayermaster.api;

public class SlayerLocation
{
    private String locationName;
    private String mapLink;
    private String amount;
    private boolean multicombat;
    private boolean cannonable;
    private boolean safespottable;
    private String notes;

    public SlayerLocation(String locationName, String mapLink, String amount, boolean multicombat, boolean cannonable, boolean safespottable, String notes)
    {
        this.locationName = locationName;
        this.mapLink = mapLink;
        this.amount = amount;
        this.multicombat = multicombat;
        this.cannonable = cannonable;
        this.safespottable = safespottable;
        this.notes = notes;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public String getMapLink()
    {
        return mapLink;
    }

    public void setMapLink(String mapLink)
    {
        this.mapLink = mapLink;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public boolean isMulticombat()
    {
        return multicombat;
    }

    public void setMulticombat(boolean multicombat)
    {
        this.multicombat = multicombat;
    }

    public boolean isCannonable()
    {
        return cannonable;
    }

    public void setCannonable(boolean cannonable)
    {
        this.cannonable = cannonable;
    }

    public boolean isSafespottable()
    {
        return safespottable;
    }

    public void setSafespottable(boolean safespottable)
    {
        this.safespottable = safespottable;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    @Override
    public String toString()
    {
        return "SlayerLocation{" +
                "locationName='" + locationName + '\'' +
                ", mapLink='" + mapLink + '\'' +
                ", amount=" + amount +
                ", multicombat=" + multicombat +
                ", cannonable=" + cannonable +
                ", safespottable=" + safespottable +
                ", notes='" + notes + '\'' +
                '}';
    }
}

