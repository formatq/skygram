package org.formatko.skygram.store;

import com.samczsun.skype4j.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;

import java.util.*;

/**
 * Cus
 *
 * @author aivanov
 */
public class MessageCache {

    private Map<Chat, Set<Message>> mapForReply;

    public MessageCache() {
        mapForReply = Collections.synchronizedMap(new HashMap<>());
    }

    public void addMessage(Chat chat, Message message) {
        if (mapForReply.containsKey(chat)) {
            Set<Message> messages = mapForReply.get(chat);
            messages.add(message);
        } else {
            HashSet<Message> objects = new HashSet<>();
            objects.add(message);
            mapForReply.put(chat, objects);
        }
    }

    public Chat getChat(Message message) {
        for (Map.Entry<Chat, Set<Message>> chatSetEntry : mapForReply.entrySet()) {
            int i = Collections.binarySearch(new ArrayList<>(chatSetEntry.getValue()), message, (o1, o2) -> Long.compare(o1.getMessageId(), o2.getMessageId()));
            if (i >= 0) {
                return chatSetEntry.getKey();
            }
        }
        return null;
    }
}
