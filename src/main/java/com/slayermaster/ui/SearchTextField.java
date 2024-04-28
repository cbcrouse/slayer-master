package com.slayermaster.ui;

import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class SearchTextField extends JTextField
{
    private BufferedImage icon;
    private final int iconWidth = 15; // Adjust as needed based on the actual sprite size
    private final Consumer<String> onFilterChange;
    private final SpriteManager spriteManager;

    public SearchTextField(int columns, SpriteManager spriteManager, Consumer<String> onFilterChange)
    {
        super(columns);
        this.spriteManager = spriteManager;
        this.onFilterChange = onFilterChange;
        loadIcon();
        setupAppearance();
        setupDocumentListener();
        setMargin(new Insets(2, 2, 2, iconWidth + 5)); // Right margin to prevent text overlap
    }

    private void loadIcon()
    {
        spriteManager.getSpriteAsync(SpriteID.GE_SEARCH, 0, sprite ->
        {
            icon = ImageUtil.bufferedImageFromImage(sprite);
            repaint(); // Repaint the text field once the icon is loaded
        });
    }

    protected void setupAppearance()
    {
        setPreferredSize(new Dimension(200, 30)); // Set preferred height to 30
        setFont(new Font("SansSerif", Font.PLAIN, 14)); // Adjust font size as needed
        setBorder(BorderFactory.createCompoundBorder(
                getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, iconWidth + 5))); // Right margin to prevent text overlap
    }

    private void setupDocumentListener()
    {
        getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                if (onFilterChange != null) onFilterChange.accept(getText().trim().toLowerCase());
            }

            public void removeUpdate(DocumentEvent e)
            {
                if (onFilterChange != null) onFilterChange.accept(getText().trim().toLowerCase());
            }

            public void changedUpdate(DocumentEvent e)
            {
                if (onFilterChange != null) onFilterChange.accept(getText().trim().toLowerCase());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (icon != null)
        {
            // Adjust as needed
            int iconHeight = 15;
            g.drawImage(icon, getWidth() - iconWidth - 5, (getHeight() - iconHeight) / 2, iconWidth, iconHeight, this);
        }
    }
}