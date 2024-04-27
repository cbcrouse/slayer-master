package com.slayermaster;

import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconTextField extends JTextField
{
    private BufferedImage icon;
    private final int iconWidth = 15; // Adjust as needed based on the actual sprite size
    private final int iconHeight = 15; // Adjust as needed
    private final SpriteManager spriteManager;

    public IconTextField(int columns, SpriteManager spriteManager)
    {
        super(columns);
        this.spriteManager = spriteManager;
        loadIcon();
        setMargin(new Insets(2, 2, 2, iconWidth + 5)); // Right margin to prevent text overlap
    }

    private void loadIcon() {
        spriteManager.getSpriteAsync(SpriteID.GE_SEARCH, 0, sprite ->
        {
            icon = sprite;
            repaint(); // Repaint the text field once the icon is loaded
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (icon != null) {
            g.drawImage(icon, getWidth() - iconWidth - 5, (getHeight() - iconHeight) / 2, iconWidth, iconHeight, this);
        }
    }
}