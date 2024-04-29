package com.slayermaster.ui;

import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.MonsterImageManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MonsterListCellRenderer extends DefaultListCellRenderer
{
    private final Map<String, SlayerAssignment> assignmentDetails;
    private final ImageCacheManager imageCacheManager;
    private final MonsterImageManager imageManager;
    private int currentSlayerLevel;
    private int hoveredIndex = -1;

    public MonsterListCellRenderer(Map<String, SlayerAssignment> assignmentDetails, int currentSlayerLevel)
    {
        this.assignmentDetails = assignmentDetails;
        this.imageCacheManager = new ImageCacheManager();
        this.imageManager = new MonsterImageManager();
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
        JLabel label = new JLabel(value.toString());
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set foreground color based on slayer level
        try
        {
            SlayerAssignment assignment = assignmentDetails.get(value.toString());
            int requiredLevel = Integer.parseInt(assignment.getSlayerLevel());
            if (currentSlayerLevel > 0 && requiredLevel > currentSlayerLevel)
            {
                label.setForeground(Color.RED);
            } else
            {
                label.setForeground(list.getForeground());
            }
        } catch (NumberFormatException e)
        {
            // Log or handle parsing error
            label.setForeground(Color.GRAY); // Set a default or error color
        }

        panel.add(label, BorderLayout.CENTER);
        panel.setToolTipText(value.toString());
        setupIcon(value.toString(), panel);

        if (isSelected)
        {
            panel.setBackground(list.getSelectionBackground());
            // label.setForeground(list.getSelectionForeground());
        } else
        {
            panel.setBackground(index == hoveredIndex ? new Color(0x555555) : list.getBackground());
            // label.setForeground(list.getForeground());
        }

        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return panel;
    }

    private void setupIcon(String monsterName, JPanel panel)
    {
        SlayerAssignment slayerAssignment = assignmentDetails.get(monsterName);
        if (slayerAssignment != null)
        {
            ImageIcon icon = imageCacheManager.getCachedMonsterImage(monsterName);
            ImageIcon resizedIcon = imageManager.resizeIcon(icon, 25);
            JLabel iconLabel = new JLabel(resizedIcon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            panel.add(iconLabel, BorderLayout.EAST);
        }
    }
}
