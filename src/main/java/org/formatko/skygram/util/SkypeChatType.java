package org.formatko.skygram.util;

/**
 * Enum of skype type
 *
 * @author aivanov
 */
public enum SkypeChatType {
    GROUP,
    INDIVIDUAL,
    P2P,
    BOT,
    UNKNOWN;

    public static SkypeChatType getType(String className) {
        switch (className) {
            case "ChatGroup":
                return GROUP;
            case "ChatIndividual":
                return INDIVIDUAL;
            case "ChatBot":
                return BOT;
            case "ChatP2P":
                return P2P;
            default:
                return UNKNOWN;
        }
    }
}
