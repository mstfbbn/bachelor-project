package me.biabani.se.song.trellis;

@FunctionalInterface
public interface QuinFunction<T, U, V, W, X, R> {

    R apply(T t, U u, V v, W w, X x);
}
