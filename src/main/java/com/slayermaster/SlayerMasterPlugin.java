package com.slayermaster;

import com.google.inject.Provides;

import java.awt.image.BufferedImage;
import javax.inject.Inject;

import com.slayermaster.api.CurrentSlayerTask;
import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.data.ImageManager;
import com.slayermaster.events.SlayerTaskUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
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
    private SlayerMasterPanel panel;
    private NavigationButton navButton;
    private RuneLiteApi runeLiteApi;
    private final ImageManager imageManager = new ImageManager();

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private SlayerMasterConfig config;

    @Inject
    private SpriteManager spriteManager;

    @Override
    protected void startUp() throws Exception
    {
        runeLiteApi = new RuneLiteApi(client);
        panel = new SlayerMasterPanel(spriteManager, eventBus, runeLiteApi, imageManager);

        spriteManager.getSpriteAsync(SpriteID.SKILL_SLAYER, 0, sprite ->
        {
            final BufferedImage icon = ImageUtil.resizeImage(sprite, 25, 25);
            navButton = NavigationButton.builder()
                    .tooltip("Slayer Master")
                    .icon(icon)
                    .priority(5)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);
        });

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception
    {
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

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.GAMEMESSAGE)
        {
            String message = event.getMessage();
            if (message.contains("You're assigned to kill"))
            {
                updateSlayerTask();
            }
        }
    }

    private void updateSlayerTask()
    {
        CurrentSlayerTask task = runeLiteApi.getCurrentSlayerTask();
        if (task != null)
        {
            // Publishing the event when the slayer task is updated
            eventBus.post(new SlayerTaskUpdatedEvent(task));
            System.out.println("Current Slayer Task: " + task.getCreatureName() + " - " + task.getTaskSize());
        } else
        {
            System.out.println("No current slayer task.");
        }
    }

    @Provides
    SlayerMasterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SlayerMasterConfig.class);
    }
}
