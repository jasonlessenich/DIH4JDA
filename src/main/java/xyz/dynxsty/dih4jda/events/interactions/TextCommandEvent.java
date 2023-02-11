package xyz.dynxsty.dih4jda.events.interactions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.events.DIH4JDAMessageEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionMapping;

import java.util.List;

// TODO: Docs
public class TextCommandEvent extends DIH4JDAMessageEvent {

	private final TextCommand textCommand;
	private final List<TextOptionMapping> mappings;

	public TextCommandEvent(@NotNull String eventName, @NotNull DIH4JDA dih4jda, MessageReceivedEvent event, TextCommand command, List<TextOptionMapping> mappings) {
		super(eventName, dih4jda, event);
		this.textCommand = command;
		this.mappings = mappings;
	}

	// TODO: Docs
	public TextCommand getTextCommand() {
		return textCommand;
	}

	// TODO: Docs
	public String[] getSplit() {
		return getMessage().getContentRaw().split("\\s+");
	}

	public TextOptionMapping getOption(String name) {
		return mappings.stream()
				.filter(m -> m.getOptionData().getName().equals(name))
				.findFirst().orElse(null);
	}
}
