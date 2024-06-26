package com.slayermaster.ui;

import com.slayermaster.infrastructure.services.CurrentSlayerTask;
import com.slayermaster.data.ImageManager;
import com.slayermaster.app.events.SlayerTaskUpdatedEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class CurrentTaskPanel extends JPanel
{
	private final JCollapsiblePanel collapsiblePanel;

	public CurrentTaskPanel(EventBus eventBus, ImageManager imageManager)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical box layout
		setBackground(new Color(0, 0, 0, 120)); // Dark semi-transparent background

		// Load and create icons
		BufferedImage icon = imageManager.getWhiteCollapseIcon();
		ImageIcon collapseIcon = new ImageIcon(icon);
		ImageIcon expandIcon = imageManager.rotateIcon(collapseIcon, 180); // Rotate 180 degrees for expand icon

		collapsiblePanel = new JCollapsiblePanel("Current Slayer Task", expandIcon, collapseIcon);
		collapsiblePanel.setTitleColor(new Color(191, 144, 0)); // Set title color
		collapsiblePanel.setTitleFont(new Font("SansSerif", Font.BOLD, 14));

		JPanel detailsPanel = createDetailsPanel(null); // Create the details panel
		collapsiblePanel.setContent(detailsPanel); // Set the details panel as content

		add(collapsiblePanel);

		eventBus.register(this);
		updateCurrentSlayerTask(null);
	}

	@Subscribe
	public void onSlayerTaskUpdatedEvent(SlayerTaskUpdatedEvent event)
	{
		updateCurrentSlayerTask(event.getSlayerTask());
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
			JLabel taskMaster = new JLabel(formatTaskHtml("Slayer Master", currentTask.getSlayerMasterName()), SwingConstants.CENTER);

			styleLabel(taskCreature);
			styleLabel(taskCount);
			styleLabel(taskLocation);
			styleLabel(taskMaster);

			detailsPanel.add(taskCreature);
			detailsPanel.add(taskCount);
			detailsPanel.add(taskLocation);
			detailsPanel.add(taskMaster);
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

	public void updateCurrentSlayerTask(CurrentSlayerTask currentSlayerTask)
	{
		if (currentSlayerTask != null)
		{
			collapsiblePanel.setTitle("Current Slayer Task");
			JPanel detailsPanel = createDetailsPanel(currentSlayerTask);
			collapsiblePanel.setContent(detailsPanel);
		}
		else
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
