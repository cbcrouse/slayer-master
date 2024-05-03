package com.slayermaster.infrastructure.services;

import lombok.Getter;

@Getter
public class CurrentSlayerTask
{
	private final int taskSize;
	private final int creatureId;
	private final String creatureName;
	private final String location;
	private final String slayerMasterName;

	public CurrentSlayerTask(int taskSize, int creatureId, String creatureName, String location, String slayerMasterName)
	{
		this.taskSize = taskSize;
		this.creatureId = creatureId;
		this.creatureName = creatureName;
		this.location = location;
		this.slayerMasterName = slayerMasterName;
	}
}
