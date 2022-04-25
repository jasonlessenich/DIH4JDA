package com.dynxsty.dih4jda;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.event.Level;

import java.util.Set;

//TODO-v1.4: Documentation
public class DIH4JDALogger {

	public enum Type {
		INFO,
		WARN,
		ERROR,
		DEBUG,
		TRACE,
		COMMANDS_QUEUED,
		SLASH_COMMAND_REGISTERED,
		CONTEXT_COMMAND_REGISTERED,
		COMMAND_PRIVILEGE_REGISTERED,
		SMART_QUEUE
	}

	private static final org.slf4j.Logger log = JDALogger.getLog(DIH4JDALogger.class);

	protected static Set<Type> blockedLogTypes;
	private static void log(String msg, Type type, Level level) {
		if (blockedLogTypes.contains(type)) return;
		switch (level) {
			case INFO: log.info(msg); break;
			case WARN: log.warn(msg); break;
			case ERROR: log.error(msg); break;
			case DEBUG: log.debug(msg); break;
			case TRACE: log.trace(msg); break;
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
}
