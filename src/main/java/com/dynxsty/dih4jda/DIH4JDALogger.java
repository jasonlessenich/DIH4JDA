package com.dynxsty.dih4jda;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.event.Level;

import java.util.HashSet;
import java.util.Set;

/**
 * This handler's own Logging System.
 * <br> To disable certain {@link DIH4JDALogger.Type}s, simple use {@link DIH4JDABuilder#disableLogging(Type...)}.
 */
public class DIH4JDALogger {

	private static final org.slf4j.Logger log = JDALogger.getLog(DIH4JDALogger.class);
	protected static Set<Type> blockedLogTypes = new HashSet<>();

	private static void log(String msg, Type type, Level level) {
		if (blockedLogTypes.contains(type)) return;
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

	public static void info(String msg, Type type) {
		log(msg, type, Level.INFO);
	}

	public static void info(String msg) {
		log(msg, Type.INFO, Level.INFO);
	}

	public static void warn(String msg, Type type) {
		log(msg, type, Level.WARN);
	}

	public static void warn(String msg) {
		log(msg, Type.WARN, Level.WARN);
	}

	public static void error(String msg, Type type) {
		log(msg, type, Level.ERROR);
	}

	public static void error(String msg) {
		log(msg, Type.ERROR, Level.ERROR);
	}

	public static void debug(String msg, Type type) {
		log(msg, type, Level.DEBUG);
	}

	public static void debug(String msg) {
		log(msg, Type.DEBUG, Level.DEBUG);
	}

	public static void trace(String msg, Type type) {
		log(msg, type, Level.TRACE);
	}

	public static void trace(String msg) {
		log(msg, Type.TRACE, Level.TRACE);
	}

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
		BUTTON_NOT_FOUND,
		SELECT_MENU_NOT_FOUND,
		MODAL_NOT_FOUND,
		EVENT_FIRED
	}
}
