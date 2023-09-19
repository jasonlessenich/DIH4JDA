package xyz.dynxsty.dih4jda.events;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.Interaction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;

/**
 * Further abstraction of the {@link DIH4JDAInteractionEvent} class, which features {@link Throwable}s.
 *
 * @param <I> The follow-up interaction for this event.
 */
public abstract class DIH4JDAThrowableEvent<I extends Interaction> extends DIH4JDAInteractionEvent<I> {

	@Getter
	private final Throwable throwable;

	protected DIH4JDAThrowableEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, @Nonnull I interaction,
									@Nonnull Throwable throwable) {
		super(eventName, dih4jda, interaction);
		this.throwable = throwable;
	}
}
