package com.dynxsty.dih4jda.interactions.commands.slash_command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Interface that must be implemented for all executable (Base- & Subcommands) Slash Commands.
 *
 * <pre>{@code
 * public class PingCommand extends GuildSlashCommand implements SlashCommand {
 *
 *     public PingCommand(Guild guild) {
 *         setCommandData(Commands.slash("ping", "Ping!"));
 *     }
 *
 *    @Override
 *    public void handleSlashCommand(SlashCommandInteractionEvent event) {
 * 		event.reply("Pong!").queue();
 *    }
 *
 * }
 * }</pre>
 */
public interface SlashCommand {
	void handleSlashCommand(SlashCommandInteractionEvent event);
}