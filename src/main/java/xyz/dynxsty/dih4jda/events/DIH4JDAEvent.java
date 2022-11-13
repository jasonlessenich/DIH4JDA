package xyz.dynxsty.dih4jda.events;

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
	private final String eventName;
	private final DIH4JDA dih4jda;
	private final I interaction;

	protected DIH4JDAEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, I interaction) {
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
		if (event.getDIH4JDA().getEventListeners().isEmpty()) {
			DIH4JDALogger.warn(DIH4JDALogger.Type.EVENT_MISSING_HANDLER, "%s was fired, but not handled (No listener registered)", event.getEventName());
			if (event instanceof DIH4JDAThrowableEvent && event.getDIH4JDA().getConfig().isDefaultPrintStacktrace()) {
				((DIH4JDAThrowableEvent<I>) event).getThrowable().printStackTrace();
			}
		}
		for (DIH4JDAEventListener listener : event.getDIH4JDA().getEventListeners()) {
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

	/**
	 * The internal name of the event.
	 *
	 * @return The internal event name.
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * The {@link DIH4JDA} instance that fired the event.
	 *
	 * @return The {@link DIH4JDA} instance.
	 */
	public DIH4JDA getDIH4JDA() {
		return dih4jda;
	}

	/**
	 * The follow-up interaction that was defined by instantiating the class.
	 *
	 * @return The follow-up interaction of this event.
	 */
	public I getInteraction() {
		return interaction;
	}
}
