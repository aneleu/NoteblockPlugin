package me.aneleu.noteblockplugin.utils;

import org.jetbrains.annotations.Contract;

public class Pair<A, B> {

    A a;
    B b;

    @Contract(pure = true)
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A first() {
        return a;
    }

    public B second() {
        return b;
    }


}