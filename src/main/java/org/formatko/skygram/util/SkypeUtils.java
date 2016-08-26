package org.formatko.skygram.util;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.chat.*;
import com.samczsun.skype4j.events.chat.ChatEvent;
import com.samczsun.skype4j.events.chat.participant.ParticipantRemovedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.internal.client.FullClient;
import com.samczsun.skype4j.participants.Participant;
import com.samczsun.skype4j.participants.info.Contact;

import java.util.Collection;
import java.util.Collections;
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
        String chatName = "";
        switch (SkypeChatType.getType(chatEvent.getChat().getClass().getSimpleName())) {
            case GROUP:
                GroupChat group = (GroupChat) skypeChat;
                chatName = group.getTopic();
                if (chatName == null || chatName.isEmpty()) {
                    try {
                        //collect all username in chat
                        String stringUsers = getStringUsers(group.getAllParticipants(), skypeChat.getSelf().getId());
                        if (stringUsers.length() > 55) {
                            stringUsers = stringUsers.substring(0, 52) + "...";
                        }
                        return stringUsers;
                    } catch (ConnectionException e) {
                        logger.log(Level.SEVERE, "Error get user name", e);
                    }
                    return "Unknown chat name";
                }
                break;
            case INDIVIDUAL:
                chatName = skypeChat.getIdentity().substring(2);
                break;
            case P2P:
                chatName = "Unknown P2P chat";
                break;
            case BOT:
                BotChat bot = (BotChat) skypeChat;
                chatName = bot.getBot().getDisplayName();
                break;
            case UNKNOWN:
                break;
        }

        return chatName;
    }

    public static boolean isGroup(ChatEvent chatEvent) {
        Chat skypeChat = chatEvent.getChat();
        return skypeChat instanceof GroupChat;
    }

    public synchronized static String getStringUsers(Collection<Participant> participants, String ignoreUsername) throws ConnectionException {
        String usersString = "";
        int i = 0;
        for (Participant participant : participants) {
            i++;
            if (ignoreUsername != null && !ignoreUsername.isEmpty()) {
                if (participant.getId().equals(ignoreUsername)) {
                    //size--;
                    continue;
                }
            }
            FullClient client = (FullClient) participant.getClient();
            client.updateContactList();
            Contact contact = client.getContact(participant.getId());
            String displayName = null;
            if (contact != null) {
                displayName = contact.getUsername();
            } else {
                displayName = getId(participant.getId());
            }
            usersString += (i == 1 ? displayName : ", " + displayName);
        }
        return usersString;
    }

    public static String getId(String username) {
        if (username == null) return null;
        return username.replace("8:", "");
    }
}
