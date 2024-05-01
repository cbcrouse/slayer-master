package com.slayermaster.api;

import com.slayermaster.events.SlayerTaskUpdatedEvent;
import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.NPCManager;

public class RuneLiteApi
{
    private final Client client;
    private final ClientThread clientThread;
    private final EventBus eventBus;

    public RuneLiteApi(Client client, ClientThread clientThread, EventBus eventBus)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.eventBus = eventBus;
    }

    public int getCurrentSlayerLevel()
    {
        if (client != null)
        {
            return client.getRealSkillLevel(Skill.SLAYER);
        }
        return -1; // Return -1 or any other appropriate error indicator if the client is not available
    }

    public void updateCurrentSlayerTask()
    {
        clientThread.invoke(() ->
        {
            if (client.getGameState() == GameState.LOGGED_IN)
            {
                int taskCreatureId = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
                int taskSize = client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
                int taskLocationId = client.getVarpValue(VarPlayer.SLAYER_TASK_LOCATION);
                int taskMasterId = client.getVarbitValue(4067); // https://oldschool.runescape.wiki/w/RuneScape:Varbit/4067

                // Trying to get the slayer master NPC name. Not sure if worth it.

                if (taskCreatureId <= 0 || taskSize <= 0)
                    return false;

                String taskCreatureName = getCreatureName(taskCreatureId);
                String taskLocation = getLocationName(taskLocationId);
                String taskMasterName = getSlayerMasterName(taskMasterId);

                CurrentSlayerTask task = new CurrentSlayerTask(taskSize, taskCreatureId, taskCreatureName, taskLocation, taskMasterName);
                System.out.println("Current Slayer Task: " + task.getCreatureName() + " - " + task.getTaskSize());
                eventBus.post(new SlayerTaskUpdatedEvent(task));
                return true;
            }

            System.out.println("Cannot update slayer task - not logged in.");
            return false;
        });
    }

    private void sendChatMessage(String message)
    {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    private String getSlayerMasterName(int taskMasterId)
    {
        switch (taskMasterId)
        {
            case 0:
                return "No task has been assigned.";
            case 1:
                return "Turael / Spria";
            case 2:
                return "Mazchna";
            case 3:
                return "Vannaka";
            case 4:
                return "Chaeldar";
            case 5:
                return "Duradel";
            case 6:
                return "Nieve / Steve";
            case 7:
                return "Krystilia";
            case 8:
                return "Konar quo Maten";
            default:
                return "Unknown";
        }
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
