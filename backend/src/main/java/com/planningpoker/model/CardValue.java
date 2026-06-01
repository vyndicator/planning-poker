package com.planningpoker.model;

public enum CardValue {
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FIVE("5"),
    EIGHT("8"),
    THIRTEEN("13"),
    TWENTY_ONE("21"),
    THIRTY_FOUR("34"),
    QUESTION("?"),
    COFFEE("☕");

    private final String display;

    CardValue(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
