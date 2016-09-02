package org.formatko.skygram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * Model of store
 *
 * @author aivanov
 */
public class Store {

    @Setter
    @Getter
    private Long tgChatId;
    @Setter
    @Getter
    private String skLogin;
    @Setter
    @Getter
    private String skPassword;
    @Setter
    @Getter
    private String skChatId;
    @Setter
    @Getter
    private String[] matchWords;
    @Setter
    @Getter
    private boolean matchWordsEnabled;
    @Setter
    @Getter
    private String greetings;


    @Override
    public String toString() {
        return "Store{" +
                "tgChatId=" + tgChatId +
                ", skLogin='" + skLogin + '\'' +
                ", skChatId=" + skChatId +
                ", matchWordsEnabled=" + matchWordsEnabled +
                ", matchWords=" + Arrays.toString(matchWords) +
                '}';
    }
}
