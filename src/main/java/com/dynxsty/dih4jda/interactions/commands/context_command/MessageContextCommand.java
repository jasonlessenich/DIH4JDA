package com.dynxsty.dih4jda.interactions.commands.context_command;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

/**
 * Interface that must be implemented for all Message Context Commands.
 *
 * <pre>{@code
 * public class PingContextMenu extends GuildContextCommand implements MessageContextCommand {
 *
 *     public PingContextMenu() {
 * 		this.setCommandData(Commands.message("Ping"));
 * 	}
 *
 * 	@Override
 * 	public void handleMessageContextInteraction(MessageContextInteractionEvent event) {
 * 		event.reply("Pong!").queue();
 * 	}
 * }}
 */
public interface MessageContextCommand {
	void handleMessageContextCommand(MessageContextInteractionEvent event);
}
