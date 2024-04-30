package com.slayermaster.ui;

import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.events.SlayerTaskUpdatedEvent;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class MonsterListPanel extends JPanel
{
    private JComboBox<String> filterComboBox;
    private JComboBox<String> sortComboBox;
    private final SpriteManager spriteManager;
    private final int hoveredIndex = -1;
    private final RuneLiteApi runeLiteApi;
    private JList<String> monsterList;
    private final MonsterSelectionListener selectionListener;
    private final Map<String, SlayerAssignment> assignmentDetails;
    private int currentSlayerLevel = -1;
    DefaultListModel<String> monsterListModel;


    public MonsterListPanel(
            Map<String, SlayerAssignment> assignmentDetails,
            SpriteManager spriteManager,
            MonsterSelectionListener selectionListener,
            RuneLiteApi runeLiteApi,
            EventBus eventBus)
    {
        this.assignmentDetails = assignmentDetails;
        this.spriteManager = spriteManager;
        this.selectionListener = selectionListener;
        this.runeLiteApi = runeLiteApi;
        setLayout(new BorderLayout());
        initializeComponents();

        // Register panel to listen to events
        eventBus.register(this);
    }

    private void initializeComponents()
    {
        currentSlayerLevel = runeLiteApi.getCurrentSlayerLevel();

        // Initialize filter combo box
        String[] filterOptions = new String[]{"Filter 1", "Filter 2", "Filter 3"}; // Add your filter options here
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);

        // Initialize sort combo box
        String[] sortOptions = new String[]{"A-Z", "Z-A", "Slayer Level High->Low", "Slayer Level Low->High"}; // Add your sort options here
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        // Initialize level combo box
        Integer[] levelOptions = new Integer[99];
        for (int i = 0; i < 99; i++)
        {
            levelOptions[i] = i + 1;
        }
        JComboBox<Integer> levelComboBox = new JComboBox<>(levelOptions);

        // Create labels for combo boxes
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setToolTipText("Filter monsters in the list by criteria (e.g., Attribute).");
        JLabel sortLabel = new JLabel("Sort:");
        sortLabel.setToolTipText("Sort monsters in the list.");
        JLabel levelLabel = new JLabel("Slayer Level:");
        levelLabel.setToolTipText("Show monsters in the list at or below the selected slayer level.");

        // Create panel for combo boxes
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new GridLayout(0, 2, 5, 5)); // 2 columns with 5px horizontal and vertical gap
        comboBoxPanel.add(filterLabel);
        comboBoxPanel.add(filterComboBox);
        comboBoxPanel.add(sortLabel);
        comboBoxPanel.add(sortComboBox);
        comboBoxPanel.add(levelLabel);
        comboBoxPanel.add(levelComboBox);

        // Add empty border around combo box panel
        comboBoxPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Initialize search field and panel
        JTextField searchField = new SearchTextField(20, spriteManager, this::filterMonsters, "Search...");
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Create a container panel to hold the combo box panel and the search panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(comboBoxPanel);
        containerPanel.add(searchPanel);

        // Add the container panel to the NORTH position of the main panel
        add(containerPanel, BorderLayout.NORTH);

        // Initialize monster list model and JList
        monsterListModel = new DefaultListModel<>();
        assignmentDetails.keySet().forEach(monsterListModel::addElement);
        monsterList = new JList<>(monsterListModel);
        monsterList.setCellRenderer(new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel));
        JScrollPane scrollPane = new JScrollPane(monsterList);
        add(scrollPane, BorderLayout.CENTER);

        setupMonsterList();
    }


    public void updateSlayerLevel()
    {
        this.currentSlayerLevel = runeLiteApi.getCurrentSlayerLevel();  // Fetch the updated level
        monsterList.setCellRenderer(new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel));  // Set a new renderer with updated level
        monsterList.repaint();  // Refresh the list to reflect changes
    }


    @Subscribe
    public void onSlayerTaskUpdatedEvent(SlayerTaskUpdatedEvent event)
    {
        updateSlayerLevel();
    }

    private void filterMonsters(String text)
    {
        DefaultListModel<String> filteredModel = new DefaultListModel<>();
        if (text == null || text.isEmpty())
        {
            // If the text is null or empty, set the monster list model to the original model
            monsterList.setModel(monsterListModel);
        } else
        {
            assignmentDetails.keySet().stream()
                    .filter(name -> name.toLowerCase().contains(text.toLowerCase()))
                    .forEach(filteredModel::addElement);
            monsterList.setModel(filteredModel);
        }
    }


    private void setupMonsterList()
    {
        monsterList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MonsterListCellRenderer renderer = new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel);
        monsterList.setCellRenderer(renderer);

        monsterList.addMouseMotionListener(new MouseAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                int index = monsterList.locationToIndex(e.getPoint());
                if (index != hoveredIndex)
                {
                    renderer.setHoveredIndex(index);
                    monsterList.repaint();
                }
            }
        });

        monsterList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting() && !monsterList.isSelectionEmpty())
            {
                String selectedMonster = monsterList.getSelectedValue();
                SlayerAssignment details = assignmentDetails.get(selectedMonster);
                if (details != null)
                {
                    selectionListener.onMonsterSelected(details);
                }
            }
        });
    }
}
