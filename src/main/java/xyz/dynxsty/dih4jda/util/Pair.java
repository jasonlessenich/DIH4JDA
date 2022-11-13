package xyz.dynxsty.dih4jda.util;

/**
 * A Pair of two elements.
 *
 * @param <F> The first value.
 * @param <S> The second value.
 * @since v1.5.2
 */
public class Pair<F, S> {
	private final F first;
	private final S second;

	/**
	 * Creates a new {@link Pair} of to {@link Object}s.
	 *
	 * @param first the first {@link Object}.
	 * @param second the second {@link Object}.
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets you the {@link Object} that was defined as first.
	 *
	 * @return the first {@link Object}.
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Gets you the {@link Object} that was defined as second.
	 *
	 * @return the second {@link Object}.
	 */
	public S getSecond() {
		return second;
	}
}
