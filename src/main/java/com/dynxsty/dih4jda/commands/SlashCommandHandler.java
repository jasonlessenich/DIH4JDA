package com.dynxsty.dih4jda.commands;

import com.dynxsty.dih4jda.SlashCommandType;
import com.dynxsty.dih4jda.commands.dto.SlashCommand;
import com.dynxsty.dih4jda.commands.dto.SlashSubcommand;
import com.dynxsty.dih4jda.commands.dto.SlashSubcommandGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SlashCommandHandler extends ListenerAdapter {

	private final String commandsPackage;
	private final HashMap<String, SlashCommandInteraction> slashCommandIndex;

	private final List<Class<? extends SlashCommand>> guild;
	private final List<Class<? extends SlashCommand>> global;

	private final org.slf4j.Logger log = JDALogger.getLog(this.getClass());

	/**
	 * Constructs a new {@link SlashCommandHandler} from the supplied commands package.
	 *
	 * @param commandsPackage The package that houses the command classes.
	 */
	public SlashCommandHandler(String commandsPackage) {
		this.guild = new ArrayList<>();
		this.global = new ArrayList<>();
		this.slashCommandIndex = new HashMap<>();
		this.commandsPackage = commandsPackage;
	}

	/**
	 * Registers all slash commands. Loops through all classes found in the commands package that is a subclass of {@link SlashCommand}.
	 * Goes through these steps with every iteration;
	 * <ol>
	 *     <li>Checks if the class is missing {@link CommandData} and doesn't register if it is.</li>
	 *     <li>Checks if the class is neither a subclass of {@link SlashSubcommand} nor {@link SlashSubcommandGroup} and registers it as regular command.</li>
	 *     <li>Checks if the class is a subclass of {@link SlashSubcommandGroup} if it is, the SlashCommandGroup is validated and another loop is fired following the two steps above for the group's sub commands.</li>
	 *     <li>Checks if the class is a subclass of {@link SlashSubcommand}, if it is, it is registered as a sub command.</li>
	 * </ol>
	 *
	 * @param jda The {@link JDA} instance.
	 * @throws Exception if anything goes wrong.
	 */
	public void registerSlashCommands(JDA jda) throws Exception {
		Reflections commands = new Reflections(this.commandsPackage);
		Set<Class<? extends SlashCommand>> classes = commands.getSubTypesOf(SlashCommand.class);
		for (var c : classes) {
			var instance = c.getConstructor().newInstance();
			if (instance.getType() == null) {
				log.warn(String.format("Class %s is missing a Command Type. It will be ignored.", c.getName()));
				continue;
			}
			switch (instance.getType()) {
				case GLOBAL -> global.add(c);
				case GUILD -> guild.add(c);
			}
		}
		if (!this.guild.isEmpty()) {
			for (var guild : jda.getGuilds()) {
				registerGuildCommand(guild);
			}
		}
		if (!this.global.isEmpty()) {
			registerGlobalCommand(jda);
		}
	}

	/**
	 * Registers a single Guild Command.
	 * @param guild The command's guild.
	 * @throws Exception If an error occurs.
	 */
	private void registerGuildCommand(@NotNull Guild guild) throws Exception {
		var updateAction = guild.updateCommands();
		for (var slashCommandClass : this.guild) {
			var instance = (SlashCommand) this.getClassInstance(SlashCommandType.GUILD, guild, slashCommandClass);
			updateAction = registerCommand(updateAction, instance, slashCommandClass, guild);
		}
		log.info(String.format("[%s] Queuing Guild SlashCommands", guild.getName()));
		updateAction.queue();
	}

	/**
	 * Registers a single Global Command.
	 * @throws Exception If an error occurs.
	 */
	private void registerGlobalCommand(@NotNull JDA jda) throws Exception {
		var updateAction = jda.updateCommands();
		for (var slashCommandClass : this.global) {
			var instance = slashCommandClass.getConstructor().newInstance();
			updateAction = this.registerCommand(updateAction, instance, slashCommandClass, null);
		}
		log.info("[*] Queuing Global SlashCommands");
		updateAction.queue();
	}

	/**
	 * Registers a single Command.
	 * @param action The {@link CommandListUpdateAction}.
	 * @param command The base command's instance.
	 * @param commandClass The base command's class.
	 * @param guild The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private CommandListUpdateAction registerCommand(CommandListUpdateAction action, @NotNull SlashCommand command, Class<? extends SlashCommand> commandClass, @Nullable Guild guild) throws Exception{
		if (command.getCommandData() == null) {
			log.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		SlashCommandData commandData = command.getCommandData();
		if (command.getSubcommandGroupClasses() != null) {
			commandData = registerSubcommandGroup(command, command.getSubcommandGroupClasses(), guild);
		} else if (command.getSubcommandClasses() != null) {
			commandData = registerSubcommand(command, command.getSubcommandClasses(), guild);
		} else {
			slashCommandIndex.put(getFullCommandName(commandData.getName(), null, null),
					new SlashCommandInteraction((ISlashCommand) command, command.getCommandPrivileges()));
			log.info(String.format("[*] Registered command: /%s", command.getCommandData().getName()));
		}
		action.addCommands(commandData);
		return action;
	}

	/**
	 * Registers a single Command Group.
	 * @param command The base command's instance.
	 * @param groupClasses All slash command group classes.
	 * @param guild The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SlashCommandData registerSubcommandGroup(@NotNull SlashCommand command, Class<? extends SlashSubcommandGroup> @NotNull [] groupClasses, @Nullable Guild guild) throws Exception {
		var data = command.getCommandData();
		for (var group : groupClasses) {
			var instance = (SlashSubcommandGroup) this.getClassInstance(command.getType(), guild, group);
			if (instance.getSubcommandGroupData() == null) {
				log.warn(String.format("Class %s is missing SubcommandGroupData. It will be ignored.", group.getName()));
				continue;
			}
			if (instance.getSubcommandClasses() == null) {
				log.warn(String.format("SubcommandGroup %s is missing Subcommands. It will be ignored.", instance.getSubcommandGroupData().getName()));
				continue;
			}
			SubcommandGroupData groupData = registerSubcommand(command, instance.getSubcommandGroupData(), instance.getSubcommandClasses(), guild);
			data.addSubcommandGroups(groupData);
		}
		return data;
	}

	/**
	 * Registers a single Sub Command for a Subcommand Group.
	 * @param command The base command's instance.
	 * @param data The subcommand group's data.
	 * @param subClasses All sub command classes.
	 * @param guild The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SubcommandGroupData registerSubcommand(SlashCommand command, SubcommandGroupData data, Class<? extends SlashSubcommand> @NotNull [] subClasses, @Nullable Guild guild) throws Exception {
		for (var sub : subClasses) {
			var instance = (SlashSubcommand) this.getClassInstance(command.getType(), guild, sub);
			if (instance.getSubCommandData() == null) {
				log.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			slashCommandIndex.put(getFullCommandName(command.getCommandData().getName(), data.getName(), instance.getSubCommandData().getName()),
					new SlashCommandInteraction((ISlashCommand) instance, command.getCommandPrivileges()));
			log.info(String.format("[*] Registered command: /%s", getFullCommandName(command.getCommandData().getName(), data.getName(), instance.getSubCommandData().getName())));
			data.addSubcommands(instance.getSubCommandData());
		}
		return data;
	}

	/**
	 * Registers a single Sub Command.
	 * @param command The base command's instance.
	 * @param subClasses All sub command classes.
	 * @param guild The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SlashCommandData registerSubcommand(@NotNull SlashCommand command, Class<? extends SlashSubcommand> @NotNull [] subClasses, @Nullable Guild guild) throws Exception {
		var data = command.getCommandData();
		for (var sub : subClasses) {
			var instance = (SlashSubcommand) this.getClassInstance(command.getType(), guild, sub);
			if (instance.getSubCommandData() == null) {
				log.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			slashCommandIndex.put(getFullCommandName(data.getName(), data.getName(), instance.getSubCommandData().getName()),
					new SlashCommandInteraction((ISlashCommand) instance, command.getCommandPrivileges()));
			log.info(String.format("[*] Registered command: /%s %s", data.getName(), instance.getSubCommandData().getName()));
			data.addSubcommands(instance.getSubCommandData());
		}
		return data;
	}

	/**
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleCommand(SlashCommandInteractionEvent event) {
		try {
			var command = slashCommandIndex.get(getFullCommandName(event.getName(), event.getSubcommandGroup(), event.getSubcommandName()));
			command.handler().handleSlashCommandInteraction(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @param first  The SlashCommand's name.
	 * @param second The SlashSubCommandGroup's name.
	 * @param third  The SlashSubCommand's name.
	 * @return One combined string.
	 */
	@Contract(pure = true)
	private @NotNull String getFullCommandName(String first, String second, String third) {
		return String.format("%s %s %s", first, second, third);
	}

	/**
	 * Creates a new Instance of the given class.
	 * @param type The slash command's type.
	 * @param guild The slash command's guild. (only available for {@link SlashCommandType#GUILD})
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws Exception If an error occurs.
	 */
	private Object getClassInstance(SlashCommandType type, Guild guild, Class<?> clazz) throws Exception {
		if (type != SlashCommandType.GLOBAL && guild != null) {
			try {
				return clazz.getConstructor(Guild.class).newInstance(guild);
			} catch (NoSuchMethodException ignored) {}
		}
		return clazz.getConstructor().newInstance();
	}

	/**
	 * Fired if Discord reports a {@link SlashCommandInteractionEvent}.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		CompletableFuture.runAsync(() -> handleCommand(event));
	}
}
