package com.dynxsty.dih4jda.interactions.components.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

//TODO-v1.4: Documentation
public interface ButtonHandler {
	void handleButton(ButtonInteractionEvent event, net.dv8tion.jda.api.interactions.components.buttons.Button button);
}
