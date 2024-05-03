package com.slayermaster.ui;

import com.slayermaster.data.ImageCacheManager;
import com.slayermaster.data.ImageManager;
import com.slayermaster.data.WikiDataLoader;
import com.slayermaster.infrastructure.services.IRuneLiteApi;
import com.slayermaster.infrastructure.services.SlayerAssignment;
import com.slayermaster.app.events.SlayerTaskUpdatedEvent;
import java.util.List;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MonsterListPanel extends JPanel
{
	private final SpriteManager spriteManager;
	private final int hoveredIndex = -1;
	private final IRuneLiteApi runeLiteApi;
	private final WikiDataLoader wikiDataLoader;
	private final ImageCacheManager imageCacheManager;
	private final ImageManager imageManager;
	private JList<String> monsterList;
	private final MonsterSelectionListener selectionListener;
	private Map<String, SlayerAssignment> assignmentDetails;
	private int currentSlayerLevel = -1;
	DefaultListModel<String> monsterListModel;
	private Integer selectedSlayerLevel = null; // Default selected slayer level
	private String currentSortMethod = "A-Z"; // Default sorting method

	public MonsterListPanel(
		SpriteManager spriteManager,
		MonsterSelectionListener selectionListener,
		IRuneLiteApi runeLiteApi,
		EventBus eventBus,
		WikiDataLoader wikiDataLoader,
		ImageManager imageManager,
		ImageCacheManager imageCacheManager)
	{
		this.spriteManager = spriteManager;
		this.selectionListener = selectionListener;
		this.runeLiteApi = runeLiteApi;
		this.wikiDataLoader = wikiDataLoader;
		this.imageManager = imageManager;
		this.imageCacheManager = imageCacheManager;
		setLayout(new BorderLayout());
		initializeMonsterDetails();
		initializeComponents();

		// Register panel to listen to events
		eventBus.register(this);
	}

	private void initializeComponents()
	{
		currentSlayerLevel = runeLiteApi.getCurrentSlayerLevel();
		JComboBox<String> attributeFilterComboBox = getAttributeFilterComboBox();
		JComboBox<Integer> slayerLevelComboBox = getSlayerLevelComboBox();
		JComboBox<String> sortComboBox = getSortComboBox();

		// Create labels for combo boxes
		JLabel attributeFilterLabel = new JLabel("Attribute Filter:");
		attributeFilterLabel.setToolTipText("Filter monsters in the list by attribute.");
		JLabel sortLabel = new JLabel("Sort:");
		sortLabel.setToolTipText("Sort monsters in the list.");
		JLabel levelLabel = new JLabel("Slayer Level:");
		levelLabel.setToolTipText("Show monsters in the list at or below the selected slayer level.");

		// Create clear filters button
		JButton clearFiltersButton = new JButton("Clear Filters");
		clearFiltersButton.addActionListener(e ->
		{
			clearFilters(attributeFilterComboBox, sortComboBox, slayerLevelComboBox);
		});

		// Create panel for combo boxes
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.setLayout(new GridLayout(0, 2, 5, 5)); // 2 columns with 5px horizontal and vertical gap
		comboBoxPanel.add(attributeFilterLabel);
		comboBoxPanel.add(attributeFilterComboBox);
		comboBoxPanel.add(sortLabel);
		comboBoxPanel.add(sortComboBox);
		comboBoxPanel.add(levelLabel);
		comboBoxPanel.add(slayerLevelComboBox);
		comboBoxPanel.add(clearFiltersButton);

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
		monsterList.setCellRenderer(new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel, imageManager, imageCacheManager));
		JScrollPane scrollPane = new JScrollPane(monsterList);
		add(scrollPane, BorderLayout.CENTER);

		setupMonsterList();
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
		assignmentDetails = sortedMonsterDetails;
	}

	private JComboBox<String> getSortComboBox()
	{
		String[] sortOptions = new String[]{"A-Z", "Z-A", "Slayer Level High->Low", "Slayer Level Low->High"}; // Add your sort options here
		JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
		sortComboBox.setSelectedIndex(0); // Set default selection to "A-Z"
		sortComboBox.addActionListener(e ->
		{
			currentSortMethod = (String) sortComboBox.getSelectedItem();
			applySorting();
		});
		return sortComboBox;
	}

	private JComboBox<Integer> getSlayerLevelComboBox()
	{
		Integer[] levelOptions = new Integer[100];
		levelOptions[0] = null; // Set the first option to null
		for (int i = 1; i < 100; i++)
		{
			levelOptions[i] = i;
		}
		JComboBox<Integer> levelComboBox = new JComboBox<>(levelOptions);
		levelComboBox.addActionListener(e ->
		{
			selectedSlayerLevel = (Integer) levelComboBox.getSelectedItem();
			filterBySlayerLevel(selectedSlayerLevel);
			applySorting();
		});
		return levelComboBox;
	}

	private JComboBox<String> getAttributeFilterComboBox()
	{
		String[] attributeFilterOptions = new String[]{"All", "Draconic", "Fiery", "Undead", "Spooky", "Frosty", "Aquatic", "Reptilian", "Insectoid"}; // Add your attribute filter options here
		JComboBox<String> attributeFilterComboBox = new JComboBox<>(attributeFilterOptions);
		attributeFilterComboBox.addActionListener(e ->
		{
			String selectedAttribute = (String) attributeFilterComboBox.getSelectedItem();
			if (!selectedAttribute.equals("All"))
			{
				filterByAttribute(selectedAttribute);
			}
			else
			{
				resetMonsterList();
			}
		});
		return attributeFilterComboBox;
	}

	public void updateSlayerLevel()
	{
		this.currentSlayerLevel = runeLiteApi.getCurrentSlayerLevel();  // Fetch the updated level
		monsterList.setCellRenderer(new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel, imageManager, imageCacheManager));  // Set a new renderer with updated level
		monsterList.repaint();  // Refresh the list to reflect changes
	}

	@Subscribe
	public void onSlayerTaskUpdatedEvent(SlayerTaskUpdatedEvent event)
	{
		updateSlayerLevel();
	}

	private void applySorting()
	{
		switch (Objects.requireNonNull(currentSortMethod))
		{
			case "A-Z":
				sortByNameAscending(selectedSlayerLevel);
				break;
			case "Z-A":
				sortByNameDescending(selectedSlayerLevel);
				break;
			case "Slayer Level High->Low":
				sortBySlayerLevelDescending(selectedSlayerLevel);
				break;
			case "Slayer Level Low->High":
				sortBySlayerLevelAscending(selectedSlayerLevel);
				break;
		}
	}

	private void resetMonsterList()
	{
		monsterListModel.clear();
		assignmentDetails.keySet().forEach(monsterListModel::addElement);
	}

	private void clearFilters(JComboBox<String> attributeFilterComboBox, JComboBox<String> sortComboBox, JComboBox<Integer> levelComboBox)
	{
		attributeFilterComboBox.setSelectedIndex(0);
		sortComboBox.setSelectedIndex(0);
		levelComboBox.setSelectedIndex(0);
		updateSlayerLevel();
	}

	private void filterByAttribute(String selectedAttribute)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> assignment.getAttribute().equalsIgnoreCase(selectedAttribute))
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void filterBySlayerLevel(Integer level)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> level == null || Integer.parseInt(assignment.getSlayerLevel()) <= level) // Filter by Slayer Level if it's not null
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void filterMonsters(String text)
	{
		DefaultListModel<String> filteredModel = new DefaultListModel<>();
		if (text == null || text.isEmpty())
		{
			// If the text is null or empty, set the monster list model to the original model
			monsterList.setModel(monsterListModel);
		}
		else
		{
			assignmentDetails.keySet().stream()
				.filter(name -> name.toLowerCase().contains(text.toLowerCase()))
				.forEach(filteredModel::addElement);
			monsterList.setModel(filteredModel);
		}
	}

	private void sortByNameDescending(Integer maxLevel)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> maxLevel == null || Integer.parseInt(assignment.getSlayerLevel()) <= maxLevel) // Filter by Slayer Level if it's not null
			.sorted(Comparator.comparing(SlayerAssignment::getMonsterName).reversed()) // Sort by name descending
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void sortByNameAscending(Integer maxLevel)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> maxLevel == null || Integer.parseInt(assignment.getSlayerLevel()) <= maxLevel) // Filter by Slayer Level if it's not null
			.sorted(Comparator.comparing(SlayerAssignment::getMonsterName)) // Sort by name ascending
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void sortBySlayerLevelDescending(Integer maxLevel)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> maxLevel == null || Integer.parseInt(assignment.getSlayerLevel()) <= maxLevel) // Filter by Slayer Level if it's not null
			.sorted((a1, a2) -> Integer.compare(Integer.parseInt(a2.getSlayerLevel()), Integer.parseInt(a1.getSlayerLevel())))
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void sortBySlayerLevelAscending(Integer maxLevel)
	{
		monsterListModel.clear();
		assignmentDetails.values().stream()
			.filter(assignment -> maxLevel == null || Integer.parseInt(assignment.getSlayerLevel()) <= maxLevel) // Filter by Slayer Level if it's not null
			.sorted(Comparator.comparingInt(assignment -> Integer.parseInt(assignment.getSlayerLevel())))
			.map(SlayerAssignment::getMonsterName)
			.forEach(monsterListModel::addElement);
	}

	private void setupMonsterList()
	{
		monsterList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		MonsterListCellRenderer renderer = new MonsterListCellRenderer(assignmentDetails, currentSlayerLevel, imageManager, imageCacheManager);
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
