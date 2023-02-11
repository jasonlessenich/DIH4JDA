package xyz.dynxsty.dih4jda.events.text;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.events.DIH4JDAMessageEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionMapping;

import java.util.List;
import java.util.function.Function;

// TODO: Docs
public class HelpTextCommandEvent extends DIH4JDAMessageEvent {

	public HelpTextCommandEvent(@NotNull DIH4JDA dih4jda, MessageReceivedEvent event) {
		super("onHelpTextCommand", dih4jda, event);
	}
}
