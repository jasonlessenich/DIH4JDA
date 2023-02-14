package xyz.dynxsty.dih4jda.util;

import org.jetbrains.annotations.Contract;

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
	 * Creates a new {@link Pair} of two {@link Object}s.
	 *
	 * @param first the first {@link Object}.
	 * @param second the second {@link Object}.
	 */
	public Pair(@Nonnull F first, @Nonnull S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Creates a new {@link Pair} of two {@link Object}s.
	 *
	 * @param first The first value.
	 * @param second The second value.
	 * @return The new {@link Pair}.
	 * @param <F> The first {@link Object}
	 * @param <S> The second {@link Object}.
	 */
	@Nonnull
	@Contract(value = "_, _ -> new", pure = true)
	public static <F, S> Pair<F, S> of(@Nonnull F first, @Nonnull S second) {
		return new Pair<>(first, second);
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

	/**
	 * Returns a string representation of the object.
	 *
	 * @return The representation as a {@link String}.
	 */
	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}

	/**
	 * Checks if the objects are qual.
	 *
	 * @param o The {@link Object} you want to compare.
	 * @return True if they contain the same values.
	 */
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

	/**
	 * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by HashMap.
	 *
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}
