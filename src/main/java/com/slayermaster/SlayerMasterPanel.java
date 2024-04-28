package com.slayermaster;

import com.slayermaster.api.OSRSWikiScraper;
import com.slayermaster.data.Monster;
import com.slayermaster.data.WikiDataLoader;
import com.slayermaster.ui.MonsterListPanel;
import com.slayermaster.ui.MonsterDetailPanel;
import com.slayermaster.ui.MonsterSelectionListener;
import com.slayermaster.ui.NavigationController;
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
    private Map<String, Monster> monsterDetails;

    private final WikiDataLoader wikiDataLoader = new WikiDataLoader(new OSRSWikiScraper());

    private MonsterDetailPanel detailPanel;

    public SlayerMasterPanel(SpriteManager spriteManager)
    {
        if (spriteManager == null)
        {
            System.out.println("SpriteManager not initialized");
            return; // or handle the case more gracefully
        }

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Use layout managers effectively to space elements
        setLayout(new BorderLayout(10, 10)); // Adds 10 pixels of spacing both horizontally and vertically between components

        add(cardPanel, BorderLayout.CENTER);

        monsterDetails = new HashMap<>();
        initializeMonsterDetails();

        // Setting up the list and detail panels
        JPanel listPanel = new MonsterListPanel(monsterDetails, spriteManager, this);
        cardPanel.add(listPanel, "List");
        detailPanel = new MonsterDetailPanel(this); // Assume constructors are correctly defined
        cardPanel.add(detailPanel, "Details"); // Correctly add to cardPanel with a reference name
    }

    private void initializeMonsterDetails()
    {
        List<Monster> wikiMonsters = wikiDataLoader.getWikiSlayerMonsters();

        // Sort the wikiMonsters list alphabetically by monster name
        wikiMonsters.sort(Comparator.comparing(Monster::getName));

        // Use a TreeMap to maintain the order
        Map<String, Monster> sortedMonsterDetails = new TreeMap<>();

        // Iterate over each sorted monster and add it to the sortedMonsterDetails map
        for (var monster : wikiMonsters)
        {
            sortedMonsterDetails.put(monster.getName(), monster);
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
    public void onMonsterSelected(Monster monster)
    {
        detailPanel.setMonsterDetails(monster);
        showPanel("Details");
    }
}
