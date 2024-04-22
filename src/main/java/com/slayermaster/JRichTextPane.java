package com.slayermaster;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JRichTextPane extends JEditorPane
{
	private HyperlinkListener linkHandler;

	public JRichTextPane()
	{
		super();
		setHighlighter(null);
		setEditable(false);
		setOpaque(false);
		enableAutoLinkHandler(true);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		HTMLEditorKit ek = (HTMLEditorKit) getEditorKitForContentType("text/html");
		ek.getStyleSheet().addRule("a {color: #DDDDDD }");
	}

	public JRichTextPane(String type, String text)
	{
		this();
		setContentType(type);
		setText(text);
	}

	public void enableAutoLinkHandler(boolean enable)
	{
		if (enable == (linkHandler == null))
		{
			if (enable)
			{
				linkHandler = e ->
				{
					if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()) && e.getURL() != null)
					{
						if (Desktop.isDesktopSupported())
						{
							try
							{
								Desktop.getDesktop().browse(e.getURL().toURI());
							}
							catch (URISyntaxException | IOException ex)
							{
								log.warn("Error opening link", ex);
							}
						}
					}
				};
				addHyperlinkListener(linkHandler);
			}
			else
			{
				removeHyperlinkListener(linkHandler);
				linkHandler = null;
			}
		}
	}

	public boolean getAutoLinkHandlerEnabled()
	{
		return linkHandler != null;
	}
}