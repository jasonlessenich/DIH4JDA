package xyz.dynxsty.dih4jda.util;

import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * A Pair of two elements.
 *
 * @param <F> The first value.
 * @param <S> The second value.
 * @since v1.5.2
 */
public class Pair<F, S> {
	@Getter
	private final F first;
	@Getter
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
}
