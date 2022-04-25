package com.dynxsty.dih4jda.interactions.components.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface Button {
	void handleButton(ButtonInteractionEvent event, Button button);
}
