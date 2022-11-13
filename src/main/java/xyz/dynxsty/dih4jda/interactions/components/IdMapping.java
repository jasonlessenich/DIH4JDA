package xyz.dynxsty.dih4jda.interactions.components;

import javax.annotation.Nonnull;

public class IdMapping<T> {

	private final T handler;
	private final String[] ids;

	private IdMapping(T handler, String... ids) {
		this.handler = handler;
		this.ids = ids;
	}

	/**
	 * Creates a new {@link IdMapping} with the specified handler and ids.
	 *
	 * @param handler The corresponding handler class.
	 * @param ids An {@link String} array of component ids.
	 * @return The {@link IdMapping} instance.
	 * @param <T> The {@link IdMapping}s type.
	 */
	@Nonnull
	public static <T> IdMapping<T> of(T handler, String... ids) {
		return new IdMapping<>(handler, ids);
	}

	public String[] getIds() {
		return ids;
	}

	public T getHandler() {
		return handler;
	}
}
