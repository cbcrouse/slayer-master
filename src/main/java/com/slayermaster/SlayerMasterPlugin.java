package com.slayermaster;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@Slf4j
@PluginDescriptor(
	name = "Slayer Master",
	description = "Description",
	loadWhenOutdated = true
)
public class SlayerMasterPlugin extends Plugin
{
//	private static final Logger log = LoggerFactory.getLogger(SlayerMasterPlugin.class);

	private static final String ICON_PATH = "/images/slayer_icon.png"; // must be in "src/main/resources"

	private TestPanel panel;
	private NavigationButton navButton;

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SlayerMasterConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Slayer Master started!");
		panel = injector.getInstance(TestPanel.class);
		panel.init();

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), ICON_PATH);

		navButton = NavigationButton.builder()
			.tooltip("Slayer Master")
			.icon(icon)
			.priority(10)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Slayer Master stopped!");
		panel.deinit();
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Slayer Master says " + config.greeting(), null);
		}
	}

	@Provides
	SlayerMasterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerMasterConfig.class);
	}
}
