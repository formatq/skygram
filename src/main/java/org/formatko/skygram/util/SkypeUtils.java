package org.formatko.skygram.util;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import com.samczsun.skype4j.events.chat.ChatEvent;

/**
 * Class of org.formatko.skygram.util
 *
 * @author aivanov
 */
public class SkypeUtils {

    public static String getChatName(ChatEvent chatEvent) {
        Chat skypeChat = chatEvent.getChat();
        String chatName = "";
        if (isGroup(chatEvent)) {
            chatName = ((GroupChat) skypeChat).getTopic();
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
