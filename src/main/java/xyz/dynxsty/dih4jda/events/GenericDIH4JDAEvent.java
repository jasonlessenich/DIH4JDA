package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * An enum class that handles all events fired by {@link DIH4JDA}
 */
public abstract class GenericDIH4JDAEvent<I extends Interaction> {
	private final String eventName;
	private final DIH4JDA dih4jda;
	private final I interaction;

	protected GenericDIH4JDAEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, I interaction) {
		this.eventName = eventName;
		this.dih4jda = dih4jda;
		this.interaction = interaction;
	}

	public String getEventName() {
		return eventName;
	}

	public DIH4JDA getDIH4JDA() {
		return dih4jda;
	}

	public I getInteraction() {
		return interaction;
	}

	/**
	 * Fires an event from the {@link DIH4JDAEventListener}.
	 *
	 * @param event The {@link GenericDIH4JDAEvent} to fire.
	 * @since v1.5
	 */
	public static <I extends Interaction> void fire(@NotNull GenericDIH4JDAEvent<I> event) {
		if (event.getDIH4JDA().getListeners().isEmpty()) {
			DIH4JDALogger.warn(DIH4JDALogger.Type.EVENT_FIRED, "%s was fired, but not handled (No listener registered)", event.getEventName());
			if (event instanceof ThrowableDIH4JDAEvent && event.getDIH4JDA().getConfig().isDefaultPrintStacktrace()) {
				((ThrowableDIH4JDAEvent<I>) event).getThrowable().printStackTrace();
			}
		}
		for (DIH4JDAEventListener listener : event.getDIH4JDA().getListeners()) {
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
