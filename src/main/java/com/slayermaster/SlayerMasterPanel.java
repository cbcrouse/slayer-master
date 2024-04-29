package com.slayermaster;

import com.slayermaster.api.OSRSWikiScraper;
import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.data.ImageManager;
import com.slayermaster.data.WikiDataLoader;
import com.slayermaster.ui.*;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SlayerMasterPanel extends PluginPanel implements NavigationController, MonsterSelectionListener
{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<String, SlayerAssignment> monsterDetails;

    private final WikiDataLoader wikiDataLoader = new WikiDataLoader(new OSRSWikiScraper());

    private CurrentTaskPanel currentTaskPanel;
    private MonsterListPanel listPanel;
    private MonsterDetailPanel detailPanel;

    public SlayerMasterPanel(SpriteManager spriteManager, EventBus eventBus, RuneLiteApi runeLiteApi, ImageManager imageManager)
    {
        if (spriteManager == null || runeLiteApi == null || eventBus == null)
        {
            System.out.println("Initialization error");
            return;
        }

        setLayout(new BorderLayout(10, 10));
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        monsterDetails = new HashMap<>();
        initializeMonsterDetails();

        currentTaskPanel = new CurrentTaskPanel(runeLiteApi, eventBus, imageManager);
        add(currentTaskPanel, BorderLayout.NORTH); // Always visible at the top

        listPanel = new MonsterListPanel(monsterDetails, spriteManager, this, runeLiteApi, eventBus);
        detailPanel = new MonsterDetailPanel(this);
        cardPanel.add(listPanel, "List");
        cardPanel.add(detailPanel, "Details");
        add(cardPanel, BorderLayout.CENTER); // List and Details are switchable views
    }

    private void initializeMonsterDetails()
    {
        List<SlayerAssignment> wikiMonsters = wikiDataLoader.getWikiSlayerMonsters();

        // Sort the wikiMonsters list alphabetically by monster name
        wikiMonsters.sort(Comparator.comparing(SlayerAssignment::getMonsterName));

        // Use a TreeMap to maintain the order
        Map<String, SlayerAssignment> sortedMonsterDetails = new TreeMap<>();

        // Iterate over each sorted monster and add it to the sortedMonsterDetails map
        for (var monster : wikiMonsters)
        {
            sortedMonsterDetails.put(monster.getMonsterName(), monster);
        }

        // Now monsterDetails will have all monsters in alphabetical order
        monsterDetails = sortedMonsterDetails;
    }

    @Override
    public void showPanel(String panelName)
    {
        cardLayout.show(cardPanel, panelName);
    }

    @Override
    public void onMonsterSelected(SlayerAssignment slayerAssignment)
    {
        detailPanel.setMonsterDetails(slayerAssignment);
        showPanel("Details");
    }
}
