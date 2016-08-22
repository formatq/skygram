package org.formatko.skygram.util;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import com.samczsun.skype4j.events.chat.ChatEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.user.User;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for skype components
 *
 * @author aivanov
 */
public class SkypeUtils {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    public static String getChatName(ChatEvent chatEvent) {
        Chat skypeChat = chatEvent.getChat();
        String loginer = skypeChat.getClient().getUsername();
        String chatName = "Unknown chat";
        if (isGroup(chatEvent)) {
            GroupChat group = (GroupChat) skypeChat;
            chatName = group.getTopic();
            if (chatName == null || chatName.isEmpty()) {
                int size = group.getAllUsers().size();
                int i = -1;
                for (User user : group.getAllUsers()) {
                    i++;
                    try {
                        if (!loginer.equals(user.getUsername())) {
                            chatName = chatName + user.getDisplayName();
                            if (i < size - 1) {
                                chatName = chatName + ", ";
                            }
                            if (chatName.length() > 55) {
                                chatName = chatName.substring(0, 52) + "...";
                                return chatName;
                            }
                        }
                    } catch (ConnectionException e) {
                        logger.log(Level.SEVERE, "Error get user name", e);
                    }
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
}
