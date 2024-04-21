package com.slayermaster;

import com.google.inject.Provides;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@PluginDescriptor(
	name = "Slayer Master"
)
public class SlayerMasterPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SlayerMasterPlugin.class);
	
	@Inject
	private Client client;

	@Inject
	private SlayerMasterConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Slayer Master started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Slayer Master stopped!");
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
