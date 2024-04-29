package com.slayermaster.events;

import com.slayermaster.api.CurrentSlayerTask;

public class SlayerTaskUpdatedEvent
{
    private final CurrentSlayerTask slayerTask;

    public SlayerTaskUpdatedEvent(CurrentSlayerTask slayerTask)
    {
        this.slayerTask = slayerTask;
    }

    public CurrentSlayerTask getSlayerTask()
    {
        return slayerTask;
    }
}
