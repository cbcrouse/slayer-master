package com.slayermaster;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlayerMasterPanel extends PluginPanel
{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DefaultListModel<String> monsterListModel;
    private JList<String> monsterList;
    private JTextArea detailTextArea;
    private Map<String, Monster> monsterDetails;

    public SlayerMasterPanel()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        setLayout(new BorderLayout());
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
        JTextField searchField = new JTextField();
        listPanel.add(searchField, BorderLayout.NORTH);
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
    }

    private void initializeMonsterDetails()
    {
        monsterDetails.put("Monster1", new Monster("Monster1", List.of(new String[]{"Some Location"}), "Recommended Location", new String[]{"Item1", "Item2"}, "Best Gear", "Melee"));
        monsterDetails.put("Monster2", new Monster("Monster2", List.of(new String[]{"Another Location"}), "Recommended Location", new String[]{"Item3"}, "Alternative Gear", "Magic"));
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
