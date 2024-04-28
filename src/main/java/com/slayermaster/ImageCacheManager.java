package com.slayermaster;

import com.slayermaster.osrswiki.WikiScraper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ImageCacheManager
{
    private MonsterImageManager imageManager = new MonsterImageManager();
    private static final String CACHE_DIR = "imageCache/";

    public ImageCacheManager()
    {
        // Ensure the cache directory exists
        File directory = new File(CACHE_DIR);
        if (!directory.exists())
        {
            directory.mkdirs();
        }
    }

    public ImageIcon getCachedImage(String monsterName)
    {
        return getCachedImage(monsterName, 25);
    }

    public ImageIcon getCachedImage(String monsterName, int maxWidth)
    {
        try
        {
            monsterName = getEdgeCaseMonsterName(monsterName);

            String fileName = monsterName.toLowerCase().replace(" ", "_") + ".png";
            Path imagePath = Paths.get(CACHE_DIR + fileName);
            ImageIcon imageIcon;

            // Check if the image is cached
            if (Files.exists(imagePath))
            {
                // Load and return the image from cache
                imageIcon = new ImageIcon(ImageIO.read(imagePath.toFile()));
            } else
            {
                // Fetch the image from the web
                imageIcon = imageManager.getWikiImageAsIcon(monsterName);

                // Cache the image
                File outputFile = imagePath.toFile();
                ImageIO.write((RenderedImage) imageIcon.getImage(), "png", outputFile);
            }

            return imageIcon;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static String getEdgeCaseMonsterName(String monsterName)
    {
        // Handled edge-cases with monster names
        if (monsterName.equalsIgnoreCase("troll"))
        {
            return "Mountain troll";
        }
        if (monsterName.equalsIgnoreCase("monkey"))
        {
            return "Monkey (monster)";
        }
        if (monsterName.equalsIgnoreCase("Sea snake"))
        {
            return "Giant Sea Snake";
        }
        if (monsterName.equalsIgnoreCase("Elf"))
        {
            return "Elf Warrior";
        }
        if (monsterName.equalsIgnoreCase("TzHaar"))
        {
            return "TzHaar-Ket";
        }
        if (monsterName.equalsIgnoreCase("Scabarite"))
        {
            return "Scarabs";
        }
        if (monsterName.equalsIgnoreCase("Bear"))
        {
            return "Grizzly bear";
        }
        if (monsterName.equalsIgnoreCase("Kalphites"))
        {
            return "Kalphite Worker";
        }
        if (monsterName.equalsIgnoreCase("Vampyre"))
        {
            return "Vyrewatch Sentinel";
        }
        if (monsterName.equalsIgnoreCase("Revenant"))
        {
            return "Revenant dragon";
        }
        if (monsterName.equalsIgnoreCase("Spiritual creature"))
        {
            return "Spiritual ranger";
        }
        if (monsterName.equalsIgnoreCase("Dogs"))
        {
            return "Guard dog";
        }
        if (monsterName.equalsIgnoreCase("Fossil island wyvern"))
        {
            return "Spitting Wyvern";
        }
        if (monsterName.equalsIgnoreCase("Warped creatures"))
        {
            return "Warped Tortoise";
        }
        return monsterName;
    }
}

