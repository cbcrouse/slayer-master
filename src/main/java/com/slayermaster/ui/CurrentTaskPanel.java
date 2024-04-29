package com.slayermaster.ui;

import com.slayermaster.api.CurrentSlayerTask;
import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.events.SlayerTaskUpdatedEvent;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;

public class CurrentTaskPanel extends JPanel
{
    private final RuneLiteApi runeLiteApi;
    private JLabel taskLabel, taskCreature, taskCount, taskLocation;

    public CurrentTaskPanel(RuneLiteApi runeLiteApi, EventBus eventBus)
    {
        this.runeLiteApi = runeLiteApi;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical box layout
        setBackground(new Color(0, 0, 0, 120)); // Dark semi-transparent background

        taskLabel = new JLabel("Current Slayer Task", SwingConstants.CENTER);
        taskCreature = new JLabel("", SwingConstants.CENTER);
        taskCount = new JLabel("", SwingConstants.CENTER);
        taskLocation = new JLabel("", SwingConstants.CENTER);

        styleLabel(taskLabel, true);
        styleLabel(taskCreature, false);
        styleLabel(taskCount, false);
        styleLabel(taskLocation, false);

        add(taskLabel);
        add(new JSeparator(SwingConstants.HORIZONTAL)); // White divider
        add(taskCreature);
        add(taskCount);
        add(taskLocation);

        // Register panel to listen to events
        eventBus.register(this);
        updateCurrentSlayerTask();
    }

    @Subscribe
    public void onSlayerTaskUpdatedEvent(SlayerTaskUpdatedEvent event)
    {
        updateCurrentSlayerTask();
    }

    private void styleLabel(JLabel label, boolean isTitle)
    {
        label.setFont(new Font("SansSerif", isTitle ? Font.BOLD : Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setForeground(isTitle ? new Color(191, 144, 0) : Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for the label
    }

    public void updateCurrentSlayerTask()
    {
        CurrentSlayerTask currentTask = runeLiteApi.getCurrentSlayerTask();
        if (currentTask != null)
        {
            taskCreature.setText(formatTaskHtml("Creature", currentTask.getCreatureName()));
            taskCount.setText(formatTaskHtml("Count", String.valueOf(currentTask.getTaskSize())));
            taskLocation.setText(formatTaskHtml("Location", currentTask.getLocation()));
        } else
        {
            taskCreature.setText("<html><div style='text-align: center; width: 100%; color: white;'>No current task</div></html>");
            taskCount.setText("");
            taskLocation.setText("");
        }
    }

    private String formatTaskHtml(String label, String value)
    {
        return "<html><div style='text-align: center; width: 100%;'>" +
                "<span style='color:#FFD700;'>" + label + ":</span>" +
                "<br/>" + value + "</div></html>";
    }

}
