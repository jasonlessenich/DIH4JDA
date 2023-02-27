package xyz.dynxsty.dih4jda.events;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.Interaction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * A generic event, which holds the events' name, the {@link DIH4JDA} instance and the {@link I follow-up interaction}.
 *
 * @param <I> The follow-up {@link Interaction}
 */
public abstract class DIH4JDAEvent<I extends Interaction> {

	@Getter
	private final String eventName;
	@Getter
	private final DIH4JDA dih4jda;
	@Getter
	private final I interaction;

	protected DIH4JDAEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, @Nonnull I interaction) {
		this.eventName = eventName;
		this.dih4jda = dih4jda;
		this.interaction = interaction;
	}

	/**
	 * Fires an event from the {@link DIH4JDAEventListener}.
	 *
	 * @param event The {@link DIH4JDAEvent} to fire.
	 * @param <I> The follow-up {@link Interaction}
	 * @since v1.5
	 */
	public static <I extends Interaction> void fire(@Nonnull DIH4JDAEvent<I> event) {
		if (event.getDih4jda().getEventListeners().isEmpty()) {
			DIH4JDALogger.warn(DIH4JDALogger.Type.EVENT_MISSING_HANDLER, "%s was fired, but not handled (No listener registered)", event.getEventName());
			if (event instanceof DIH4JDAThrowableEvent && event.getDih4jda().getConfig().isDefaultPrintStacktrace()) {
				((DIH4JDAThrowableEvent<I>) event).getThrowable().printStackTrace();
			}
		}
		for (DIH4JDAEventListener listener : event.getDih4jda().getEventListeners()) {
			try {
				for (Method method : listener.getClass().getMethods()) {
					if (method.getName().equals(event.getEventName())) {
						method.invoke(listener.getClass().getConstructor().newInstance(), event);
					}
				}
			} catch (ReflectiveOperationException e) {
				DIH4JDALogger.error(e.getMessage());
			}
		}
	}
}
