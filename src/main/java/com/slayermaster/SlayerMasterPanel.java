package com.slayermaster;

import com.slayermaster.osrswiki.WikiScraper;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class SlayerMasterPanel extends PluginPanel
{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DefaultListModel<String> monsterListModel;
    private JList<String> monsterList;
    private Map<String, Monster> monsterDetails;

    private JLabel detailsImageLabel = new JLabel();
    private JLabel detailsNameLabel = new JLabel();
    private JTextPane detailsTextPane = new JTextPane();
    private int hoveredIndex = -1;

    private SpriteManager spriteManager;
    private MonsterImageManager imageManager = new MonsterImageManager();
    private ImageCacheManager imageCacheManager = new ImageCacheManager();

    private WikiDataLoader wikiDataLoader = new WikiDataLoader(new WikiScraper());

    public SlayerMasterPanel(SpriteManager spriteManager)
    {
        if (spriteManager == null)
        {
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
        searchField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                filter();
            }

            public void removeUpdate(DocumentEvent e)
            {
                filter();
            }

            public void changedUpdate(DocumentEvent e)
            {
                filter();
            }

            private void filter()
            {
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

    private String getMonsterDetails(Monster monster)
    {
        // Set the monster's image and name
        ImageIcon monsterImage = imageCacheManager.getCachedImage(monster.getName());
        ImageIcon resizedImage = imageManager.resizeIcon(monsterImage, 100);
        detailsImageLabel.setIcon(resizedImage);
        detailsImageLabel.setHorizontalAlignment(JLabel.CENTER);
        detailsNameLabel.setText(monster.getName());
        detailsNameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Build the details string with HTML for styling
        StringBuilder details = new StringBuilder("<html>");
        details.append("<div style='text-align: center;'><h1>").append(monster.getName()).append("</h1><hr></div>");

        // Append locations
        details.append("<div style='text-align: left; margin-left: 20px;'>")
                .append("<h2>Locations:</h2>");
        for (String location : monster.getLocations())
        {
            details.append("- ").append(location).append("<br>");
        }

        // Append alternatives if available
        if (monster.getAlternatives().length > 0)
        {
            details.append("<h2>Alternatives:</h2>");
            for (String alternative : monster.getAlternatives())
            {
                details.append("- ").append(alternative).append("<br>");
            }
        }

        // Append required items if not empty and not "N/A"
        if (monster.getRequiredItems().length > 0 && !Arrays.asList(monster.getRequiredItems()).contains("N/A"))
        {
            details.append("<h2>Required Items:</h2>")
                    .append(String.join(", ", monster.getRequiredItems())).append("<br>");
        }

        // Append recommended gear if not "N/A"
        if (!"N/A".equalsIgnoreCase(monster.getRecommendedGear()))
        {
            details.append("<h2>Recommended Gear:</h2>").append(monster.getRecommendedGear()).append("<br>");
        }

        // Append attack style
        details.append("<h2>Attack Style:</h2>").append(monster.getAttackStyle())
                .append("</div></html>");

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
        detailPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        detailsTextPane = new JTextPane();
        detailsTextPane.setContentType("text/html");  // Set content type to HTML
        detailsTextPane.setEditable(false);
        detailsTextPane.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(detailsTextPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "List"));

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 20)); // Create a spacer panel 20 pixels high

        detailPanel.add(detailsImageLabel, BorderLayout.NORTH);
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.add(spacerPanel, BorderLayout.SOUTH);
        detailPanel.add(backButton, BorderLayout.PAGE_END);

        cardPanel.add(detailPanel, "Details");
    }

    private void setupMonsterList()
    {
        ImageCacheManager imageCacheManager = new ImageCacheManager();
        monsterList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monsterList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                JPanel panel = new JPanel(new BorderLayout());
                JLabel label = new JLabel(value.toString());

                ImageIcon icon = imageCacheManager.getCachedImage(value.toString());
                ImageIcon resizedIcon = imageManager.resizeIcon(icon, 25);
                JLabel iconLabel = new JLabel(resizedIcon);
                iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                panel.add(iconLabel, BorderLayout.EAST);

                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(label, BorderLayout.CENTER);
                panel.setToolTipText(value.toString());

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(index == hoveredIndex ? new Color(0x555555) : list.getBackground());
                    label.setForeground(list.getForeground());
                }

                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
                return panel;
            }
        });

        // Adding mouse listener to handle hover effect
        monsterList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = monsterList.locationToIndex(e.getPoint());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    monsterList.repaint();
                }
            }
        });

        monsterList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                String selectedMonster = monsterList.getSelectedValue();
                if (selectedMonster != null && monsterDetails.containsKey(selectedMonster))
                {
                    Monster details = monsterDetails.get(selectedMonster);
                    if (details != null && detailsTextPane != null)
                    {
                        detailsTextPane.setText(getMonsterDetails(details));
                        cardLayout.show(cardPanel, "Details");
                    } else
                    {
                        System.out.println("Details not found or detailTextArea is not initialized");
                    }
                } else
                {
                    System.out.println("Selected monster is null or not found in monsterDetails");
                }
            }
        });

    }
}
