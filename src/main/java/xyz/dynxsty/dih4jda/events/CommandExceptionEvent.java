package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

public class CommandExceptionEvent extends ThrowableDIH4JDAEvent<CommandInteraction> {
	public CommandExceptionEvent(DIH4JDA dih4jda, CommandInteraction interaction, Throwable throwable) {
		super("onCommandException", dih4jda, interaction, throwable);
	}
}
