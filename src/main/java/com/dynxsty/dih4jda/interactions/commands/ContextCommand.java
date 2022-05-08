package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

// TODO v1.5: Documentation
public abstract class ContextCommand extends ExecutableCommand {
	private CommandData commandData;

	protected ContextCommand() {
	}

	public CommandData getCommandData() {
		return commandData;
	}

	// TODO v1.5: Documentation
	public void setCommandData(CommandData commandData) {
		if (commandData.getType() == Command.Type.MESSAGE || commandData.getType() == Command.Type.USER) {
			this.commandData = commandData;
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
		}
	}

	public abstract static class User extends ContextCommand {
		/**
		 * Abstract method that must be implemented for all User Context Commands.
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
		public abstract void execute(UserContextInteractionEvent event);
	}

	public abstract static class Message extends ContextCommand {
		/**
		 * Abstract method that must be implemented for all Message Context Commands.
		 *
		 * <pre>{@code
		 * public class PingContextMenu extends GuildContextCommand implements MessageContextCommand {
		 *
		 *    public PingContextMenu() {
		 * 		this.setCommandData(Commands.message("Ping"));
		 *    }
		 *
		 *    @Override
		 *    public void handleMessageContextInteraction(MessageContextInteractionEvent event) {
		 * 		event.reply("Pong!").queue();
		 *    }
		 * }}
		 */
		public abstract void execute(MessageContextInteractionEvent event);
	}
}