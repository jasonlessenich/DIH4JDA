package xyz.dynxsty.dih4jda.interactions.components;

import javax.annotation.Nonnull;

/**
 * Simple POJO that holds a handler, {@link T}, and an array of {@link String}s.
 *
 * @param <T> {@link T The handlers type}.
 */
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

	/**
	 * Gets you the ids that linked themselves with their {@link IdMapping#getHandler()}.
	 *
	 * @return the ids as an {@link String} array.
	 */
	public String[] getIds() {
		return ids;
	}

	/**
	 * Gets you the handler that was linked with the {@link IdMapping#getIds()}.
	 *
	 * @return the handler instance.
	 */
	public T getHandler() {
		return handler;
	}
}
