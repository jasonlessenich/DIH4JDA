package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.event.Level;
import xyz.dynxsty.dih4jda.util.ArrayUtil;

import javax.annotation.Nonnull;

/**
 * This handler's own Logging System.
 * <br> To disable certain {@link DIH4JDALogger.Type}s, simple use {@link DIH4JDABuilder#disableLogging(Type...)}.
 */
//TODO java docs
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

	public static void info(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.INFO);
	}

	public static void info(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.INFO, Level.INFO);
	}

	public static void warn(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.WARN);
	}

	public static void warn(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.WARN, Level.WARN);
	}

	public static void error(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.ERROR);
	}

	public static void error(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.ERROR, Level.ERROR);
	}

	public static void debug(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.DEBUG);
	}

	public static void debug(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.DEBUG, Level.DEBUG);
	}

	public static void trace(@Nonnull Type type, @Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), type, Level.TRACE);
	}

	public static void trace(@Nonnull String msg, @Nonnull Object... args) {
		log0(String.format(msg, args), Type.TRACE, Level.TRACE);
	}

	/**
	 * All types that are supported by the {@link DIH4JDALogger}.
	 */
	public enum Type {
		INFO,
		WARN,
		ERROR,
		DEBUG,
		TRACE,
		COMMANDS_QUEUED,
		SLASH_COMMAND_REGISTERED,
		SLASH_COMMAND_SKIPPED,
		CONTEXT_COMMAND_REGISTERED,
		CONTEXT_COMMAND_SKIPPED,
		SMART_QUEUE,
		SMART_QUEUE_IGNORED,
		SMART_QUEUE_DELETED_UNKNOWN,
		SMART_QUEUE_IGNORED_UNKNOWN,
		BUTTON_NOT_FOUND,
		SELECT_MENU_NOT_FOUND,
		MODAL_NOT_FOUND,
		EVENT_MISSING_HANDLER,
		INVALID_TEXT_COMMAND
	}
}
