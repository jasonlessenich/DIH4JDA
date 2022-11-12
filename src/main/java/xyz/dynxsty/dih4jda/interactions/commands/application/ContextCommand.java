package xyz.dynxsty.dih4jda.interactions.commands.application;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Model class which represents a single Context Command.
 *
 * @see ContextCommand.User#execute
 * @see ContextCommand.Message#execute
 * @since v1.5
 */
public abstract class ContextCommand<E> extends BaseApplicationCommand<E, CommandData> {

	private ContextCommand() {}

	public abstract static class User extends ContextCommand<UserContextInteractionEvent> {}

	public abstract static class Message extends ContextCommand<MessageContextInteractionEvent> {}
}
