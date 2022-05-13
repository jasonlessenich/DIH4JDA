package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;

/**
 * Interface that must be implemented for all classes that have {@link ComponentHandler#enableAutoCompleteHandling()} set.
 *
 * <pre>{@code
 * public class PingCommand extends SlashCommand implements AutoCompleteHandler {
 *
 *     public PingCommand(Guild guild) {
 *         setCommandData(Commands.slash("ping", "Ping someone").addOption(OptionType.STRING, "user-id", "The user's id", true, true));
 *         enableAutoCompleteHandling();
 *     }
 *
 *     @Override
 *     public void execute(SlashCommandInteractionEvent event) {
 *         OptionMapping mapping = event.getOption("user-id");
 *         String userId = mapping.getAsString();
 *         event.replyFormat("Ping! <@%s>", userId).queue();
 *     }
 *
 *     @Override
 *     public void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target) {
 *         if (target.getName().equals("user-id")) {
 *             List<Member> members = event.getGuild().getMembers().stream().limit(25).collect(Collectors.toList());
 *             List<Command.Choice> choices = new ArrayList<>(25);
 *             for (Member member : members) {
 *                 choices.add(new Command.Choice(member.getUser().getAsTag(), member.getId()));
 *             }
 *             event.replyChoices(AutoCompleteUtils.filterChoices(event, choices)).queue();
 *         }
 *     }
 *
 * }
 * }</pre>
 *
 * @see com.dynxsty.dih4jda.util.AutoCompleteUtils
 * @since v1.4
 */
public interface AutoCompleteHandler {
	void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target);
}
