package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

/**
 * An event that gets fired when an exception gets raised while interacting with a message component.
 */
public class ComponentExceptionEvent extends ThrowableDIH4JDAEvent<ComponentInteraction> {
	public ComponentExceptionEvent(DIH4JDA dih4jda, ComponentInteraction interaction, Throwable throwable) {
		super("onComponentException", dih4jda, interaction, throwable);
	}
}
