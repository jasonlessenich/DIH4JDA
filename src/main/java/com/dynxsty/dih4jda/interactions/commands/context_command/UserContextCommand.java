package com.dynxsty.dih4jda.interactions.commands.context_command;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

/**
 * Interface that must be implemented for all User Context Commands.
 *
 * <pre>{@code
 * public class PingContextMenu extends GuildContextCommand implements UserContextCommand {
 *
 *    public PingContextMenu() {
 * 		this.setCommandData(Commands.user("Ping"));
 *    }
 *
 *    @Override
 *    public void handleUserContextInteraction(UserContextInteractionEvent event) {
 * 		event.reply("Pong!").queue();
 *    }
 * }}
 */
public interface UserContextCommand {
	void handleUserContextCommand(UserContextInteractionEvent event);
}
