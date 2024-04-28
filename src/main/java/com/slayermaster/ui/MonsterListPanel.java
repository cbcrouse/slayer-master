package com.slayermaster.ui;

import com.slayermaster.data.Monster;
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
    private JList<String> monsterList;
    private final MonsterSelectionListener selectionListener;
    private final Map<String, Monster> monsterDetails;

    public MonsterListPanel(Map<String, Monster> monsterDetails, SpriteManager spriteManager, MonsterSelectionListener selectionListener)
    {
        this.monsterDetails = monsterDetails;
        this.spriteManager = spriteManager;
        this.selectionListener = selectionListener;
        setLayout(new BorderLayout());
        initializeComponents();
    }

    public void initializeComponents()
    {
        DefaultListModel<String> monsterListModel = new DefaultListModel<>();
        monsterDetails.keySet().forEach(monsterListModel::addElement);

        monsterList = new JList<>(monsterListModel);
        monsterList.setCellRenderer(new MonsterListCellRenderer(monsterDetails));
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


    private void filterMonsters(String text)
    {
        DefaultListModel<String> filteredModel = new DefaultListModel<>();
        monsterDetails.keySet().stream()
                .filter(name -> name.toLowerCase().contains(text))
                .forEach(filteredModel::addElement);
        monsterList.setModel(filteredModel);
    }

    private void setupMonsterList()
    {
        monsterList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MonsterListCellRenderer renderer = new MonsterListCellRenderer(monsterDetails);
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
                Monster details = monsterDetails.get(selectedMonster);
                if (details != null)
                {
                    selectionListener.onMonsterSelected(details);
                }
            }
        });
    }
}
