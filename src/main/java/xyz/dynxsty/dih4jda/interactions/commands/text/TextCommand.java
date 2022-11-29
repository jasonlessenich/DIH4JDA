package xyz.dynxsty.dih4jda.interactions.commands.text;

import xyz.dynxsty.dih4jda.events.interactions.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

public abstract class TextCommand extends RestrictedCommand implements ExecutableCommand<TextCommandEvent> {
	private String name;
	private String description;
	private String category;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
