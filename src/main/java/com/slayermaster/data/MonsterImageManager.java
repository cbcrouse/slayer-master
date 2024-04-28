package com.slayermaster.data;

import com.slayermaster.api.HttpClientSingleton;
import com.slayermaster.api.OSRSWikiScraper;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class MonsterImageManager
{
    public ImageIcon getWikiImageAsIcon(String monsterName)
    {
        try
        {
            OSRSWikiScraper wikiScraper = new OSRSWikiScraper();
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
}
