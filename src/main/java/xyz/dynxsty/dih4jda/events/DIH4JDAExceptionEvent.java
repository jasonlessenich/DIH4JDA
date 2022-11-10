package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;

public abstract class DIH4JDAExceptionEvent<I extends Interaction> extends DIH4JDAEvent<I> {

	private final Throwable throwable;

	protected DIH4JDAExceptionEvent(@NotNull String eventName, @NotNull DIH4JDA dih4jda, I interaction, Throwable throwable) {
		super(eventName, dih4jda, interaction);
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
