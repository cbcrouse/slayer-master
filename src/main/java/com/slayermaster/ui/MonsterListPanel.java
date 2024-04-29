package com.slayermaster.ui;

import com.slayermaster.api.RuneLiteApi;
import com.slayermaster.api.SlayerAssignment;
import com.slayermaster.events.SlayerTaskUpdatedEvent;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
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
    private final SpriteManager spriteManager;
    private final int hoveredIndex = -1;
    private final RuneLiteApi runeLiteApi;
    private JList<String> monsterList;
    private final MonsterSelectionListener selectionListener;
    private final Map<String, SlayerAssignment> assignmentDetails;
    private int currentSlayerLevel = -1;

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

        DefaultListModel<String> monsterListModel = new DefaultListModel<>();
        assignmentDetails.keySet().forEach(monsterListModel::addElement);

        monsterList = new JList<>(monsterListModel);
        monsterList.setCellRenderer(new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel));
        JScrollPane scrollPane = new JScrollPane(monsterList);
        add(scrollPane, BorderLayout.CENTER);

        JTextField searchField = new SearchTextField(20, spriteManager, this::filterMonsters);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacer
        add(searchPanel, BorderLayout.NORTH);

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
        assignmentDetails.keySet().stream()
                .filter(name -> name.toLowerCase().contains(text))
                .forEach(filteredModel::addElement);
        monsterList.setModel(filteredModel);
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
