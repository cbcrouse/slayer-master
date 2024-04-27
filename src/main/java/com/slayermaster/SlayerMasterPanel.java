package com.slayermaster;

import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class SlayerMasterPanel extends PluginPanel
{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DefaultListModel<String> monsterListModel;
    private JList<String> monsterList;
    private JTextArea detailTextArea;
    private Map<String, Monster> monsterDetails;

    private SpriteManager spriteManager;
    private MonsterImageManager imageManager;

    public SlayerMasterPanel(SpriteManager spriteManager)
    {
        if (spriteManager == null) {
            System.out.println("SpriteManager not initialized");
            return; // or handle the case more gracefully
        }
        this.spriteManager = spriteManager;
        this.imageManager = new MonsterImageManager();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Use layout managers effectively to space elements
        setLayout(new BorderLayout(10, 10)); // Adds 10 pixels of spacing both horizontally and vertically between components

        add(cardPanel, BorderLayout.CENTER);

        monsterDetails = new HashMap<>();
        initializeMonsterDetails();

        // Setting up the list and detail panels
        setupListPanel();
        setupDetailPanel();
    }

    private JTextField getSearchField()
    {
        IconTextField searchField = new IconTextField(20, spriteManager); // Assuming spriteManager is available

        // Customize search field size and font
        searchField.setPreferredSize(new Dimension(200, 30)); // Set preferred height to 30
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Adjust font size as needed

        // Set margins around the search field for spacing
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));

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

        return searchField;
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
    }

    private String getMonsterDetails(Monster monster)
    {
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(monster.getName()).append("\n");
        details.append("Locations:\n");
        for (String location : monster.getLocations())
        {
            if (location.equals(monster.getRecommendedLocation()))
            {
                details.append("* Recommended: ").append(location).append("\n");
            } else
            {
                details.append("- ").append(location).append("\n");
            }
        }
        details.append("Required Items: ").append(String.join(", ", monster.getRequiredItems())).append("\n");
        details.append("Recommended Gear: ").append(monster.getRecommendedGear()).append("\n");
        details.append("Attack Style: ").append(monster.getAttackStyle()).append("\n");
        return details.toString();
    }

    private void setupListPanel()
    {
        JPanel listPanel = new JPanel(new BorderLayout());

        // Create a panel for the search field with BoxLayout
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JTextField searchField = getSearchField();
        searchField.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the search field

        // Create a rigid area to act as a spacer
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 10 pixels space

        listPanel.add(searchPanel, BorderLayout.NORTH);

        monsterListModel = new DefaultListModel<>();
        monsterList = new JList<>(monsterListModel);
        setupMonsterList();
        monsterDetails.forEach((name, details) -> monsterListModel.addElement(name));
        JScrollPane scrollPane = new JScrollPane(monsterList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        cardPanel.add(listPanel, "List");
    }

    private void setupDetailPanel()
    {
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailTextArea = new JTextArea();
        detailTextArea.setEditable(false);
        detailPanel.add(new JScrollPane(detailTextArea), BorderLayout.CENTER);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "List"));
        detailPanel.add(backButton, BorderLayout.SOUTH);
        cardPanel.add(detailPanel, "Details");
    }

    private void setupMonsterList()
    {
        monsterList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                JLabel label = new JLabel(value.toString());

                // Use the image manager to get the icon
                ImageIcon icon = imageManager.getThumbnailIcon(value.toString());
                if (icon != null)
                {
                    JLabel iconLabel = new JLabel(icon);
                    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Add padding around the icon
                    panel.add(iconLabel, BorderLayout.EAST); // Icon on the right
                }

                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around text
                panel.add(label, BorderLayout.CENTER);
                panel.setToolTipText(value.toString());
                panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

                // Add mouse listener to the panel for showing details
                panel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Monster details = monsterDetails.get(value.toString());
                        detailTextArea.setText(getMonsterDetails(details));
                        cardLayout.show(cardPanel, "Details");
                    }
                });

                // Adding padding around each list cell
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                return panel;
            }
        });

        monsterList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting()) {
                String selectedMonster = monsterList.getSelectedValue();
                if (selectedMonster != null) {
                    Monster details = monsterDetails.get(selectedMonster);
                    detailTextArea.setText(getMonsterDetails(details));
                    cardLayout.show(cardPanel, "Details");
                }
            }
        });
    }
}
