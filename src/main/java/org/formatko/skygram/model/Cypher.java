package org.formatko.skygram.model;

import java.util.Base64;

/**
 * Crytor for skype password
 *
 * @author aivanov
 */
public class Cypher {

    public static String encrypt(String word) {
        return Base64.getEncoder().encodeToString(word.getBytes());
    }

    public static String decrypt(String word) {
        return new String(Base64.getDecoder().decode(word));
    }
}
