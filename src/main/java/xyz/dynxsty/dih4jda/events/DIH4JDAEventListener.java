package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import xyz.dynxsty.dih4jda.interactions.commands.AbstractCommand;
import xyz.dynxsty.dih4jda.interactions.commands.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.SlashCommand;

/**
 * An interface containing all events and their method that DIH4JDA can fire.
 */
public interface DIH4JDAEventListener {

	/**
	 * An event that gets fired when an exception gets raised while executing any command.
	 *
	 * @param event The {@link CommandExceptionEvent} that was fired.
	 * @see SlashCommand#execute(SlashCommandInteractionEvent)
	 * @see SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
	 * @see ContextCommand.User#execute(Object)
	 * @see ContextCommand.Message#execute(Object)
	 */
	default void onCommandException(CommandExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while interacting with a message component.
	 *
	 * @param event The {@link ComponentExceptionEvent} that was fired.
	 */
	default void onComponentException(ComponentExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while handling an autocomplete interaction.
	 *
	 * @param event The {@link AutoCompleteExceptionEvent} that was fired.
	 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
	 */
	default void onAutoCompleteException(AutoCompleteExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while handling a modal interaction.
	 *
	 * @param event The {@link ModalExceptionEvent} that was fired.
	 */
	default void onModalException(ModalExceptionEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, does NOT have one of the required permissions.
	 *
	 * @param event The {@link InsufficientPermissionsEvent} that was fired.
	 * @see AbstractCommand#setRequiredPermissions(Permission...)
	 */
	default void onInsufficientPermissions(InsufficientPermissionsEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
	 *
	 * @param event The {@link InvalidUserEvent} that was fired.
	 * @see AbstractCommand#setRequiredUsers(Long...)
	 */
	default void onInvalidUser(InvalidUserEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
	 *
	 * @param event The {@link InvalidRoleEvent} that was fired.
	 * @see AbstractCommand#setRequiredRoles(Long...)
	 */
	default void onInvalidRole(InvalidRoleEvent event) {}

	// TODO: docs
	default void onCommandCooldown(CommandCooldownEvent event) {}
}

