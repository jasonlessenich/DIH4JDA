package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.event.Level;
import xyz.dynxsty.dih4jda.util.ArrayUtil;

import javax.annotation.Nonnull;

/**
 * This handler's own Logging System.
 * <br> To disable certain {@link DIH4JDALogger.Type}s, simple use {@link DIH4JDABuilder#disableLogging(Type...)}.
 */
public class DIH4JDALogger {
	/**
	 * Creates a new, default instance of the {@link DIH4JDALogger}.
	 */
	public DIH4JDALogger() {}

	private static final org.slf4j.Logger log = JDALogger.getLog(DIH4JDALogger.class);
	protected static Type[] blockedLogTypes = new Type[]{};

	private static void log0(@Nonnull String msg, @Nonnull Type type, @Nonnull Level level) {
		if (ArrayUtil.contains(blockedLogTypes, type)) return;
		switch (level) {
			case INFO:
				log.info(msg);
				break;
			case WARN:
				log.warn(msg);
				break;
			case ERROR:
				log.error(msg);
				break;
			case DEBUG:
				log.debug(msg);
				break;
			case TRACE:
				log.trace(msg);
				break;
		}
	}

	/**
	 * Logs a message with the specified {@link DIH4JDALogger.Type} and {@link Level#INFO}.
	 * @param type The {@link DIH4JDALogger.Type} of the message.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void info(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.INFO);
	}

	/**
	 * Logs a message with the specified {@link Level#INFO}.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void info(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.INFO, Level.INFO);
	}

	/**
	 * Logs a message with the specified {@link DIH4JDALogger.Type} and {@link Level#WARN}.
	 * @param type The {@link DIH4JDALogger.Type} of the message.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void warn(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.WARN);
	}

	/**
	 * Logs a message with the specified {@link Level#WARN}.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void warn(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.WARN, Level.WARN);
	}

	/**
	 * Logs a message with the specified {@link DIH4JDALogger.Type} and {@link Level#ERROR}.
	 * @param type The {@link DIH4JDALogger.Type} of the message.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void error(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.ERROR);
	}

	/**
	 * Logs a message with the specified {@link Level#ERROR}.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void error(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.ERROR, Level.ERROR);
	}

	/**
	 * Logs a message with the specified {@link DIH4JDALogger.Type} and {@link Level#DEBUG}.
	 * @param type The {@link DIH4JDALogger.Type} of the message.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void debug(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.DEBUG);
	}

	/**
	 * Logs a message with the specified {@link Level#DEBUG}.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void debug(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.DEBUG, Level.DEBUG);
	}

	/**
	 * Logs a message with the specified {@link DIH4JDALogger.Type} and {@link Level#TRACE}.
	 * @param type The {@link DIH4JDALogger.Type} of the message.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void trace(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.TRACE);
	}

	/**
	 * Logs a message with the specified {@link Level#TRACE}.
	 * @param msg The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void trace(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.TRACE, Level.TRACE);
	}

	/**
	 * All types that are supported by the {@link DIH4JDALogger}.
	 */
	public enum Type {
		/**
		 * Use this if you want to inform the user.
		 */
		INFO,
		/**
		 * Use this if you want to warn the user.
		 */
		WARN,
		/**
		 * Use this if an error occurred.
		 */
		ERROR,
		/**
		 * Use this if you want to debug something/debug information.
		 */
		DEBUG,
		/**
		 * Use this if you want to trace something down.
		 */
		TRACE,
		/**
		 * Something related to queueing commands.
		 */
		COMMANDS_QUEUED,
		/**
		 * Use this if a slash command was registered.
		 */
		SLASH_COMMAND_REGISTERED,
		/**
		 * Use this if a slash command was skipped.<br>
		 * For example if the command should not be registered on a specific guild.
		 */
		SLASH_COMMAND_SKIPPED,
		/**
		 * Use this if a context command was registered.
		 */
		CONTEXT_COMMAND_REGISTERED,
		/**
		 * Use this if a context command was skipped.<br>
		 * For example if the command should not be registered on a specific guild.
		 */
		CONTEXT_COMMAND_SKIPPED,
		/**
		 * Used together with {@link SmartQueue}.
		 */
		SMART_QUEUE,
		/**
		 * If a command got ignored by the {@link SmartQueue}.
		 */
		SMART_QUEUE_IGNORED,
		/**
		 * If a command got deleted by the {@link SmartQueue}.
		 */
		SMART_QUEUE_DELETED_UNKNOWN,
		/**
		 * If a command got ignored by the {@link SmartQueue} because it was unknown.
		 */
		SMART_QUEUE_IGNORED_UNKNOWN,
		/**
		 * If a button could not be found.
		 */
		BUTTON_NOT_FOUND,
		/**
		 * If a select menu could not be found.
		 */
		SELECT_MENU_NOT_FOUND,
		/**
		 * If a modal could not be found.
		 */
		MODAL_NOT_FOUND,
		/**
		 * If an implementation for {@link xyz.dynxsty.dih4jda.events.DIH4JDAEventListener} is missing.
		 */
		EVENT_MISSING_HANDLER
	}
}
