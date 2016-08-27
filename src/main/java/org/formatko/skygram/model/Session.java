package org.formatko.skygram.model;

import java.util.*;

/**
 * Class of org.formatko.skygram.model
 *
 * @author aivanov
 */
public class Session {

    private Long tgUser;

    //    tgUser - Login+Password
    private Map<Long, Set<Credential>> userCredential = new HashMap<>();

    private Map<Long, Set<ChatLink>> userChatLinks = new HashMap<>();


    private class Credential {
        private String login;
        private String password;

        public Credential(String login, String password) {
            this.login = login;
            this.password = Base64.getEncoder().encodeToString(password.getBytes());
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return new String(Base64.getDecoder().decode(password));
        }
    }

    private class ChatLink {
        private Long tgUserRegistrator;

        private Long tgChat;
        private String skChat;

    }

    private class ChatLinkStore {

        Set<ChatLink> chatLinks = new HashSet<>();

    }

}
