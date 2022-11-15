package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;


/**
 * An event that gets fired when an exception gets raised while handling a modal interaction.
 */
public class ModalExceptionEvent extends DIH4JDAThrowableEvent<ModalInteraction> {
	/**
	 * Creates a new {@link ModalExceptionEvent}.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired this event.
	 * @param interaction the {@link ModalInteraction}.
	 * @param throwable the {@link Throwable} that caused this event.
	 */
	public ModalExceptionEvent(@Nonnull DIH4JDA dih4jda, @Nonnull ModalInteraction interaction,
							   @Nonnull Throwable throwable) {
		super("onModalException", dih4jda, interaction, throwable);
	}
}
