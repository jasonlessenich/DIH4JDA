package xyz.dynxsty.dih4jda.events.text;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.events.DIH4JDAMessageEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;

// TODO: Docs
public class InvalidOptionsEvent extends DIH4JDAMessageEvent {
	private final TextCommand textCommand;

	public InvalidOptionsEvent(@NotNull String eventName, @NotNull DIH4JDA dih4jda, MessageReceivedEvent event, TextCommand command) {
		super(eventName, dih4jda, event);
		this.textCommand = command;
	}

	public TextCommand getTextCommand() {
		return textCommand;
	}
}
