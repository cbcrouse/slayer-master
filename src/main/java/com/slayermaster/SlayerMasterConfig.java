package com.slayermaster;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("slayermaster")
public interface SlayerMasterConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Let's do this!";
	}

	@ConfigItem(
		keyName = "uniqueKey",
		name = "Display text",
		description = "Hover text"
	)
	default boolean myCheckbox()
	{
		return true;
	}
}
