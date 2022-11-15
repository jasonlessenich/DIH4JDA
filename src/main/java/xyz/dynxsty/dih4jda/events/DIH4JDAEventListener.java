package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.annotation.Nonnull;
import java.time.Duration;

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
	default void onCommandException(@Nonnull CommandExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while interacting with a message component.
	 *
	 * @param event The {@link ComponentExceptionEvent} that was fired.
	 */
	default void onComponentException(@Nonnull ComponentExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while handling an autocomplete interaction.
	 *
	 * @param event The {@link AutoCompleteExceptionEvent} that was fired.
	 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
	 */
	default void onAutoCompleteException(@Nonnull AutoCompleteExceptionEvent event) {}

	/**
	 * An event that gets fired when an exception gets raised while handling a modal interaction.
	 *
	 * @param event The {@link ModalExceptionEvent} that was fired.
	 */
	default void onModalException(@Nonnull ModalExceptionEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, does NOT have one of the required permissions.
	 *
	 * @param event The {@link InsufficientPermissionsEvent} that was fired.
	 * @see RestrictedCommand#setRequiredPermissions(Permission...)
	 */
	default void onInsufficientPermissions(@Nonnull InsufficientPermissionsEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
	 *
	 * @param event The {@link InvalidUserEvent} that was fired.
	 * @see RestrictedCommand#setRequiredUsers(Long...)
	 */
	default void onInvalidUser(@Nonnull InvalidUserEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
	 *
	 * @param event The {@link InvalidRoleEvent} that was fired.
	 * @see RestrictedCommand#setRequiredRoles(Long...)
	 */
	default void onInvalidRole(@Nonnull InvalidRoleEvent event) {}

	/**
	 * An event that gets fired when the command is NOT executed in one of the required guild.
	 *
	 * @param event the provided {@link InvalidGuildEvent} instance.
	 * @see RestrictedCommand#setRequiredGuilds(Long...)
	 */
	default void onInvalidGuild(@Nonnull InvalidGuildEvent event) {}

	/**
	 * An event that gets fired when the user, which invoked the command, is not yet able to use this command due to
	 * a specified {@link RestrictedCommand#setCommandCooldown(Duration) Command Cooldown}<br>
	 *
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param event The {@link CommandCooldownEvent} that was fired.
	 * @see RestrictedCommand#setCommandCooldown(Duration)
	 */
	default void onCommandCooldown(@Nonnull CommandCooldownEvent event) {}
}

