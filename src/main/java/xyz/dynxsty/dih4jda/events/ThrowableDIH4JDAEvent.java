package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.Interaction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;

/**
 * Further abstraction of the {@link GenericDIH4JDAEvent} class, which features {@link Throwable}s.
 *
 * @param <I> The follow-up interaction for this event.
 */
public abstract class ThrowableDIH4JDAEvent<I extends Interaction> extends GenericDIH4JDAEvent<I> {

	private final Throwable throwable;

	protected ThrowableDIH4JDAEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, @Nonnull I interaction, @Nonnull Throwable throwable) {
		super(eventName, dih4jda, interaction);
		this.throwable = throwable;
	}

	/**
	 * @return The {@link Throwable} that was thrown.
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}
