package com.slayermaster.ui;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MonsterListCellRenderer extends DefaultListCellRenderer
{
    private final Map<String, SlayerAssignment> assignmentDetails;
    private final ImageCacheManager imageCacheManager;
    private final ImageManager imageManager;
    private int currentSlayerLevel;
    private int hoveredIndex = -1;

    public MonsterListCellRenderer(Map<String, SlayerAssignment> assignmentDetails, int currentSlayerLevel)
    {
        this.assignmentDetails = assignmentDetails;
        this.imageCacheManager = new ImageCacheManager();
        this.imageManager = new ImageManager();
        this.currentSlayerLevel = currentSlayerLevel;
    }

    public void setHoveredIndex(int index)
    {
        this.hoveredIndex = index;
    }

    public void setCurrentSlayerLevel(int currentSlayerLevel)
    {
        this.currentSlayerLevel = currentSlayerLevel;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        JPanel panel = new JPanel(new BorderLayout());

        // Get the slayer level and pad it with spaces
        String monsterName = value.toString();
        SlayerAssignment assignment = assignmentDetails.get(monsterName);
        String slayerLevelString = assignment != null ? assignment.getSlayerLevel() : "0";
        JLabel slayerLevelLabel = new JLabel(slayerLevelString);
        slayerLevelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adjust padding as needed
        slayerLevelLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left-align the slayer level

        // Set preferred width for slayerLevelLabel
        slayerLevelLabel.setPreferredSize(new Dimension(40, slayerLevelLabel.getPreferredSize().height)); // Adjust width as needed

        // Set foreground color based on slayer level
        try
        {
            int requiredLevel = Integer.parseInt(slayerLevelString);
            if (currentSlayerLevel > 0 && requiredLevel > currentSlayerLevel)
            {
                slayerLevelLabel.setForeground(Color.RED);
            } else
            {
                slayerLevelLabel.setForeground(list.getForeground());
            }
        } catch (NumberFormatException e)
        {
            // Log or handle parsing error
            slayerLevelLabel.setForeground(Color.GRAY); // Set a default or error color
        }

        panel.add(slayerLevelLabel, BorderLayout.WEST);

        if (assignment != null)
        {
            JLabel nameLabel = new JLabel(assignment.getTaskName());
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10)); // Adjust padding as needed
            panel.add(nameLabel, BorderLayout.CENTER);
            panel.setToolTipText(monsterName);
            setupIcon(monsterName, panel);
        }

        if (isSelected)
        {
            panel.setBackground(list.getSelectionBackground());
        } else
        {
            panel.setBackground(index == hoveredIndex ? new Color(0x555555) : list.getBackground());
        }

        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return panel;
    }

    private void setupIcon(String monsterName, JPanel panel)
    {
        ImageIcon icon = imageCacheManager.getCachedMonsterImage(monsterName);
        ImageIcon resizedIcon = imageManager.resizeIcon(icon, 25);
        JLabel iconLabel = new JLabel(resizedIcon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        panel.add(iconLabel, BorderLayout.EAST);
    }
}
