package org.formatko.skygram.util;

import pro.zackpollard.telegrambot.api.chat.message.Message;

import java.util.LinkedHashMap;

/**
 * Class of org.formatko.skygram.util
 *
 * @author aivanov
 */
public class MessageStack {

    private LinkedHashMap<Message, fr.delthas.skype.message.Message> telegramToSkype;

    public synchronized void add(Message tg, fr.delthas.skype.message.Message sk){

    }

}
