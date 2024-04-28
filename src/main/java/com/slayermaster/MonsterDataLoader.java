package com.slayermaster;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import net.runelite.client.RuneLite;

public class MonsterDataLoader
{
    public List<Monster> loadMonsterData()
    {
        // Get the resource as a stream
        InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(RuneLite.class.getResourceAsStream("/data/monsters.json"))
        );

        // Define the type token for the list of Monster objects
        Type monsterListType = new TypeToken<List<Monster>>() {}.getType();

        // Use Gson to convert the JSON into a list of Monster objects
        Gson gson = new Gson();
        return gson.fromJson(reader, monsterListType);
    }
}
