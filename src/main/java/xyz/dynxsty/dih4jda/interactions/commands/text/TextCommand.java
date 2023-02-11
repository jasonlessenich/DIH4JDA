package xyz.dynxsty.dih4jda.interactions.commands.text;

import xyz.dynxsty.dih4jda.events.text.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import java.util.List;

public abstract class TextCommand extends RestrictedCommand implements ExecutableCommand<TextCommandEvent> {
	private String name;
	private String description;
	private String category;
	private String[] aliases = new String[]{};
	private TextOptionData[] options = new TextOptionData[]{};

	// TODO: Docs
	public String getName() {
		return name;
	}

	// TODO: Docs
	public void setName(String name) {
		this.name = name;
	}

	// TODO: Docs
	public String getDescription() {
		return description;
	}

	// TODO: Docs
	public void setDescription(String description) {
		this.description = description;
	}

	// TODO: Docs
	public String getCategory() {
		return category;
	}

	// TODO: Docs
	public void setCategory(String category) {
		this.category = category;
	}

	// TODO: Docs
	public void setAliases(String... aliases) {
		this.aliases = aliases;
	}

	// TODO: Docs
	public String[] getAliases() {
		return aliases;
	}

	// TODO: Docs
	public void setOptions(TextOptionData... options) {
		this.options = options;
	}

	// TODO: Docs
	public List<TextOptionData> getOptions() {
		return List.of(options);
	}
}
