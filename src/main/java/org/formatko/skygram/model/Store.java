package org.formatko.skygram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Class of org.formatko.skygram
 *
 * @author aivanov
 */
public class Store {

    @Setter
    @Getter
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    @Override
    public String toString() {
        return "Store{" + users + '}';
    }
}
