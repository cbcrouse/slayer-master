package com.slayermaster.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.MonsterImageManager;

public class MonsterDetailPanel extends JPanel
{
    private final ImageCacheManager imageCacheManager = new ImageCacheManager();
    private final MonsterImageManager imageManager = new MonsterImageManager();

    private JTextPane detailsTextPane;
    private JLabel detailsImageLabel;
    private JLabel detailsNameLabel;
    private SlayerAssignment currentMonster;

    public MonsterDetailPanel(NavigationController navigationController)
    {
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

    private String getMonsterDetails(SlayerAssignment monster)
    {
        // Set the monster's image and name
        ImageIcon monsterImage = imageCacheManager.getCachedMonsterImage(monster.getMonsterName());
        ImageIcon resizedImage = imageManager.resizeIcon(monsterImage, 100);
        detailsImageLabel.setIcon(resizedImage);
        detailsImageLabel.setHorizontalAlignment(JLabel.CENTER);
        detailsNameLabel.setText(monster.getMonsterName());
        detailsNameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Build the details string with HTML for styling
        StringBuilder details = new StringBuilder("<html>");
        details.append("<div style='text-align: center;'><h1>").append(monster.getMonsterName()).append("</h1><hr></div>");

        // Append locations
        details.append("<div style='text-align: left; margin-left: 20px;'>")
                .append("<h2>Locations:</h2>");
        for (String location : monster.getLocations())
        {
            details.append("- ").append(location).append("<br>");
        }

        // Append alternatives if available
        if (monster.getAlternatives().length > 0)
        {
            details.append("<h2>Alternatives:</h2>");
            for (String alternative : monster.getAlternatives())
            {
                details.append("- ").append(alternative).append("<br>");
            }
        }

        // Append required items if not empty and not "N/A"
        if (monster.getRequiredItems().length > 0 && !Arrays.asList(monster.getRequiredItems()).contains("N/A"))
        {
            details.append("<h2>Required Items:</h2>")
                    .append(String.join(", ", monster.getRequiredItems())).append("<br>");
        }

        // Append attack style
        details.append("<h2>Attack Style:</h2>").append(monster.getAttackStyle())
                .append("</div></html>");

        return details.toString();
    }
}
