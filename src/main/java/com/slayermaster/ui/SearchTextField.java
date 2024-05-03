package com.slayermaster.ui;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

@Slf4j
public class SearchTextField extends JTextField
{
	private BufferedImage icon;
	private final int iconWidth = 15; // Adjust as needed based on the actual sprite size
	private final Consumer<String> onFilterChange;
	private final SpriteManager spriteManager;
	private final String placeholderText;

	public SearchTextField(int columns, SpriteManager spriteManager, Consumer<String> onFilterChange, String placeholderText)
	{
		super(columns);
		this.spriteManager = spriteManager;
		this.onFilterChange = onFilterChange;
		this.placeholderText = placeholderText;
		loadIcon();
		setupAppearance();
		setupDocumentListener();
		setupFocusListener();
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
		setMargin(new Insets(2, 2, 2, iconWidth + 5)); // Right margin to prevent text overlap
		setBorder(BorderFactory.createCompoundBorder(
			getBorder(),
			BorderFactory.createEmptyBorder(0, 5, 0, iconWidth + 5))); // Right margin to prevent text overlap
		setForeground(Color.GRAY); // Set placeholder text color
		setText(placeholderText); // Set placeholder text
	}

	private void setupFocusListener()
	{
		addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (getText().equals(placeholderText))
				{
					setText("");
					setForeground(Color.WHITE); // Set text color to white when typing
				}
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (getText().isEmpty() || getText().equals(placeholderText))
				{
					setText(placeholderText);
					setForeground(Color.GRAY); // Set text color to gray for placeholder text
				}
				else
				{
					setForeground(Color.WHITE); // Set text color to white when typing
				}
			}
		});
	}

	private void setupDocumentListener()
	{
		getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				handleFilterChange();
			}

			public void removeUpdate(DocumentEvent e)
			{
				handleFilterChange();
			}

			public void changedUpdate(DocumentEvent e)
			{
				handleFilterChange();
			}
		});
	}

	private void handleFilterChange()
	{
		if (onFilterChange != null)
		{
			String text = getText().trim();
			if (text.isEmpty() || text.equals(placeholderText))
			{
				onFilterChange.accept(null); // Pass null when the search text is empty or the placeholder text
			}
			else
			{
				onFilterChange.accept(text.toLowerCase());
			}
		}
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