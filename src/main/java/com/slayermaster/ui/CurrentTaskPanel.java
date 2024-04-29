package com.slayermaster.ui;

import com.slayermaster.api.CurrentSlayerTask;
import com.slayermaster.api.RuneLiteApi;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;

public class CurrentTaskPanel extends JPanel
{
    private final RuneLiteApi runeLiteApi;
    private final JLabel currentTaskLabel;

    public CurrentTaskPanel(RuneLiteApi runeLiteApi, EventBus eventBus)
    {
        this.runeLiteApi = runeLiteApi;
        setLayout(new BorderLayout());
        currentTaskLabel = new JLabel("Retrieving current task...", SwingConstants.CENTER);
        currentTaskLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(currentTaskLabel, BorderLayout.CENTER);
        updateCurrentSlayerTask();

        // Register panel to listen to events
        eventBus.register(this);
        updateCurrentSlayerTask();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage();
            if (message.contains("You're assigned to kill")) {
                updateCurrentSlayerTask();
            }
        }
    }

    public void updateCurrentSlayerTask()
    {
        CurrentSlayerTask currentTask = runeLiteApi.getCurrentSlayerTask();
        if (currentTask != null)
        {
            currentTaskLabel.setText("Current Task: " + currentTask.getCreatureName() + " - " + currentTask.getTaskSize() + " left at " + currentTask.getLocation());
        } else
        {
            currentTaskLabel.setText("No current task");
        }
    }
}
