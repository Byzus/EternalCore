package com.eternalcode.core.notification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Notification {

    protected final Set<NoticeType> types = new LinkedHashSet<>();
    protected final String message;

    protected Notification(String message, NoticeType... types) {
        this.message = message;
        this.types.addAll(Arrays.asList(types));
    }

    protected Notification(String message, Collection<NoticeType> types) {
        this.message = message;
        this.types.addAll(types);
    }

    public Notification fork(UnaryOperator<String> fork) {
        return new Notification(fork.apply(this.message), this.types);
    }

    public Set<NoticeType> getTypes() {
        return Collections.unmodifiableSet(this.types);
    }

    public String getMessage() {
        return this.message;
    }

    public static Notification of(String message, NoticeType... types) {
        return new Notification(message, types);
    }

    public static Notification of(String message, Collection<NoticeType> types) {
        return new Notification(message, types);
    }

    public static Notification actionbar(String message) {
        return new Notification(message, NoticeType.ACTIONBAR);
    }

    public static Notification chat(String message) {
        return new Notification(message, NoticeType.CHAT);
    }

    public static Notification title(String message) {
        return new Notification(message, NoticeType.TITLE);
    }

    public static Notification subtitle(String message) {
        return new Notification(message, NoticeType.SUBTITLE);
    }

    public static Notification disabled(String message) {
        return new Notification(message, NoticeType.DISABLED);
    }

}
