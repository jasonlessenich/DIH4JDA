package xyz.dynxsty.dih4jda.interactions.commands.application;

/**
 * Whether the command should be queued as a global- or as a guild command.
 * <a href="https://discord.com/developers/docs/interactions/application-commands">(Read more)</a>
 *
 */
public enum RegistrationType {
	/**
	 * Marks a command as a global command.
	 */
	GLOBAL,
	/**
	 * Marks a command as a guild only command.
	 */
	GUILD
}
