package com.slayermaster.ui;

import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.infrastructure.services.IRuneLiteApi;
import com.slayermaster.infrastructure.services.SlayerAssignment;
import com.slayermaster.data.ImageManager;
import com.slayermaster.data.WikiDataLoader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class MainPanel extends PluginPanel implements NavigationController, MonsterSelectionListener
{
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private MonsterDetailPanel monsterDetailPanel;

	public MainPanel(
		SpriteManager spriteManager,
		EventBus eventBus,
		IRuneLiteApi runeLiteApi,
		ImageManager imageManager,
		ImageCacheManager imageCacheManager,
		WikiDataLoader wikiDataLoader)
	{
		if (spriteManager == null || eventBus == null || runeLiteApi == null || imageManager == null || imageCacheManager == null || wikiDataLoader == null)
		{
			System.out.println("Initialization error");
			return;
		}

		setLayout(new BorderLayout(10, 10));
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		CurrentTaskPanel currentTaskPanel = new CurrentTaskPanel(eventBus, imageManager);
		add(currentTaskPanel, BorderLayout.NORTH); // Always visible at the top

		MonsterListPanel listPanel = new MonsterListPanel(spriteManager, this, runeLiteApi, eventBus, wikiDataLoader, imageManager, imageCacheManager);
		monsterDetailPanel = new MonsterDetailPanel(this, imageManager, imageCacheManager);
		cardPanel.add(listPanel, "List");
		cardPanel.add(monsterDetailPanel, "Details");
		add(cardPanel, BorderLayout.CENTER); // List and Details are switchable views
	}

	@Override
	public void showPanel(String panelName)
	{
		cardLayout.show(cardPanel, panelName);
		cardPanel.revalidate();
		cardPanel.repaint();
	}

	@Override
	public void onMonsterSelected(SlayerAssignment slayerAssignment)
	{
		monsterDetailPanel.setMonsterDetails(slayerAssignment);
		showPanel("Details");
		monsterDetailPanel.revalidate();
		monsterDetailPanel.repaint();
	}
}
