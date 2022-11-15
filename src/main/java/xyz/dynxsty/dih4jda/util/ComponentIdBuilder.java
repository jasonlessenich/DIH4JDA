package xyz.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class that allows for easy and consistent Component-ID building.
 *
 * @since v1.4
 */
public class ComponentIdBuilder {

	private static String separator = ":";

	private ComponentIdBuilder() {}

	/**
	 * Changes the default component-id separator.
	 *
	 * @param separator The string that should act as the separator.
	 * @since v1.4
	 */
	public static void setDefaultSeparator(@Nonnull String separator) {
		ComponentIdBuilder.separator = separator;
	}

	/**
	 * Gets the current separator.
	 *
	 * @return The separator.
	 * @since v1.4
	 */
	@Nonnull
	public static String getSeparator() {
		return separator;
	}

	/**
	 * Builds a component-id using the (set) separator and the given identifier and arguments.
	 * <pre>{@code
	 * Button.secondary(ComponentIdBuilder.build("self-role", roleId), "Click me!");
	 * }</pre>
	 *
	 * @param identifier The component's identifier.
	 * @param args       An optional parameter for arguments.
	 * @return The built component-id, as a {@link String}.
	 * @since v1.4
	 */
	@Nonnull
	public static String build(@Nonnull String identifier, @Nonnull Object... args) {
		StringBuilder sb = new StringBuilder(identifier);
		if (args.length > 0) {
			sb.append(separator).append(Arrays.stream(args).map(Object::toString).collect(Collectors.joining(separator)));
		}
		return sb.toString();
	}

	/**
	 * Splits the given id by the current separator.
	 *
	 * @param id The component-id that should be split.
	 * @return The split String as an array.
	 * @since v1.4
	 */
	@Nonnull
	public static String[] split(@Nonnull String id) {
		return id.split(separator);
	}
}
