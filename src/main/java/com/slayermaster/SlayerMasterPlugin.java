/*
 * Copyright (c) 2024, Casey Crouse <https://github.com/cbcrouse>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.slayermaster;

import com.google.inject.Inject;
import com.google.inject.Provides;

import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.WikiDataLoader;
import com.slayermaster.infrastructure.services.OSRSWikiScraper;
import com.slayermaster.ui.MainPanel;
import java.awt.image.BufferedImage;

import com.slayermaster.infrastructure.services.IRuneLiteApi;
import com.slayermaster.infrastructure.services.RuneLiteApi;
import com.slayermaster.data.ImageManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;

@Slf4j
@PluginDescriptor(
	name = "Slayer Master",
	description = "Description",
	loadWhenOutdated = true
)
public class SlayerMasterPlugin extends Plugin
{
	private MainPanel mainPanel;
	private NavigationButton navButton;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SlayerMasterConfig config;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private IRuneLiteApi runeLiteApi;

	@Inject
	private ImageManager imageManager;

	@Inject
	private ImageCacheManager imageCacheManager;

	@Inject
	private WikiDataLoader wikiDataLoader;

	@Override
	protected void startUp()
	{
		mainPanel = new MainPanel(spriteManager, eventBus, runeLiteApi, imageManager, imageCacheManager, wikiDataLoader);

		spriteManager.getSpriteAsync(SpriteID.SKILL_SLAYER, 0, sprite ->
		{
			final BufferedImage icon = ImageUtil.resizeImage(sprite, 25, 25);
			navButton = NavigationButton.builder()
				.tooltip("Slayer Master")
				.icon(icon)
				.priority(5)
				.panel(mainPanel)
				.build();

			clientToolbar.addNavigation(navButton);
		});

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		mainPanel = null;
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

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = event.getMessage();
			if (message.contains("You're assigned to kill"))
			{
				runeLiteApi.updateCurrentSlayerTask();
			}
		}
	}

	@Provides
	public SlayerMasterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerMasterConfig.class);
	}

	@Provides
	public ImageManager provideImageManager(OSRSWikiScraper wikiScraper)
	{
		return new ImageManager(wikiScraper);
	}

	@Provides
	public IRuneLiteApi provideIRuneLiteApi(Client client, ClientThread clientThread, EventBus eventBus)
	{
		return new RuneLiteApi(client, clientThread, eventBus);
	}

	@Provides
	public OSRSWikiScraper provideOSRSWikiScraper()
	{
		return new OSRSWikiScraper();
	}

	@Provides
	public WikiDataLoader provideWikiDataLoader(OSRSWikiScraper wikiScraper)
	{
		return new WikiDataLoader(wikiScraper);
	}
}
