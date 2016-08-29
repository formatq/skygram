package org.formatko.skygram.util;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.util.logging.Logger;

import static pro.zackpollard.telegrambot.api.chat.message.send.ParseMode.HTML;
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

    public static SendableMessage html(String s) {
        return SendableTextMessage.builder().message(s).parseMode(HTML).build();
    }

    public static String sanitize(String html) {
        return html.replaceAll("&apos;", "'").replaceAll("<s raw_pre=\"~\" raw_post=\"~\">", "").replaceAll("</s>", "");
    }

    public static String pre(String text) {
        return "<pre raw_pre=\"{code}\" raw_post=\"{code}\">" + text + "</pre>";
    }

    public static String b(String text) {
        return "<b raw_pre=\"*\" raw_post=\"*\">" + text + "</b>";
    }

}
