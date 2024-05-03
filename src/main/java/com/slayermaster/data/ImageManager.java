package com.slayermaster.data;

import com.google.inject.Inject;
import com.slayermaster.infrastructure.services.HttpClientSingleton;
import com.slayermaster.infrastructure.services.OSRSWikiScraper;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

@Slf4j
public class ImageManager
{
	private final OSRSWikiScraper osrsWikiScraper;

	@Inject
	public ImageManager(OSRSWikiScraper wikiScraper)
	{
		this.osrsWikiScraper = wikiScraper;
	}

	public ImageIcon getWikiImageAsIcon(String monsterName)
	{
		try
		{
			String url = osrsWikiScraper.getThumbnailUrlFromMonsterPage(monsterName);
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
		}
		catch (Exception e)
		{
			System.out.println("Exception when fetching image for: " + monsterName);
			e.printStackTrace();
			return null;
		}
	}

	public BufferedImage getWhiteCollapseIcon()
	{
		try
		{
			// Load the image from the resources folder
			URL imageUrl = getClass().getResource("/images/collapse_icon_30x30.png");
			assert imageUrl != null;
			BufferedImage originalImage = ImageIO.read(imageUrl);

			// Change the image color to white
			return changeImageColor(originalImage, Color.WHITE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null; // Return null if there was an error loading the image
		}
	}

	public BufferedImage changeImageColor(BufferedImage sourceImage, Color color)
	{
		// Create a new blank image with the same dimensions as the source image
		BufferedImage newImage = new BufferedImage(
			sourceImage.getWidth(),
			sourceImage.getHeight(),
			BufferedImage.TYPE_INT_ARGB);

		// Draw the source image onto the new image
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(sourceImage, 0, 0, null);
		g2d.dispose();

		// Apply a color filter to change the color
		return applyColorFilter(newImage, color);
	}

	private BufferedImage applyColorFilter(BufferedImage image, Color color)
	{
		LookupTable lookup = new LookupTable(0, 4)
		{
			@Override
			public int[] lookupPixel(int[] src, int[] dest)
			{
				dest[0] = (int) (src[0] * color.getRed() / 255.0);   // Scale red
				dest[1] = (int) (src[1] * color.getGreen() / 255.0); // Scale green
				dest[2] = (int) (src[2] * color.getBlue() / 255.0);  // Scale blue
				dest[3] = src[3]; // Preserve alpha
				return dest;
			}
		};

		BufferedImageOp op = new LookupOp(lookup, null);
		return op.filter(image, null);
	}

	public ImageIcon rotateIcon(ImageIcon icon, double angle)
	{
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		BufferedImage rotatedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotatedImg.createGraphics();
		g2d.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
		g2d.drawImage(icon.getImage(), 0, 0, null);
		g2d.dispose();
		return new ImageIcon(rotatedImg);
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
