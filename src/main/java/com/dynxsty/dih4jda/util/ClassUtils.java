package com.dynxsty.dih4jda.util;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GlobalSlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for working with classes.
 */
public class ClassUtils {

	private ClassUtils() {
	}

	public static boolean doesImplement(Class<?> base, Class<?> implementation) {
		return implementation.isAssignableFrom(base);
	}

	/**
	 * Creates a new Instance of the given class.
	 *
	 * @param guild The slash command's guild. (if available)
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	public static @NotNull Object getInstance(Guild guild, Class<?> clazz) throws ReflectiveOperationException {
		if (guild != null || !clazz.getSuperclass().equals(GlobalSlashCommand.class)) {
			try {
				return clazz.getConstructor(Guild.class).newInstance(guild);
			} catch (NoSuchMethodException ignored) {
			}
		}
		return clazz.getConstructor().newInstance();
	}
}
