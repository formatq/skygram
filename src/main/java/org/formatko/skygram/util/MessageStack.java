package org.formatko.skygram.util;

import pro.zackpollard.telegrambot.api.chat.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class of org.formatko.skygram.util
 *
 * @author aivanov
 */
public class MessageStack {

    private Map<Message, fr.delthas.skype.message.Message> telegramToSkype = new ConcurrentHashMap<>();

    public synchronized void add(Message tg, fr.delthas.skype.message.Message sk) {
        remove(tg);
        telegramToSkype.put(tg, sk);
    }

    public synchronized void remove(Message tg) {
        Map<Long, Message> tgIdMessageMap = new HashMap<>();
        for (Message message : telegramToSkype.keySet()) {
            tgIdMessageMap.put(message.getMessageId(), message);
        }

        if (tgIdMessageMap.containsKey(tg.getMessageId())) {
            telegramToSkype.remove(tgIdMessageMap.get(tg.getMessageId()));
        }
    }

    public synchronized fr.delthas.skype.message.Message findSkypeMessage(Message tg) {
        for (Map.Entry<Message, fr.delthas.skype.message.Message> entry : telegramToSkype.entrySet()) {
            if (Objects.equals(entry.getKey().getMessageId(), tg.getMessageId())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public synchronized fr.delthas.skype.message.Message findSkypeMessage(Long gtMessageId) {
        for (Map.Entry<Message, fr.delthas.skype.message.Message> entry : telegramToSkype.entrySet()) {
            if (Objects.equals(entry.getKey().getMessageId(), gtMessageId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Message findTgMessage(fr.delthas.skype.message.Message message) {
        for (Map.Entry<Message, fr.delthas.skype.message.Message> entry : telegramToSkype.entrySet()) {
            if (Objects.equals(entry.getValue().getId(), message.getId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Message findTgMessage(String mesId) {
        for (Map.Entry<Message, fr.delthas.skype.message.Message> entry : telegramToSkype.entrySet()) {
            if (Objects.equals(entry.getValue().getId(), mesId)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
