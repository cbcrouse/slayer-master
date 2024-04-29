package com.slayermaster.api;

import net.runelite.api.Skill;
import net.runelite.api.Client;
import net.runelite.api.EnumID;
import net.runelite.api.VarPlayer;
import net.runelite.api.ChatMessageType;

public class RuneLiteApi
{
    private final Client client;

    public RuneLiteApi(Client client)
    {
        this.client = client;
    }

    public int getCurrentSlayerLevel() {
        if (client != null) {
            return client.getRealSkillLevel(Skill.SLAYER);
        }
        return -1; // Return -1 or any other appropriate error indicator if the client is not available
    }

    public CurrentSlayerTask getCurrentSlayerTask()
    {
        int taskCreatureId = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
        int taskSize = client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
        int taskLocationId = client.getVarpValue(VarPlayer.SLAYER_TASK_LOCATION);

        if (taskCreatureId <= 0 || taskSize <= 0)
            return null;

        String taskCreatureName = getCreatureName(taskCreatureId);
        String taskLocation = getLocationName(taskLocationId);

        return new CurrentSlayerTask(taskSize, taskCreatureId, taskCreatureName, taskLocation);
    }

    public void printCurrentSlayerTask()
    {
        CurrentSlayerTask slayerTask = getCurrentSlayerTask();

        if (slayerTask != null){
            sendChatMessage("Current Slayer Task: " + slayerTask.getCreatureName() + " at " + slayerTask.getLocation() + " - Remaining: " + slayerTask.getTaskSize());
        } else
        {
            sendChatMessage("You do not currently have a Slayer task.");
        }
    }

    private void sendChatMessage(String message)
    {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    private String getCreatureName(int creatureId)
    {
        return client.getEnum(EnumID.SLAYER_TASK_CREATURE).getStringValue(creatureId);
    }

    private String getLocationName(int locationId)
    {
        if (locationId <= 0)
            return null;

        return client.getEnum(EnumID.SLAYER_TASK_LOCATION).getStringValue(locationId);
    }
}
