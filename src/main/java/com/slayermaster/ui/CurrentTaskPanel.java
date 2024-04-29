package com.slayermaster.ui;

import com.slayermaster.api.CurrentSlayerTask;
import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.data.ImageManager;
import com.slayermaster.events.SlayerTaskUpdatedEvent;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CurrentTaskPanel extends JPanel
{
    private final RuneLiteApi runeLiteApi;
    private JCollapsiblePanel collapsiblePanel;
    private ImageIcon collapseIcon, expandIcon;

    public CurrentTaskPanel(RuneLiteApi runeLiteApi, EventBus eventBus, ImageManager imageManager)
    {
        this.runeLiteApi = runeLiteApi;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical box layout
        setBackground(new Color(0, 0, 0, 120)); // Dark semi-transparent background

        // Load and create icons
        BufferedImage icon = imageManager.getWhiteCollapseIcon();
        collapseIcon = new ImageIcon(icon);
        expandIcon = imageManager.rotateIcon(collapseIcon, 180); // Rotate 180 degrees for expand icon

        collapsiblePanel = new JCollapsiblePanel("Current Slayer Task", expandIcon, collapseIcon);
        collapsiblePanel.setTitleColor(new Color(191, 144, 0)); // Set title color
        collapsiblePanel.setTitleFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel detailsPanel = createDetailsPanel(null); // Create the details panel
        collapsiblePanel.setContent(detailsPanel); // Set the details panel as content

        add(collapsiblePanel);

        eventBus.register(this);
        updateCurrentSlayerTask();
    }

    @Subscribe
    public void onSlayerTaskUpdatedEvent(SlayerTaskUpdatedEvent event)
    {
        updateCurrentSlayerTask();
    }

    private JPanel createDetailsPanel(CurrentSlayerTask currentTask)
    {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(3, 1));
        detailsPanel.setBackground(new Color(0, 0, 0, 60)); // Dark semi-transparent background

        if (currentTask != null)
        {
            JLabel taskCreature = new JLabel(formatTaskHtml("Creature", currentTask.getCreatureName()), SwingConstants.CENTER);
            JLabel taskCount = new JLabel(formatTaskHtml("Count", String.valueOf(currentTask.getTaskSize())), SwingConstants.CENTER);
            JLabel taskLocation = new JLabel(formatTaskHtml("Location", currentTask.getLocation()), SwingConstants.CENTER);

            styleLabel(taskCreature);
            styleLabel(taskCount);
            styleLabel(taskLocation);

            detailsPanel.add(taskCreature);
            detailsPanel.add(taskCount);
            detailsPanel.add(taskLocation);
        }

        return detailsPanel;
    }

    private void styleLabel(JLabel label)
    {
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.WHITE); // Regular color for other labels
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure alignment is centered
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    public void updateCurrentSlayerTask()
    {
        CurrentSlayerTask currentTask = runeLiteApi.getCurrentSlayerTask();
        if (currentTask != null)
        {
            collapsiblePanel.setTitle("Current Slayer Task");
            JPanel detailsPanel = createDetailsPanel(currentTask);
            collapsiblePanel.setContent(detailsPanel);
        } else
        {
            collapsiblePanel.setTitle("No current task");
        }
    }

    private String formatTaskHtml(String label, String value)
    {
        return "<html><div style='text-align: center; width: 100%;'>" +
                "<span style='color:#FFD700;'>" + label + ":</span>" +
                "<br/>" + value + "</div></html>";
    }
}
