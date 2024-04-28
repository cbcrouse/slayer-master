package com.slayermaster;

import com.slayermaster.osrswiki.HttpClientSingleton;
import com.slayermaster.osrswiki.WikiScraper;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class MonsterImageManager
{
    public ImageIcon getWikiImageAsIcon(String monsterName)
    {
        // Example https://oldschool.runescape.wiki/w/File:Aberrant_spectre.png
        try
        {
            WikiScraper wikiScraper = new WikiScraper();
            String url = wikiScraper.getThumbnailUrlFromMonsterPage(monsterName);
            Request request = new Request.Builder().url(url).build();
            Response response = HttpClientSingleton.getInstance().newCall(request).execute();

            if (!response.isSuccessful())
            {
                System.out.println("Failed to fetch image for: " + monsterName);
                return null;
            }

            InputStream inputStream = new ByteArrayInputStream(response.body().bytes());
            Image image = ImageIO.read(inputStream);
            if (image == null)
            {
                System.out.println("Failed to decode image for: " + monsterName);
                return null;
            }

            return new ImageIcon(image);
        } catch (Exception e)
        {
            System.out.println("Exception when fetching image for: " + monsterName);
            e.printStackTrace();
            return null;
        }
    }

    public ImageIcon getThumbnailIcon(String monsterName)
    {
        String normalizedPath = "/images/monsters/" + monsterName.toLowerCase().replace(" ", "_") + ".png";
        URL url = getClass().getResource(normalizedPath);
        if (url == null)
        {
            System.out.println("Could not find resource: " + normalizedPath);
            return null;
        }
        try
        {
            Image image = ImageIO.read(url);
            if (image == null)
            {
                System.out.println("Failed to load image for: " + monsterName);
                return null;
            }
            ImageIcon icon = new ImageIcon(image);
            return resizeIcon(icon, 35); // Larger size for details panel

            //return new ImageIcon(image.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        } catch (Exception e)
        {
            System.out.println("Exception when loading image for: " + monsterName);
            e.printStackTrace();
            return null;
        }
    }

    public ImageIcon getDetailIcon(String monsterName)
    {
        ImageIcon icon = loadIcon(monsterName);
        return resizeIcon(icon, 200); // Larger size for details panel
    }

    private ImageIcon loadIcon(String monsterName)
    {
        try
        {
            // Normalize the monster name to lowercase and replace spaces with underscores
            String normalizedPath = monsterName.toLowerCase().replace(" ", "_") + ".png";
            URL url = getClass().getResource("/images/monsters/" + normalizedPath);
            if (url != null)
            {
                return new ImageIcon(url);
            }
        } catch (Exception e)
        {
            System.out.println("Error loading image for: " + monsterName);
            e.printStackTrace();
        }
        return new ImageIcon(); // Return an empty icon in case of failure
    }

    public ImageIcon resizeIcon(ImageIcon icon, int maxHeight)
    {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (height > maxHeight)
        {
            // Calculate the new width maintaining the aspect ratio
            double aspectRatio = (double) width / (double) height;
            int newWidth = (int) (maxHeight * aspectRatio);
            Image image = icon.getImage();
            Image resizedImage = image.getScaledInstance(newWidth, maxHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }

        // Return the original icon if no resizing is needed
        return icon;
    }

    public ImageIcon resizeIconByWidth(ImageIcon icon, int maxWidth)
    {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (width > maxWidth)
        {
            // Calculate the new height maintaining the aspect ratio
            double aspectRatio = (double) height / (double) width;
            int newHeight = (int) (maxWidth * aspectRatio);
            Image image = icon.getImage();
            Image resizedImage = image.getScaledInstance(maxWidth, newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }

        // Return the original icon if no resizing is needed
        return icon;
    }

}

