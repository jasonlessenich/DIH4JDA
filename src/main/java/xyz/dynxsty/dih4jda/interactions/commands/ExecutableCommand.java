package xyz.dynxsty.dih4jda.interactions.commands;

// TODO: Docs
public interface ExecutableCommand<E> {
	// TODO: Docs
	void execute(E event);

	// TODO: Docs
	SlashCommand getSlashCommand();
}
