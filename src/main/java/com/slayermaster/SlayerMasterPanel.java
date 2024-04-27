package com.slayermaster;

import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;

public class SlayerMasterPanel extends PluginPanel
{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DefaultListModel<String> monsterListModel;
    private JList<String> monsterList;
    private JTextArea detailTextArea;
    private Map<String, Monster> monsterDetails;

    private SpriteManager spriteManager;

    public SlayerMasterPanel(SpriteManager spriteManager)
    {
        if (spriteManager == null) {
            System.out.println("SpriteManager not initialized");
            return; // or handle the case more gracefully
        }

        this.spriteManager = spriteManager;
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Use layout managers effectively to space elements
        setLayout(new BorderLayout(10, 10)); // Adds 10 pixels of spacing both horizontally and vertically between components

        add(cardPanel, BorderLayout.CENTER);

        monsterDetails = new HashMap<>();
        initializeMonsterDetails();

        // Monster list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        monsterListModel = new DefaultListModel<>();
        monsterList = new JList<>(monsterListModel);
        monsterDetails.forEach((name, details) -> monsterListModel.addElement(name));
        JScrollPane scrollPane = new JScrollPane(monsterList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Search field
        JTextField searchField = getSearchField();
        listPanel.add(searchField, BorderLayout.NORTH);

        // Search field search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText().trim().toLowerCase();
                DefaultListModel<String> filteredModel = new DefaultListModel<>();
                monsterDetails.keySet().stream()
                        .filter(name -> name.toLowerCase().contains(text))
                        .forEach(filteredModel::addElement);
                monsterList.setModel(filteredModel);
            }
        });

        // Detail panel
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailTextArea = new JTextArea();
        detailTextArea.setEditable(false);
        detailPanel.add(new JScrollPane(detailTextArea), BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "List"));
        detailPanel.add(backButton, BorderLayout.SOUTH);

        // Add panels to card layout
        cardPanel.add(listPanel, "List");
        cardPanel.add(detailPanel, "Details");

        // Selection listener to show details
        monsterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedMonster = monsterList.getSelectedValue();
                Monster details = monsterDetails.get(selectedMonster);
                detailTextArea.setText(getMonsterDetails(details));
                cardLayout.show(cardPanel, "Details");
            }
        });

        // Set borders around the monster list for spacing
        monsterList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JTextField getSearchField()
    {
        // JTextField searchField = new JTextField();
        IconTextField searchField = new IconTextField(20, spriteManager); // Assuming spriteManager is available

        // Customize search field size and font
        searchField.setPreferredSize(new Dimension(200, 30)); // Set preferred height to 30
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Adjust font size as needed

        // Set margins around the search field for spacing
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // addSearchIcon(searchField);

        return searchField;
    }

    public void addSearchIcon(JTextField searchField)
    {
        // Load the sprite asynchronously
        spriteManager.getSpriteAsync(SpriteID.GE_SEARCH, 0, sprite -> {
            if (sprite != null) {
                ImageIcon icon = new ImageIcon(sprite);
                JLabel label = new JLabel(icon);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Right padding to prevent overlap
                searchField.setLayout(new BorderLayout());
                searchField.add(label, BorderLayout.EAST);
                searchField.setBorder(BorderFactory.createCompoundBorder(
                        searchField.getBorder(),
                        BorderFactory.createEmptyBorder(0, 5, 0, 0) // Add padding inside the field for text
                ));
            } else {
                System.out.println("Sprite could not be loaded");
            }
        });
    }

    private void initializeMonsterDetails()
    {
        MonsterDataLoader dataLoader = new MonsterDataLoader();
        // Load the monster data using the data loader
        var monsters = dataLoader.loadMonsterData();  // Assuming this returns some collection of Monster objects

        // Iterate over each monster and add it to the monsterDetails map
        for (var monster : monsters) {
            monsterDetails.put(monster.getName(), monster);
        }
//        monsterDetails.put("Monster1", new Monster("Monster1", List.of(new String[]{"Some Location"}), "Recommended Location", new String[]{"Item1", "Item2"}, "Best Gear", "Melee"));
//        monsterDetails.put("Monster2", new Monster("Monster2", List.of(new String[]{"Another Location"}), "Recommended Location", new String[]{"Item3"}, "Alternative Gear", "Magic"));
    }

    private String getMonsterDetails(Monster monster) {
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(monster.getName()).append("\n");
        details.append("Locations:\n");
        for (String location : monster.getLocations()) {
            if (location.equals(monster.getRecommendedLocation())) {
                details.append("* Recommended: ").append(location).append("\n");
            } else {
                details.append("- ").append(location).append("\n");
            }
        }
        details.append("Required Items: ").append(String.join(", ", monster.getRequiredItems())).append("\n");
        details.append("Recommended Gear: ").append(monster.getRecommendedGear()).append("\n");
        details.append("Attack Style: ").append(monster.getAttackStyle()).append("\n");
        return details.toString();
    }
}
