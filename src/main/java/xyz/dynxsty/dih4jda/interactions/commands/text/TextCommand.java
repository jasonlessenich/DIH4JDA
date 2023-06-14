package xyz.dynxsty.dih4jda.interactions.commands.text;

import xyz.dynxsty.dih4jda.events.text.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

public abstract class TextCommand extends RestrictedCommand implements ExecutableCommand<TextCommandEvent> {
	private TextCommandData commandData;

	public void setCommandData(TextCommandData commandData) {
		this.commandData = commandData;
	}

	public TextCommandData getCommandData() {
		return commandData;
	}
}
