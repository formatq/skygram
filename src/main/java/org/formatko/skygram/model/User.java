package org.formatko.skygram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Model of user
 *
 * @author aivanov
 */
public class User {

    @Setter
    @Getter
    private Long tgUserId;
    @Setter
    @Getter
    private String skLogin;
    @Setter
    @Getter
    private String skPassword;

    private Set<String> ignoreSkypeChat;


    public User(Long tgUserId) {
        this.tgUserId = tgUserId;
    }

    public User(Long tgUserId, String skLogin, String skPassword) {
        this.tgUserId = tgUserId;
        this.skLogin = skLogin;
        this.skPassword = skPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return tgUserId.equals(user.tgUserId);
    }

    @Override
    public int hashCode() {
        return tgUserId.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "tgUserId=" + tgUserId +
                ", skLogin='" + skLogin + '\'' +
                '}';
    }
}
