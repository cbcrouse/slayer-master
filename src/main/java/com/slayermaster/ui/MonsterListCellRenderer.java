package com.slayermaster.ui;

import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.MonsterImageManager;
import com.slayermaster.data.Monster;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MonsterListCellRenderer extends DefaultListCellRenderer
{
    private final Map<String, Monster> monsterDetails;
    private final ImageCacheManager imageCacheManager;
    private final MonsterImageManager imageManager;
    private int hoveredIndex = -1;

    public MonsterListCellRenderer(Map<String, Monster> monsterDetails)
    {
        this.monsterDetails = monsterDetails;
        this.imageCacheManager = new ImageCacheManager();
        this.imageManager = new MonsterImageManager();
    }

    public void setHoveredIndex(int index)
    {
        this.hoveredIndex = index;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(value.toString());
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.CENTER);
        panel.setToolTipText(value.toString());
        setupIcon(value.toString(), panel);

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(index == hoveredIndex ? new Color(0x555555) : list.getBackground());
            label.setForeground(list.getForeground());
        }

        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return panel;
    }

    private void setupIcon(String monsterName, JPanel panel)
    {
        Monster monster = monsterDetails.get(monsterName);
        if (monster != null)
        {
            ImageIcon icon = imageCacheManager.getCachedMonsterImage(monsterName);
            ImageIcon resizedIcon = imageManager.resizeIcon(icon, 25);
            JLabel iconLabel = new JLabel(resizedIcon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            panel.add(iconLabel, BorderLayout.EAST);
        }
    }
}
