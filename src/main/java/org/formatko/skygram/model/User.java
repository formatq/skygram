package org.formatko.skygram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class of org.formatko.skygram.store
 *
 * @author aivanov
 */
@AllArgsConstructor
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
