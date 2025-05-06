package com.noom.interview.fullstack.sleep.enums;

public enum Feeling {
    BAD ("BAD"),
    GOOD ("GOOD"),
    OK ("OK");

    final String name;

    Feeling(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Feeling fromString(String name) {
        for (Feeling feeling : Feeling.values()) {
            if (feeling.name.equalsIgnoreCase(name.trim())) {
                return feeling;
            }
        }
        throw new IllegalArgumentException("No enum constant " + Feeling.class.getCanonicalName() + "." + name);
    }
}
