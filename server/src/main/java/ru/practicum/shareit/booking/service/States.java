package ru.practicum.shareit.booking.service;

public enum States {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;


    public static States stringToState(String stringState) {
        for (States state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return state;
            }
        }
        return null;
    }
}
