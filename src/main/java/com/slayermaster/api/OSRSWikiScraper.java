package com.slayermaster.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.runelite.client.RuneLite;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OSRSWikiScraper
{
    private final static String baseUrl = "https://oldschool.runescape.wiki";
    private final static String baseWikiUrl = baseUrl + "/w/";
    private final static String baseWikiLookupUrl = baseWikiUrl + "Special:Lookup";
    private static final String USER_AGENT = RuneLite.USER_AGENT + " (slayer-master)";

    public String getThumbnailUrlFromMonsterPage(String monsterName)
    {
        try
        {
            String url = baseWikiUrl + monsterName.replace(" ", "_");
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", USER_AGENT)
                    .build();
            Response response = HttpClientSingleton.getInstance().newCall(request).execute();

            if (!response.isSuccessful())
            {
                System.out.println("Failed to fetch page for: " + monsterName);
                return null;
            }

            assert response.body() != null;
            String html = response.body().string();
            Document doc = Jsoup.parse(html);
            Element imageElement = doc.select("table.infobox img").first();

            if (imageElement != null)
            {
                String imageUrl = imageElement.attr("src");
                // If the image URL is relative, prepend the base URL
                if (!imageUrl.startsWith("http"))
                {
                    imageUrl = baseUrl + imageUrl;
                }
                return imageUrl;
            }
        } catch (Exception e)
        {
            System.out.println("Exception when fetching image URL for: " + monsterName);
            e.printStackTrace();
        }
        return null;
    }

    public List<SlayerAssignment> parseSlayerTaskPage()
    {
        List<SlayerAssignment> assignments = new ArrayList<>();
        CompletableFuture<String> future = requestAsync(HttpClientSingleton.getInstance(), baseWikiUrl + "Slayer_task");
        future.thenAccept(html ->
        {
            Document doc = Jsoup.parse(html);
            // Selects all table rows in the tbody of the table, skipping the first row which is the header
            Elements rows = doc.select("table.wikitable.sortable tbody tr:gt(0)"); // ":gt(0)" skips the first row

            for (Element row : rows)
            {
                Elements tds = row.select("td");
                String slayerLevel = !tds.isEmpty() ? tds.get(0).text() : "0";
                String monsterHref = tds.size() > 1 && tds.get(1).select("a").first() != null ? tds.get(1).select("a").first().attr("href") : "";
                String taskName = ""; // Initialize taskName variable
                Element taskNameElement = tds.size() > 1 ? tds.get(1).select("a").first() : null; // Select the first <a> element
                if (taskNameElement != null)
                {
                    taskName = taskNameElement.text(); // Get the text of the <a> element
                }
                String monsterName = monsterHref.isEmpty() ? "Unknown" : monsterHref.substring(monsterHref.lastIndexOf('/') + 1).replace("_", " ");
                String location = tds.size() > 2 ? tds.get(2).text() : "Unknown";

                List<String> requiredItemsList = tds.size() > 3 ? row.select("td:nth-child(4) a").stream()
                        .map(link -> link.attr("title"))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList()) : new ArrayList<>();
                String[] requiredItems = requiredItemsList.isEmpty() ? new String[]{} : requiredItemsList.toArray(new String[0]);

                String attribute = tds.size() > 4 ? tds.get(4).text() : "None";
                String attackStyle = tds.size() > 5 ? tds.get(5).text() : "Unknown";
                String alternatives = tds.size() > 6 ? tds.get(6).text() : "None";

                List<String> slayerMastersList = tds.size() > 7 ? row.select("td:last-child a").stream()
                        .map(link -> link.attr("title"))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList()) : new ArrayList<>();
                String[] slayerMasters = slayerMastersList.isEmpty() ? new String[]{"Unknown"} : slayerMastersList.toArray(new String[0]);

                SlayerAssignment assignment = new SlayerAssignment(
                        slayerLevel,
                        taskName,
                        monsterName,
                        new String[]{location}, // Handle multiple locations if necessary
                        requiredItems,
                        attribute,
                        attackStyle,
                        new String[]{alternatives}, // Handle multiple alternatives if necessary
                        slayerMasters
                );

                assignments.add(assignment);
            }
        }).join(); // Wait for the future to complete before returning the list
        return assignments;
    }

    public List<SlayerLocation> parseLocationComparisonTable(String taskName)
    {
        List<SlayerLocation> locations = new ArrayList<>();
        String url = baseWikiUrl + "Slayer_task%2F" + taskName;
        CompletableFuture<String> future = requestAsync(HttpClientSingleton.getInstance(), url);
        future.thenAccept(html ->
        {
            Document doc = Jsoup.parse(html);
            Element comparisonHeader = doc.selectFirst("h2:contains(Location Comparison)");
            if (comparisonHeader != null)
            {
                Element comparisonTable = comparisonHeader.nextElementSibling();
                if (comparisonTable != null && comparisonTable.tagName().equalsIgnoreCase("table"))
                {
                    Elements rows = comparisonTable.select("tbody tr");
                    for (Element row : rows)
                    {
                        Elements cells = row.select("td");
                        if (cells.size() >= 7)
                        {
                            String location = cells.get(0).text();
                            String mapLink = ""; // Initialize mapLink to empty string
                            Element mapLinkElement = cells.get(1).select("a").first(); // Select the first <a> element
                            if (mapLinkElement != null)
                            {
                                String zoom = mapLinkElement.attr("data-zoom"); // Get latitude
                                String lat = mapLinkElement.attr("data-lat"); // Get latitude
                                String lon = mapLinkElement.attr("data-lon"); // Get longitude
                                String mapId = mapLinkElement.attr("data-mapid"); // Get map ID
                                String plane = mapLinkElement.attr("data-plane"); // Get plane
                                // Example : <td><a class="mw-kartographer-maplink mw-kartographer-link" data-mw="interface" data-zoom="2" data-lat="10074" data-lon="1631" data-mapid="32" data-plane="0" data-overlays="[&quot;pins&quot;]" href="#mapFullscreen">Maplink</a></td>
                                // https://maps.runescape.wiki/osrs/#3/32/0/1631/10074
                                mapLink = "https://maps.runescape.wiki/osrs/#" + zoom + "/" + mapId + "/" + plane + "/" + lon + "/" + lat;
                            }
                            String amount = cells.get(2).text();
                            boolean multicombat = parseBoolean(cells.get(3).text());
                            boolean cannonable = parseBoolean(cells.get(4).text());
                            boolean safespottable = parseBoolean(cells.get(5).text());
                            String notes = cells.get(6).text();

                            SlayerLocation slayerLocation = new SlayerLocation(location, mapLink, amount, multicombat, cannonable, safespottable, notes);
                            locations.add(slayerLocation);
                        }
                    }
                } else
                {
                    System.out.println("Location Comparison table not found for task: " + taskName);
                }
            } else
            {
                System.out.println("Location Comparison header not found for task: " + taskName);
            }
        }).exceptionally(ex ->
        {
            // Handle exceptions here
            System.out.println("Failed to parse Location Comparison table for task: " + taskName + " - " + ex.getMessage());
            return null;
        }).join(); // Wait for the future to complete before returning the list
        return locations;
    }

    private boolean parseBoolean(String value)
    {
        return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }

    public static String getWikiUrl(String itemOrMonsterName)
    {
        String sanitizedName = sanitizeName(itemOrMonsterName);
        return baseWikiUrl + sanitizedName;
    }

    public static String getWikiUrlWithId(String monsterName, int id)
    {
        String sanitizedName = sanitizeName(monsterName);
        // --- Handle edge cases for specific pages ---
        if (id == 7851 || id == 7852)
        {
            // Redirect Dusk and Dawn to Grotesque Guardians page
            id = -1;
            sanitizedName = "Grotesque_Guardians";
        }
        // ---
        return baseWikiLookupUrl + "?type=npc&id=" + String.valueOf(id) + "&name=" + sanitizedName;
    }

    public static String sanitizeName(String name)
    {
        // --- Handle edge cases for specific pages ---
        if (name.equalsIgnoreCase("tzhaar-mej"))
        {
            name = "tzhaar-mej (monster)";
        }

        if (name.equalsIgnoreCase("dusk") || name.equalsIgnoreCase("dawn"))
        {
            name = "grotesque guardians";
        }

        name = name.trim().toLowerCase().replaceAll("\\s+", "_");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static CompletableFuture<String> requestAsync(OkHttpClient okHttpClient, String url)
    {
        CompletableFuture<String> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException ex)
            {
                future.completeExceptionally(ex);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                try (response; ResponseBody responseBody = response.body())
                {
                    if (!response.isSuccessful())
                    {
                        future.completeExceptionally(new IOException("Unexpected code " + response));
                    } else if (responseBody != null)
                    {
                        future.complete(responseBody.string());
                    } else
                    {
                        future.completeExceptionally(new NullPointerException("Response body is null"));
                    }
                } catch (IOException e)
                {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }
}
