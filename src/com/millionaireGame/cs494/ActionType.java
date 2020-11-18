package com.millionaireGame.cs494;

public enum ActionType {
    // Rule: 4 UPPERCASE letters + " " + body. E.g.: MESG Hello World
    MESG("MESG"),
    CLID("CLID"),
    DISS("DISS"),
    CONN("CONN"),
    DBCN("DBCN"), // Database connected
    CLCN("CLCN"),
    CLDN("CLDN"), // Client Disconnect
    ERRO("ERRO"),
    STGM("STGM"), // Start game - Server send to all clients.
    NAME("NAME"),
    ANSA("ANSA"),
    ANSB("ANSB"),
    ANSC("ANSC"),
    ANSD("ANSD"),
    QUES("QUES"),
    TANS("TANS"),// True answer
    ALAN("ALAN"), // Allow client to answer, it's client turn.
    CLAN("CLAN"), // Client answered
    NXQT("NXQT"), // Next question
    LOST("LOST"); // Client is lost, wrong answer.

    private String type;

    ActionType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}