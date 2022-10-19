package xyz.mauwh.featherchat.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(@NotNull L left, @NotNull R right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
        this.left = left;
        this.right = right;
    }

    @NotNull
    public L getLeft() {
        return left;
    }

    @NotNull
    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        } else if (o == this) {
            return true;
        }
        Pair<?, ?> other = (Pair<?, ?>)o;
        return left.equals(other.left) && right.equals(other.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

}
