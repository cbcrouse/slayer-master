package com.slayermaster.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import com.slayermaster.infrastructure.services.SlayerAssignment;
import com.slayermaster.infrastructure.services.SlayerLocation;
import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.ImageManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonsterDetailPanel extends JPanel
{
	private final ImageCacheManager imageCacheManager;
	private final ImageManager imageManager;

	private JTextPane detailsTextPane;
	private JLabel detailsImageLabel;
	private JLabel detailsNameLabel;
	private SlayerAssignment currentMonster;

	public MonsterDetailPanel(NavigationController navigationController, ImageManager imageManager, ImageCacheManager imageCacheManager)
	{
		this.imageManager = imageManager;
		this.imageCacheManager = imageCacheManager;
		setLayout(new BorderLayout());
		initializeComponents(navigationController);
	}

	private void initializeComponents(NavigationController navigationController)
	{
		detailsImageLabel = new JLabel();
		detailsNameLabel = new JLabel();
		detailsTextPane = new JTextPane();
		detailsTextPane.setContentType("text/html");  // Set content type to HTML
		detailsTextPane.setEditable(false);
		detailsTextPane.setFont(new Font("SansSerif", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(detailsTextPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		JButton backButton = new JButton("Back");
		backButton.addActionListener(e -> navigationController.showPanel("List"));

		JPanel spacerPanel = new JPanel();
		spacerPanel.setPreferredSize(new Dimension(0, 20)); // Create a spacer panel 20 pixels high

		add(detailsImageLabel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(spacerPanel, BorderLayout.SOUTH);
		add(backButton, BorderLayout.PAGE_END);
	}

	public void setMonsterDetails(SlayerAssignment monster)
	{
		this.currentMonster = monster;
		updateDetails();
	}

	private void updateDetails()
	{
		// Assume getMonsterDetails returns HTML formatted details
		detailsTextPane.setText(getMonsterDetails(currentMonster));
		// Set the monster's image and name
		ImageIcon monsterImage = imageCacheManager.getCachedMonsterImage(currentMonster.getMonsterName());
		ImageIcon resizedImage = imageManager.resizeIcon(monsterImage, 100);
		detailsImageLabel.setIcon(resizedImage);
		detailsNameLabel.setText(currentMonster.getMonsterName());
	}

	private String getMonsterDetails(SlayerAssignment slayerAssignment)
	{
		// Set the monster's image and name
		ImageIcon monsterImage = imageCacheManager.getCachedMonsterImage(slayerAssignment.getMonsterName());
		ImageIcon resizedImage = imageManager.resizeIcon(monsterImage, 100);
		detailsImageLabel.setIcon(resizedImage);
		detailsImageLabel.setHorizontalAlignment(JLabel.CENTER);
		detailsNameLabel.setText(slayerAssignment.getMonsterName());
		detailsNameLabel.setHorizontalAlignment(JLabel.CENTER);

		// Build the details string with HTML for styling
		StringBuilder details = new StringBuilder("<html>");
		details.append("<div style='text-align: center;'><h1>").append(slayerAssignment.getMonsterName()).append("</h1><hr></div>");
		details.append("<div style='text-align: left; margin-left: 20px;'>");
		// Append required level
		details.append("<h2>Slayer level:</h2>");
		details.append("- ").append(slayerAssignment.getSlayerLevel()).append("<br>");

		// Append locations or slayer locations if available
		if (slayerAssignment.getSlayerLocations() != null && !slayerAssignment.getSlayerLocations().isEmpty())
		{
			details.append("<h2>Slayer Locations:</h2>");
			for (SlayerLocation slayerLocation : slayerAssignment.getSlayerLocations())
			{
				details.append("<h3>").append(slayerLocation.getLocationName()).append("</h3>");
				details.append("<ul>");
				details.append("<li><a href='").append(slayerLocation.getMapLink()).append("'>Map Link</a>").append("</li>");
				details.append("<li><b>Amount:</b> ").append(slayerLocation.getAmount()).append("</li>");
				details.append("<li><b>Multicombat:</b> ").append(slayerLocation.isMulticombat() ? "Yes" : "No").append("</li>");
				details.append("<li><b>Cannonable:</b> ").append(slayerLocation.isCannonable() ? "Yes" : "No").append("</li>");
				details.append("<li><b>Safespottable:</b> ").append(slayerLocation.isSafespottable() ? "Yes" : "No").append("</li>");
				details.append("<li><b>Notes:</b> ").append(slayerLocation.getNotes()).append("</li>");
				details.append("</ul>");
			}
		}
		else
		{
			details.append("<h2>Locations:</h2>");
			for (String location : slayerAssignment.getLocations())
			{
				details.append("- ").append(location).append("<br>");
			}
		}

		// Append alternatives if available
		if (slayerAssignment.getAlternatives().length > 0)
		{
			details.append("<h2>Alternatives:</h2>");
			for (String alternative : slayerAssignment.getAlternatives())
			{
				details.append("- ").append(alternative).append("<br>");
			}
		}

		// Append required items if not empty and not "N/A"
		if (slayerAssignment.getRequiredItems().length > 0 && !Arrays.asList(slayerAssignment.getRequiredItems()).contains("N/A"))
		{
			details.append("<h2>Required Items:</h2>")
				.append(String.join(", ", slayerAssignment.getRequiredItems())).append("<br>");
		}

		// Append attack style
		details.append("<h2>Attack Style:</h2>").append(slayerAssignment.getAttackStyle());

		// Append attribute
		details.append("<h2>Attribute:</h2>").append(slayerAssignment.getAttribute());

		// Append slayer masters
		details.append("<h2>Slayer Masters:</h2>");
		for (String master : slayerAssignment.getSlayerMasters())
		{
			details.append("- ").append(master).append("<br>");
		}

		details.append("</div></html>");

		return details.toString();
	}
}
