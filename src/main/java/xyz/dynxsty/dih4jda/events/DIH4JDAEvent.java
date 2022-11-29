package xyz.dynxsty.dih4jda.events;

import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * A generic event, which holds the events' name and the {@link DIH4JDA} instance.
 */
public abstract class DIH4JDAEvent {
	private final String eventName;
	private final DIH4JDA dih4jda;

	protected DIH4JDAEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda) {
		this.eventName = eventName;
		this.dih4jda = dih4jda;
	}

	/**
	 * Fires an event from the {@link DIH4JDAEventListener}.
	 *
	 * @param event The {@link DIH4JDAEvent} to fire.
	 * @since v1.5
	 */
	public static void fire(@Nonnull DIH4JDAEvent event) {
		if (event.getDIH4JDA().getEventListeners().isEmpty()) {
			DIH4JDALogger.warn(DIH4JDALogger.Type.EVENT_MISSING_HANDLER, "%s was fired, but not handled (No listener registered)", event.getEventName());
			if (event instanceof DIH4JDAThrowableEvent && event.getDIH4JDA().getConfig().isDefaultPrintStacktrace()) {
				((DIH4JDAThrowableEvent<?>) event).getThrowable().printStackTrace();
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
	@Nonnull
	public String getEventName() {
		return eventName;
	}

	/**
	 * The {@link DIH4JDA} instance that fired the event.
	 *
	 * @return The {@link DIH4JDA} instance.
	 */
	@Nonnull
	public DIH4JDA getDIH4JDA() {
		return dih4jda;
	}
}
