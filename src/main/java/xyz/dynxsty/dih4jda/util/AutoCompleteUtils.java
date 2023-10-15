package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Utility class that contains some useful methods regarding the AutoComplete
 * system.
 *
 * @since v1.4
 */
public class AutoCompleteUtils {
	private AutoCompleteUtils() {
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices(event, choices));
	 * }</pre>
	 *
	 * @param event   The {@link CommandAutoCompleteInteractionEvent} that was
	 *                fired.
	 * @param choices A {@link List} of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	@Nonnull
	public static List<Command.Choice> filterChoices(@Nonnull CommandAutoCompleteInteractionEvent event,
			@Nonnull List<Command.Choice> choices) {
		return filterChoices(event.getFocusedOption().getValue().toLowerCase(Locale.ROOT), choices);
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices(event, choices));
	 * }</pre>
	 *
	 * @param event   The {@link CommandAutoCompleteInteractionEvent} that was
	 *                fired.
	 * @param choices An array of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	@Nonnull
	public static List<Command.Choice> filterChoices(@Nonnull CommandAutoCompleteInteractionEvent event,
			@Nonnull Command.Choice... choices) {
		return filterChoices(event.getFocusedOption().getValue().toLowerCase(Locale.ROOT), List.of(choices));
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices("abc", choices));
	 * }</pre>
	 *
	 * @param filter  The filter.
	 * @param choices A {@link List} of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	@Nonnull
	public static List<Command.Choice> filterChoices(@Nonnull String filter, @Nonnull List<Command.Choice> choices) {
		return choices
				.stream()
				.filter(choice -> choice.getName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)))
				.limit(OptionData.MAX_CHOICES)
				.collect(Collectors.toList());
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices("abc", choices));
	 * }</pre>
	 *
	 * @param filter  The filter.
	 * @param choices A {@link List} of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	@Nonnull
	public static List<Command.Choice> filterChoices(@Nonnull String filter, @Nonnull Command.Choice... choices) {
		return filterChoices(filter, List.of(choices));
	}
}
