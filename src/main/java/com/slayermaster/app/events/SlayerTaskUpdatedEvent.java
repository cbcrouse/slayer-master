package com.slayermaster.app.events;

import com.slayermaster.infrastructure.services.CurrentSlayerTask;
import lombok.Getter;

@Getter
public class SlayerTaskUpdatedEvent
{
	private final CurrentSlayerTask slayerTask;

	public SlayerTaskUpdatedEvent(CurrentSlayerTask slayerTask)
	{
		this.slayerTask = slayerTask;
	}
}
