package org.formatko.skygram.util;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import com.samczsun.skype4j.events.chat.ChatEvent;
import com.samczsun.skype4j.events.chat.user.UserRemoveEvent;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.user.User;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for skype components
 *
 * @author aivanov
 */
public class SkypeUtils {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    public static String getChatName(ChatEvent chatEvent) throws ConnectionException {
        Chat skypeChat = chatEvent.getChat();
        String chatName = "Unknown chat";
        if (isGroup(chatEvent)) {
            GroupChat group = (GroupChat) skypeChat;
            chatName = group.getTopic();
            if (chatName == null || chatName.isEmpty()) {
                // bug hack for UserRemoveEvent
                // TODO cant get topic if event is UserRemoveEvent
                if (chatEvent instanceof UserRemoveEvent) {
                    try {

                        Chat chat = group.getClient().getOrLoadChat(group.getIdentity());
                        if (chat instanceof GroupChat) {
                            chatName = ((GroupChat) chat).getTopic();
                            if (chatName != null && !chatName.isEmpty()) {
                                return chatName;
                            }
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Can't get topic for chat " + skypeChat.getIdentity() + ", event " + chatEvent.getClass(), e);
                    }
                    chatName = skypeChat.getIdentity();
                }

                try {
                    //collect all username in chat
                    String stringUsers = getStringUsers(group.getAllUsers(), skypeChat.getClient().getUsername());
                    if (stringUsers.length() > 55) {
                        stringUsers = stringUsers.substring(0, 52) + "...";
                        return stringUsers;
                    }
                } catch (ConnectionException e) {
                    logger.log(Level.SEVERE, "Error get user name", e);
                }
            }
        } else if (skypeChat instanceof IndividualChat) {
            chatName = skypeChat.getIdentity().substring(2);
        }
        return chatName;
    }

    public static boolean isGroup(ChatEvent chatEvent) {
        Chat skypeChat = chatEvent.getChat();
        return skypeChat instanceof GroupChat;
    }

    public static String getStringUsers(Collection<User> users, String ignoreUsername) throws ConnectionException {
        String usersString = "";
        int size = users.size();
        int i = -1;
        for (User user : users) {
            i++;
            if (ignoreUsername != null && !ignoreUsername.isEmpty()) {
                if (user.getUsername().equals(ignoreUsername)) {
                    continue;
                }
            }
            usersString = usersString + user.getDisplayName();
            if (i < size - 1) {
                usersString = usersString + ", ";
            }
        }
        return usersString;
    }
}
