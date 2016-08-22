package org.formatko.skygram.util;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.util.logging.Logger;

import static pro.zackpollard.telegrambot.api.chat.message.send.ParseMode.MARKDOWN;

/**
 * Utilities for skype components
 *
 * @author aivanov
 */
public class TgUtils {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    public static SendableMessage markdown(String s) {
        return SendableTextMessage.builder().message(s).parseMode(MARKDOWN).build();
    }

}
