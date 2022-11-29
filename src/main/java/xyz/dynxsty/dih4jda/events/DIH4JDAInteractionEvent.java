package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.Interaction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;

/**
 * A generic event, which holds the events' name, the {@link DIH4JDA} instance and the {@link I follow-up interaction}.
 *
 * @param <I> The follow-up {@link Interaction}
 */
public abstract class DIH4JDAInteractionEvent<I extends Interaction> extends DIH4JDAEvent {
	private final I interaction;

	protected DIH4JDAInteractionEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, @Nonnull I interaction) {
		super(eventName, dih4jda);
		this.interaction = interaction;
	}

	/**
	 * The follow-up interaction that was defined by instantiating the class.
	 *
	 * @return The follow-up interaction of this event.
	 */
	@Nonnull
	public I getInteraction() {
		return interaction;
	}
}
