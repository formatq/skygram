package org.formatko.skygram;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import pro.zackpollard.telegrambot.api.TelegramBot;

/**
 * Class of org.formatko.skygram
 *
 * @author aivanov
 */
public class Main {
    public static void main(String[] args) throws ConnectionException, NotParticipatingException, InvalidCredentialsException {
        Skygram skygram = new Skygram(args[0]);
        skygram.init();
        skygram.start();
    }
}
