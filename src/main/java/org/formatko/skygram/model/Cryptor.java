package org.formatko.skygram.model;

import java.util.Base64;

/**
 * Class of org.formatko.skygram.model
 *
 * @author aivanov
 */
public class Cryptor {

    private String salt;

    public Cryptor(String salt) {
        this.salt = salt;
    }

    public String encrypt(String word) {
        return Base64.getEncoder().encodeToString(word.getBytes());
    }
}
