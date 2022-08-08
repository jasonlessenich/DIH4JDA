package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

/**
 * Model class which represents a single Context Command.
 *
 * @see ContextCommand.User#execute
 * @see ContextCommand.Message#execute
 * @since v1.5
 */
public class ContextCommand extends Command {
	private CommandData commandData = null;

	private ContextCommand() {}

	public CommandData getCommandData() {
		return commandData;
	}

	/**
	 * Sets this commands' {@link CommandData}.
	 *
	 * @param commandData The corresponding {@link CommandData} which should be used for this context command.
	 * @see net.dv8tion.jda.api.interactions.commands.build.Commands#user(String)
	 * @see net.dv8tion.jda.api.interactions.commands.build.Commands#message(String)
	 */
	public void setCommandData(@Nonnull CommandData commandData) {
		if (commandData.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE ||
				commandData.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.USER) {
			this.commandData = commandData;
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
		}
	}

	public abstract static class User extends ContextCommand implements ExecutableCommand<UserContextInteractionEvent> {}

	public abstract static class Message extends ContextCommand implements ExecutableCommand<MessageContextInteractionEvent> {}
}
