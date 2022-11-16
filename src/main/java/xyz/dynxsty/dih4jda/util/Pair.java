package xyz.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;
import java.util.Objects;

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
	public Pair(@Nonnull F first, @Nonnull S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets you the {@link Object} that was defined as first.
	 *
	 * @return the first {@link Object}.
	 */
	@Nonnull
	public F getFirst() {
		return first;
	}

	/**
	 * Gets you the {@link Object} that was defined as second.
	 *
	 * @return the second {@link Object}.
	 */
	@Nonnull
	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}
