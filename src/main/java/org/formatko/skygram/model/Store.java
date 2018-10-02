package org.formatko.skygram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    @Setter
    @Getter
    private BirthdayStore birthdays;

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


    public class BirthdayStore {
        @Setter
        @Getter
        Map<String, String> dates = new HashMap<>();
        @Setter
        @Getter
        String[] templates;
    }
}
