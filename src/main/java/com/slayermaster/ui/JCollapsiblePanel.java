package com.slayermaster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JCollapsiblePanel extends JPanel
{
    private final ImageIcon expandIcon, collapseIcon;
    private final JLabel titleLabel;
    private final JPanel contentPanel;
    private boolean isCollapsed = true;

    public JCollapsiblePanel(String title, ImageIcon expandIcon, ImageIcon collapseIcon)
    {
        super(new BorderLayout());

        this.expandIcon = expandIcon;
        this.collapseIcon = collapseIcon;
        titleLabel = new JLabel(title, collapseIcon, SwingConstants.LEFT);
        titleLabel.setHorizontalTextPosition(SwingConstants.TRAILING); // Text to the right of the icon
        titleLabel.setIconTextGap(10);  // Set gap between text and icon
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        titleLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                toggleVisibility();
            }
        });

        add(titleLabel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setVisible(false); // Initially hide the content panel
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setTitle(String title)
    {
        titleLabel.setText(title);
    }

    public void setContent(JPanel content)
    {
        contentPanel.removeAll();
        contentPanel.add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public JPanel getContent()
    {
        return contentPanel;
    }

    public void setTitleColor(Color color)
    {
        titleLabel.setForeground(color);
    }

    public void setTitleFont(Font font)
    {
        titleLabel.setFont(font);
    }

    private void toggleVisibility()
    {
        isCollapsed = !isCollapsed;
        contentPanel.setVisible(isCollapsed);
        titleLabel.setIcon(isCollapsed ? expandIcon : collapseIcon);
    }
}
