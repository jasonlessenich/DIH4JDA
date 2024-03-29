package xyz.dynxsty.dih4jda.interactions.commands.application;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;

/**
 * An extension of {@link ApplicationCommand} which contains a {@link RegistrationType} and {@link BaseApplicationCommand#queueableGuilds}.
 * This abstract class is meant for top-level commands, which can be queued either per-guild or globally.
 *
 * @param <E> The event this class uses.
 * @param <T> The type of {@link net.dv8tion.jda.api.interactions.commands.build.CommandData} this class uses.
 */
public abstract class BaseApplicationCommand<E extends GenericCommandInteractionEvent, T> extends ApplicationCommand<E, T> {

    private RegistrationType registrationType = DIH4JDA.getDefaultRegistrationType();
    private Long[] queueableGuilds = new Long[]{};

    /**
     * Creates a default {@link BaseApplicationCommand}.
     */
    public BaseApplicationCommand() {}

    /**
     * The {@link RegistrationType} the command got assigned.
     *
     * @return the {@link RegistrationType}.
     */
    @Nonnull
    public final RegistrationType getRegistrationType() {
        return registrationType;
    }

    /**
     * How the command should be queued. This DOES NOT work with {@link SlashCommand.Subcommand}!
     *
     * @param type the {@link RegistrationType} to set.
     */
    public final void setRegistrationType(@Nonnull RegistrationType type) {
        this.registrationType = type;
    }

	/**
	 * Limits this command to only be queued in the specified guilds. Leave this blank (or null) if the command
	 * should be queued everywhere.
	 *
	 * @param queueableGuilds The guild ids, as a {@link Long} array.
	 */
    public void setQueueableGuilds(@Nonnull Long... queueableGuilds) {
        this.queueableGuilds = queueableGuilds;
    }

    /**
     * Gets all the guilds that the command should be registered/queued in.
     *
     * @return the guild ids as an array of {@link Long}s.
     */
    @Nonnull
    public Long[] getQueueableGuilds() {
        return queueableGuilds;
    }
}
