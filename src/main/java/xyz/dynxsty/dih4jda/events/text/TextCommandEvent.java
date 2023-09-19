package xyz.dynxsty.dih4jda.events.text;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.events.DIH4JDAMessageEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionMapping;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

// TODO: Docs
public class TextCommandEvent extends DIH4JDAMessageEvent {

	private final TextCommand textCommand;
	private final List<TextOptionMapping> mappings;

	public TextCommandEvent(@NotNull DIH4JDA dih4jda, MessageReceivedEvent event, TextCommand command, List<TextOptionMapping> mappings) {
		super("onTextCommand", dih4jda, event);
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

	public <T> T getOption(String name, T fallback, Function<TextOptionMapping, T> resolver) {
		final TextOptionMapping mapping = getOption(name);
		if (mapping == null) {
			return fallback;
		}
		return resolver.apply(mapping);
	}

	public <T> T getOption(String name, Function<TextOptionMapping, T> resolver) {
		return getOption(name, null, resolver);
	}
}
