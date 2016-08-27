package org.formatko.skygram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public String toString() {
        return "Store{" +
                "tgChatId=" + tgChatId +
                ", skLogin='" + skLogin + '\'' +
                ", skChatId=" + skChatId +
                '}';
    }
}
