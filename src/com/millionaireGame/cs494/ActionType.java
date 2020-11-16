package com.millionaireGame.cs494;

public enum ActionType {
    // Rule: 4 UPPERCASE letters + " " + body. E.g.: MESG Hello World
    MESG("MESG"),
    CLID("CLID"),
    DISS("DISS"),
    CONN("CONN"),
    CLCN("CLCN"),
    CLDN("CLDN"), // Client Disconnect
    ERRO("ERRO"),
    STGM("STGM"), // Start game - Server send to all clients.
    NAME("NAME");

    private String type;

    ActionType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}