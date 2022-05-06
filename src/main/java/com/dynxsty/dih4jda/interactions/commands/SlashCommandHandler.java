package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandHandler {
	void execute(SlashCommandInteractionEvent event);
}
