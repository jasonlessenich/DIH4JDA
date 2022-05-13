package com.dynxsty.dih4jda.util;

/**
 * Data class which holds two values.
 *
 * @param <F> The first value.
 * @param <S> The second value.
 */
public class Pair<F, S> {
	F first;
	S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}
}
