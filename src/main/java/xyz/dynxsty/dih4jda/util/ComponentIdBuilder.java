package xyz.dynxsty.dih4jda.util;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class that allows for easy and consistent Component-ID building.
 *
 * @since v1.4
 */
public class ComponentIdBuilder {

	@Getter
	@Setter
	private static String defaultSeparator = ":";

	private ComponentIdBuilder() {}

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
			sb.append(defaultSeparator).append(Arrays.stream(args).map(Object::toString).collect(Collectors.joining(defaultSeparator)));
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
		return id.split(defaultSeparator);
	}
}
