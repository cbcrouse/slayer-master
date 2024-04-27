package com.slayermaster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class MonsterImageManager
{
    public ImageIcon getThumbnailIcon(String monsterName)
    {
        String normalizedPath = "/images/monsters/" + monsterName.toLowerCase().replace(" ", "_") + ".png";
        URL url = getClass().getResource(normalizedPath);
        if (url == null)
        {
            System.out.println("Could not find resource: " + normalizedPath);
            return null;
        }
        try {
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

    private ImageIcon resizeIcon(ImageIcon icon, int maxHeight)
    {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (height > maxHeight) {
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
}

